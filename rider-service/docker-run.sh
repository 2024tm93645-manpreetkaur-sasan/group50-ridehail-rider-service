#!/bin/bash
echo "Starting Rider Service stack (detached mode)..."
docker-compose up -d --build
echo "Running. Rider: http://localhost:9081  Kibana: http://localhost:5601  ES: http://localhost:9200"
