package at.fhv.jn.googleMaps;

/**
 * Created by Johannes on 01.02.2017.
 */
public enum Thickness {
    extraFine(1),
    fine(2),
    Normal(3),
    Bold(4),
    ExtraBold(5);

    private int value;
    Thickness(int i) {
        value = i;
    }
    public int  getValue(){
        return value;
    }
}
