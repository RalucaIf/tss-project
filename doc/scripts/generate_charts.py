#!/usr/bin/env python3
"""
Generează grafice pentru documentația proiectului TSS.
"""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np

OUT = "/home/claude/doc/images/"

plt.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 11,
    "axes.spines.top": False,
    "axes.spines.right": False,
})

# 1) Mutation coverage per mutator
def chart_mutation_per_mutator():
    mutators = [
        "VoidMethodCall",
        "NullReturnVals",
        "RemoveCondEqual",
        "RemoveCondOrder",
        "ConditionalsBoundary",
        "EmptyObjectReturn",
        "MathMutator",
    ]
    generated = [11, 18, 53, 21, 21, 9, 4]
    killed    = [11, 17, 30, 12, 7, 4, 0]
    survived  = [g - k for g, k in zip(generated, killed)]

    x = np.arange(len(mutators))
    w = 0.35

    fig, ax = plt.subplots(figsize=(10, 5))
    b1 = ax.bar(x - w/2, killed, w, label="Omorâți", color="#43A047")
    b2 = ax.bar(x + w/2, survived, w, label="Supraviețuiți", color="#E53935")

    ax.set_ylabel("Număr mutanți")
    ax.set_title("Distribuția mutanților pe tip de mutator (PITest)")
    ax.set_xticks(x)
    ax.set_xticklabels(mutators, rotation=20, ha="right")
    ax.legend()

    for bars in [b1, b2]:
        for bar in bars:
            h = bar.get_height()
            if h > 0:
                ax.text(bar.get_x() + bar.get_width()/2., h + 0.5,
                        f"{int(h)}", ha="center", va="bottom", fontsize=9)

    plt.tight_layout()
    plt.savefig(OUT + "08-mutation-per-mutator.png", dpi=150, bbox_inches="tight")
    plt.close()
    print("OK: 08-mutation-per-mutator.png")


# 2) Coverage per metoda
def chart_coverage_per_method():
    methods = [
        "addExpense",
        "computeSummary",
        "updateBudget",
        "deleteTransaction",
        "buildRiskUi",
        "buildRecommendation",
        "computeSmartAdvice",
        "findUserBy...",
        "computeInsights",
    ]
    statement = [100, 100, 100, 100, 100, 100, 96, 82, 55]
    branch    = [100, 100, 100, 100, 91, 76, 75, 62, 50]

    x = np.arange(len(methods))
    w = 0.35

    fig, ax = plt.subplots(figsize=(11, 5.5))
    b1 = ax.bar(x - w/2, statement, w, label="Statement coverage", color="#1976D2")
    b2 = ax.bar(x + w/2, branch, w, label="Branch coverage", color="#FB8C00")

    ax.axhline(80, color="#E53935", linestyle="--", linewidth=1, alpha=0.7,
               label="Țintă min. (80%)")

    ax.set_ylabel("Acoperire (%)")
    ax.set_ylim(0, 110)
    ax.set_title("Acoperire JaCoCo pe metode în WalletServiceImpl")
    ax.set_xticks(x)
    ax.set_xticklabels(methods, rotation=25, ha="right")
    ax.legend(loc="lower left")

    for bars in [b1, b2]:
        for bar in bars:
            h = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2., h + 1,
                    f"{int(h)}", ha="center", va="bottom", fontsize=9)

    plt.tight_layout()
    plt.savefig(OUT + "09-coverage-per-method.png", dpi=150, bbox_inches="tight")
    plt.close()
    print("OK: 09-coverage-per-method.png")


# 3) Comparatie suita propriu vs AI
def chart_ai_vs_ours():
    metrics = ["Statement\ncoverage", "Branch\ncoverage", "Mutation\nscore", "Tests care\ncompilează"]
    ours = [91, 80, 60, 100]
    ai   = [0, 0, 0, 0]

    x = np.arange(len(metrics))
    w = 0.35

    fig, ax = plt.subplots(figsize=(8.5, 4.8))
    b1 = ax.bar(x - w/2, ours, w, label="Suita proprie", color="#1976D2")
    b2 = ax.bar(x + w/2, ai, w, label="Suita generată AI", color="#E53935")

    ax.set_ylabel("Procent (%)")
    ax.set_ylim(0, 110)
    ax.set_title("Comparație suita proprie vs. suita generată automat de ChatGPT")
    ax.set_xticks(x)
    ax.set_xticklabels(metrics)
    ax.legend()

    for bars in [b1, b2]:
        for bar in bars:
            h = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2., h + 1.5,
                    f"{int(h)}%", ha="center", va="bottom", fontsize=10, fontweight="bold")

    plt.tight_layout()
    plt.savefig(OUT + "10-ai-vs-ours.png", dpi=150, bbox_inches="tight")
    plt.close()
    print("OK: 10-ai-vs-ours.png")


# 4) Threshold cascade for BVA - pragurile 75/90/100
def chart_thresholds():
    fig, ax = plt.subplots(figsize=(11, 3.5))

    # background bands
    ax.axvspan(0, 75, color="#A5D6A7", alpha=0.5)
    ax.axvspan(75, 90, color="#FFE082", alpha=0.6)
    ax.axvspan(90, 100, color="#FFAB91", alpha=0.6)
    ax.axvspan(100, 130, color="#EF9A9A", alpha=0.6)

    # vertical lines on thresholds
    for threshold, color in [(75, "#F57F17"), (90, "#D84315"), (100, "#B71C1C")]:
        ax.axvline(threshold, color=color, linestyle="--", linewidth=2)
        ax.text(threshold, 0.85, f"  {threshold}%", color=color, fontsize=11,
                fontweight="bold")

    # labels for states
    ax.text(37.5, 0.5, "OK", ha="center", va="center", fontsize=14, fontweight="bold",
            color="#1B5E20")
    ax.text(82.5, 0.5, "ATENȚIE", ha="center", va="center", fontsize=12, fontweight="bold",
            color="#E65100")
    ax.text(95, 0.5, "CRITIC", ha="center", va="center", fontsize=10, fontweight="bold",
            color="#BF360C")
    ax.text(115, 0.5, "DEPĂȘIT", ha="center", va="center", fontsize=12, fontweight="bold",
            color="#7F0000")

    # test markers — offsets ca să nu se suprapună
    test_specs = [
        (74, -1.5),
        (75, +1.5),
        (99, -1.5),
        (100, +1.5),
    ]
    for v, dx in test_specs:
        ax.scatter([v], [0.18], color="#0D47A1", s=80, zorder=5, marker="v")
        ax.text(v + dx, 0.05, str(v), ha="center", fontsize=9, color="#0D47A1", fontweight="bold")

    ax.set_xlim(0, 130)
    ax.set_ylim(0, 1)
    ax.set_yticks([])
    ax.set_xlabel("Procent buget consumat (%)")
    ax.set_title("Cascada de praguri în computeSummaryOwnedByUser și valorile testate prin BVA")

    plt.tight_layout()
    plt.savefig(OUT + "11-thresholds-cascade.png", dpi=150, bbox_inches="tight")
    plt.close()
    print("OK: 11-thresholds-cascade.png")


# 5) Doughnut: distribuția testelor pe strategii
def chart_test_distribution():
    labels = ["Equivalence (9)", "Boundary (10)", "Statement (3)", "Decision (2)",
              "Condition (7)", "Circuits (3)", "Mutation (5+)"]
    sizes  = [9, 10, 3, 2, 7, 3, 5]
    colors = ["#80CBC4", "#80CBC4", "#B39DDB", "#B39DDB", "#B39DDB", "#B39DDB", "#FF7043"]

    fig, ax = plt.subplots(figsize=(7.5, 6))
    wedges, texts, autotexts = ax.pie(
        sizes, labels=labels, autopct="%1.0f%%",
        startangle=90, colors=colors, pctdistance=0.78,
        wedgeprops=dict(width=0.45, edgecolor="white", linewidth=2),
    )
    for t in autotexts:
        t.set_color("white")
        t.set_fontweight("bold")
        t.set_fontsize(10)
    for t in texts:
        t.set_fontsize(10)

    ax.set_title("Distribuția celor ~40 de teste pe strategii", fontsize=12, pad=10)

    # legend - axe color = strategy class
    from matplotlib.patches import Patch
    legend_elems = [
        Patch(facecolor="#80CBC4", label="Black Box"),
        Patch(facecolor="#B39DDB", label="White Box (structural)"),
        Patch(facecolor="#FF7043", label="Mutation Testing"),
    ]
    ax.legend(handles=legend_elems, loc="lower right", framealpha=0.95)

    plt.tight_layout()
    plt.savefig(OUT + "12-test-distribution.png", dpi=150, bbox_inches="tight")
    plt.close()
    print("OK: 12-test-distribution.png")


if __name__ == "__main__":
    chart_mutation_per_mutator()
    chart_coverage_per_method()
    chart_ai_vs_ours()
    chart_thresholds()
    chart_test_distribution()
    print("\nDONE: 5 grafice generate in", OUT)
