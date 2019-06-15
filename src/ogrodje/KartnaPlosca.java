
package ogrodje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JPanel;

import skupno.Karta;
import skupno.MnozicaKart;
import static ogrodje.Razno.ri;

public class KartnaPlosca extends JPanel implements MouseListener, MouseMotionListener {

    /** barva obrobe karte, ki jo je v danem trenutku mogo"ce odvre"ci */
    private static final Color B_VELJAVNA_POTEZA = new Color(0, 200, 0);

    /** barva obrobe karte, ki je v danem trenutku ni mogo"ce odvre"ci */
    private static final Color B_NEVELJAVNA_POTEZA = Color.RED;

    /** barva obrobe pritisnjene karte */
    private static final double R_D_PRITISNJENA_W_KARTA = 0.06;

    /** barva obrobe karte pod mi"sko */
    private static final double R_D_PODMISKO_W_KARTA = 0.03;

    /** za koliko je osve"zevalni okvir karte (argument metode repaint) na vse
     * strani ve"cji od omejevalnega */
    private static final int PRIBITEK_OKVIR = 2;

    /** indeks igralca, "cigar karte so prikazane na plo"s"ci this */
    private int igralec;

    /** najprej false; postane true, ko se izvr"si paintComponent */
    private boolean guiVzpostavljen;

    /** karte, ki naj se prika"zejo na plo"s"ci this */
    private MnozicaKart karte;

    /** karte, ki jih je v danem trenutku mogo"ce odvre"ci */
    private MnozicaKart veljavnePoteze;

    /** funkcija, ki se pokli"ce po uporabnikovi izbiri poteze */
    private Consumer<Karta> pokliciPoIzbiri;

    /** .get(karta) = pravokotnik, ki podaja viden del karte `karta' */
    private Map<Karta, Rectangle> karta2vidno;

    /** .get(karta) = pravokotnik, ki podaja celoten omejevalni okvir karte `karta' */
    private Map<Karta, Rectangle> karta2vse;

    /** karta, ki je v danem trenutku pod mi"sko (null, "ce ni nobena) */
    private Karta osvetljenaKarta;

    /** karta, ki je v danem trenutku pritisnjena (null, "ce ni nobena) */
    private Karta pritisnjenaKarta;

    /**
     * Izdela objekt, ki predstavlja kartno plo"s"co.
     * @param igralec indeks igralca, ki mu pripada kartna plo"s"ca this
     */
    public KartnaPlosca(int igralec) {
        this.igralec = igralec;
        this.guiVzpostavljen = false;
        this.setBackground(Razno.B_SPLOSNO_OZADJE);
        this.karte = null;

        this.karta2vidno = null;
        this.karta2vse = null;
        this.osvetljenaKarta = null;
        this.pritisnjenaKarta = null;
    }

    /**
     * Vrne true natanko v primeru, "ce se je vsebina kartne plo"s"ce "ze narisala.
     */
    public boolean jeGuiVzpostavljen() {
        return this.guiVzpostavljen;
    }

    /**
     * Registrira poslu"salca za mi"skine klike in premike.
     */
    public void inicializiraj() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Nastavi mno"zico kart za prikaz in osve"zi plo"s"co.
     */
    public void nastaviKarte(MnozicaKart karte) {
        this.karte = new MnozicaKart(karte);
        this.repaint();
    }

    /**
     * Uporabniku omogo"ci izbiro poteze.
     * @param veljavnePoteze karte, ki jih je mogo"ce odvre"ci 
     *    (te bodo obrobljene z barvo B_VELJAVNA_POTEZA, ostale pa z barvo
     *    B_NEVELJAVNA_POTEZA)
     * @param pokliciPoIzbiri funkcija, ki se pokli"ce po izbiri karte
     *    (to se zgodi, ko uporabnik izpusti mi"skin gumb na izbrani karti)
     */
    public void omogociIzbiroPoteze(MnozicaKart veljavnePoteze, Consumer<Karta> pokliciPoIzbiri) {
        this.veljavnePoteze = veljavnePoteze;
        this.pokliciPoIzbiri = pokliciPoIzbiri;
    }

    /**
     * Onemogo"ci izbiro poteze.  Vse karte, ki se jih uporabnik `dotakne', se
     * obrobijo z barvo B_NEVELJAVNA_POTEZA.
     */
    public void onemogociIzbiroPoteze() {
        this.veljavnePoteze = MnozicaKart.PRAZNA;
    }

    /**
     * Nari"se vsebino plo"s"ce.
     */
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Razno.nastaviKakovostRisanja(g);
        super.paintComponent(g);

        if (this.karte == null) {
            return;
        }

        double wPlosca = (double) this.getWidth();
        double wMaxRoka = Razno.R_W_STATUSNA_KROVNA * wPlosca;
        double hPlosca = (double) this.getHeight();
        int stKart = this.karte.steviloKart();
        this.karta2vidno = new HashMap<>();
        this.karta2vse = new HashMap<>();

        double wKarta = (double) SlikeKart.vrniSirinoKarte();
        double hKarta = (double) SlikeKart.vrniVisinoKarte();

        // razmik med sosednjima kartama (0 pomeni, da se bodo karte ravno
        // dotikale; negativen razmik pomeni, da se bodo karte prekrivale)
        double razmik = Math.min(0, (wMaxRoka - stKart * wKarta) / (stKart - 1));
        double wRoka = wKarta * stKart + razmik * (stKart - 1);
        int faktor = (this.igralec <= 1) ? (0) : (1);
        double x = (wMaxRoka - wRoka) / 2 + faktor * (wPlosca - wMaxRoka);
        double y = (hPlosca - hKarta) / 2;

        boolean skrij = Most.vrni().skriteKarte(this.igralec);

        // nari"si karte
        int ixKarta = 0;
        for (Karta karta: this.karte) {
            g.drawImage(SlikeKart.pridobiSliko(karta, skrij), ri(x), ri(y), null);
            Rectangle vidniPravokotnik = new Rectangle(ri(x), ri(y), ri(wKarta + razmik), ri(hKarta));
            if (ixKarta == stKart - 1) {
                vidniPravokotnik.width = ri(wKarta);
            }
            this.karta2vidno.put(karta, vidniPravokotnik);
            this.karta2vse.put(karta, new Rectangle(ri(x), ri(y), ri(wKarta), ri(hKarta)));
            x += wKarta + razmik;
            ixKarta++;
        }

        if (!skrij && (this.pritisnjenaKarta != null || this.osvetljenaKarta != null)) {
            // nari"si "se osvetljeno karto
            Karta karta = (this.pritisnjenaKarta != null) ? this.pritisnjenaKarta : this.osvetljenaKarta;
            Rectangle okvir = this.karta2vse.get(karta);
            Image slikaKarte = SlikeKart.pridobiSliko(karta, false);
            g.drawImage(slikaKarte, okvir.x, okvir.y, null);

            if (this.veljavnePoteze != null) {
                double dRel = (this.pritisnjenaKarta != null) ?
                    (R_D_PRITISNJENA_W_KARTA) : (R_D_PODMISKO_W_KARTA);
                g.setStroke(new BasicStroke((float) (wKarta * dRel)));
                g.setColor(this.veljavnePoteze.vsebuje(karta) ? B_VELJAVNA_POTEZA : B_NEVELJAVNA_POTEZA);
                g.draw(okvir);
            }
        }
        this.guiVzpostavljen = true;
    }

    /** 
     * Vrne karto, katere vidni del vsebuje podano to"cko.
     */
    public Karta kartaNaTocki(Point tocka) {
        for (Karta karta: this.karte) {
            if (karta2vidno.get(karta).contains(tocka)) {
                return karta;
            }
        }
        return null;
    }

    /** 
     * Osve"zi (repaint) pravokotnik, ki vsebuje podano karto.
     */
    private void osveziKarto(Karta karta) {
        if (karta != null) {
            Rectangle r = new Rectangle(this.karta2vse.get(karta));
            r.grow(PRIBITEK_OKVIR, PRIBITEK_OKVIR);
            this.repaint(r);
        }
    }

    /**
     * Odstrani podano karto s seznama in osve"zi prikaz.
     */
    public void odstraniKarto(Karta karta) {
        this.osvetljenaKarta = null;
        this.pritisnjenaKarta = null;
        this.karte.odstrani(karta);
        this.repaint();
    }

    /**
     * Odzivnik na premik mi"ske: posodabljamo le osvetlitev.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.pritisnjenaKarta != null) {
            return;
        }
        Karta prejsnjaOsvetljena = this.osvetljenaKarta;
        this.osvetljenaKarta = this.kartaNaTocki(e.getPoint());
        if (prejsnjaOsvetljena != this.osvetljenaKarta) {
            if (prejsnjaOsvetljena != null) {
                this.osveziKarto(prejsnjaOsvetljena);
            }
            if (this.osvetljenaKarta != null) {
                this.osveziKarto(this.osvetljenaKarta);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.mouseMoved(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.mouseMoved(e);
    }

    /**
     * Odzivnik na pritisk mi"skinega gumba.  Karto, na kateri je uporabnik
     * pritisnil gumb, ozna"cimo kot `pritisnjeno'.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Karta karta = this.kartaNaTocki(e.getPoint());
        if (karta == null || this.veljavnePoteze == null || !this.veljavnePoteze.vsebuje(karta)) {
            this.pritisnjenaKarta = null;
            return;
        }
        this.pritisnjenaKarta = karta;
        if (this.pritisnjenaKarta != null) {
            this.osveziKarto(this.pritisnjenaKarta);
        }
    }

    /**
     * Odzivnik na izpustitev mi"skinega gumba.  "Ce je karta, na kateri je
     * uporabnik izpustil gumb, ista kot karta, na kateri je uporabnik
     * pritisnil gumb, se to tolma"ci kot izbira poteze; pokli"ce se funkcija
     * this.pokliciPoIzbiri.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.pritisnjenaKarta != null) {
            Karta karta = this.kartaNaTocki(e.getPoint());
            if (karta != null && karta.equals(this.pritisnjenaKarta)) {
                // spro"zi izbiro karte
                this.pokliciPoIzbiri.accept(karta);
            }
            this.osveziKarto(this.pritisnjenaKarta);
            this.pritisnjenaKarta = null;
        }
    }
}
