# Solver - SOC100 three-cell thermal-abuse v0.3

## Scope

`SOC100_3cell_offset_v03.java` adds an explicit transient solver sequence for the three-cell thermal-abuse model. The default remains `SOLVE_MODEL = false`, so running the Java main method creates an unsolved MPH template unless the source is deliberately edited.

## Study

| Item | Setting |
|---|---|
| Study tag | `std1` |
| Step tag | `time` |
| Type | Time dependent / transient |
| Output time list | `range(0,0.1,500)` |

## Solver sequence

| Solver node | Purpose |
|---|---|
| `sol1/st1` | Study step for `std1/time` |
| `sol1/v1` | Dependent variables for the time step |
| `sol1/t1` | Time-dependent solver with `tlist = range(0,0.1,500)` and maximum BDF order `2` |
| `sol1/t1/se1` | Segregated loop separating temperature and reaction-state variables |
| `sol1/t1/se1/ssT` | Temperature step, variable candidate `comp1_T` |
| `sol1/t1/se1/ssTR` | Reaction-state step, variables `comp1_c_sep`, `comp1_Qaccum`, `comp1_c_e`, `comp1_c_neg`, `comp1_c_sei`, `comp1_c_pos1`, `comp1_c_pos2` |
| `sol1/t1/dT` | PARDISO direct solver for the temperature step |
| `sol1/t1/dTR` | PARDISO direct solver for the reaction-state step |

## Lower-limit handling

The v03 source attempts to apply lower-limit candidate properties to the solver variable nodes for:

- `c_sep`
- `c_e`
- `c_neg`
- `c_sei`
- `c_pos1`
- `c_pos2`
- `Qaccum`

COMSOL exported Java trees vary in the exact property names and availability for lower-limit controls. The source catches unsupported lower-limit property attempts and prints a message rather than changing reaction equations or silently failing the model build. Local COMSOL 6.4 verification must confirm whether these bounds appear in the generated solver tree.

## Scientific invariants

The solver addition does not intentionally alter:

- thermal runaway kinetic parameters
- heat-release values
- threshold temperatures
- reaction equations
- `Hchem_base = 12000[J]`
- `m_soh1..3` and `h_soh1..3` semantics
- `SOLVE_MODEL = false` default

## Local verification commands

From the directory containing `SOC100_3cell_offset_v03.java`:

```powershell
& 'C:\Program Files\COMSOL\COMSOL64\Multiphysics\bin\win64\comsolcompile.exe' SOC100_3cell_offset_v03.java
& 'C:\Program Files\COMSOL\COMSOL64\Multiphysics\bin\win64\comsolbatch.exe' -inputfile SOC100_3cell_offset_v03.class -batchlog SOC100_3cell_v03_build.log
```

The generated `.class`, `.mph`, `.log`, and `.mph.lock` files must remain local artifacts and must not be committed.
