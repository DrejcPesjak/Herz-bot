
package ogrodje;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import skupno.Konstante;
import skupno.MnozicaKart;

/**
 * Objekt tega razreda predstavlja kvartopirsko `seanso' --- na"celoma
 * poljubno dolgo zaporedje partij.
 */
public class Seansa {

    /**
     * Objekt tega razreda vsebuje parametre seanse.
     */
    private static class Parametri {
        static final int PRIVZETI_ZACETNI_CAS = 10;
        static final int PRIVZETO_STEVILO_PARTIJ = 1;
        static final boolean PRIVZETO_SKRIVANJE_KART = false;
        static final int PRIVZETI_PRVI_NA_POTEZI = 0;
        static final boolean PRIVZETO_BESEDILNI_VMESNIK = false;
        static final boolean PRIVZETO_TIHO = false;
        static final int PRIVZETO_SEME = -1;

        int zacetniCas;
        int skupnoSteviloPartij;
        boolean skrivanjeKart;
        int prviNaPotezi;
        boolean besedilniVmesnik;
        boolean tiho;
        int seme;
        String dnevniskaDatoteka;

        public Parametri() {
            this.zacetniCas = PRIVZETI_ZACETNI_CAS;
            this.skupnoSteviloPartij = PRIVZETO_STEVILO_PARTIJ;
            this.skrivanjeKart = PRIVZETO_SKRIVANJE_KART;
            this.prviNaPotezi = PRIVZETI_PRVI_NA_POTEZI;
            this.besedilniVmesnik = PRIVZETO_BESEDILNI_VMESNIK;
            this.tiho = PRIVZETO_TIHO;
            this.seme = PRIVZETO_SEME;
            this.dnevniskaDatoteka = null;
        }
    }

    /** igralci[i]: objekt, ki predstavlja igralca z indeksom i */
    private Igralec[] igralci;

    /** parametri seanse (tisti, ki jih uporabnik poda ob zagonu programa) */
    private Parametri parametri;

    /** seznam razporeditev, prebranih iz datoteke z razporeditvami */
    private List<Razporeditev> razporeditve;

    /** generator naklju"cnih "stevil */
    private Random nakljucniGenerator;

    /** indeks trenutne partije */
    private int indeksPartije;

    /** indeks igralca, ki pri"cne trenutno partijo */
    private int pricnePartijo;

    /** trenutna partija */
    private Partija partija;

    /**
     * Ustvari objekt Seansa na podlagi parametrov, posredovanih ob zagonu
     * programa.
     */
    public static Seansa izArgumentov(String[] args) {
        String[] razredi = new String[Konstante.ST_IGRALCEV];
        Parametri parametri = new Parametri();
        String datotekaRazporeditev = null;

        int iArg = 0;
        int ixRazred = 0;

        try {
            while (iArg < args.length) {
                switch (args[iArg]) {
                    case "-t": {
                        iArg++;
                        int zacetniCas = Integer.parseInt(args[iArg]);
                        if (zacetniCas >= 0) {
                            parametri.zacetniCas = zacetniCas;
                        } else {
                            System.err.println("Vrednost parametra -t mora biti >= 0");
                        }
                        break;
                    }

                    case "-n": {
                        iArg++;
                        int skupnoSteviloPartij = Integer.parseInt(args[iArg]);
                        if (skupnoSteviloPartij >= 1) {
                            parametri.skupnoSteviloPartij = skupnoSteviloPartij;
                        } else {
                            System.err.println("Vrednost parametra -n mora biti >= 1");
                        }
                        break;
                    }

                    case "-p": {
                        iArg++;
                        int prviNaPotezi = Integer.parseInt(args[iArg]);
                        if (prviNaPotezi >= 0 && prviNaPotezi < Konstante.ST_IGRALCEV) {
                            parametri.prviNaPotezi = prviNaPotezi;
                        } else {
                            System.err.printf("Vrednost parametra -p mora biti med 0 in vključno %d%n",
                                    Konstante.ST_IGRALCEV - 1);
                        }
                        break;
                    }

                    case "-h": {
                        parametri.skrivanjeKart = true;
                        break;
                    }

                    case "-r": {
                        iArg++;
                        datotekaRazporeditev = args[iArg];
                        break;
                    }

                    case "-b": {
                        parametri.besedilniVmesnik = true;
                        break;
                    }

                    case "-d": {
                        iArg++;
                        parametri.dnevniskaDatoteka = args[iArg];
                        break;
                    }

                    case "-q": {
                        parametri.tiho = true;
                        break;
                    }

                    case "-s": {
                        iArg++;
                        try {
                            parametri.seme = Integer.parseInt(args[iArg]);
                            if (parametri.seme <= 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            System.err.println("Seme mora biti pozitivno celo število.");
                        }
                        break;
                    }

                    case "-": {
                        ixRazred++;
                        break;
                    }

                    case "-?": {
                        izpisiNavodila();
                        return null;
                    }

                    default: {
                        if (args[iArg].startsWith("-")) {
                            izpisiNavodila();
                            return null;
                        }
                        razredi[ixRazred++] = args[iArg];
                        break;
                    }
                }
                iArg++;
            }
        } catch (RuntimeException ex) {
            izpisiNavodila();
            return null;
        }

        boolean samiRacunalniki = Arrays.stream(razredi).allMatch(r -> r != null);
        boolean samiLjudje = Arrays.stream(razredi).allMatch(r -> r == null);

        // preveri smiselnost uporabljene kombinacije parametrov
        if (parametri.besedilniVmesnik) {
            // besedilni na"cin
            if (parametri.skrivanjeKart) {
                System.err.println("Parameter -h ima smisel samo v grafičnem načinu.");
            }
            if (parametri.tiho && !samiRacunalniki) {
                System.err.println("Parameter -q ima smisel le tedaj, ko med seboj igrajo sami stroji.");
            }
            if (parametri.tiho && parametri.dnevniskaDatoteka == null) {
                System.err.println("Parameter -q ima smisel le v kombinaciji s pisanjem v dnevniško datoteko.");
            }
        } else {
            // grafi"cni na"cin
            if (parametri.tiho) {
                System.err.println("Parameter -q ima smisel samo v besedilnem načinu.");
            }
            if (parametri.skupnoSteviloPartij > 1) {
                System.err.println("Parameter -n ima smisel samo v besedilnem načinu.");
            }
            if (parametri.skrivanjeKart && samiLjudje) {
                System.err.println("Parameter -h ima smisel le tedaj, ko igra vsaj en stroj.");
            }
        }
        if (datotekaRazporeditev != null && parametri.seme > 0) {
            System.err.println("Pri branju razporeditev iz datoteke se parameter -s ignorira.");
        }

        // ustvari objekte, ki predstavljajo posamezne igralce
        Igralec[] igralci = new Igralec[Konstante.ST_IGRALCEV];
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            if (razredi[i] == null) {
                igralci[i] = new Clovek(i);
            } else {
                try {
                    String polnoIme = polnoImeRazreda(razredi[i]);
                    igralci[i] = new Racunalnik(i, Class.forName(polnoIme), parametri.zacetniCas);
                } catch (ClassNotFoundException e) {
                    System.err.printf("Ne najdem razreda %s.%n", razredi[i]);
                    return null;
                }
            }
        }

        // ustvari stroje za ra"cunalni"ske igralce
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            igralci[i].ustvariStroj();
        }

        // preberi razporeditve
        Scanner bralnik = null;
        if (datotekaRazporeditev != null) {
            try {
                bralnik = new Scanner(new File(datotekaRazporeditev));
            } catch (FileNotFoundException e) {
                System.err.printf("Ne najdem datoteke %s.%n", datotekaRazporeditev);
                return null;
            }
        }

        List<Razporeditev> razporeditve = new ArrayList<>();
        if (bralnik != null) {
            while (bralnik.hasNextLine()) {
                try {
                    Razporeditev razporeditev = Razporeditev.preberi(bralnik);
                    if (razporeditev != null) {
                        razporeditve.add(razporeditev);
                    }
                } catch (Razporeditev.NeveljavnaRazporeditevException ex) {
                    System.err.println(ex.getMessage());
                    return null;
                }
            }
            bralnik.close();
        }

        return new Seansa(igralci, parametri, razporeditve);
    }

    /**
     * Izdela objekt tipa Seansa.
     * @param igralci igralci[i]: objekt, ki predstavlja igralca i
     * @param parametri parametri, podani ob zagonu programa
     * @param razporeditve razporeditve, prebrane iz datoteke razporeditev
     */
    private Seansa(Igralec[] igralci, Parametri parametri, List<Razporeditev> razporeditve) {
        this.igralci = igralci;
        this.parametri = parametri;
        this.razporeditve = razporeditve;
        if (parametri.seme <= 0) {
            this.nakljucniGenerator = new Random();
        } else {
            this.nakljucniGenerator = new Random(parametri.seme);
        }
    }

    /**
     * Izpi"se navodila za zagon programa.
     */
    public static void izpisiNavodila() {
        System.err.println("java Herc <jug> <zahod> <sever> <vzhod> [neobvezni_parametri]");
        System.err.println();
        System.err.println("<jug>   : stroj, ki bo igral na južni strani   (-: človek)");
        System.err.println("<zahod> : stroj, ki bo igral na zahodni strani (-: človek)");
        System.err.println("<sever> : stroj, ki bo igral na severni strani (-: človek)");
        System.err.println("<vzhod> : stroj, ki bo igral na vzhodni strani (-: človek)");
        System.err.println();
        System.err.println("[neobvezni_parametri]:");
        System.err.println("    -t <čas>      : začetni čas za vse stroje (v sekundah)");
        System.err.println("    -r <datoteka> : razporeditve se preberejo iz podane datoteke");
        System.err.println("    -s <seme>     : seme naključnega generatorja za razporejanje kart");
        System.err.println("    -p            : položaj igralca, ki prične (prvo) partijo (0, 1, 2 ali 3)");
        System.err.println("    -h            : skrivanje kart strojev");
        System.err.println("    -b            : besedilni vmesnik namesto grafičnega");
        System.err.println("    -n <število>  : število odigranih partij v besedilnem načinu");
        System.err.println("    -q            : brez izpisov v besedilnem načinu");
        System.err.println("    -d <datoteka> : po koncu vsake partije dodaj njen potek v podano datoteko");
    }

    /**
     * Vrne polno ime razreda za podano kratko ime ra"cunalni"skega igralca.
     * Na primer, za ime Nakljucko vrne ime s12345678.Stroj_Nakljucko.
     */
    private static String polnoImeRazreda(String ime) {
        // "ce je ime "ze polno, vrni kar isto ime
        if (ime.matches("s\\d{8}.Stroj_.\\w*")) {
            return ime;
        }

        // sicer v podimenikih oblike sXXXXXXXX poi"s"ci datoteko
        // Stroj_<ime>.class
        File imenik = new File(Herc.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File[] podimeniki = imenik.listFiles((im, dat) -> dat.matches("s\\d{8}"));
        List<File> ujemajociPodimeniki = Stream.of(podimeniki).
            filter(podimenik -> new File(podimenik, "Stroj_" + ime + ".class").exists()).
            collect(Collectors.toList());
        if (ujemajociPodimeniki.size() == 0) {
            throw new RuntimeException(
                    String.format("Ne najdem datoteke %s/sXXXXXXXX/Stroj_%s.class%n(XXXXXXXX: 8-mestna vpisna številka)", imenik, ime));
        }
        if (ujemajociPodimeniki.size() >= 2) {
            throw new RuntimeException(
                    String.format("Obstaja več datotek %s/sXXXXXXXX/Stroj_%s.class%n(XXXXXXXX: 8-mestna vpisna številka)", imenik, ime));
        }

        return String.format("%s.Stroj_%s", ujemajociPodimeniki.get(0).getName(), ime);
    }

    /**
     * Vrne true, "ce se seansa odvija v besedilnem na"cinu.
     */
    public boolean besedilniVmesnik() {
        return this.parametri.besedilniVmesnik;
    }

    /**
     * Vrne true natanko v primeru, "ce naj bodo karte podanega igralca na
     * kartni plo"s"ci prikazane kot skrite.
     * @param igralec indeks igralca
     */
    public boolean skriteKarte(int igralec) {
        return this.parametri.skrivanjeKart && this.igralci[igralec].jeRacunalnik();
    }

    /**
     * Inicializira seanso.
     */
    public void inicializiraj() {
        for (Igralec igralec: this.igralci) {
            igralec.inicializiraj();
        }
        this.indeksPartije = 0;
        this.pricnePartijo = this.parametri.prviNaPotezi;
    }

    /**
     * Pri"cne naslednjo partijo in vrne referenco na objekt, ki hrani njeno
     * stanje.
     */
    public Partija naslednja() {
        // "ce smo v besedilnem na"cinu, preveri, ali smo "ze dosegli ciljno
        // "stevilo partij
        if (this.besedilniVmesnik() && this.indeksPartije >= this.parametri.skupnoSteviloPartij) {
            return null;
        }

        if (this.indeksPartije > 0) {
            this.println();
        }
        this.println("============");
        this.printf(" Partija %d%n", indeksPartije + 1);
        this.println("============");
        this.println();

        // pri"cni partijo
        this.partija = new Partija(this, this.igralci, () -> naslednja_poKoncuPartije());
        Razporeditev razporeditev = this.razporeditve.isEmpty() ?
            Razporeditev.nakljucna(this.nakljucniGenerator) :
            this.razporeditve.get(indeksPartije % this.razporeditve.size());

        this.partija.pricni(this.pricnePartijo, razporeditev);
        return this.partija;
    }

    /**
     * Ta metoda se pokli"ce po zaklju"cku partije.  Metoda zapi"se podatke o
     * partiji v dnevnik in izvr"si priprave na naslednjo partijo.
     */
    public void naslednja_poKoncuPartije() {
        this.vDnevnik();
        this.pricnePartijo = (this.pricnePartijo + 1) % Konstante.ST_IGRALCEV;
        this.indeksPartije++;
    }

    /**
     * Vrne indeks igralca, ki je v trenutni partiji trenutno na potezi.
     */
    public int kdoNaPotezi() {
        return this.partija.kdoNaPotezi();
    }

    public boolean tihiNacin() {
        return this.parametri.tiho;
    }

    /**
     * Vrne mno"zico kart, ki jih je v trenutni partiji v danem trenutku
     * mogo"ce odvre"ci.
     */
    public MnozicaKart veljavnePoteze() {
        return this.partija.veljavnePoteze();
    }

    /**
     * V besedilnem na"cinu izpi"se svoj argument in sko"ci v naslednjo
     * vrstico, v grafi"cnem pa ne stori ni"cesar.
     */
    public void println(Object argument) {
        if (this.parametri.besedilniVmesnik && !this.parametri.tiho) {
            System.out.println(argument);
        }
    }

    /**
     * V besedilnem na"cinu sko"ci v naslednjo vrstico, v grafi"cnem pa ne
     * stori ni"cesar.
     */
    public void println() {
        if (this.parametri.besedilniVmesnik && !this.parametri.tiho) {
            System.out.println();
        }
    }

    /**
     * V besedilnem na"cinu izpi"se svoj argument, v grafi"cnem pa ne stori
     * ni"cesar.
     */
    public void print(Object argument) {
        if (this.parametri.besedilniVmesnik && !this.parametri.tiho) {
            System.out.print(argument);
        }
    }

    /**
     * V besedilnem na"cinu izpi"se svoj argument (na enak na"cin kot metoda
     * PrintStream.printf), v grafi"cnem pa ne stori ni"cesar.
     */
    public void printf(String format, Object... argumenti) {
        if (this.parametri.besedilniVmesnik && !this.parametri.tiho) {
            System.out.printf(format, argumenti);
        }
    }

    /**
     * "Ce pi"semo v dnevnik, zapi"se vanj podatke o pravkar odigrani partiji,
     * sicer pa ne stori ni"cesar.
     */
    public void vDnevnik() {
        if (this.parametri.dnevniskaDatoteka != null) {
            try {
                PrintWriter tok = new PrintWriter(new BufferedWriter(
                            new FileWriter(this.parametri.dnevniskaDatoteka, true)));
                tok.println("<<<");
                tok.printf("# Partija %d%n", this.indeksPartije + 1);
                this.partija.vDnevnik(tok);
                tok.printf("# Skupne točke: %s%n", 
                        Arrays.toString(
                            Arrays.stream(this.igralci).map(Igralec::vrniTocke).toArray()));
                tok.println(">>>");
                tok.println();
                tok.close();
            } catch (IOException ex) {
                System.err.println("Težave pri pisanju v datoteko " + this.parametri.dnevniskaDatoteka);
            }
        }
    }

}
