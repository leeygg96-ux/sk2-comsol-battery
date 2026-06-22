#!/usr/bin/env bash
set -euo pipefail

MODULE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$MODULE_DIR/outputs/build"
MODEL_DIR="$MODULE_DIR/outputs/models"
LOG_DIR="$MODULE_DIR/outputs/logs"
OUTPUT="${1:-$MODEL_DIR/SOC100_3cell_offset_v02.mph}"
MODE="${2:-template}"

"$MODULE_DIR/scripts/compile-model.sh" "$MODULE_DIR/src/main/java/SOC100_3cell_offset_v02.java" "$BUILD_DIR"
mkdir -p "$MODEL_DIR" "$LOG_DIR"

ARGS=(batch -inputfile "$BUILD_DIR/SOC100_3cell_offset_v02.class" -outputfile "$OUTPUT" -batchlog "$LOG_DIR/SOC100_3cell_offset_v02.log")
if [[ "$MODE" == "solve" ]]; then
  ARGS+=(-study std1)
else
  ARGS+=(-norun)
fi

comsol "${ARGS[@]}"
echo "MPH output: $OUTPUT"
echo "Batch log: $LOG_DIR/SOC100_3cell_offset_v02.log"
