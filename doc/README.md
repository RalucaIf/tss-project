# Documentație LaTeX - Proiect TSS T3 (Echipa 13)

Aceasta e documentația completă pentru proiectul TSS T3 - testarea unitară a clasei `WalletServiceImpl` din aplicația *Călătorii*.

## Structura

```
doc/
├── main.tex              # Fișierul principal LaTeX
├── bibliography.bib      # Referințe bibliografice (18 surse)
├── main.pdf              # PDF-ul gata compilat (79 pagini)
├── README.md             # Acest fișier
│
├── sections/             # Capitolele documentației
│   ├── 0-title.tex       # Pagina de titlu
│   ├── 0-abstract.tex    # Rezumat bilingv RO/EN
│   ├── 1-introducere.tex # Cap. 1 - Introducere
│   ├── 2-preliminarii.tex# Cap. 2 - Preliminarii teoretice
│   ├── 3-configurare.tex # Cap. 3 - Configurația de lucru
│   ├── 4-aplicatia.tex   # Cap. 4 - Aplicația și clasa testată
│   ├── 5-strategii.tex   # Cap. 5 - Strategiile de testare aplicate
│   ├── 6-jacoco.tex      # Cap. 6 - Rezultate JaCoCo
│   ├── 7-pitest.tex      # Cap. 7 - Mutation Testing PITest
│   ├── 8-comparatie-ai.tex # Cap. 8 - Suita AI - comparație
│   ├── 9-concluzii.tex   # Cap. 9 - Concluzii
│   └── A-anexe.tex       # Anexa A - Cod sursă relevant
│
├── images/               # Toate imaginile folosite (12 generate + 7 placeholder)
│   ├── 01-architecture.png        # Arhitectura aplicației (Graphviz)
│   ├── 02-classes.png             # Diagrama claselor (Graphviz)
│   ├── 03-cfg-addExpense.png      # CFG addExpenseOwnedByUser (Graphviz)
│   ├── 04-cfg-daysRemaining.png   # CFG computeDaysRemainingSafe (Graphviz)
│   ├── 05-cfg-buildRiskUi.png     # CFG buildRiskUi (Graphviz)
│   ├── 06-pipeline.png            # Pipeline-ul de testare (Graphviz)
│   ├── 07-strategies.png          # Ierarhia strategiilor (Graphviz)
│   ├── 08-mutation-per-mutator.png# Bar chart mutanți per mutator (Python)
│   ├── 09-coverage-per-method.png # Coverage JaCoCo per metodă (Python)
│   ├── 10-ai-vs-ours.png          # Comparație AI vs propriu (Python)
│   ├── 11-thresholds-cascade.png  # Cascada de praguri BVA (Python)
│   ├── 12-test-distribution.png   # Doughnut distribuție teste (Python)
│   │
│   ├── Coverage.png               # PLACEHOLDER - de înlocuit cu screenshot real JaCoCo
│   ├── Pit_Test.png               # PLACEHOLDER - de înlocuit cu screenshot real PITest
│   ├── ss1.png ... ss4.png        # PLACEHOLDER - de înlocuit cu capturi ChatGPT
│   └── erori1.png                 # PLACEHOLDER - de înlocuit cu captură erori IDE
│
├── diagrams/             # Sursele Graphviz (.dot) pentru diagrame
└── scripts/              # Scripturi Python pentru chart-uri
```

## ⚠️ Înainte de compilare

**Înlocuiește placeholder-urile cu screenshot-urile reale** din folderul `Screenshots/` al proiectului tău:

| Placeholder existent în `images/` | Înlocuiește cu fișierul din `Screenshots/` |
|----------------------------------|---------------------------------------------|
| `Coverage.png` | `Coverage.png` (raportul JaCoCo) |
| `Pit_Test.png` | `Pit_Test.png` (raportul PITest) |
| `ss1.png` | `ss1.png` (promptul ChatGPT) |
| `ss2.png` | `ss2.png` (răspunsul AI - partea 1) |
| `ss3.png` | `ss3.png` (răspunsul AI - partea 2) |
| `ss4.png` | `ss4.png` (răspunsul AI - partea 3) |
| `erori1.png` | `erori1.png` (erorile compilare AI) |

Pur și simplu copiază peste fișierele din `images/` cu cele reale.

## Cum compilezi

Documentația folosește **xelatex** (pentru suport diacritice românești) și **bibtex** clasic.

### Comenzile (în ordine, în terminal/PowerShell):

```bash
xelatex main.tex
bibtex main
xelatex main.tex
xelatex main.tex
```

Sau, dacă ai `latexmk` instalat (recomandat):

```bash
latexmk -xelatex main.tex
```

## De unde a venit fiecare diagramă

- **Diagrame structurale** (arhitectură, clase, CFG-uri, pipeline, ierarhia strategiilor) - generate cu **Graphviz** din fișierele `.dot` din folderul `diagrams/`. Pentru a le regenera:
  ```bash
  cd diagrams
  for f in *.dot; do dot -Tpng -Gdpi=150 "$f" -o "../images/${f%.dot}.png"; done
  ```

- **Chart-uri cu rezultate** (bar charts, doughnut, threshold cascade) - generate cu **Python + matplotlib** din scriptul `scripts/generate_charts.py`. Pentru a le regenera:
  ```bash
  cd scripts
  python3 generate_charts.py
  ```

## Statistici

- **79 de pagini** finale (după compilare)
- **9 capitole** + 1 anexă + 1 bibliografie
- **12 diagrame** generate cu tooluri profesionale (Graphviz + matplotlib)
- **18 surse** bibliografice citate
- **20+ tabele** profesionale (booktabs)
- **Code listings** Java cu syntax highlighting
- **Conținut bilingv** (rezumat RO + EN)

## Cerințe TSS T3 acoperite

- Strategii de testare (toate cele 6: EP, BVA, Statement, Decision, Condition, Circuits)
- Mutation Testing cu 2 mutanți neechivalenți omorâți explicit
- Diagrame realizate cu tooluri dedicate (Graphviz, matplotlib - nu fotografiate/scanate)
- Configurația hardware
- Configurația software cu versiuni explicite ale toate tool-urilor
- Mențiunea că nu s-a folosit mașină virtuală (rulare nativă)
- Bucăți de cod cu syntax highlighting
- Capturi de ecran cu rularea testelor (locuri pentru ele - placeholder-e)
- Comparații rezultate / tooluri în formă tabelară (multe tabele)
- Interpretări detaliate ale rezultatelor
- Referințe bibliografice citate în text (cu `\cite{}`)
- Raport AI complet (prompt, răspuns, capturi, comparație, concluzie)
