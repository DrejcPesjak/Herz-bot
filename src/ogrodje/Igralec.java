
package ogrodje;

import java.util.function.Consumer;

import skupno.Karta;
import skupno.MnozicaKart;

/**
 * Objekt tega razreda predstavlja posameznega igralca.
 * <p>
 * Podrazreda:
 * <ul>
 * <li> Clovek ("clove"ski igralec) </li>
 * <li> Racunalnik (ra"cunalni"ski igralec) </li>
 * </ul>
 */
public abstract class Igralec {

    /** true natanko v primeru, "ce velja "casovna omejitev */
    private boolean casOmejen;

    /** razpolo"zljivi "cas ob pri"cetku partije (v milisekundah) */
    private long zacetniCas;

    /** polo"zaj igralca (0: jug; 1: zahod; 2: sever; 3: vzhod) */
    private int polozaj;

    /** razpolo"zljivi "cas (v milisekundah) do konca partije; ta atribut se
     * posodablja po vsaki potezi, med `razmi"sljanjem' pred izbiro poteze pa
     * se ne */
    private long casDoKonca;

    /** trenutek, ko smo nazadnje shranili "cas na ra"cunalnikovi uri; na
     * podlagi tega trenutka, vrednosti atributa casDoKonca in trenutne
     * ra"cunalnikove ure dolo"cimo trenutni razpolo"zljivi "cas do konca
     * partije */
    private long zadnjiCasovniZaznamek;

    /** mno"zica trenutnih kart igralca */
    private MnozicaKart karte;

    /** dobitki v trenutni partiji (posodobi se po vsakem "stihu) */
    private int dobitki;

    /** trenutno "stevilo to"ck (posodobi se po vsaki partiji) */
    private int tocke;

    /**
     * Izdela igralca na podanem polo"zaju in s podanim razpolo"zljivim "casom
     * ob za"cetku partije.
     * @param polozaj polo"zaj igralca (0: spodaj levo, 1: zgoraj levo, 
     *    2: zgoraj desno, 3: spodaj desno)
     * @param zacetniCasSek razpolo"zljivi "cas ob za"cetku partije (v sekundah)
     */
    public Igralec(int polozaj, int zacetniCasSek) {
        this.polozaj = polozaj;
        this.casOmejen = (zacetniCasSek > 0);
        this.zacetniCas = zacetniCasSek * 1000;
        this.dobitki = 0;
        this.tocke = 0;
        this.casDoKonca = Razno.NEVELJAVEN_CAS;
    }

    /**
     * Vrne true natanko v primeru, "ce ima igralec this omejen "cas.
     */
    public boolean jeCasOmejen() {
        return this.casOmejen;
    }

    /**
     * Vrne polno ime igralca this.  Polno in kratko ime se razlikujeta samo
     * pri ra"cunalni"skem igralcu.
     */
    public abstract String vrniIme();

    /**
     * Vrne kratko ime igralca this.  Polno in kratko ime se razlikujeta samo
     * pri ra"cunalni"skem igralcu.
     */
    public abstract String vrniKratkoIme();

    /**
     * Za ra"cunalni"skega igralca ne naredi ni"cesar, za "clove"skega pa glej
     * redefinicijo v razredu Clovek.
     */
    public void inicializiraj() {
    }

    /**
     * Vrne true natanko v primeru, "ce je igralec this ra"cunalnik.
     */
    public abstract boolean jeRacunalnik();

    /**
     * Vrne polo"zaj igralca this.
     */
    public int vrniPolozaj() {
        return this.polozaj;
    }

    /**
     * Vrne preostali "cas (v milisekundah) do konca partije, ki ga ima v
     * danem trenutku na voljo igralec this.
     */
    public long vrniCasDoKonca() {
        return (this.zadnjiCasovniZaznamek < 0) ? 
                (this.casDoKonca) :
                (this.casDoKonca - (System.currentTimeMillis() - this.zadnjiCasovniZaznamek));
    }

    /**
     * Posodobi atribut casDoKonca na vrednost, ki jo vrne metoda
     * vrniCasDoKonca.  Metoda posodobiCasDoKonca se pokli"ce po vsaki potezi.
     */
    public void posodobiCasDoKonca() {
        if (this.jeCasOmejen() && this.casDoKonca != Razno.NEVELJAVEN_CAS) {
            this.casDoKonca = this.vrniCasDoKonca();
            this.zadnjiCasovniZaznamek = -1;
        }
    }
 
    /**
     * Vrne mno"zico kart, ki jih ima igralec trenutno v rokah
     */
    public MnozicaKart vrniKarte() {
        return this.karte;
    }

    /**
     * Vrne trenutno vsoto dobitkov igralca this v trenutni partiji.
     */
    public int vrniDobitke() {
        return this.dobitki;
    }

    /**
     * Vrne trenutno "stevilo to"ck igralca this (posodobi se po vsaki
     * partiji).
     */
    public int vrniTocke() {
        return this.tocke;
    }

    /**
     * Za "clove"skega igralca vrne zgolj true, za ra"cunalni"skega pa glej
     * redefinicijo metode v razredu Racunalnik.
     */
    public boolean ustvariStroj() {
        return true;
    }

    /**
     * Stori vse potrebno ob pri"cetku partije.
     * @param karte karte, ki jih ima igralec na za"cetku v roki
     */
    public void pricetekPartije(MnozicaKart karte) {
        this.casDoKonca = this.zacetniCas;
        this.karte = new MnozicaKart(karte);
        this.dobitki = 0;
        this.zadnjiCasovniZaznamek = -1;
    }

    /**
     * Shrani trenutno uro v atribut zadnjiCasovniZaznamek.  Preostali
     * razpolo"zljivi "cas nato ra"cuna tako, da od atributa casDoKonca
     * od"steje razliko med trenutno uro in atributom zadnjiCasovniZaznamek.
     */
    public void shraniCasovniZaznamek() {
        this.zadnjiCasovniZaznamek = System.currentTimeMillis();
    }

    /**
     * Za "clove"skega igralca ne stori ni"cesar, za ra"cunalni"skega pa glej
     * redefinicijo metode v razredu Racunalnik.
     */
    public void pricetekStiha(int polozaj) {
    }

    /**
     * Za "clove"skega igralca ne stori ni"cesar, za ra"cunalni"skega pa glej
     * redefinicijo metode v razredu Racunalnik.
     */
    public void sprejmiPotezo(int polozaj, Karta karta) {
    }

    /**
     * Omogo"ci izbiro poteze.
     * @param moznosti mno"zica kart, ki jih igralec lahko odvr"ze v trenutni
     *    situaciji
     * @param pokliciPoIzbiri funkcija, ki se pokli"ce po izbiri poteze
     */
    public abstract void izberiPotezo(MnozicaKart moznosti, Consumer<Karta> pokliciPoIzbiri);

    /**
     * Prekine nit, v kateri stroj izbia potezo.  Pri "clove"skem igralcu ta
     * metoda nima u"cinka.
     */
    public void prekiniDelovnoNit() {
    }

    /**
     * Za "clove"skega igralca ne stori ni"cesar, za ra"cunalni"skega pa glej
     * redefinicijo metode v razredu Racunalnik.
     */
    public void rezultat(int[] tockeObKoncuPartije) {
    }

    /**
     * Odstrani podano karto iz mno"zice kart igralca this.
     */
    public void uveljaviPotezo(Karta karta) {
        this.karte.odstrani(karta);
    }

    /**
     * Skupno vsoto v dobitkih za igralca this pove"ca za vrednost `dodatek'.
     */
    public void dodajDobitke(int dodatek) {
        this.dobitki += dodatek;
    }

    /**
     * Skupno "stevilo to"ck igralca this pove"ca za vrednost `dodatek'.
     */
    public void dodajTocke(int dodatek) {
        this.tocke += dodatek;
    }

    /**
     * Vrne polno ime igralca this.
     */
    @Override
    public String toString() {
        return this.vrniIme();
    }
}
