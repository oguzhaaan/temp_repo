#!/bin/bash

set -e

echo "Running tests and generating coverage report for ReservationService..."
cd ReservationService
./gradlew clean test jacocoTestReport
cd ..

echo "Running tests and generating coverage report for UserManagementService..."
cd UserManagementService
./gradlew clean test jacocoTestReport
cd ..

# Open the HTML reports in the default browser
echo "Opening Jacoco coverage reports..."

open_report() {
    local report_path="$1"
    if command -v xdg-open > /dev/null; then
        xdg-open "$report_path"
    elif command -v open > /dev/null; then
        open "$report_path"
    elif command -v start > /dev/null; then
        start "" "$report_path"
    else
        echo "⚠️ Could not detect a method to open HTML files automatically. Please open $report_path manually."
    fi
}

open_report "ReservationService/build/jacocoHtml/index.html"
open_report "UserManagementService/build/jacocoHtml/index.html"

echo "✅ All tests completed and coverage reports opened."
