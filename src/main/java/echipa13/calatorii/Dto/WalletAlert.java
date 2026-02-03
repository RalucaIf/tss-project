package echipa13.calatorii.Dto;

public class WalletAlert {

    private final String cssClass; // ex: "alert-success", "alert-warning"
    private final String title;
    private final String message;

    public WalletAlert(String cssClass, String title, String message) {
        this.cssClass = cssClass;
        this.title = title;
        this.message = message;
    }

    public String getCssClass() { return cssClass; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }

    public static WalletAlert fromSummary(WalletSummary summary) {
        if (summary == null || summary.getStatus() == null) return null;

        switch (summary.getStatus()) {
            case NESETAT:
                return new WalletAlert(
                        "alert-secondary",
                        "Buget nesetat",
                        "Setează un buget pentru a vedea progresul și recomandările."
                );
            case OK:
                return new WalletAlert(
                        "alert-success",
                        "În grafic",
                        "Cheltuielile sunt în limite normale."
                );
            case ATENTIE:
                return new WalletAlert(
                        "alert-warning",
                        "Atenție",
                        "Ești aproape de buget. Verifică burn-rate și recomandarea."
                );
            case DEPASIT:
                return new WalletAlert(
                        "alert-danger",
                        "Depășit",
                        "Ai depășit bugetul. Poți ajusta bugetul sau reduce cheltuielile."
                );
            default:
                return null;
        }
    }
}
