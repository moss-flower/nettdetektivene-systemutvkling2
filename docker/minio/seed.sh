#!/bin/sh
set -eu

until mc alias set local http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null 2>&1; do
  sleep 2
done

mc mb --ignore-existing "local/$MINIO_PUBLIC_BUCKET"
mc anonymous set download "local/$MINIO_PUBLIC_BUCKET"

for directory in stations tasks worlds; do
  mc cp --recursive "/seed-data/$directory" "local/$MINIO_PUBLIC_BUCKET/seed/"
done
