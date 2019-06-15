
package ogrodje;

import java.util.Scanner;
import java.util.function.Consumer;

import skupno.Karta;
import skupno.MnozicaKart;

/**
 * Objekt tega razreda predstavlja "clove"skega igralca.
 */
public class Clovek extends Igralec {

    /** bralnik za branje uporabnikovih izbir kart v besedilnem na"cinu */
    private Scanner bralnik;

    /** 
     * Izdela objekt, ki predstavlja "clove"skega igralca na podanem
     * polo"zaju.
     * @param polozaj 0 (spodaj levo), 1 (zgoraj levo), 2 (zgoraj desno) ali 
     *                3 (spodaj desno)
     */
    public Clovek(int polozaj) {
        super(polozaj, 0);
    }

    /**
     * V besedilnem na"cinu pripravi bralnik za branje s standardnega vhoda, v
     * grafi"cnem pa ne naredi ni"cesar.
     */
    @Override
    public void inicializiraj() {
        this.bralnik = (Most.vrni().besedilniVmesnik()) ? (new Scanner(System.in)) : (null);
    }

    /**
     * Vrne false, ker igralec this ni ra"cunalnik. 
     */
    @Override
    public boolean jeRacunalnik() {
        return false;
    }

    /**
     * Vrne niz "Človek".
     */
    @Override
    public String vrniIme() {
        return "Človek";
    }

    /**
     * Vrne niz "Človek".
     */
    @Override
    public String vrniKratkoIme() {
        return "Človek";
    }

    /**
     * Spro"zi postopek za izbiro poteze.
     * @param moznosti mno"zica kart, ki jih je mogo"ce odvre"ci v trenutni
     *    situaciji
     * @param pokliciPoIzbiri funkcija, ki se pokli"ce po izbiri karte, kot
     *    parameter pa se ji poda izbrana karta
     */
    @Override
    public void izberiPotezo(MnozicaKart moznosti, Consumer<Karta> pokliciPoIzbiri) {
        if (Most.vrni().besedilniVmesnik()) {
            this.izberiPotezoTekstovno(moznosti, pokliciPoIzbiri);
        } else {
            this.izberiPotezoGraficno(pokliciPoIzbiri);
        }
    }

    /**
     * Prebere uporabnikovo izbiro karte s standardnega vhoda (pri "cemer mu
     * ponudi, da izbira med kartami v mno"zici `moznosti') in pokli"ce
     * funkcijo pokliciPoIzbiri.  Funkciji posreduje izbrano karto.
     */
    private void izberiPotezoTekstovno(MnozicaKart moznosti, Consumer<Karta> pokliciPoIzbiri) {
        Karta karta = null;
        while (karta == null || !moznosti.vsebuje(karta)) {
            System.out.printf("Izberi karto %s: ", moznosti);
            try {
                karta = Karta.objekt(bralnik.nextLine().trim());
            } catch (Karta.NeveljavnaKartaException ex) {
                karta = null;
            }
        }
        System.out.println();
        pokliciPoIzbiri.accept(karta);
    }

    /**
     * Omogo"ci uporabniku, da klikne na karto, ki jo "zeli odvre"ci.  Po
     * izbiri karte pokli"ci funkcijo pokliciPoIzbiri, pri "cemer ji posreduje
     * izbrano karto.
     */
    private void izberiPotezoGraficno(Consumer<Karta> pokliciPoIzbiri) {
        Most.vrni().omogociIzbiroPoteze(pokliciPoIzbiri);
    }
}
