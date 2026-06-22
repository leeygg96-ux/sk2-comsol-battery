# Thermal-abuse instructions

## Current baseline
- Primary source: `src/main/java/SOC100_3cell_offset_v02.java`.
- Target runtime: COMSOL Multiphysics 6.4 Java API.
- Scope: external heating, thermal runaway, and three-cell propagation.
- Default source setting keeps `SOLVE_MODEL = false` and creates an unsolved template.

## Preserve unless explicitly requested
- Geometry feature tags and generated selection names.
- Named selections instead of hard-coded entity numbers.
- Unit-bearing COMSOL expressions.
- Independent reaction-state fields on all active-cell domains.
- Existing cell order and `z1`, `z2`, `z3` interpretation.
- The distinction between calibrated, placeholder, and baseline parameters.

## Scientific-change requirements
A change is scientific if it affects geometry, material properties, heat sources, kinetics, ODEs, initial conditions, boundary conditions, mesh, study time, or solver behavior.

For a scientific change:
1. Update `docs/MODEL_CARD.md` or add an entry to `CHANGELOG.md`.
2. State the old and new expressions or values.
3. State the evidence or owner instruction.
4. Run static checks.
5. Compile/run in COMSOL when available and preserve the log outside Git.

## Do not
- Replace `Rth_int`, interface properties, or gap dimensions with literature values without a task-specific source and approval.
- Enable busbars merely to make geometry look complete.
- commit generated `.mph`, `.class`, or log files.
- claim that the source compiles or solves based only on text inspection.

## Checks
```bash
python thermal-abuse/tests/verify_source_contract.py
python thermal-abuse/scripts/extract_parameters.py --check
```
