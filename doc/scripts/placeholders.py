import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

OUT = "/home/claude/doc/images/"

def placeholder(name, text, w=10, h=6):
    fig, ax = plt.subplots(figsize=(w, h))
    ax.text(0.5, 0.5, text, ha="center", va="center",
            fontsize=14, fontweight="bold", color="#444")
    ax.text(0.5, 0.3, "[Placeholder pentru documentatie - de inlocuit\nin proiectul Andreei cu captura reala]",
            ha="center", va="center", fontsize=10, color="#888", style="italic")
    ax.set_xlim(0, 1)
    ax.set_ylim(0, 1)
    ax.set_xticks([])
    ax.set_yticks([])
    for s in ax.spines.values():
        s.set_color("#bbbbbb")
    plt.tight_layout()
    plt.savefig(OUT + name, dpi=120, bbox_inches="tight")
    plt.close()
    print("OK:", name)

placeholder("Coverage.png", "Captura raport JaCoCo HTML\n(target/site/jacoco/index.html)")
placeholder("Pit_Test.png", "Captura raport PITest HTML\n(target/pit-reports/index.html)")
placeholder("ss1.png", "Promptul ChatGPT")
placeholder("ss2.png", "Raspunsul ChatGPT - partea 1")
placeholder("ss3.png", "Raspunsul ChatGPT - partea 2")
placeholder("ss4.png", "Raspunsul ChatGPT - partea 3")
placeholder("erori1.png", "Erori compilare - suita AI in IntelliJ")
