# First Codex task — COMSOL 6.4 baseline verification

## Objective

Verify the imported thermal-abuse Java baseline on the licensed COMSOL 6.4 workstation **without changing scientific behavior**.

## Read first

- `/AGENTS.md`
- `/thermal-abuse/AGENTS.md`
- `/thermal-abuse/docs/MODEL_CARD.md`
- `/thermal-abuse/docs/IMPORT_PROVENANCE.md`

## Allowed paths

- `thermal-abuse/scripts/`
- `thermal-abuse/tests/`
- `thermal-abuse/docs/`
- `thermal-abuse/src/main/java/SOC100_3cell_offset_v02.java` only for a compile/API correction that does not alter intended physics

## Forbidden changes

- Do not change parameter values, kinetics, heat-source expressions, boundary conditions, geometry dimensions, study time, or mesh level.
- Do not enable `INCLUDE_BUSBARS` or `SOLVE_MODEL` by default.
- Do not replace named selections with hard-coded entity numbers.
- Do not commit `.mph`, `.class`, or `.log` files.

## Procedure

1. Record the exact COMSOL version and operating system.
2. Run the static checks.
3. Compile the Java source with COMSOL 6.4.
4. Generate an unsolved MPH template.
5. Open or inspect the model and verify that the required named selections are nonempty:
   - `activeAll`
   - `interfaceAll`
   - `negativeMetal`
   - `positiveMetal`
   - `outerBnd`
6. Verify that study `std1`, all five Domain ODE interfaces, heat transfer, and the nine cell probes exist.
7. If an API error occurs, make the smallest possible correction and explain why it does not change intended physics.
8. Save logs under `thermal-abuse/outputs/logs/` locally; do not stage them.
9. Write the result to `thermal-abuse/docs/BASELINE_VERIFICATION.md` using the template below.

## Acceptance criteria

- `python thermal-abuse/tests/verify_source_contract.py` passes.
- `python thermal-abuse/scripts/extract_parameters.py --check` passes.
- COMSOL Java compilation succeeds.
- An unsolved MPH template is generated and opens.
- Required selections and model nodes are present and nonempty.
- `git status --short` contains no generated artifact.
- Any source change is limited to API/compile compatibility and is documented line by line.

## Verification report template

```markdown
# Baseline verification

- Date:
- OS:
- COMSOL version/build:
- Git commit:
- Java source SHA-256:

## Commands

## Static checks

## COMSOL compile

## MPH generation

## Selection and node audit

## Source changes, if any

## Result
- Status: PASS / FAIL / PARTIAL
- Remaining uncertainty:
- Local log paths:
```

## Suggested Codex prompt

```text
Work only on the first thermal-abuse baseline verification described in
thermal-abuse/docs/NEXT_TASK.md. Do not alter scientific behavior. Run every
available check locally, preserve exact commands and errors, and create
thermal-abuse/docs/BASELINE_VERIFICATION.md. Do not commit generated MPH,
class, or log files. Stop before any physics or parameter change.
```
