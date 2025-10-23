#!/bin/bash

echo "⏳ Waiting for Kafka (kafka:9092) to be ready..."

MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
  echo "Attempt $((RETRY_COUNT + 1)): Checking Kafka connectivity..."

  # Use native Kafka command to test connection (internal Docker hostname)
  if kafka-topics --bootstrap-server kafka:9092 --list > /dev/null 2>&1; then
    echo "✅ Kafka is ready!"
    break
  fi

  sleep 5
  RETRY_COUNT=$((RETRY_COUNT + 1))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
  echo "⚠️ Kafka not ready after $MAX_RETRIES attempts. Continuing anyway..."
fi

echo "📦 Creating Kafka topics..."

# Create topics (using internal broker address)
kafka-topics --create --if-not-exists \
  --bootstrap-server kafka:9092 \
  --topic microservice.logs \
  --partitions 1 \
  --replication-factor 1

kafka-topics --create --if-not-exists \
  --bootstrap-server kafka:9092 \
  --topic microservice.metrics \
  --partitions 1 \
  --replication-factor 1

echo "📋 Listing topics:"
kafka-topics --list --bootstrap-server kafka:9092

echo "🎉 Kafka topics created successfully!"
