
package skupno;

/**
 * Ta razred vsebuje globalne konstante, ki jih lahko uporabljajo tudi stroji.
 */
public class Konstante {

    /** skupno "stevilo kart */
    public static final int ST_KART = 52;

    /** "stevilo barv */
    public static final int ST_BARV = 4;

    /** "stevilo igralcev */
    public static final int ST_IGRALCEV = 4;

    /** "stevilo kart v vsaki barvi */
    public static final int ST_KART_NA_BARVO = ST_KART / ST_BARV;

    /** "stevilo kart, ki jih na za"cetku partije prejme vsak igralec  */
    public static final int ST_KART_NA_IGRALCA = ST_KART / ST_IGRALCEV;

    /** vrednost najmanj"se karte v vsaki barvi (tj. dvojke)  */
    public static final int ZACETNA_VREDNOST = 2;

    /** indeks barve aduta (srce)  */
    public static final int BARVA_ADUTA = 0;

    /** "stevilo "stihov v partiji */
    public static final int ST_STIHOV = ST_KART_NA_IGRALCA;

    /** polovica vsote vrednosti kart */
    public static final int POLOVICA_VSOTE = 
        2 * vsota(ZACETNA_VREDNOST, ST_KART_NA_BARVO + ZACETNA_VREDNOST);

    /** 
     * Vrne vsoto "stevil v celo"stevilskem intervalu [zacetek, konec - 1].
     */
    private static int vsota(int zacetek, int konec) {
        int rezultat = 0;
        for (int i = zacetek;  i < konec;  i++) {
            rezultat += i;
        }
        return rezultat;
    }
}
