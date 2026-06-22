# Repository instructions for Codex

## Scope
This repository contains COMSOL battery-model source code. The paper manuscript belongs in a separate repository and must not be created here.

## Repository boundaries
- `thermal-abuse/`: thermal abuse, thermal runaway, and propagation.
- `charge-discharge/`: charge/discharge electrochemical and thermal work.
- `shared/`: only code or data definitions proven to be needed by both modules.
- Do not edit another module unless the task explicitly requests it.
- Do not move code into `shared/` merely because it might be useful later.

## Scientific-change rules
- Treat unit-bearing COMSOL expressions, selections, feature tags, kinetics, boundary conditions, and material properties as scientific behavior.
- Do not change scientific behavior silently.
- For every scientific change, update the affected model card or change log and state the evidence used.
- Placeholder or calibration parameters must remain clearly labeled until the model owner supplies a value or source.
- Never claim COMSOL compilation, convergence, or numerical validation unless it was actually run and the log is available.

## Generated artifacts
- Java source and small text/CSV manifests are versioned.
- Generated `.mph`, `.class`, logs, recovery folders, and large result exports are not committed by default.
- If a validated MPH must be preserved, discuss Git LFS or an external release artifact before adding it.

## Required checks
Run the checks relevant to changed paths. For thermal-abuse source changes, run:

```bash
python thermal-abuse/tests/verify_source_contract.py
python thermal-abuse/scripts/extract_parameters.py --check
```

If COMSOL 6.4 is available, also compile the Java model and preserve the batch log outside Git.

## Definition of done
- Changes stay within the requested module boundary.
- Static checks pass.
- Scientific changes are documented.
- Generated files are not staged.
- The final summary distinguishes tested facts from untested assumptions.
