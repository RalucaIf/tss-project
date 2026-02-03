package echipa13.calatorii.models;

public enum WalletCategory {
    FOOD("Mâncare"),
    TRANSPORT("Transport"),
    LODGING("Cazare"),
    ACTIVITIES("Activități"),
    SOUVENIRS("Suveniruri"),
    OTHER("Altele");

    private final String labelRo;

    WalletCategory(String labelRo) {
        this.labelRo = labelRo;
    }

    public String getLabelRo() {
        return labelRo;
    }
}
