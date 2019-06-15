
package skupno;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Objekt tega razreda predstavlja igralno karto.
 */
public class Karta implements Comparable<Karta> {

    /** Izjema tega tipa se vr"ze v primeru neveljavne karte. */
    public static class NeveljavnaKartaException extends RuntimeException {
        public NeveljavnaKartaException(String niz) { 
            super("Neveljavna karta: " + niz);
        }
    }

    /** 0: srce; 1: pik; 2: karo; 3: kri"z */
    private int barva;

    /**
     * 2, 3, ..., 10: dvojka, trojka, ..., desetka;
     * 11: fant;
     * 12: dama;
     * 13: kralj;
     * 14: as
     */
    private int vrednost;

    /** Srce, Pik, kAro, kRi"z */
    private static final char[] BARVA_V_ZNAK = {'s', 'p', 'a', 'r'};

    // srca (s)
    public static final Karta sA = new Karta(0, 14);   // indeks 0
    public static final Karta sK = new Karta(0, 13);   // indeks 1
    public static final Karta sQ = new Karta(0, 12);
    public static final Karta sJ = new Karta(0, 11);
    public static final Karta sT = new Karta(0, 10);
    public static final Karta s9 = new Karta(0, 9);
    public static final Karta s8 = new Karta(0, 8);
    public static final Karta s7 = new Karta(0, 7);
    public static final Karta s6 = new Karta(0, 6);
    public static final Karta s5 = new Karta(0, 5);
    public static final Karta s4 = new Karta(0, 4);
    public static final Karta s3 = new Karta(0, 3);
    public static final Karta s2 = new Karta(0, 2);

    // piki (p)
    public static final Karta pA = new Karta(1, 14);   // indeks 13
    public static final Karta pK = new Karta(1, 13);
    public static final Karta pQ = new Karta(1, 12);
    public static final Karta pJ = new Karta(1, 11);
    public static final Karta pT = new Karta(1, 10);
    public static final Karta p9 = new Karta(1, 9);
    public static final Karta p8 = new Karta(1, 8);
    public static final Karta p7 = new Karta(1, 7);
    public static final Karta p6 = new Karta(1, 6);
    public static final Karta p5 = new Karta(1, 5);
    public static final Karta p4 = new Karta(1, 4);
    public static final Karta p3 = new Karta(1, 3);
    public static final Karta p2 = new Karta(1, 2);

    // kare (a)
    public static final Karta aA = new Karta(2, 14);   // indeks 26
    public static final Karta aK = new Karta(2, 13);
    public static final Karta aQ = new Karta(2, 12);
    public static final Karta aJ = new Karta(2, 11);
    public static final Karta aT = new Karta(2, 10);
    public static final Karta a9 = new Karta(2, 9);
    public static final Karta a8 = new Karta(2, 8);
    public static final Karta a7 = new Karta(2, 7);
    public static final Karta a6 = new Karta(2, 6);
    public static final Karta a5 = new Karta(2, 5);
    public static final Karta a4 = new Karta(2, 4);
    public static final Karta a3 = new Karta(2, 3);
    public static final Karta a2 = new Karta(2, 2);

    // kri"zi (r)
    public static final Karta rA = new Karta(3, 14);   // indeks 39
    public static final Karta rK = new Karta(3, 13);
    public static final Karta rQ = new Karta(3, 12);
    public static final Karta rJ = new Karta(3, 11);
    public static final Karta rT = new Karta(3, 10);
    public static final Karta r9 = new Karta(3, 9);
    public static final Karta r8 = new Karta(3, 8);
    public static final Karta r7 = new Karta(3, 7);
    public static final Karta r6 = new Karta(3, 6);
    public static final Karta r5 = new Karta(3, 5);
    public static final Karta r4 = new Karta(3, 4);
    public static final Karta r3 = new Karta(3, 3);
    public static final Karta r2 = new Karta(3, 2);    // indeks 51

    /** tabela kart, urejenih po nara"s"cajo"cih indeksih */
    private static final Karta[] VSE_KARTE = {
        sA, sK, sQ, sJ, sT, s9, s8, s7, s6, s5, s4, s3, s2,
        pA, pK, pQ, pJ, pT, p9, p8, p7, p6, p5, p4, p3, p2,
        aA, aK, aQ, aJ, aT, a9, a8, a7, a6, a5, a4, a3, a2,
        rA, rK, rQ, rJ, rT, r9, r8, r7, r6, r5, r4, r3, r2,
    };

    // indeksi barv
    public static final int SRCE = 0;
    public static final int PIK = 1;
    public static final int KARO = 2;
    public static final int KRIZ = 3;

    // vrednosti kart
    public static final int DVOJKA = 2;
    public static final int TROJKA = 3;
    public static final int STIRKA = 4;
    public static final int PETKA = 5;
    public static final int SESTKA = 6;
    public static final int SEDEMKA = 7;
    public static final int OSEMKA = 8;
    public static final int DEVETKA = 9;
    public static final int DESETKA = 10;
    public static final int FANT = 11;
    public static final int DAMA = 12;
    public static final int KRALJ = 13;
    public static final int AS = 14;

    /** VREDNOST_V_ZNAK[v] = znak za karto z vrednostjo v (pri besedilnem vmesniku) */
    public static final char[] VREDNOST_V_ZNAK = {
        '-', '-', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'
    };

    /** VREDNOST_V_NAPIS[v] = napis karte z vrednostjo v (na `umetni' sliki) */
    public static final String[] VREDNOST_V_NAPIS = {
        "-", "-", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"
    };

    /** ZNAK_V_BARVO[c] = indeks barve z znakom c (npr. ZNAK_V_BARVO['p'] == 1) */
    private static final int[] ZNAK_V_BARVO;

    /** ZNAK_V_VREDNOST[c] = vrednost karte z znakom c (npr. ZNAK_V_VREDNOST['J'] == 11) */
    private static final int[] ZNAK_V_VREDNOST;

    static {
        // napolni tabeli ZNAK_V_BARVO in ZNAK_V_VREDNOST
        ZNAK_V_BARVO = new int[128];
        Arrays.fill(ZNAK_V_BARVO, -1);
        for (int i = 0;  i < BARVA_V_ZNAK.length;  i++) {
            ZNAK_V_BARVO[BARVA_V_ZNAK[i]] = i;
        }

        ZNAK_V_VREDNOST = new int[128];
        Arrays.fill(ZNAK_V_VREDNOST, -1);
        for (int i = 0;  i < VREDNOST_V_ZNAK.length;  i++) {
            ZNAK_V_VREDNOST[VREDNOST_V_ZNAK[i]] = i;
        }
        ZNAK_V_VREDNOST['-'] = -1;
    }

    /**
     * Izdela karto s podano barvo in podano vrednostjo.  Ker so identitete
     * kart fiksne, smo zunanjemu svetu onemogo"cili ustvarjanje kart z
     * operatorjem new.  Zato je konstruktor privaten.
     */
    private Karta(int barva, int vrednost) {
        this.barva = barva;
        this.vrednost = vrednost;
    }

    /**
     * Vrne objekt, ki predstavlja karto s podano barvo in vrednostjo.
     * @param barva  barva karte (0: srce; 1: pik; 2: karo; 3: kri"z)
     * @param vrednost  vrednost karte
     *                  (2: dvojka; 3: trojka; ..., 10: desetka; 
     *                   11: fant; 12: dama; 13: kralj; 14: as)
     */
    public static Karta objekt(int barva, int vrednost) {
        if (barva < 0 ||
                barva >= Konstante.ST_BARV || 
                vrednost < Konstante.ZACETNA_VREDNOST ||
                vrednost >= Konstante.ZACETNA_VREDNOST + Konstante.ST_KART_NA_BARVO) {
            throw new NeveljavnaKartaException(String.format("barva = %d, vrednost = %d", barva, vrednost));
        }
        return VSE_KARTE[bv2indeks(barva, vrednost)];
    }

    /**
     * Ustvari objekt, ki predstavlja karto s podanim indeksom
     * (0: sr"cev as; 1: sr"cev kralj; ...; 12: sr"ceva dvojka;
     *  13: pikov as; ...; 25: pikova dvojka;
     *  26: karin as; ...; 38: karina dvojka;
     *  39: kri"zev as; ...; 51: kri"zeva dvojka).
     */
    public static Karta objekt(int indeks) {
        if (indeks < 0 || indeks >= Konstante.ST_KART) {
            throw new NeveljavnaKartaException(String.format("indeks = %d", indeks));
        }
        return VSE_KARTE[indeks];
    }

    /**
     * Ustvari objekt, ki predstavlja karto s podanim nizom, kakr"sne vra"ca
     * metoda toString (npr. "s7", "pJ", "aT", "r3").
     */
    public static Karta objekt(String niz) {
        if (niz.length() != 2) {
            throw new NeveljavnaKartaException(niz);
        }
        char barva = niz.charAt(0);
        char vrednost = niz.charAt(1);
        if (barva < 0 || barva >= ZNAK_V_BARVO.length ||
                vrednost < 0 || vrednost >= ZNAK_V_VREDNOST.length ||
                ZNAK_V_BARVO[barva] < 0 || ZNAK_V_VREDNOST[vrednost] < 0) {
            throw new NeveljavnaKartaException(niz);
        }
        return VSE_KARTE[bv2indeks(ZNAK_V_BARVO[barva], ZNAK_V_VREDNOST[vrednost])];
    }

    /** 
     * Vrne barvo karte this ("stevilo med 0 in 3).
     */
    public int vrniBarvo() {
        return this.barva;
    }

    /** 
     * Vrne vrednost karte this ("stevilo med 2 in 14).
     */
    public int vrniVrednost() {
        return this.vrednost;
    }

    /**
     * Vrne linearni indeks karte na podlagi njene barve in vrednosti:
     * sA:  0, sK:  1, ..., s2: 12,
     * pA: 13, pK: 14, ..., p2: 25,
     * aA: 26, aK: 27, ..., a2: 38,
     * rA: 39, rK: 40, ..., r2: 51
     */
    public int indeks() {
        return bv2indeks(this.barva, this.vrednost);
    }

    /** 
     * Vrne true natanko v primeru, "ce ima karta this barvo aduta.
     */
    public boolean jeAdut() {
        return (this.barva == Konstante.BARVA_ADUTA);
    }

    /**
     * Vrne linearni indeks karte na podlagi njene barve in vrednosti.
     * Sr"cev as ima indeks 0, sr"cev kralj 1, ..., kri"zeva dvojka pa 51.
     */
    public static int bv2indeks(int barva, int vrednost) {
        return Konstante.ST_KART_NA_BARVO * barva +
            (Konstante.ZACETNA_VREDNOST + Konstante.ST_KART_NA_BARVO - vrednost - 1);
    }

    /** 
     * Vrne dvoznakovni niz, ki predstavlja karto, denimo aT za kArino desetko (T).
     */
    @Override
    public String toString() {
        return String.format("%c%c",
                BARVA_V_ZNAK[this.barva], VREDNOST_V_ZNAK[this.vrednost]);
    }

    /** 
     * Vrne zgo"s"cevalno kodo objekta this.
     */
    @Override
    public int hashCode() {
        return this.indeks();
    }

    /**
     * Vrne true natanko v primeru, "ce objekt this predstavlja isto karto kot
     * objekt `objekt'.
     */
    @Override
    public boolean equals(Object objekt) {
        if (!(objekt instanceof Karta)) {
            return false;
        }
        return (this.hashCode() == objekt.hashCode());
    }

    /**
     * Vrne pozitivno "stevilo / 0 / negativno "stevilo, "ce ima karta `this'
     * ve"cji / enak / manj"si indeks od karte `karta'.  Metodo lahko
     * uporabimo za urejanje po indeksih.  Karte v roki, denimo, so vedno
     * urejene po nara"s"cajo"cih indeksih.
     */
    @Override
    public int compareTo(Karta karta) {
        return (this.indeks() - karta.indeks());
    }

    /**
     * Vrne funkcijo, ki podani karti primerja po vrednosti (vrne pozitivno
     * "stevilo / 0 / negativno "stevilo, "ce je prva karta ve"cja / enaka /
     * manj"sa kot druga).  Metodo lahko neposredno uporabimo za urejanje kart
     * po vrednosti.
     */
    public static Comparator<Karta> primerjalnikPoVrednosti() {
        return (k1, k2) -> (k1.vrednost - k2.vrednost);
    }
}
