# Model card — SOC100 three-cell thermal-abuse baseline v0.2

## Intended use

Create a three-cell, scaled pouch-cell COMSOL model for external thermal abuse and cell-to-cell thermal propagation studies. The model is a research baseline, not a validated safety prediction model.

## Geometry

- Geometric scale: `1/3`
- Three identical pouch cells stacked in the `z` direction
- Active domain, shell, positive tab, and negative tab for each cell
- Two thin effective interlayers (`int12`, `int23`)
- Thin heater solid on the outer broad face of Cell 1
- Optional approximate busbars are disabled by default

## Physics

- Heat Transfer in Solids
- Cell-active-domain thermal source `Q_tot`
- Arrhenius-type reaction rates for SEI, negative, electrolyte, positive, and separator terms
- Independent domain ODE fields over each disconnected active cell
- Convective heat flux on the true exterior boundary selection
- Time-dependent study: `range(0,0.1,500)`

## Main outputs

For each active cell:

- average temperature
- maximum temperature
- average separator state `c_sep`

## Explicit placeholders and calibration handles

| Parameter | Current expression | Status |
|---|---|---|
| `gap_full` | `0.6[mm]` | measured module value required |
| `Rth_int` | `1e-3[m^2*K/W]` | calibration parameter |
| `rho_int` | `1000[kg/m^3]` | placeholder |
| `Cp_int` | `1000[J/(kg*K)]` | placeholder |
| `Hchem_base` | `12000[J]` | model-owner corrected energy; provenance should be retained |
| `m_soh1..3` | `1` | future coupling handle |
| `h_soh1..3` | `1` | future coupling handle |

## Known limitations

- The active-cell conductivity is currently isotropic despite being parameterized by direction.
- Interface behavior is represented as an effective solid layer, not a contact pair.
- Heater thermal mass is artificially minimized to approximate a heat-flux patch.
- Busbar geometry is approximate and disabled.
- No COMSOL compilation or solve evidence is stored in this initial repository.

## Baseline acceptance evidence still required

1. Compile with COMSOL 6.4 `comsolcompile`.
2. Generate/open the MPH without empty named selections.
3. Confirm all ODE fields initialize on three disconnected active domains.
4. Confirm probes populate the cell table.
5. Establish mesh/time-step sensitivity and experimental calibration separately.
