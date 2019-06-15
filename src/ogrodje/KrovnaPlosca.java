
package ogrodje;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import skupno.Karta;
import skupno.Konstante;
import skupno.MnozicaKart;
import static ogrodje.Razno.ri;

/**
 * Objekt tega razreda predstavlja plo"s"co, ki vsebuje vse ostale plo"s"ce.
 *
 * Vsebovalna hierarhija:
 *
 * <pre>
 * KrovnaPlosca {
 *     KartnaPlosca [x Konstante.ST_IGRALCEV],
 *     StatusnaPlosca [x Konstante.ST_IGRALCEV],
 *     OsrednjaPlosca {
 *         CardLayout(OsrednjaPloscaStih, OsrednjaPloscaRezultati)  // dve pojavni obliki
 *     }
 * }
 * </pre>
 */
public class KrovnaPlosca extends JPanel {

    /** razmerje med vi"sino statusne plo"s"ce in krovne plo"s"ce */
    private static final double R_H_STATUSNA_KROVNA = 0.05;

    /** razmerje med vi"sino kartne plo"s"ce in krovne plo"s"ce */
    private static final double R_H_KARTNA_KROVNA = 0.23;

    /** razmerje med vi"sino karte in krovne plo"s"ce */
    private static final double R_H_KARTA_KROVNA = R_H_KARTNA_KROVNA;

    /** razmerje med vi"sino pisave in krovne plo"s"ce */
    private static final double R_H_PISAVA_KROVNA = 0.03;

    /** okno, na katero je postavljena plo"s"ca this */
    private JFrame starsevskoOkno;

    /** plo"s"ce, ki prikazujejo karte posameznih igralcev */
    private KartnaPlosca[] kartnePlosce;

    /** plo"s"ce, ki prikazujejo imena igralcev in druge statusne podatke */
    private StatusnaPlosca[] statusnePlosce;

    /** plo"s"ca, ki prikazuje trenutni "stih in podatke o igralcih */
    private OsrednjaPlosca osrednjaPlosca;

    /**
     * Izdela objekt, ki predstavlja krovno plo"s"co.
     */
    public KrovnaPlosca(JFrame starsevskoOkno) {
        this.starsevskoOkno = starsevskoOkno;

        this.setBackground(Razno.B_SPLOSNO_OZADJE);

        // lege in velikosti komponent bomo dolo"cali ro"cno
        this.setLayout(null);

        // ustvari in dodaj komponente
        this.kartnePlosce = new KartnaPlosca[] {
            new KartnaPlosca(0),   // spodaj levo
            new KartnaPlosca(1),   // zgoraj levo
            new KartnaPlosca(2),   // zgoraj desno
            new KartnaPlosca(3),   // spodaj desno
        };
        this.statusnePlosce = new StatusnaPlosca[] {
            new StatusnaPlosca(0),  // spodaj levo
            new StatusnaPlosca(1),  // zgoraj levo
            new StatusnaPlosca(2),  // zgoraj desno
            new StatusnaPlosca(3),  // spodaj desno
        };
        this.osrednjaPlosca = new OsrednjaPlosca();

        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.add(this.kartnePlosce[i]);
            this.add(this.statusnePlosce[i]);
        }
        this.add(this.osrednjaPlosca);

        // ko se nastavi/spremeni velikost krovne plo"s"ce,
        // nastavi/spremeni tudi velikosti podplo"s"c
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                KrovnaPlosca kp = KrovnaPlosca.this;
                double wKrovna = (double) kp.getWidth();
                double hKrovna = (double) kp.getHeight();
                SlikeKart.nastaviVisinoKarte(hKrovna * R_H_KARTA_KROVNA);
                Razno.nastaviVelikostPisave(hKrovna * R_H_PISAVA_KROVNA);

                double wKartna = wKrovna / 2;
                double hKartna = hKrovna * R_H_KARTNA_KROVNA;
                double wStatusna = wKartna;
                double hStatusna = hKrovna * R_H_STATUSNA_KROVNA;
                double hOboje = hStatusna + hKartna;

                kp.statusnePlosce[0].setBounds(0, ri(hKrovna - hStatusna), ri(wStatusna), ri(hStatusna));
                kp.statusnePlosce[1].setBounds(0, 0, ri(wStatusna), ri(hStatusna));
                kp.statusnePlosce[2].setBounds(ri(wStatusna), 0, ri(wStatusna), ri(hStatusna));
                kp.statusnePlosce[3].setBounds(ri(wStatusna), ri(hKrovna - hStatusna), ri(wStatusna), ri(hStatusna));

                kp.kartnePlosce[0].setBounds(0, ri(hKrovna - hOboje), ri(wStatusna), ri(hKartna));
                kp.kartnePlosce[1].setBounds(0, ri(hStatusna), ri(wStatusna), ri(hKartna));
                kp.kartnePlosce[2].setBounds(ri(wStatusna), ri(hStatusna), ri(wStatusna), ri(hKartna));
                kp.kartnePlosce[3].setBounds(ri(wStatusna), ri(hKrovna - hOboje), ri(wStatusna), ri(hKartna));

                kp.osrednjaPlosca.setBounds(0, ri(hOboje), ri(wKrovna), ri(hKrovna - 2 * hOboje));
            }
        });
    }

    /**
     * Podplo"s"cam po"slje sporo"cilo, naj se inicializirajo.
     */
    public void inicializiraj() {
        for (KartnaPlosca kartnaPlosca: this.kartnePlosce) {
            kartnaPlosca.inicializiraj();
        }
        for (StatusnaPlosca statusnaPlosca: this.statusnePlosce) {
            statusnaPlosca.inicializiraj();
        }
        this.osrednjaPlosca.inicializiraj();
    }

    /**
     * Podplo"s"cam posreduje ustrezne podatke o partiji.
     */
    public void posredujPartijo(Partija partija) {
        Igralec[] igralci = partija.vrniIgralce();
        Razporeditev razporeditev = partija.vrniRazporeditev();

        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.statusnePlosce[i].nastaviIme(igralci[i].vrniKratkoIme());
            this.kartnePlosce[i].nastaviKarte(razporeditev.vrniKarte(i));
        }
        this.osrednjaPlosca.prikaziStih();
    }

    /**
     * Kartni plo"s"ci, ki pripada igralcu na potezi, po"slje sporo"cilo, naj
     * omogo"ci izbiro poteze.
     */
    public void omogociIzbiroPoteze(int naPotezi, MnozicaKart veljavnePoteze, 
            Consumer<Karta> pokliciPoIzbiri) {

        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            if (i == naPotezi) {
                this.kartnePlosce[i].omogociIzbiroPoteze(veljavnePoteze, pokliciPoIzbiri);
            } else {
                this.kartnePlosce[i].omogociIzbiroPoteze(MnozicaKart.PRAZNA, null);
            }
        }
    }

    /**
     * Na statusnih plo"s"cah nastavi "stevilo teko"cih to"ck na 0.
     */
    public void posodobiGuiObPricetkuPartije() {
        for (StatusnaPlosca plosca: this.statusnePlosce) {
            plosca.ponastaviTekoceTocke();
        }
    }

    /**
     * Po"cisti osrednjo plo"s"co ob pri"cetku "stiha.
     * @param zacetnikStiha indeks igralca, ki pri"cne "stih
     */
    public void posodobiGuiObPricetkuStiha(int zacetnikStiha) {
        this.osrednjaPlosca.novStih(zacetnikStiha);
    }

    /**
     * Tik pred pri"cetkom poteze osvetli statusno plo"s"co igralca, ki ima
     * potezo.
     * @param naPotezi indeks igralca na potezi
     */
    public void posodobiGuiPredPotezo(int naPotezi) {
        this.statusnePlosce[naPotezi].osvetli(true);
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

        Runnable post = () -> {
            this.statusnePlosce[kdoJeVrgel].osvetli(false);
            this.kartnePlosce[kdoJeVrgel].onemogociIzbiroPoteze();
            this.kartnePlosce[kdoJeVrgel].odstraniKarto(karta);
            this.osrednjaPlosca.dodajKarto(karta);
            postFunkcija.run();
        };

        if (jeRacunalnik) {
            // po ra"cunalnikovi potezi po"cakamo
            // AnimacijskeKonstante.PO_RACUNALNIKOVI_POTEZI milisekund
            Timer casovnik = new Timer(
                    AnimacijskeKonstante.vrni(AnimacijskeKonstante.PO_RACUNALNIKOVI_POTEZI), 
                    (e) -> post.run());
            casovnik.setRepeats(false);
            casovnik.start();
        } else {
            post.run();
        }
    }

    /**
     * Na statusni plo"s"ci dobitnika "stiha prika"ze dobitek.
     */
    public void posodobiGuiPoStihu(int dobitnik, int dobitek) {
        this.statusnePlosce[dobitnik].povecajTekoceTocke(dobitek);
    }

    /**
     * Spro"zi `animacijo' ob zaklju"cku "stiha.
     */
    public void animirajZakljucekStiha(Runnable pokliciPoAnimaciji) {
        this.osrednjaPlosca.animirajZakljucekStiha(pokliciPoAnimaciji);
    }

    /**
     * Na osrednji plo"s"ci prika"ze rezultate partije in skupne to"cke.
     * Posodobi tudi statusne plo"s"ce.
     * @param imenaIgralcev imenaIgralcev[i] = ime igralca i
     * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je igralec i
     *    dobil v tej partiji
     * @param skupneTocke skupneTocke[i] = skupne to"cke, ki jih igralec i
     *    trenutno ima
     */
    public void posodobiGuiPoPartiji(String[] imenaIgralcev, int[] dobljeneTocke, int[] skupneTocke) {
        this.osrednjaPlosca.prikaziRezultate(imenaIgralcev, dobljeneTocke, skupneTocke);
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            this.statusnePlosce[i].nastaviSkupneTocke(skupneTocke[i]);
        }
    }

    /**
     * Vrne true, "ce je GUI dokon"cno vzpostavljen.  To se zgodi, ko se
     * izvr"si paintComponent na vseh kartnih plo"s"cah.
     */
    public boolean jeGuiVzpostavljen() {
        return this.kartnePlosce[0].jeGuiVzpostavljen() &&
            this.kartnePlosce[1].jeGuiVzpostavljen() &&
            this.kartnePlosce[2].jeGuiVzpostavljen() &&
            this.kartnePlosce[3].jeGuiVzpostavljen();
    }

    /**
     * Pokli"ce podano funkcijo, ko je GUI dokon"cno vzpostavljen.
     */
    public void pokliciKoBoGuiVzpostavljen(Runnable funkcija) {
        if (this.jeGuiVzpostavljen()) {
            funkcija.run();

        } else {
            // "ce GUI "se ni vzpostavljen, po"cakamo, da bo
            final Timer casovnik = new Timer(50, null);

            casovnik.addActionListener(new ActionListener() {
                // da prepre"cimo morebitne ve"ckratne klice
                boolean poklical = false;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (this.poklical) {
                        return;
                    }
                    if (KrovnaPlosca.this.jeGuiVzpostavljen()) {
                        casovnik.stop();
                        this.poklical = true;
                        funkcija.run();
                    }
                }
            });
            casovnik.start();
        }
    }

    /**
     * Posodobi prikaz razpolo"zljivega "casa za podanega igralca.
     * @param igralec polo"zaj (indeks) igralca (0, 1, 2 ali 3)
     * @param casDoKonca "cas v milisekundah, ki ga ima igralec na voljo do
     *    konca partije
     */
    public void posodobiPrikazCasa(int igralec, long casDoKonca) {
        this.statusnePlosce[igralec].nastaviCas(casDoKonca);
    }

    /**
     * Prika"ze obvestilo o neveljavni potezi.
     * @param igralec igralec, ki je izbral neveljavno potezo
     * @param karta karta, ki jo je izbral
     */
    public void obvestiONeveljavniPotezi(Igralec igralec, Karta karta) {
        JOptionPane.showMessageDialog(this.starsevskoOkno,
                String.format("Igralec %d (%s) je izbral neveljavno potezo (%s).", 
                              igralec.vrniPolozaj(), igralec.vrniKratkoIme(), karta),
                "Pozor!",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Prika"ze obvestilo o prekora"citvi "casovne omejitve.
     * @param igralec igralec, ki je prekora"cil "casovno omejitev
     */
    public void obvestiOPrekoracitviCasa(Igralec igralec) {
        JOptionPane.showMessageDialog(this.starsevskoOkno,
                String.format("Igralec %d (%s) je prekoračil časovno omejitev.", 
                              igralec.vrniPolozaj(), igralec.vrniKratkoIme()),
                "Pozor!",
                JOptionPane.WARNING_MESSAGE);
    }
}
