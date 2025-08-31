#!/bin/sh

echo "Build and Run Start"

BASE_PATH=~/personal/kotlin-multi-module

cd $BASE_PATH

./gradlew jar -p user-service

export JAVA_TOOL_OPTIONS="-javaagent:$BASE_PATH/opentelemetry/opentelemetry-javaagent.jar" \
  OTEL_TRACES_EXPORTER=logging \
  OTEL_METRICS_EXPORTER=logging \
  OTEL_LOGS_EXPORTER=logging \
  OTEL_METRIC_EXPORT_INTERVAL=15000

java -jar $BASE_PATH/user-service/build/libs/user-service-1.0.jar