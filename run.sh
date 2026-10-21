#!/bin/bash
# run.sh

set -e

COMMAND=${1:-serve}

if [[ "$COMMAND" == "dev" || "$COMMAND" == "prod" ]]; then
  ENV=$COMMAND
  COMMAND="serve"
else
  ENV=${2:-dev}
fi

if [[ "$ENV" != "dev" && "$ENV" != "prod" ]]; then
  echo "Unknown environment: $ENV"
  echo "Available environments: dev, prod"
  exit 1
fi

case "$COMMAND" in
  purge)
    echo "Purging containers and volumes..."
    cd docker || exit 1
    docker compose -f docker-compose.$ENV.yaml down -v
    ;;

  serve)
    echo "Serving with env=$ENV..."

    if [[ "$ENV" == "dev" ]]; then
      mvn clean install -DskipTests || exit 1
    fi

    cd docker || exit 1

    docker ps -a --format '{{.Names}}' |
      grep '^faculty-flow-' |
      grep -v 'postgres' |
      xargs -r docker rm -f

    if [[ "$ENV" == "prod" ]]; then
      docker compose -f docker-compose.$ENV.yaml pull
      docker compose -f docker-compose.$ENV.yaml up
    else
      SPRING_PROFILES_ACTIVE=$ENV \
        docker compose -f docker-compose.$ENV.yaml up --build
    fi
    ;;

  build)
    echo "Building with env=$ENV..."

    if [[ "$ENV" == "dev" ]]; then
      mvn clean install -DskipTests || exit 1
      cd docker || exit 1
      docker compose -f docker-compose.$ENV.yaml build
    else
      echo "Production uses published images; nothing to build."
      exit 0
    fi
    ;;

  *)
    echo "Unknown command: $COMMAND"
    echo "Usage: $0 [serve|build|purge] [dev|prod]"
    echo "       $0 [dev|prod] (shorthand for serve)"
    exit 1
    ;;
esac
```