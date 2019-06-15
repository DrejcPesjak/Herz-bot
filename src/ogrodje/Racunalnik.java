
package ogrodje;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import skupno.Karta;
import skupno.MnozicaKart;
import skupno.Stroj;

/**
 * Objekt tega razreda predstavlja ra"cunalni"skega igralca.
 */
public class Racunalnik extends Igralec {

    /** dol"zina predpone sXXXXXXXX.Stroj_, ki predstavlja razliko med dolgim
     * in kratkim imenom igralca. */
    private static final int DOLZINA_PREDPONE = "sXXXXXXXX.Stroj_".length();

    /** razred, ki mu pripada stroj za igralca this. */
    private Class<?> razred;

    /** stroj, ki implementira logiko za izbiro potez za igralca this. */
    private Stroj stroj;

    /** nit, v kateri stroj izbira potezo */
    private Thread delovnaNit;

    /**
     * Izdela ra"cunalni"skega igralca na podanem polo"zaju.  Stroj za igralca
     * bomo izdelali kot objekt razreda `razred', na za"cetku partije pa bo stroj
     * za vsa svoja dejanja imel na voljo `zacetniCasSek' sekund "casa.
     * @param polozaj 0 (spodaj levo), 1 (zgoraj levo), 2 (zgoraj desno) ali 
     *                3 (spodaj desno)
     */
    public Racunalnik(int polozaj, Class razred, int zacetniCasSek) {
        super(polozaj, zacetniCasSek);
        this.razred = razred;
        this.delovnaNit = null;
    }

    /**
     * Vrne true, ker je igralec this ra"cunalnik. 
     */
    @Override
    public boolean jeRacunalnik() {
        return true;
    }

    /**
     * Vrne dolgo ime igralca this (sXXXXXXXX.Stroj_Ime).
     */
    @Override
    public String vrniIme() {
        return this.razred.getName();
    }

    /**
     * Vrne kratko ime igralca this (dolgo ime brez predpone
     * sXXXXXXXX.Stroj_).
     */
    @Override
    public String vrniKratkoIme() {
        return this.vrniIme().substring(DOLZINA_PREDPONE);
    }

    /**
     * Ustvari stroj za igralno logiko za igralca `this'.
     */
    @Override
    public boolean ustvariStroj() {
        try {
            this.stroj = (Stroj) this.razred.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            System.err.println("Ne morem ustvariti objekta razreda " + this.vrniIme());
            return false;
        }
        return true;
    }

    /**
     * Pokli"ce strojevo metodo novaPartija in ji posreduje podano mno"zico
     * kart.  "Cas, ki ga porabi stroj, od"steje od razpolo"zljivega
     * igral"cevega "casa.
     */
    @Override
    public void pricetekPartije(MnozicaKart karte) {
        super.pricetekPartije(karte);
        this.shraniCasovniZaznamek();
        this.stroj.novaPartija(this.vrniPolozaj(), new MnozicaKart(karte));
        this.posodobiCasDoKonca();
    }

    /**
     * Pokli"ce strojevo metodo pricetekStiha in ji posreduje podani polo"zaj
     * za"cetnika "stiha.  "Cas, ki ga porabi stroj, od"steje od
     * razpolo"zljivega igral"cevega "casa.
     */
    @Override
    public void pricetekStiha(int polozaj) {
        this.shraniCasovniZaznamek();
        this.stroj.pricetekStiha(polozaj);
        this.posodobiCasDoKonca();
    }

    /**
     * Pokli"ce strojevo metodo sprejmiPotezo in ji posreduje podani polo"zaj
     * akterja poteze in odvr"zeno karto.  "Cas, ki ga porabi stroj, od"steje
     * od razpolo"zljivega igral"cevega "casa.
     */
    @Override
    public void sprejmiPotezo(int polozaj, Karta karta) {
        this.shraniCasovniZaznamek();
        this.stroj.sprejmiPotezo(polozaj, karta);
        this.posodobiCasDoKonca();
    }

    /**
     * Posredno pokli"ce strojevo metodo izberiPotezo.
     * @param moznosti parameter se ne uporablja, vendar pa mora biti prisoten
     *    zaradi redefinicije metode iz nadrazreda
     * @param pokliciPoIzbiri funkcija, ki se pokli"ce po izbiri karte
     *    (posreduje se ji karta, ki jo je izbral stroj)
     */
    @Override
    public void izberiPotezo(MnozicaKart moznosti, Consumer<Karta> pokliciPoIzbiri) {
        if (Most.vrni().besedilniVmesnik()) {
            this.izberiPotezoTekstovno(pokliciPoIzbiri);
        } else {
            this.izberiPotezoGraficno(pokliciPoIzbiri);
        }
    }

    /**
     * Pokli"ce strojevo metodo izberiPotezo, ne da bi za to ustvarila posebno
     * nit.  "Cas, ki ga porabi stroj, od"steje od razpolo"zljivega
     * igral"cevega "casa.  Metoda se pokli"ce v besedilnem na"cinu, zato ni
     * potrebe po izdelavi posebne niti.  Po izbiri karte se pokli"ce funkcija
     * pokliciPoIzbiri.  Funkciji se posreduje karta, ki jo je izbral stroj.
     */
    private void izberiPotezoTekstovno(Consumer<Karta> pokliciPoIzbiri) {
        this.shraniCasovniZaznamek();
        long casDoKonca = this.vrniCasDoKonca();
        if (!Most.vrni().tihiNacin()) {
            System.out.printf("Čas do konca: %d ms%n", casDoKonca);
        }
        Karta izbranaKarta = this.stroj.izberiPotezo(this.vrniCasDoKonca());
        this.posodobiCasDoKonca();
        if (!Most.vrni().tihiNacin()) {
            System.out.printf("Izbrana karta: %s%n", izbranaKarta);
            System.out.println();
        }
        pokliciPoIzbiri.accept(izbranaKarta);
    }

    /**
     * Ustvari posebno nit in v okviru te niti pokli"ce strojevo metodo
     * izberiPotezo.  "Cas, ki ga porabi stroj, od"steje od razpolo"zljivega
     * igral"cevega "casa.  Metoda se pokli"ce v grafi"cnem na"cinu; nit se
     * ustvari zato, da uporabni"ski vmesnik ostane odziven.  Po izbiri karte
     * se pokli"ce funkcija pokliciPoIzbiri.  Funkciji se posreduje karta, ki
     * jo je izbral stroj.
     */
    private void izberiPotezoGraficno(Consumer<Karta> pokliciPoIzbiri) {
        this.delovnaNit = new Thread(() -> {
            this.shraniCasovniZaznamek();
            Karta izbranaKarta = this.stroj.izberiPotezo(this.vrniCasDoKonca());
            if (!this.delovnaNit.isInterrupted()) {
                this.posodobiCasDoKonca();
                pokliciPoIzbiri.accept(izbranaKarta);
            }
        });
        this.delovnaNit.start();
    }

    /**
     * Prekine nit, v kateri stroj izbira potezo.
     */
    @Override
    public void prekiniDelovnoNit() {
        if (this.delovnaNit != null) {
            this.delovnaNit.interrupt();
        }
    }

    /**
     * Pokli"ce strojevo metodo `rezultat' in ji posreduje tabelo to"ck, ki so
     * jih posamezni igralci prejeli v pravkar kon"cani partiji.
     * @param tockeObKoncuPartije tockeObKoncuPartije[i]: to"cke, ki jih je v
     *    pravkar odigrani partiji prejel igralec na polo"zaju i.
     */
    @Override
    public void rezultat(int[] tockeObKoncuPartije) {
        this.stroj.rezultat(tockeObKoncuPartije);
    }
}
