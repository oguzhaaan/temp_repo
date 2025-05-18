# Dockerfile for frontend production
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Nginx base image
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Optional: custom nginx.conf
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
