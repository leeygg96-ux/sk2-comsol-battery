#!/usr/bin/env bash
set -euo pipefail

MODULE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_FILE="${1:-$MODULE_DIR/src/main/java/SOC100_3cell_offset_v02.java}"
BUILD_DIR="${2:-$MODULE_DIR/outputs/build}"

if ! command -v comsol >/dev/null 2>&1; then
  echo "comsol was not found on PATH." >&2
  exit 127
fi

mkdir -p "$BUILD_DIR"
cp "$JAVA_FILE" "$BUILD_DIR/"
(
  cd "$BUILD_DIR"
  comsol compile -verbose "$(basename "$JAVA_FILE")"
)

echo "Compiled class files are in $BUILD_DIR"
