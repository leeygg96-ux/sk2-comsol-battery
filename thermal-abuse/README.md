# Thermal abuse module

## Baseline

- Source: `src/main/java/SOC100_3cell_offset_v02.java`
- Purpose: three-cell pouch-cell thermal-abuse and propagation baseline
- COMSOL target: 6.4
- Geometry: three scaled cells stacked along `z`, two effective interfaces, one external heater solid
- Physics: solid heat transfer plus independent domain ODE reaction states in each active cell
- Default behavior: `SOLVE_MODEL = false`, so the Java source is intended to create an unsolved MPH template unless changed or run through a study externally

## Current model-owner items

The following values are explicitly placeholders or calibration handles and must not be silently treated as validated:

- `gap_full`
- `Rth_int`
- `rho_int`
- `Cp_int`
- approximate busbar geometry when enabled
- cell-specific `m_soh*` and `h_soh*` factors

See `docs/MODEL_CARD.md` and `docs/PARAMETER_REGISTER.csv`.

## Static checks

```bash
python tests/verify_source_contract.py
python scripts/extract_parameters.py --check
```

## COMSOL 6.4 compile and MPH generation

Windows:

```powershell
./scripts/compile-model.ps1
./scripts/build-mph.ps1
```

Linux:

```bash
./scripts/compile-model.sh
./scripts/build-mph.sh
```

The scripts expect COMSOL commands to be available on `PATH`. They have not been executed in this repository-build environment because COMSOL is not installed here.
