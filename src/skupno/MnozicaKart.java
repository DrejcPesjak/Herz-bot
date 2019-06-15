
package skupno;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Objekt tega razreda predstavlja mno"zico kart.  Mno"zica je neindeksirana
 * zbirka elementov brez podvajanj.
 */
public class MnozicaKart implements Iterable<Karta> {

    /**
     * Tip izjeme, ki se vr"ze, ko posku"samo izbrati element iz prazne
     * mno"zice.
     */
    public static class PraznaMnozicaException extends RuntimeException {
        public PraznaMnozicaException(String sprozitelj) {
            super(String.format("MnozicaKart.%s: množica je prazna", sprozitelj));
        }
    }

    /** mno"zica kart je predstavljena kot bitno polje dol"zine
     * Konstante.ST_KART.  Velja bitset[i] == 1 natanko tedaj, ko je karta z
     * indeksom i prisotna v mno"zici. */
    private BitSet bitset;

    /** BARVE[i]: mno"zica vseh kart barve i (0: srce, 1: pik, 2: karo, 3:
     * kri"z) */
    private static final MnozicaKart[] BARVE;

    /** VREDNOSTI[i]: mno"zica vseh kart z vrednostjo i (2: dvojke, 3: trojke;
     * ....; 14: asi) */
    private static final MnozicaKart[] VREDNOSTI;

    /** prazna mno"zica */
    public static final MnozicaKart PRAZNA = new MnozicaKart();

    static {
        // napolni tabeli BARVE in VREDNOSTI
        BARVE = new MnozicaKart[Konstante.ST_BARV];
        for (int barva = 0;  barva < Konstante.ST_BARV;  barva++) {
            BARVE[barva] = new MnozicaKart();
            BARVE[barva].bitset = new BitSet(Konstante.ST_KART);
            BARVE[barva].bitset.set(
                    Konstante.ST_KART_NA_BARVO * barva,
                    Konstante.ST_KART_NA_BARVO * (barva + 1));
        }

        VREDNOSTI = new MnozicaKart[Konstante.ST_KART_NA_BARVO];
        for (int v = 0;  v < Konstante.ST_KART_NA_BARVO;  v++) {
            VREDNOSTI[v] = new MnozicaKart();
            BitSet bs = new BitSet(Konstante.ST_KART);
            for (int b = 0;  b < Konstante.ST_BARV;  b++) {
                bs.set(v + b * Konstante.ST_KART_NA_BARVO);
            }
            VREDNOSTI[v].bitset = bs;
        }
    }

    /** 
     * Ustvari prazno mno"zico kart.
     */
    public MnozicaKart() {
        this.bitset = new BitSet(Konstante.ST_BARV);
    }

    /**
     * Ustvari mno"zico kart kot kopijo mno"zice `podlaga'.
     */
    public MnozicaKart(MnozicaKart podlaga) {
        this.bitset = new BitSet(Konstante.ST_BARV);
        this.bitset.or(podlaga.bitset);
    }

    /**
     * Ustvari mno"zico, ki vsebuje natanko podane elemente.
     */
    public MnozicaKart(Karta... karte) {
        this.bitset = new BitSet(Konstante.ST_KART);
        for (Karta karta: karte) {
            this.bitset.set(karta.indeks());
        }
    }

    /** 
     * Vrne mno"zico, ki vsebuje vse karte v podani barvi. 
     * @param barva indeks barve (0: srce, 1: pik, 2: karo, 3: kri"z)
     */
    public static MnozicaKart vsiPrimerkiBarve(int barva) {
        return BARVE[barva];
    }

    /** 
     * Vrne mno"zico, ki vsebuje vse karte s podano vrednostjo.
     * @param vrednost vrednost karte (2: dvojka, ..., 14: as)
     */
    public static MnozicaKart vsiPrimerkiVrednosti(int vrednost) {
        return VREDNOSTI[Konstante.ST_KART_NA_BARVO - 1 - vrednost + Konstante.ZACETNA_VREDNOST];
    }

    /** 
     * Vrne true natanko v primeru, "ce mno"zica this vsebuje podano karto.
     */
    public boolean vsebuje(Karta karta) {
        return this.bitset.get(karta.indeks());
    }

    /** 
     * Doda podano karto v mno"zico this.  "Ce podana karta v mno"zici "ze
     * obstaja, se ne zgodi ni"c.
     */
    public void dodaj(Karta karta) {
        this.bitset.set(karta.indeks());
    }

    /** 
     * Odstrani podano karto iz mno"zice this.  "Ce podane karte v mno"zici
     * ni, se ne zgodi ni"c.
     */
    public void odstrani(Karta karta) {
        this.bitset.clear(karta.indeks());
    }

    /** 
     * Vrne "stevilo kart v mno"zici this.
     */
    public int steviloKart() {
        return this.bitset.cardinality();
    }

    /** 
     * Vrne true natanko v primeru, "ce je mno"zica this prazna.
     */
    public boolean jePrazna() {
        return (this.steviloKart() == 0);
    }

    /** 
     * Izdela in vrne novo mno"zico kot presek mno"zice this in mno"zice
     * druga.
     */
    public MnozicaKart presek(MnozicaKart druga) {
        MnozicaKart rezultat = new MnozicaKart(this);
        rezultat.bitset.and(druga.bitset);
        return rezultat;
    }

    /** 
     * Izdela in vrne novo mno"zico kot unijo mno"zice this in mno"zice druga.
     */
    public MnozicaKart unija(MnozicaKart druga) {
        MnozicaKart rezultat = new MnozicaKart(this);
        rezultat.bitset.or(druga.bitset);
        return rezultat;
    }

    /** 
     * Izdela in vrne novo mno"zico kot komplement mno"zice this v univerzalni
     * mno"zici vseh kart.
     */
    public MnozicaKart komplement() {
        MnozicaKart rezultat = new MnozicaKart();
        rezultat.bitset.set(0, Konstante.ST_KART);
        rezultat.bitset.andNot(this.bitset);
        return rezultat;
    }

    /** 
     * Izdela in vrne novo mno"zico kot razliko mno"zice this in mno"zice druga.
     */
    public MnozicaKart razlika(MnozicaKart druga) {
        return this.presek(druga.komplement());
    }

    /** 
     * Izdela in vrne novo mno"zico, ki vsebuje vse karte barve `barva' v
     * mno"zici this.
     * @param barva indeks barve (0: srce, 1: pik, 2: karo, 3: kri"z)
     */
    public MnozicaKart karteVBarvi(int barva) {
        return this.presek(BARVE[barva]);
    }

    /** 
     * Izdela in vrne novo mno"zico, ki vsebuje vse karte z vrednostjo
     * `vrednost' v mno"zici this.
     * @param vrednost vrednost karte (2: dvojka, ..., 14: as)
     */
    public MnozicaKart karteVVrednosti(int vrednost) {
        return this.presek(VREDNOSTI[Konstante.ST_KART_NA_BARVO - 1 - vrednost + Konstante.ZACETNA_VREDNOST]);
    }

    /** 
     * Vrne naklju"cno karto iz mno"zice this.  "Ce je mno"zica prazna, vr"ze
     * izjemo tipa PraznaMnozicaException.
     * @param nakljucniGenerator vir naklju"cnosti
     */
    public Karta izberiNakljucno(Random nakljucniGenerator) {
        if (this.jePrazna()) {
            throw new PraznaMnozicaException("izberiNakljucno");
        }

        // izberemo naklju"cni indeks in poi"s"cemo karto na tem indeksu
        int izbraniIndeks = nakljucniGenerator.nextInt(this.steviloKart());
        int i = 0;
        for (Karta karta: this) {
            if (i == izbraniIndeks) {
                return karta;
            }
            i++;
        }
        return null;
    }

    /** 
     * Izdela in vrne tabelo, ki vsebuje vse karte mno"zice this, urejene po
     * nara"s"cajo"cem indeksu.
     */
    public Karta[] vTabelo() {
        Karta[] karte = new Karta[this.steviloKart()];
        int i = 0;
        for (Karta karta: this) {
            karte[i++] = karta;
        }
        return karte;
    }

    /** 
     * Vrne tisto karto v mno"zici this, ki ima najmanj"si indeks.
     * Indeksi kart si po nara"s"cajo"cem vrstnem redu sledijo takole:
     * sA (indeks = 0), sK, ..., s2, pA, ..., p2, aA, ..., a2, rA, ..., r2
     * (indeks = 51)
     */
    public Karta najmanjsiIndeks() {
        if (this.jePrazna()) {
            throw new PraznaMnozicaException("najmanjsiIndeks");
        }
        return Karta.objekt(this.bitset.nextSetBit(0));
    }

    /** 
     * Vrne tisto karto v mno"zici this, ki ima najve"cji indeks.
     */
    public Karta najvecjiIndeks() {
        if (this.jePrazna()) {
            throw new PraznaMnozicaException("najvecjiIndeks");
        }
        return Karta.objekt(this.bitset.previousSetBit(this.bitset.length()));
    }

    /** 
     * Vrne predstavitev mno"zice this v obliki niza
     * [k_0, k_1, ..., k_{n-1}].
     */
    @Override
    public String toString() {
        return "[" + String.join(", ", 
                this.bitset.stream().
                    mapToObj(i -> Karta.objekt(i).toString()).
                        collect(Collectors.toList())) + "]";
    }

    /** 
     * Vrne predstavitev mno"zice this v obliki niza
     * k_0 k_1 ... k_{n-1}.
     */
    public String toString_neokrancljano() {
        return String.join(" ", 
                this.bitset.stream().
                    mapToObj(i -> Karta.objekt(i).toString()).
                        collect(Collectors.toList()));
    }

    /** 
     * Vrne true natanko tedaj, ko objekt obj predstavlja mno"zico z enako
     * vsebino kot objekt this.
     */
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof MnozicaKart)) {
            return false;
        }
        return this.bitset.equals(((MnozicaKart) obj).bitset);
    }

    /** 
     * Vrne zgo"s"cevalno kodo mno"zice this.
     */
    @Override
    public int hashCode() {
        final int FAKTOR = Konstante.ST_KART;
        final int MODULO = 10000019;
        int koda = 0;
        for (Karta karta: this) {
            koda = (FAKTOR * koda + karta.hashCode()) % MODULO;
        }
        return koda;
    }

    /** 
     * Vrne iterator, ki ga je mogo"ce uporabiti za sprehod po mno"zici this
     * (gl. odsek `iterator' v razredu Test).
     */
    public Iterator<Karta> iterator() {
        return new Iterator<Karta>() {
            int indeks = 0;

            @Override
            public boolean hasNext() {
                BitSet bs = MnozicaKart.this.bitset;
                return (bs.nextSetBit(this.indeks) >= this.indeks);
            }

            @Override
            public Karta next() {
                BitSet bs = MnozicaKart.this.bitset;
                this.indeks = bs.nextSetBit(this.indeks) + 1;
                return Karta.objekt(this.indeks - 1);
            }
        };
    }
}
