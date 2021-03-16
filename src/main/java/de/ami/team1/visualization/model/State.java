package de.ami.team1.visualization.model;

public enum State {

    // Nordrhein-Westfalen, Niedersachen, Bayern, Rheinland-Pfalz, Hessen, Saarland, Berlin, Brandenburg, Schleswig-Holstein, Mecklenburg-Vorpommern, Thüringen, Sachen, Sachsen-Anhalt, Bremen, Baden-Württemberg, Hamburg

    NordrheinWestfalen("Nordrhein-Westfalen", "nw"),
    Niedersachsen("Niedersachsen", "ni"),
    Bayern("Bayern", "by"),
    RheinlandPfalz("Rheinland-Pfalz", "rp"),
    Hessen("Hessen", "he"),
    Saarland("Saarland", "sl"),
    Berlin("Berlin", "be"),
    Brandenburg("Brandenburg", "bb"),
    SchleswigHolstein("Schleswig-Holstein", "sh"),
    MecklenburgVorpommern("Mecklenburg-Vorpommern", "mv"),
    Thueringen("Thüringen", "th"),
    Sachsen("Sachsen", "sn"),
    SachsenAnhalt("Sachsen-Anhalt", "st"),
    Bremen("Bremen", "hb"),
    BadenWuerttemberg("Baden-Württemberg", "bw"),
    Hamburg("Hamburg", "hh");

    private final String stateName;
    private final String stateValue;

    State(String stateName, String stateValue) {
        this.stateName = stateName;
        this.stateValue = stateValue;
    }

    public String getStateName() {
        return stateName;
    }

    public String getStateValue() {
        return stateValue;
    }
}
