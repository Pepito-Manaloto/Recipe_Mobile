#!/bin/bash

COMMIT_COUNT=$(git log --oneline | wc -l);
SHA=$(git rev-parse --short HEAD);
BRANCH=$(git rev-parse --abbrev-ref HEAD);

echo "${COMMIT_COUNT}-${SHA}-${BRANCH}";