
package ogrodje;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import skupno.Karta;
import skupno.Konstante;

/**
 * Objekt tega razreda predstavlja zaklju"ceni ali delni "stih.
 * <p>
 * V tem razredu lo"cimo absolutne in relativne indekse igralcev.  Absolutni
 * indeksi so fiksni polo"zaji igralcev (0: spodaj levo, 1: zgoraj levo itd.),
 * relativni pa so polo"zaji glede na vrstni red v "stihu (0: za"cetnik
 * "stiha, 1: njegov naslednik itd.).  Izraz `indeks' brez dodatkov pomeni
 * absolutni indeks.
 */
public class Stih implements Iterable<Karta> {

    /**
     * Tip izjeme, ki se vr"ze v primeru, ko "zelimo dolo"citi dobitnika
     * "stiha, preden se "stih zaklju"ci.
     */
    public static class StihaSeNiKonecException extends RuntimeException {
        public StihaSeNiKonecException() { 
            super("Štih se še ni zaključil!");
        }
    }

    /** karte, ki tvorijo (delni) "stih;
     * karte[i]: karta, ki jo je vrgel igralec z indeksom i */
    private Karta[] karte;

    /** "stevilo odvr"zenih kart v "stihu */
    private int stKart;

    /** indeks igralca, ki je pri"cel "stih */
    private int zacetnik;

    /** indeks igralca, ki je trenutno na potezi */
    private int naPotezi;

    /** vsota vrednosti odvr"zenih kart */
    private int vrednost;

    /**
     * Izdela "stih, v katerem "se ni bila odvr"zena nobena karta.
     */
    public Stih(int zacetnik) {
        this.karte = new Karta[Konstante.ST_IGRALCEV];
        this.zacetnik = zacetnik;
        this.naPotezi = zacetnik;
        this.stKart = 0;
        this.vrednost = 0;
    }

    /**
     * Vrne indeks igralca, ki je trenutno na potezi.
     */
    public int kdoNaPotezi() {
        return this.naPotezi;
    }

    /**
     * Vrne indeks igralca, ki je pri"cel "stih.
     */
    public int zacetnik() {
        return this.zacetnik;
    }

    /**
     * Vrne karto, ki jo je odvrgel za"cetnik "stiha.
     */
    public Karta zacetnaKarta() {
        return this.karte[this.zacetnik];
    }

    /**
     * Vrne "stevilo odvr"zenih kart v "stihu.
     */
    public int steviloKart() {
        return this.stKart;
    }

    /**
     * Vrne zaporedno "stevilko naslednje odvr"zene karte.
     */
    public int stevilkaNaslednjeKarte() {
        return (this.stKart + 1);
    }

    /**
     * Vrne vsoto odvr"zenih kart.
     */
    public int vrednost() {
        return this.vrednost;
    }

    /**
     * Doda podano karto v trenutni "stih.
     */
    public void dodaj(Karta karta) {
        this.karte[this.naPotezi] = karta;
        this.stKart++;
        this.vrednost += karta.vrniVrednost();
    }

    /**
     * Vrne true natanko v primeru, "ce se je trenutni "stih "ze zaklju"cil.
     */
    public boolean konec() {
        return (this.stKart == Konstante.ST_IGRALCEV);
    }

    /**
     * Vrne indeks igralca, ki je dobil pravkar zaklju"ceni "stih.
     */
    public int dobitnik() {
        if (!this.konec()) {
            throw new StihaSeNiKonecException();
        }

        // "ce je padel vsaj en adut, je dobitnik "stiha tisti, ki je odvrgel
        // najve"cjega aduta
        Comparator<Karta> prim = Karta.primerjalnikPoVrednosti();
        IntBinaryOperator primerjalnikIndeksov = 
            ((i, j) -> (prim.compare(this.karte[i], this.karte[j]) > 0 ? i : j));

        if (IntStream.range(0, Konstante.ST_IGRALCEV).anyMatch(i -> this.karte[i].jeAdut())) {
            return IntStream.range(0, Konstante.ST_IGRALCEV).filter(i -> this.karte[i].jeAdut()).
                           reduce(primerjalnikIndeksov).getAsInt();
        }

        // sicer "stih dobi tisti, ki je odvrgel najve"cjo karto v vodilni
        // barvi
        int vodilnaBarva = this.karte[this.zacetnik].vrniBarvo();
        return IntStream.range(0, Konstante.ST_IGRALCEV).
                filter(i -> this.karte[i].vrniBarvo() == vodilnaBarva).
                reduce(primerjalnikIndeksov).getAsInt();
    }

    /**
     * Pripravi se na naslednjo karto v "stihu oziroma na naslednji "stih.
     * @return true: pri"cne se naslednji "stih;
     *         false: nadaljuje se trenutni "stih
     */
    public boolean naprej() {
        if (this.stKart == Konstante.ST_IGRALCEV) {
            this.zacetnik = this.dobitnik();
            this.karte = new Karta[]{null, null, null, null};
            this.naPotezi = this.zacetnik;
            this.stKart = 0;
            this.vrednost = 0;
            return true;
        } else {
            this.naPotezi = (this.naPotezi + 1) % Konstante.ST_IGRALCEV;
            return false;
        }
    }

    /**
     * Vrne iterator, ki omogo"ca sprehod po odvr"zenih kartah od za"cetne
     * naprej.
     */
    public Iterator<Karta> iterator() {
        return Arrays.asList(
                this.karte[this.zacetnik],
                this.karte[(this.zacetnik + 1) % 4],
                this.karte[(this.zacetnik + 2) % 4],
                this.karte[(this.zacetnik + 3) % 4]
            ).subList(0, this.stKart).iterator();
    }

    /**
     * Vrne niz, ki vsebuje seznam odvr"zenih kart od za"cetne naprej.
     */
    @Override
    public String toString() {
        return String.format("[%s]",
            String.join(", ",
                IntStream.range(0, Konstante.ST_IGRALCEV).
                    mapToObj(i -> String.format("%d: %s", this.relIndeks2igralec(i), this.karte[this.relIndeks2igralec(i)])).
                    collect(Collectors.toList())
            )
        );
    }

    /**
     * Vrne absolutni indeks igralca s podanim relativnim indeksom glede na
     * za"cetnika "stiha.
     */
    private int relIndeks2igralec(int relIndeks) {
        return (this.zacetnik + relIndeks) % Konstante.ST_IGRALCEV;
    }
}
