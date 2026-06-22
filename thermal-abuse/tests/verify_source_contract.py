#!/usr/bin/env python3
"""Static, COMSOL-independent checks for the thermal-abuse Java baseline."""

from __future__ import annotations

import re
import sys
from pathlib import Path

MODULE_DIR = Path(__file__).resolve().parents[1]
SOURCE = MODULE_DIR / "src" / "main" / "java" / "SOC100_3cell_offset_v02.java"


def require(condition: bool, message: str, failures: list[str]) -> None:
    if not condition:
        failures.append(message)


def main() -> int:
    failures: list[str] = []
    text = SOURCE.read_text(encoding="utf-8")

    class_match = re.search(r"public\s+class\s+(\w+)", text)
    require(class_match is not None, "Public class declaration was not found", failures)
    if class_match:
        require(class_match.group(1) == SOURCE.stem, "Public class does not match the Java filename", failures)

    require("SOC100_3cell_offset_v02.java" in text[:300], "Header filename is not v02", failures)
    require("Main assumptions in v0.2" in text[:500], "Header assumptions version is not v0.2", failures)
    require("private static final boolean SOLVE_MODEL = false;" in text,
            "Baseline must keep SOLVE_MODEL=false", failures)

    required_tokens = [
        'selection().create("activeAll", "Union")',
        'selection().create("interfaceAll", "Union")',
        'selection().create("outerBnd", "Adjacent")',
        'physics().create("ht", "HeatTransfer", "geom1")',
        'physics().create("dodeSep", "DomainODE", "geom1")',
        'physics().create("dodeChem", "DomainODE", "geom1")',
        'physics().create("dodeEle", "DomainODE", "geom1")',
        'physics().create("dodeNeg", "DomainODE", "geom1")',
        'physics().create("dodePos", "DomainODE", "geom1")',
        'study("std1").create("time", "Transient")',
        'range(0,0.1,500)',
        'geom1_act1_dom',
        'geom1_act2_dom',
        'geom1_act3_dom',
    ]
    for token in required_tokens:
        require(token in text, f"Required model contract token missing: {token}", failures)

    hard_coded_selection = re.search(r"selection\(\)\.set\(\s*new\s+int", text)
    require(hard_coded_selection is None,
            "Potential hard-coded entity-number selection found; use named selections", failures)

    if failures:
        print("Thermal-abuse source contract FAILED:", file=sys.stderr)
        for failure in failures:
            print(f"- {failure}", file=sys.stderr)
        return 1

    print(f"Thermal-abuse source contract passed: {SOURCE}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
