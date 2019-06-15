
package s12345678;

import skupno.Stroj;
import skupno.Karta;
import skupno.MnozicaKart;
import skupno.Konstante;

import java.util.Random;
import java.util.Arrays;

/**
 * Objekt tega razreda je stroj, ki poteze izbira povsem naklju"cno, vendar pa
 * nikoli ne izbere neveljavne poteze in se strogo dr"zi predpisanih "casovnih
 * omejitev.
 *
 * @author Naključko Randomè, Fakulteta za naklju"cne "studije
 */
public class Stroj_Nakljucko implements Stroj {

    /** generator naklju"cnih "stevil */
    private final Random nakljucniGenerator;

    /** moj polo"zaj v trenutni partiji
     * (0: spodaj levo, 1: zgoraj levo, 2: zgoraj desno, 3: spodaj desno */
    private int polozaj;

    /** mno"zica kart, ki jih imam trenutno v roki */
    private MnozicaKart roka;

    /** igralec, ki pri"cne trenutni "stih */
    private int pricneStih;

    /** karte v trenutnem "stihu;
     * stih[i]: karta, ki jo je odvrgel igralec i */
    private Karta[] stih;

    /**
     * V konstruktorju inicializiram generator naklju"cnih "stevil in tabelo, ki
     * bo hranila "ze odvr"zene karte v trenutnem "stihu.
     */
    public Stroj_Nakljucko() {
        this.nakljucniGenerator = new Random();
        this.stih = new Karta[4];
    }

    /**
     * Ob pri"cetku partije nastavim svoj polo"zaj in karte v roki.
     */
    @Override
    public void novaPartija(int polozaj, MnozicaKart karte) {
        this.polozaj = polozaj;
        this.roka = karte;
    }

    /**
     * Ko se pri"cne nov "stih, nastavim vse karte v trenutnem "stihu na null.
     */
    @Override
    public void pricetekStiha(int zacetnik) {
        this.pricneStih = zacetnik;
        this.stih[0] = null;
        this.stih[1] = null;
        this.stih[2] = null;
        this.stih[3] = null;
    }

    /**
     * Sem na potezi.  Izberem naklju"cno veljavno karto in jo vrnem kot
     * rezultat.
     */
    @Override
    public Karta izberiPotezo(long preostaliCas) {
        Karta karta = this.izberiPotezo();
        this.roka.odstrani(karta);  // karte, ki jo bom odvrgel, ne bom ve"c imel v roki
        return karta;
    }

    /**
     * Izberem naklju"cno veljavno karto in jo vrnem kot rezultat.
     */
    private Karta izberiPotezo() {
        if (this.pricneStih == this.polozaj) {
            // ko pri"cnem "stih, lahko odigram poljubno potezo
            return this.roka.izberiNakljucno(this.nakljucniGenerator);
        }

        // ugotovim, katera karta je bila v tem "stihu odvr"zena prva
        Karta prva = this.stih[this.pricneStih];
        int barvaPrve = prva.vrniBarvo();

        // izberem eno od kart ujemajo"ce barve, "ce jo imam
        MnozicaKart ujemajoce = this.roka.karteVBarvi(barvaPrve);
        if (!ujemajoce.jePrazna()) {
            return ujemajoce.izberiNakljucno(this.nakljucniGenerator);
        }

        // izberem enega od adutov, "ce ga imam
        MnozicaKart aduti = this.roka.karteVBarvi(Konstante.BARVA_ADUTA);
        if (!aduti.jePrazna()) {
            return aduti.izberiNakljucno(this.nakljucniGenerator);
        }

        // nimam niti adutov, zato izberem poljubno karto
        return this.roka.izberiNakljucno(this.nakljucniGenerator);
    }

    /**
     * Igralec z indeksom (polo"zajem) `akter' je odvrgel karto `karta'.
     * Odvr"zeno karto postavim na ustrezno mesto v tabeli this.stih.
     */
    @Override
    public void sprejmiPotezo(int akter, Karta karta) {
        this.stih[akter] = karta;
    }

    /**
     * Ko se partija zaklju"ci, mi ni treba storiti ni"cesar, saj mi to"cke niso
     * pomembne ...
     */
    @Override
    public void rezultat(int[] tocke) {
    }
}
