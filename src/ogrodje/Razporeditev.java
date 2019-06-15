
package ogrodje;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import skupno.Karta;
import skupno.Konstante;
import skupno.MnozicaKart;

/**
 * Objekt tega razreda predstavlja razporeditev kart med igralce.
 */
public class Razporeditev {

    /** Tip izjeme, ki se vr"ze v primeru neveljavne razporeditve */
    public static class NeveljavnaRazporeditevException extends RuntimeException {
        public NeveljavnaRazporeditevException(String niz) { 
            super("Neveljavna razporeditev: " + niz);
        }
    }

    /** lo"cilo med seznami kart za posamezne igralce */
    private static final String LOCILO = "|";

    /** mnozice[i]: mno"zica kart, dodeljena igralcu i */
    MnozicaKart[] mnozice;

    /**
     * Izdela razporeditev na podlagi podanih mno"zic kart.
     */
    private Razporeditev(MnozicaKart[] mnozice) {
        this.mnozice = mnozice;
    }

    /**
     * Prebere razporeditev s pomo"cjo podanega bralnika.
     */
    public static Razporeditev preberi(Scanner bralnik) {
        MnozicaKart[] mnozice = new MnozicaKart[Konstante.ST_IGRALCEV];
        Set<Karta> vseKarte = new HashSet<>();

        String vrstica = bralnik.nextLine().trim();
        if (vrstica.isEmpty() || vrstica.startsWith("#")) {
            return null;
        }
        Scanner vrsticniBralnik = new Scanner(vrstica);

        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            mnozice[i] = new MnozicaKart();
            for (int j = 0;  j < Konstante.ST_KART_NA_IGRALCA;  j++) {
                String beseda = null;
                try {
                    beseda = vrsticniBralnik.next();
                } catch (NoSuchElementException ex) {
                    throw new NeveljavnaRazporeditevException("prezgodnji zaključek seznama kart"); 
                }
                Karta karta = null;
                try {
                    karta = Karta.objekt(beseda);
                } catch (Karta.NeveljavnaKartaException ex) {
                    throw new NeveljavnaRazporeditevException(String.format("neveljavna karta (%s)", beseda));
                }
                mnozice[i].dodaj(karta);
                if (vseKarte.contains(karta)) {
                    throw new NeveljavnaRazporeditevException(String.format("podvojena karta (%s)", karta));
                }
                vseKarte.add(karta);
            }
            if (i < Konstante.ST_IGRALCEV - 1) {
                String locilo = vrsticniBralnik.next();    // preberi lo"cilo (niz LOCILO)
                if (!locilo.equals(LOCILO)) {
                    throw new NeveljavnaRazporeditevException(String.format("pričakoval sem ločilo (%s)", LOCILO));
                }
            }
        }
        if (vseKarte.size() < Konstante.ST_KART) {
            throw new NeveljavnaRazporeditevException("premalo kart");
        }
        return new Razporeditev(mnozice);
    }

    /**
     * Izdela in vrne naklju"cno razporeditev kart med igralce.
     */
    public static Razporeditev nakljucna(Random random) {
        List<Integer> indeksi = IntStream.range(0, Konstante.ST_KART).boxed().collect(Collectors.toList());
        Collections.shuffle(indeksi, random);

        MnozicaKart[] mnozice = new MnozicaKart[Konstante.ST_IGRALCEV];
        int k = 0;
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            mnozice[i] = new MnozicaKart();
            for (int j = 0;  j < Konstante.ST_KART_NA_IGRALCA;  j++) {
                mnozice[i].dodaj(Karta.objekt(indeksi.get(k)));
                k++;
            }
        }
        return new Razporeditev(mnozice);
    }

    /**
     * Vrne mno"zico kart za podanega igralca.
     * @param igralec polo"zaj igralca (0, 1, 2 ali 3)
     */
    public MnozicaKart vrniKarte(int igralec) {
        return this.mnozice[igralec];
    }

    /**
     * Vrne predstavitev razporeditve this v obliki niza:
     * karte_0 | karte_1 | karte_2 | karte_3
     * karte_i = k_0 k_1 ... k_{12}
     */
    @Override
    public String toString() {
        return String.join(" | ", 
                Arrays.asList(this.mnozice).stream().
                map(MnozicaKart::toString_neokrancljano).collect(Collectors.toList()));
    }
}
