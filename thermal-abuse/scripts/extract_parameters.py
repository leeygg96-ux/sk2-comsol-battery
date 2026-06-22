#!/usr/bin/env python3
"""Extract COMSOL global parameters from the thermal-abuse Java source."""

from __future__ import annotations

import argparse
import csv
import io
import re
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
MODULE_DIR = SCRIPT_DIR.parent
SOURCE = MODULE_DIR / "src" / "main" / "java" / "SOC100_3cell_offset_v02.java"
OUTPUT = MODULE_DIR / "docs" / "PARAMETER_REGISTER.csv"

PARAM_RE = re.compile(
    r'model\.param\(\)\.set\(\s*"(?P<name>[^"]+)"\s*,\s*'
    r'"(?P<expression>(?:\\.|[^"])*)"'
    r'(?:\s*,\s*"(?P<description>(?:\\.|[^"])*)")?\s*\);',
    re.DOTALL,
)

STATUS = {
    "gap_full": "measure",
    "Rth_int": "calibrate",
    "rho_int": "placeholder",
    "Cp_int": "placeholder",
    "Hchem_base": "owner-defined",
    "m_soh1": "future-handle",
    "m_soh2": "future-handle",
    "m_soh3": "future-handle",
    "h_soh1": "future-handle",
    "h_soh2": "future-handle",
    "h_soh3": "future-handle",
}


def extract(text: str) -> list[dict[str, str | int]]:
    rows: list[dict[str, str | int]] = []
    for match in PARAM_RE.finditer(text):
        line = text.count("\n", 0, match.start()) + 1
        rows.append(
            {
                "name": match.group("name"),
                "expression": match.group("expression").replace('\\"', '"'),
                "description": (match.group("description") or "").replace('\\"', '"'),
                "review_status": STATUS.get(match.group("name"), "baseline"),
                "source_line": line,
            }
        )
    return rows


def render(rows: list[dict[str, str | int]]) -> str:
    buffer = io.StringIO(newline="")
    writer = csv.DictWriter(
        buffer,
        fieldnames=["name", "expression", "description", "review_status", "source_line"],
        lineterminator="\n",
    )
    writer.writeheader()
    writer.writerows(rows)
    return buffer.getvalue()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true", help="Fail if the checked-in CSV is stale")
    args = parser.parse_args()

    if not SOURCE.exists():
        print(f"Source not found: {SOURCE}", file=sys.stderr)
        return 2

    content = render(extract(SOURCE.read_text(encoding="utf-8")))
    if args.check:
        if not OUTPUT.exists() or OUTPUT.read_text(encoding="utf-8") != content:
            print(f"Parameter register is stale. Run: {Path(__file__).name}", file=sys.stderr)
            return 1
        print(f"Parameter register is current: {OUTPUT}")
        return 0

    OUTPUT.write_text(content, encoding="utf-8", newline="")
    print(f"Wrote {OUTPUT}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
