package echipa13.calatorii.models;

public enum Highlight {

    TURURI_ARTE_RENASCENTISTA("Tururi de artă renascentistă", "fa-palette"),
    EXCURSII_SAFARI("Excursii safari", "fa-binoculars"),
    DEGUSTARI_VIN("Degustări de vin", "fa-wine-glass"),
    PLAJE_AVENTURA("Plaje și aventuri", "fa-umbrella-beach"),
    CAZARE_BOUTIQUE("Cazare boutique", "fa-hotel"),
    TRANSPORT_PRIVAT("Transport privat", "fa-car"),
    GHID_ROMAN("Ghid român", "fa-user"),
    GHID_STRAIN("Ghid străin", "fa-user-check"),
    CROAZIERA("Croazieră", "fa-ship"),
    AVION_INCLUS("Zbor inclus", "fa-plane"),
    TURURI_MUZEE("Tururi muzeu", "fa-landmark"),
    TURURI_ACVARII("Tururi acvarii", "fa-water");

    private final String label;
    private final String icon;

    Highlight(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }
}
