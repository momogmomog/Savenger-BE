#!/bin/bash

# ctx dir setup
ORIGINAL_DIR=$(pwd)
SCRIPT_DIR=$(dirname "$0")
cd "$SCRIPT_DIR"
trap 'cd "$ORIGINAL_DIR"' EXIT

# end ctx dir setup


BE_BRANCH="master"
IMAGE_NAME="savanger-be"
CONTAINER_NAME="savanger-be"
IMAGE_VERSION="1.0"

# Get the latest version number of the existing images
LATEST_VERSION=$(docker images --format '{{.Tag}}' $IMAGE_NAME | sort -V | tail -n 1)

# If no version is found, start from 1.0
if [ -z "$LATEST_VERSION" ]; then
    LATEST_VERSION="1.0"
fi

# Increment the version number
IFS='.' read -ra ADDR <<< "$LATEST_VERSION"
MAJOR=${ADDR[0]}
MINOR=${ADDR[1]}
MINOR=$((MINOR + 1))
IMAGE_VERSION="$MAJOR.$MINOR"

git fetch origin
git pull origin $BE_BRANCH


FINAL_IMAGE_NAME=$IMAGE_NAME:$IMAGE_VERSION

if ! docker build -t $FINAL_IMAGE_NAME .; then
    echo "Docker build failed. Exiting..."
    exit 1
fi

if docker ps -a --format '{{.Names}}' | grep -w "^$CONTAINER_NAME$"; then
    echo "Container $CONTAINER_NAME exists, stopping and removing..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
else
    echo "Container $CONTAINER_NAME does not exist."
fi

docker run --restart=always -d --name $CONTAINER_NAME -p 8089:8080 --network savanger-network --env-file ./conf/.env $FINAL_IMAGE_NAME

