#!/bin/bash
# run.sh
# Check parameter
COMMAND=${1:-serve}

# If first arg looks like an env (dev|prod|staging...), treat it as: serve + that env
if [[ "$COMMAND" == "dev" || "$COMMAND" == "prod" || "$COMMAND" == "staging" ]]; then
  ENV=$COMMAND
  COMMAND="serve"
else
  # Second arg can be explicit env: ff serve dev
  ENV=${2:-prod}
fi

case "$COMMAND" in
  purge)
    echo "Purging containers and volumes..."
    cd docker || exit 1
    docker compose down -v
    ;;
  serve)
    echo "Building and serving with env=$ENV..."
    mvn clean install -D skipTests || exit 1
    cd docker || exit 1
    docker ps -a --format '{{.Names}}' | grep '^faculty-flow-' | grep -v 'postgres' | xargs -r docker rm -f
    SPRING_PROFILES_ACTIVE=$ENV docker compose up --build
    ;;
  build)
    echo "Building only..."
    mvn clean install -D skipTests || exit 1
    cd docker || exit 1
    docker compose build
    ;;
  *)
    echo "Unknown command: $COMMAND"
    echo "Usage: $0 [serve|build|purge] [dev|prod|staging]"
    echo "       $0 [dev|prod|staging]          (shorthand for serve)"
    exit 1
    ;;
esac
