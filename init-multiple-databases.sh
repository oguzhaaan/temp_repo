#!/bin/bash
set -e

function create_user_and_database() {
  local database=$1
  local user=$2
  local password=$3

  echo "Creating user '$user' and database '$database'..."

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE USER $user WITH PASSWORD '$password';
    ALTER ROLE $user WITH REPLICATION;
    CREATE DATABASE $database WITH OWNER $user;
EOSQL
}

if [[ -z "$POSTGRES_MULTIPLE_DATABASES" ]]; then
  echo "POSTGRES_MULTIPLE_DATABASES not set — skipping database creation"
  exit 0
fi

IFS=',' read -ra DB_TUPLES <<< "$POSTGRES_MULTIPLE_DATABASES"

for db_tuple in "${DB_TUPLES[@]}"; do
  IFS=':' read -ra PARTS <<< "$db_tuple"
  db="${PARTS[0]}"
  user="${PARTS[1]}"
  pass="${PARTS[2]}"

  if [[ -z "$db" || -z "$user" || -z "$pass" ]]; then
    echo "Invalid entry in POSTGRES_MULTIPLE_DATABASES: $db_tuple"
    exit 1
  fi

  create_user_and_database "$db" "$user" "$pass"
done

# Optional: Wait for payment_db to be fully created before issuing commands
echo "Waiting for 'payment_db' to be available..."
until psql -U "$POSTGRES_USER" -d payment_db -c '\q' 2>/dev/null; do
  sleep 1
done

echo "Setting up publication and permissions in 'payment_db'..."
psql -U "$POSTGRES_USER" -d payment_db <<-EOSQL
  CREATE PUBLICATION dbz_publication FOR TABLE public.paypal_outbox_events;

  GRANT SELECT ON TABLE public.paypal_outbox_events TO pay_user;
  GRANT USAGE ON SCHEMA public TO pay_user;
EOSQL
