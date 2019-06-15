
package ogrodje;

import java.util.function.Consumer;

import skupno.Karta;

/**
 * Objekt tega razreda je namenjen komunikaciji med objekti drugih tipov,
 * predvsem med objekti, ki predstavljajo plo"s"ce, in objekti, ki hranijo
 * stanje igre.
 * <p>
 * Objekt razreda Most vsebuje referenci na objekta tipov Seansa in
 * KrovnaPlosca, preko katerih je mogo"ce posredovati sporo"cila drugim
 * objektom.
 */
public class Most {

    /** referenca na objekt, ki hrani stanje seanse (zaporedja partij) */
    private Seansa seansa;

    /** referenca na objekt, ki predstavlja krovno plo"s"co */
    private KrovnaPlosca krovnaPlosca;

    /** eden in edini primerek razreda Most v celotnem sistemu */
    private static Most s_most = null;

    /**
     * Konstruktor; kli"cemo ga lahko le posredno preko metode ustvari.
     */
    private Most(Seansa seansa, KrovnaPlosca krovnaPlosca) {
        this.seansa = seansa;
        this.krovnaPlosca = krovnaPlosca;
    }

    /**
     * Ustvari objekt razreda Most.
     * @param seansa objekt, ki hrani stanje seanse (zaporedja partij)
     * @param krovnaPlosca objekt, ki predstavlja krovno plo"s"co
     */
    public static void ustvari(Seansa seansa, KrovnaPlosca krovnaPlosca) {
        s_most = new Most(seansa, krovnaPlosca);
    }

    /**
     * Vrne eden in edini objekt razreda Most.
     */
    public static Most vrni() {
        return s_most;
    }

    /**
     * Vrne true natanko v primeru, "ce igramo v besedilnem na"cinu.
     */
    public boolean besedilniVmesnik() {
        return this.seansa.besedilniVmesnik();
    }

    /**
     * Vrne true natanko v primeru, "ce naj bodo karte podanega igralca na
     * kartni plo"s"ci prikazane kot skrite.
     * @param igralec indeks igralca
     */
    public boolean skriteKarte(int igralec) {
        return this.seansa.skriteKarte(igralec);
    }

    /**
     * Vrne true natanko v primeru, "ce program te"ce v tihem (besedilnem)
     * na"cinu (stikalo -q ob zagonu programa).
     */
    public boolean tihiNacin() {
        return this.seansa.tihiNacin();
    }

    /**
     * Omogo"ci igralcu na potezi, da izbere karto, ki jo bo odvrgel.
     * @param pokliciPoIzbiri funkcija, ki se pokli"ce po izbiri poteze
     *    (ta funkcija se odzove na igral"cevo izbiro poteze)
     */
    public void omogociIzbiroPoteze(Consumer<Karta> pokliciPoIzbiri) {
        this.krovnaPlosca.omogociIzbiroPoteze(
                this.seansa.kdoNaPotezi(),
                this.seansa.veljavnePoteze(),
                pokliciPoIzbiri
        );
    }

    /**
     * Posodobi grafi"cni vmesnik ob pri"cetku partije.
     */
    public void posodobiGuiObPricetkuPartije() {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posodobiGuiObPricetkuPartije();
        }
    }

    /**
     * Posodobi grafi"cni vmesnik (npr. po"cisti osrednjo plo"s"co) ob pri"cetku "stiha.
     * @param zacetnikStiha indeks igralca, ki pri"cne "stih
     */
    public void posodobiGuiObPricetkuStiha(int zacetnikStiha) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posodobiGuiObPricetkuStiha(zacetnikStiha);
        }
    }

    /**
     * Posodobi grafi"cni vmesnik (npr. osvetli ustrezno statusno plo"s"co)
     * tik pred pri"cetkom poteze.
     * @param naPotezi indeks igralca, ki ima potezo
     */
    public void posodobiGuiPredPotezo(int naPotezi) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posodobiGuiPredPotezo(naPotezi);
        }
    }

    /** 
     * Ta metoda se pokli"ce, ko uporabnik izbere karto, ki jo bo odvrgel.
     * Metoda posodobi grafi"cni vmesnik (npr. odstrani karto s kartne
     * plo"s"ce in jo doda na osrednjo plo"s"co) in pokli"ce funkcijo
     * postFunkcija.
     * @param kdoJeVrgel indeks igralca, ki je izbral potezo
     * @param jeRacunalnik potezo je izbral ra"cunalnik (true) oziroma "clovek
     *    (false)
     * @param karta izbrana karta
     * @param postFunkcija funkcija, ki se pokli"ce po posodobitvi grafi"cnega
     *    vmesnika
     */
    public void posodobiGuiPoPotezi(int kdoJeVrgel, boolean jeRacunalnik, 
            Karta karta, Runnable postFunkcija) {

        if (this.besedilniVmesnik()) {
            postFunkcija.run();
        } else {
            this.krovnaPlosca.posodobiGuiPoPotezi(kdoJeVrgel, jeRacunalnik, karta, postFunkcija);
        }
    }

    /**
     * Ta metoda se pokli"ce takoj po zaklju"cenem "stihu.
     * @param dobitnik indeks igralca, ki je dobil "stih
     * @param dobitek vrednost "stiha
     */
    public void posodobiGuiPoStihu(int dobitnik, int dobitek) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posodobiGuiPoStihu(dobitnik, dobitek);
        }
    }

    /**
     * Izvr"si `animacijo' pobiranja "stiha.
     * @param pokliciPoAnimaciji funkcija, ki se pokli"ce po zaklju"cku
     *    `animacije'
     */
    public void animirajZakljucekStiha(Runnable pokliciPoAnimaciji) {
        if (this.besedilniVmesnik()) {
            pokliciPoAnimaciji.run();
        } else {
            this.krovnaPlosca.animirajZakljucekStiha(pokliciPoAnimaciji);
        }
    }

    /**
     * Posodobi grafi"cni vmesnik po zaklju"cku partije (npr. na osrednji
     * plo"s"ci prika"ze rezultate).
     * @param imenaIgralcev imenaIgralcev[i] = ime igralca i
     * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je igralec i
     *    dobil v tej partiji
     * @param skupneTocke skupneTocke[i] = skupne to"cke, ki jih igralec i
     *    trenutno ima
     */
    public void posodobiGuiPoPartiji(String[] imenaIgralcev, int[] dobljeneTocke, int[] skupneTocke) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posodobiGuiPoPartiji(imenaIgralcev, dobljeneTocke, skupneTocke);
        }
    }

    /**
     * Pri"cne novo partijo.
     */
    public void novaPartija() {
        Partija partija = this.seansa.naslednja();
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posredujPartijo(partija);
        }
    }

    /**
     * Ko je grafi"cni vmesnik vzpostavljen (tj. ko so se poklicale vse metode
     * paintComponent), pokli"ce podano funkcijo.  V besedilnem na"cinu se
     * funkcija nemudoma pokli"ce.
     */
    public void pokliciKoBoGuiVzpostavljen(Runnable funkcija) {
        if (this.besedilniVmesnik()) {
            funkcija.run();
        } else {
            this.krovnaPlosca.pokliciKoBoGuiVzpostavljen(funkcija);
        }
    }

    /**
     * Posodobi prikaz razpolo"zljivega "casa za podanega igralca.
     * @param igralec indeks igralca
     * @param casDoKonca "cas v milisekundah, ki ga ima igralec na voljo do
     *    konca partije
     */
    public void posodobiPrikazCasa(int igralec, long casDoKonca) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.posodobiPrikazCasa(igralec, casDoKonca);
        }
    }

    /**
     * Prika"ze obvestilo o neveljavni potezi.
     */
    public void obvestiONeveljavniPotezi(Igralec igralec, Karta karta) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.obvestiONeveljavniPotezi(igralec, karta);
        }
    }

    /**
     * Prika"ze obvestilo o prekora"citvi "casa.
     */
    public void obvestiOPrekoracitviCasa(Igralec igralec) {
        if (!this.besedilniVmesnik()) {
            this.krovnaPlosca.obvestiOPrekoracitviCasa(igralec);
        }
    }
}
