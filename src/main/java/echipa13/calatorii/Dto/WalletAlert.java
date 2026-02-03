package echipa13.calatorii.Dto;

public class WalletAlert {

    public enum Level {
        INFO, SUCCESS, WARNING, DANGER
    }

    private final Level level;
    private final String title;
    private final String message;

    public WalletAlert(Level level, String title, String message) {
        this.level = level;
        this.title = title;
        this.message = message;
    }

    public Level getLevel() { return level; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }

    public String getBootstrapClass() {
        return switch (level) {
            case INFO -> "info";
            case SUCCESS -> "success";
            case WARNING -> "warning";
            case DANGER -> "danger";
        };
    }

    public String getIconClass() {
        return switch (level) {
            case INFO -> "bi bi-info-circle";
            case SUCCESS -> "bi bi-check-circle";
            case WARNING -> "bi bi-exclamation-triangle";
            case DANGER -> "bi bi-x-octagon";
        };
    }

    public static WalletAlert fromSummary(WalletSummary summary) {
        if (summary == null || summary.getStatus() == null) {
            return new WalletAlert(Level.INFO, "Portofel", "Setează bugetul ca să primești avertizări și progres.");
        }

        return switch (summary.getStatus()) {
            case NESETAT -> new WalletAlert(
                    Level.INFO,
                    "Buget nesetat",
                    "Setează bugetul itinerariului pentru a vedea progresul și avertizările automat."
            );
            case OK -> new WalletAlert(
                    Level.SUCCESS,
                    "Ești în grafic",
                    summary.getPercent() != null
                            ? ("Ai cheltuit " + summary.getPercent() + "% din buget. Continuă așa!")
                            : "Ești în grafic. Continuă să înregistrezi cheltuielile."
            );
            case ATENTIE -> new WalletAlert(
                    Level.WARNING,
                    "Atenție",
                    summary.getPercent() != null
                            ? ("Ești la " + summary.getPercent() + "% din buget. Verifică următoarele cheltuieli.")
                            : "Ești aproape de limita bugetului. Verifică următoarele cheltuieli."
            );
            case DEPASIT -> new WalletAlert(
                    Level.DANGER,
                    "Buget depășit",
                    summary.getRemaining() != null
                            ? ("Ai depășit bugetul cu " + summary.getRemaining().abs() + ". Ajustează bugetul sau reduce cheltuielile.")
                            : "Ai depășit bugetul. Ajustează bugetul sau reduce cheltuielile."
            );
        };
    }
}
