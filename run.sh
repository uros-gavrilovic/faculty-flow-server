#!/bin/bash
# run.sh

# Check parameter
COMMAND=${1:-serve}

case "$COMMAND" in
  purge)
    echo "Purging containers and volumes..."
    cd faculty-flow-docker || exit 1
    docker compose down -v
    ;;
  serve)
    echo "Building and serving..."
    mvn clean install -D skipTests || exit 1
    cd faculty-flow-docker || exit 1
    docker compose up --build
    ;;
  *)
    echo "Unknown command: $COMMAND"
    echo "Usage: $0 [serve|purge]"
    exit 1
    ;;
esac
