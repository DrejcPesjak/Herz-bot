
package ogrodje;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.Timer;

import skupno.Karta;
import skupno.Konstante;
import skupno.MnozicaKart;

/**
 * Objekt tega razreda hrani in vzdr"zuje stanje trenutne partije.
 */
public class Partija {

    /** 
     * Objekt tega razreda predstavlja potezo ali zaklju"cek "stiha.
     */
    private static abstract class Dogodek {

        /** indeks igralca, ki je odigral potezo oziroma pobral "stih */
        int akter;

        protected Dogodek(int akter) {
            this.akter = akter;
        }

        @Override
        public String toString() {
            return String.format("[%d]", this.akter);
        }
    }

    /** 
     * Objekt tega razreda predstavlja odigrano potezo.
     */
    private static class Poteza extends Dogodek {

        /** karta, ki jo je akter izigral */
        Karta karta;

        /** trenutek, ko je akter dobil pravico do poteze */
        LocalDateTime zacetek;

        /** trenutek, ko je akter dejansko odigral potezo */
        LocalDateTime konec;

        public Poteza(int akter, Karta karta, LocalDateTime zacetek, LocalDateTime konec) {
            super(akter);
            this.karta = karta;
            this.zacetek = zacetek;
            this.konec = konec;
        }

        @Override
        public String toString() {
            return String.format("- %s %s %s %s",
                    super.toString(), this.karta, this.zacetek, this.konec);
        }
    }

    /** 
     * Objekt tega razreda predstavlja zaklju"cen "stih.
     */
    private static class ZakljucenStih extends Dogodek {

        /** vrednost pravkar zaklju"cenega "stiha */
        int vrednost;

        public ZakljucenStih(int akter, int vrednost) {
            super(akter);
            this.vrednost = vrednost;
        }

        @Override
        public String toString() {
            return String.format("= %s pobral %d", super.toString(), this.vrednost);
        }
    }

    /** seansa, katere sestavni del je partija this */
    private Seansa seansa;

    /** igralci[i]: objekt, ki predstavlja igralca z indeksom i */
    private Igralec[] igralci;

    /** indeks igralca, ki pri"cne partijo */
    private int zacetnikPartije;

    /** razporeditev kart v partiji */
    private Razporeditev razporeditev;

    /** trenutek, ko je igralec na potezi dobil pravico do poteze */
    private LocalDateTime trenutekPricetkaPoteze;

    /** zaporedna "stevilka trenutnega "stiha */
    private int stevilkaStiha;

    /** trenutni "stih */
    private Stih stih;

    /** "casovnik, v katerem posodabljamo "cas, ki ga ima igralec na potezi
     * "se na voljo (samo v grafi"cnem na"cinu) */
    private Timer casovnikZaPreostaliCas;

    /** funkcija, ki naj se pokli"ce po koncu partije */
    private Runnable seansa_kliciPoKoncuPartije;

    /** dogodki, ki tvorijo partijo */
    private List<Dogodek> dogodki;

    /** izid partije */
    private Izid.Osnova izid;

    /**
     * Izdela objekt tipa Partija.
     * @param seansa seansa, katere del je partija this
     * @param igralci igralci[i]: objekt, ki predstavlja igralca z indeksom i
     * @param seansa_kliciPoKoncuPartije funkcija, ki se pokli"ce po koncu
     *    partije
     */
    public Partija(Seansa seansa, Igralec[] igralci, Runnable seansa_kliciPoKoncuPartije) {
        this.seansa = seansa;
        this.igralci = igralci;
        this.zacetnikPartije = -1;
        this.seansa_kliciPoKoncuPartije = seansa_kliciPoKoncuPartije;
        this.dogodki = new ArrayList<>();
    }

    /**
     * Pri"cne partijo.
     * @param zacetnikPartije indeks igralca, ki pri"cne partijo
     * @param razporeditev razporeditev kart v partiji
     */
    public void pricni(int zacetnikPartije, Razporeditev razporeditev) {
        // "ce smo v grafi"cnem na"cinu, spro"zi "casovnik za spremljanje
        // razpolo"zljivega "casa do konca partije
        if (!Most.vrni().besedilniVmesnik()) {
            this.casovnikZaPreostaliCas = new Timer(
                    AnimacijskeKonstante.LOCLJIVOST_CASOVNIKA,
                    (e) -> this.posodobiPreostaliCas());
            this.casovnikZaPreostaliCas.start();
        }

        this.izid = null;
        this.zacetnikPartije = zacetnikPartije;
        this.stevilkaStiha = 1;
        this.razporeditev = razporeditev;

        // obvesti stroje, da se je pri"cela partija, in vsakemu posreduje
        // mno"zico njegovih kart
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.igralci[i].pricetekPartije(this.razporeditev.vrniKarte(i));
        }

        this.seansa.println("--------");
        this.seansa.printf(" Štih %d%n", this.stevilkaStiha);
        this.seansa.println("--------");
        this.seansa.println();

        this.dogodki.clear();

        // obvesti stroje, da se je pri"cel nov "stih, in jim posreduje indeks
        // igralca, ki odvr"ze prvo karto v "stihu
        this.stih = new Stih(this.zacetnikPartije);
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.igralci[i].pricetekStiha(this.zacetnikPartije);
        }

        // posodobi grafi"cni vmesnik
        Most.vrni().posodobiGuiObPricetkuPartije();
        Most.vrni().posodobiGuiObPricetkuStiha(this.zacetnikPartije);
        Most.vrni().pokliciKoBoGuiVzpostavljen(() -> this.izvrsiNaslednjoPotezo());
    }

    /**
     * Vrne tabelo Konstante.ST_IGRALCEV objektov, ki predstavljajo posamezne
     * igralce.
     */
    public Igralec[] vrniIgralce() {
        return this.igralci;
    }

    /**
     * Vrne razporeditev kart med igralce.
     */
    public Razporeditev vrniRazporeditev() {
        return this.razporeditev;
    }

    /**
     * Omogo"ci "clove"skemu igralcu, da odigra potezo (odvr"ze karto),
     * oziroma obvesti stroj na potezi, naj izbere potezo.
     */
    public void izvrsiNaslednjoPotezo() {
        this.izpisiPredPotezo();

        int naPotezi = this.kdoNaPotezi();
        Most.vrni().posodobiGuiPredPotezo(naPotezi);
        Igralec akter = this.igralci[naPotezi];
        MnozicaKart veljavnePoteze = legalnePoteze(akter, this.stih.zacetnaKarta());
        this.trenutekPricetkaPoteze = LocalDateTime.now();
        akter.izberiPotezo(veljavnePoteze, (karta) -> izvrsiNaslednjoPotezo_poIzbiriKarte(karta));
    }

    /**
     * Ta metoda se pokli"ce po tem, ko igralec izbere karto, ki jo bo
     * odvrgel.  "Ce je karto izbral stroj, potem v tem trenutku "se ne vemo,
     * ali je njegova izbira legalna.
     */
    private void izvrsiNaslednjoPotezo_poIzbiriKarte(Karta karta) {
        // "ce se je partija "ze kon"cala, ne naredi ni"cesar
        if (this.izid != null) {
            return;
        }

        int naPotezi = this.kdoNaPotezi();
        Igralec akter = this.igralci[naPotezi]; 
        if (Most.vrni().besedilniVmesnik() && akter.jeCasOmejen() && akter.vrniCasDoKonca() < 0) {
            this.obravnavajPrekoracitevCasa(naPotezi);
            return;
        }

        // preveri veljavnost odigrane poteze
        MnozicaKart veljavnePoteze = legalnePoteze(akter, this.stih.zacetnaKarta());

        if (!veljavnePoteze.vsebuje(karta)) {
            // poseben primer: izbrana poteza ni veljavna
            this.obravnavajNeveljavnoPotezo(naPotezi, karta);
            return;
        }

        // izbrana poteza je veljavna; dodaj jo v dnevnik
        this.dogodki.add(new Poteza(naPotezi, karta, this.trenutekPricetkaPoteze, LocalDateTime.now()));

        // uveljavi potezo in obvesti ostale igralce o odigrani potezi
        akter.uveljaviPotezo(karta);
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            if (i != naPotezi) {
                this.igralci[i].sprejmiPotezo(naPotezi, karta);
            }
        }
        this.stih.dodaj(karta);

        // posodobi grafi"cni vmesnik
        Most.vrni().posodobiGuiPoPotezi(naPotezi, akter.jeRacunalnik(), karta,
                this::izvrsiNaslednjoPotezo_poPosodobitviGui);
    }

    /**
     * Ta metoda se pokli"ce po tem, ko se po izvr"seni legalni potezi
     * posodobi grafi"cni vmesnik, torej ko se izte"ce "cakalna doba po
     * odigrani strojevi potezi.
     */
    private void izvrsiNaslednjoPotezo_poPosodobitviGui() {
        if (this.stih.konec()) {   // konec trenutnega "stiha
            int dobitnik = this.stih.dobitnik();
            int dobitek = this.stih.vrednost();
            this.dogodki.add(new ZakljucenStih(dobitnik, dobitek));
            this.igralci[dobitnik].dodajDobitke(this.stih.vrednost());
            this.izpisiPoKoncuStiha();
            Most.vrni().posodobiGuiPoStihu(dobitnik, dobitek);
        }

        // pripravi se na nadaljevanje "stiha oziroma za"cetek
        // naslednjega "stiha
        boolean naslednjiStih = this.stih.naprej();

        if (naslednjiStih) {
            // konec "stiha
            Most.vrni().animirajZakljucekStiha(() -> {

                if (this.stevilkaStiha == Konstante.ST_STIHOV) {
                    // regularen konec partije (odigrani so bili vsi "stihi)
                    int sj = this.igralci[0].vrniDobitke() + this.igralci[2].vrniDobitke();
                    int vz = this.igralci[1].vrniDobitke() + this.igralci[3].vrniDobitke();
                    final int PV = Konstante.POLOVICA_VSOTE;
                    int[] tockeObKoncuPartije = null;
                    if (sj > vz) {
                        tockeObKoncuPartije = new int[]{sj - PV, 0, sj - PV, 0};
                    } else {
                        tockeObKoncuPartije = new int[]{0, vz - PV, 0, vz - PV};
                    }
                    this.zakljuci(new Izid.Regularen(tockeObKoncuPartije));

                } else {
                    // partije "se ni konec; pri"cni naslednji "stih
                    this.stevilkaStiha++;
                    this.seansa.println("--------");
                    this.seansa.printf(" Štih %d%n", this.stevilkaStiha);
                    this.seansa.println("--------");
                    this.seansa.println();

                    int zacetnikStiha = this.stih.zacetnik();
                    for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
                        this.igralci[i].pricetekStiha(zacetnikStiha);
                    }
                    Most.vrni().posodobiGuiObPricetkuStiha(zacetnikStiha);
                    this.izvrsiNaslednjoPotezo();
                }
            });

        } else {
            // "stiha "se ni konec; izvr"si naslednjo potezo v "stihu
            this.izvrsiNaslednjoPotezo();
        }
    }

    /**
     * Ta metoda se pokli"ce, ko se partija zaklju"ci s podanim izidom.
     */
    private void zakljuci(Izid.Osnova izid) {
        if (!Most.vrni().besedilniVmesnik()) {
            this.casovnikZaPreostaliCas.stop();
        }
        this.izid = izid;
        int[] dobljeneTocke = izid.vrniDobljeneTocke();
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.igralci[i].dodajTocke(dobljeneTocke[i]);
        }

        this.seansa.println("===== Konec partije =====");
        this.seansa.printf("Točke v tej partiji: %s%n", Arrays.toString(dobljeneTocke));
        this.seansa.printf("Skupne točke: %s%n",
                Stream.of(this.igralci).
                map(Igralec::vrniTocke).
                collect(Collectors.toList()));
        this.seansa.println();

        // obvesti stroje o rezultatu partije
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.igralci[i].rezultat(dobljeneTocke);
        }
        String[] imenaIgralcev = Arrays.stream(this.igralci).map(Igralec::vrniKratkoIme).toArray(String[]::new);
        int[] skupneTocke = Arrays.stream(this.igralci).mapToInt(Igralec::vrniTocke).toArray();

        // posodobi grafi"cni vmesnik
        Most.vrni().posodobiGuiPoPartiji(imenaIgralcev, dobljeneTocke, skupneTocke);
        this.seansa_kliciPoKoncuPartije.run();
    }

    /**
     * Vrne indeks igralca na potezi.
     */
    public int kdoNaPotezi() {
        return this.stih.kdoNaPotezi();
    }

    /**
     * Vrne true natanko v primeru, "ce se je partija "ze zaklju"cila.
     */
    public boolean jeKonec() {
        return (this.izid != null);
    }

    /**
     * Ta metoda se pokli"ce ob vsaki spro"zitvi "casovnika
     * this.casovnikZaPreostaliCas.  Metoda posodobi prikaz preostalega
     * razpolo"zljivega "casa.
     */
    private void posodobiPreostaliCas() {
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            if (this.igralci[i].jeRacunalnik() && this.igralci[i].jeCasOmejen()) {
                long casDoKonca = this.igralci[i].vrniCasDoKonca();
                Most.vrni().posodobiPrikazCasa(i, casDoKonca);
                if (casDoKonca < 0) {
                    this.obravnavajPrekoracitevCasa(i);
                    break;
                }
            }
        }
    }

    /**
     * Ta metoda se pokli"ce pred izbiro poteze.  Izpi"se karte v rokah
     * posameznih igralcev, zaporedno "stevilko "stiha, poteze znotraj "stiha
     * in indeks igralca na potezi.
     */
    private void izpisiPredPotezo() {
        int naPotezi = this.kdoNaPotezi();
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.seansa.printf("Igralec %d: %s%n", i, this.igralci[i].vrniKarte());
        }
        this.seansa.printf("štih %d/%d | poteza %d/%d | na potezi %d [%s]%n",
                this.stevilkaStiha, Konstante.ST_STIHOV,
                this.stih.stevilkaNaslednjeKarte(), Konstante.ST_IGRALCEV,
                naPotezi, this.igralci[naPotezi].vrniKratkoIme());
    }

    /**
     * Ta metoda se pokli"ce po zaklju"cku "stiha.  Izpi"se karte v "stihu,
     * vrednost "stiha, dobitnika "stiha in dosedanje skupne dobitke
     * posameznih igralcev.
     */
    private void izpisiPoKoncuStiha() {
        this.seansa.println("----- Konec štiha -----");
        this.seansa.printf("Štih: %s%n", this.stih);
        this.seansa.printf("Vrednost: %d%n", this.stih.vrednost());
        int dobitnik = this.stih.dobitnik();
        this.seansa.printf("Dobitnik štiha: %d [%s]%n",
                dobitnik, this.igralci[dobitnik].vrniIme());
        this.seansa.printf("Dobitki posameznih igralcev: %s%n", 
                Stream.of(this.igralci).
                map(Igralec::vrniDobitke).
                collect(Collectors.toList()));
        this.seansa.println();
    }

    /**
     * Vrne mno"zico veljavnih potez v trenutni situaciji.
     */
    public MnozicaKart veljavnePoteze() {
        return legalnePoteze(this.igralci[this.kdoNaPotezi()],
            this.stih.steviloKart() == 0 ? null : this.stih.zacetnaKarta());
    }

    /**
     * Vrne mno"zico legalnih potez za igralca `akter'.
     * @param akter  igralec, za katerega i"s"cemo mno"zico legalnih potez
     * @param vodilnaKarta  karta, ki jo je odvrgel za"cetnik trenutnega "stiha
     *                      (null, "ce gre za prvo karto v "stihu)
     */
    private static MnozicaKart legalnePoteze(Igralec akter, Karta vodilnaKarta) {
        MnozicaKart akterjeveKarte = new MnozicaKart(akter.vrniKarte());

        // "ce je akter za"cetnik "stiha, lahko odigra karkoli
        if (vodilnaKarta == null) {
            return akterjeveKarte;
        }

        // "ce ni za"cetnik "stiha, mora slediti barvi
        MnozicaKart karteIsteBarve = akterjeveKarte.karteVBarvi(vodilnaKarta.vrniBarvo());
        if (!karteIsteBarve.jePrazna()) {
            return karteIsteBarve;
        }

        // "ce nima barve, mora odvre"ci aduta
        MnozicaKart aduti = akterjeveKarte.karteVBarvi(Konstante.BARVA_ADUTA);
        if (!aduti.jePrazna()) {
            return aduti;
        }

        // "ce nima niti aduta, lahko odvr"ze karkoli
        return akterjeveKarte;
    }

    /**
     * Odzove se na izbiro neveljavne poteze.
     * @param ixIgralec indeks igralca, ki je izbral neveljavno potezo
     * @param karta izbrana karta
     */
    private void obravnavajNeveljavnoPotezo(int ixIgralec, Karta karta) {
        Igralec akter = this.igralci[ixIgralec];
        this.seansa.printf("Igralec %d (%s) je izbral neveljavno potezo (%s).%n",
                ixIgralec, akter.vrniKratkoIme(), karta);
        Most.vrni().obvestiONeveljavniPotezi(akter, karta);

        int[] tocke = tockeObIzrednemDogodku(ixIgralec);
        this.zakljuci(new Izid.NeveljavnaPoteza(tocke, ixIgralec, karta));
    }

    /**
     * Odzove se na prekora"citev "casa.
     * @param ixIgralec indeks igralca, ki je prekora"cil "cas
     */
    private void obravnavajPrekoracitevCasa(int ixIgralec) {
        Igralec akter = this.igralci[ixIgralec];
        akter.prekiniDelovnoNit();
        this.seansa.printf("Igralec %d (%s) je prekoračil časovno omejitev.%n",
                ixIgralec, akter.vrniKratkoIme());
        int[] tocke = tockeObIzrednemDogodku(ixIgralec);
        this.zakljuci(new Izid.PrekoracitevCasa(tocke, ixIgralec));
        Most.vrni().obvestiOPrekoracitviCasa(akter);
    }

    /**
     * Vrne "stevilo to"ck, ki jih pridobijo igralci ob izrednem dogodku
     * (neveljavni potezi ali prekora"citvi "casovne omejitve).
     * @param akter indeks igralca, ki je povzro"cil dogodek (izbral
     *    neveljavno potezo ali prekora"cil "casovno omejitev)
     * @return rezultat[i]: "stevilo to"ck za igralca z indeksom i
     */
    private static int[] tockeObIzrednemDogodku(int akter) {
        final int PV = Konstante.POLOVICA_VSOTE;
        return (akter == 0 || akter == 2) ?
            (new int[]{0, PV, 0, PV}) :
            (new int[]{PV, 0, PV, 0});
    }

    /**
     * Zapi"se celoten potek partije v obliki dnevnika na podani izhodni tok.
     */
    public void vDnevnik(PrintWriter tok) {
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            tok.printf("# Igralec [%d]: %s%n", i, this.igralci[i]);
        }
        tok.printf("# Razporeditev: %s%n", this.razporeditev);
        for (Dogodek dogodek: this.dogodki) {
            tok.println(dogodek);
        }
        tok.printf("# Izid: %s%n", this.izid);
        tok.printf("# Dobljene točke: %s%n", Arrays.toString(this.izid.vrniDobljeneTocke()));
    }
}
