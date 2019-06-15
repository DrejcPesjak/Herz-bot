
package ogrodje;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 * Objekt tega razreda predstavlja statusno plo"s"co.  Statusna plo"s"ca je
 * razdeljena na "stiri razdelke, ki predstavljajo posamezne podatke o
 * pripadajo"cem igralcu.
 * <ul>
 * <li> razpolo"zljivi "cas </li>
 * <li> ime </li>
 * <li> dobitek v trenutni partiji </li>
 * <li> vsota doslej zbranih to"ck </li>
 * </ul>
 */
public class StatusnaPlosca extends JPanel {

    /** razmerja med "sirinami razdelkov */
    private static final double[] RAZMERJA = {
        4.0, (double) Razno.MAKS_DOLZINA_IMENA, 3.0, 6.0
    };

    /** vsota razmerij "sirin razdelkov;
     * dele"z "sirine za razdelek i dobimo kot RAZMERJA[i] / VSOTA_RAZMERIJ */
    private static final double VSOTA_RAZMERIJ =
        RAZMERJA[0] + RAZMERJA[1] + RAZMERJA[2] + RAZMERJA[3];

    /** barva pisave, ko je igralec, ki mu pripada statusna plo"s"ca this, na
     * potezi */
    private static final Color B_OSVETLJENA_PISAVA = Color.YELLOW;

    /** indeks igralca, ki mu pripada statusna plo"s"ca this */
    private int igralec;

    /** "cas v milisekundah (Razno.NEVELJAVEN_CAS, "ce "casa sploh ne merimo) */
    private long cas;

    /** ime igralca */
    private String ime;

    /** to"cke v pobranih "stihih */
    private int tekoceTocke;

    /** skupne to"cke */
    private int skupneTocke;

    /** true natanko tedaj, ko je plo"s"ca osvetljena
     * (to se zgodi takrat, ko je igralec, ki mu plo"s"ca pripada, na potezi) */
    private boolean osvetljena;

    /**
     * Ustvari objekt, ki predstavlja statusno plo"s"co.
     * @param igralec indeks igralca, ki mu pripada kartna plo"s"ca this
     */
    public StatusnaPlosca(int igralec) {
        this.setBackground(Razno.B_SPLOSNO_OZADJE);
        this.igralec = igralec;
        this.cas = Razno.NEVELJAVEN_CAS;
        this.ime = null;
        this.tekoceTocke = 0;
        this.skupneTocke = 0;
        this.osvetljena = false;
    }

    /** 
     * Ne naredi ni"cesar.
     */
    public void inicializiraj() {
    }

    /**
     * "Ce je parameter `da' enak true, osvetli plo"s"co (prika"ze besedilo z
     * drugo barvo), sicer pa odstrani osvetlitev.
     */
    public void osvetli(boolean da) {
        this.osvetljena = da;
        this.repaint();
    }

    /**
     * Nastavi ime igralca in osve"zi plo"s"co.
     */
    public void nastaviIme(String ime) {
        this.ime = ime;
        this.repaint();
    }

    /**
     * Nastavi preostali razpolo"zljivi "cas in osve"zi plo"s"co.
     */
    public void nastaviCas(long cas) {
        this.cas = cas;
        this.repaint();
    }

    /**
     * Nastavi teko"ce to"cke na 0 in osve"zi plo"s"co.
     */
    public void ponastaviTekoceTocke() {
        this.tekoceTocke = 0;
        this.repaint();
    }

    /**
     * Pove"ca teko"ce to"cke za podani dodatek in osve"zi plo"s"co.
     */
    public void povecajTekoceTocke(int dodatek) {
        this.tekoceTocke += dodatek;
        this.repaint();
    }

    /**
     * Nastavi skupne to"cke na podano "stevilo to"ck in osve"zi plo"s"co.
     */
    public void nastaviSkupneTocke(int tocke) {
        this.skupneTocke = tocke;
        this.repaint();
    }

    /**
     * Nari"se vsebino plo"s"ce.
     */
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Razno.nastaviKakovostRisanja(g);
        super.paintComponent(g);

        if (this.ime == null) {
            return;
        }
        g.setFont(Razno.vrniPisavo());

        double wPlosca = (double) this.getWidth();
        double wIzrabljenaPovrsina = Razno.R_W_STATUSNA_KROVNA * wPlosca;
        double hPlosca = (double) this.getHeight();

        Color osnovnoOzadje = Razno.B_OZADJE_IGRALCA[this.igralec];
        Color svetlejseOzadje = osnovnoOzadje.brighter();
        Color temnejseOzadje = osnovnoOzadje;
        Color[] bOzadje = { temnejseOzadje, svetlejseOzadje, temnejseOzadje, svetlejseOzadje, };
        String[] napisi = {
            (this.cas == Razno.NEVELJAVEN_CAS) ? ("-") : (String.format("%.1f", ((double) this.cas) / 1000.0)),
            this.ime,
            Integer.toString(this.tekoceTocke),
            Integer.toString(this.skupneTocke)
        };

        double x = (this.igralec <= 1) ? (0.0) : (wPlosca - wIzrabljenaPovrsina);
        for (int i = 0;  i < RAZMERJA.length;  i++) {
            double w = wIzrabljenaPovrsina * RAZMERJA[i] / VSOTA_RAZMERIJ;
            g.setColor(bOzadje[i]);
            Rectangle2D okvir = new Rectangle2D.Double(x, 0.0, w, hPlosca);
            g.fill(okvir);
            g.setColor(this.osvetljena ? B_OSVETLJENA_PISAVA : Razno.B_OSNOVNA_PISAVA);
            Razno.izdelajNapis(g, napisi[i], okvir);
            x += w;
        }
    }
}
