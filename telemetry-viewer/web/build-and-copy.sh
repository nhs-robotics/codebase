#!/bin/sh
set -e

cd "$(dirname "$0")"

npm install
npm run build

echo "Built. Open dist/index.html in a browser to use the telemetry viewer."
