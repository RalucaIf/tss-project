package echipa13.calatorii.models;

import lombok.Getter;

@Getter
public enum Continent {
    ASIA("asia"),
    EUROPE("europe"),
    AFRICA("africa"),
    AMERICAS("america");


    private final String cssClass;

    Continent(String cssClass) {
        this.cssClass = cssClass;
    }

}
