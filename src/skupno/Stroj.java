
package skupno;

/**
 * Objekt tega vmesnika predstavlja stroj za igranje igre.
 */
public interface Stroj {

    /** 
     * Ta metoda se pokliče ob pričetku vsake partije.
     *
     * @param polozaj  polo"zaj (indeks) igralca this
     *                 (0: spodaj levo; 1: zgoraj levo; 2: zgoraj desno; 3:
     *                 spodaj desno)
     * @param karte  mno"zica kart, ki jih je delilec razdelil stroju this.
     */
    public void novaPartija(int polozaj, MnozicaKart karte);

    /**
     * Ta metoda se pokliče ob pri"cetku vsakega "stiha.
     *
     * @param zacetnik  polo"zaj (indeks) igralca, ki pri"cne "stih
     */
    public void pricetekStiha(int zacetnik);

    /**
     * Ta metoda se pokliče, ko je stroj this na vrsti za izbiro poteze.
     * Metoda mora v `preostaliCas' milisekundah vrniti karto, ki jo stroj
     * "zeli odvre"ci.
     *
     * @param preostaliCas  čas v milisekundah, ki ga ima stroj this na voljo do konca partije
     * 
     * @return izbrana karta
     */
    public Karta izberiPotezo(long preostaliCas);

    /**
     * Ta metoda se pokliče, ko nek drug igralec odvr"ze karto.
     *
     * @param akter  polo"zaj igralca, ki je karto odvrgel
     *                 (0: jug, 1: zahod; 2: sever; 3: vzhod)
     * @param karta  karta, ki jo je igralec odvrgel
     */
    public void sprejmiPotezo(int akter, Karta karta);

    /**
     * Ta metoda se pokliče ob koncu vsake partije.
     *
     * @param tocke tocke[i]: to"cke, ki jih prejme igralec i
     */
    public void rezultat(int[] tocke);
}
