
package ogrodje;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.stream.IntStream;
import javax.swing.JPanel;

import skupno.Konstante;
import static ogrodje.Razno.ri;

/**
 * Objekt tega razreda predstavlja plo"s"co za prikaz rezultatov pravkar
 * zaklju"cene partije.  Ta plo"s"ca je ena od `kartic' (elementov
 * razporejevalnika CardLayout) na osrednji plo"s"ci.
 */
public class OsrednjaPloscaRezultati extends JPanel implements MouseListener, MouseMotionListener {

    /** razmerje med vi"sino tabele in vi"sino plo"s"ce */
    private static final double R_H_TABELA_PLOSCA = 0.9;

    /** razmik med stolpci tabele */
    private static final String STR_RAZMIK = "   ";

    /** napis na gumbu za pri"cetek nove partije */
    private static final String GUMB_NOVA_IGRA_NAPIS = "Nova igra";

    /** barva ozadja gumba */
    private static final Color B_OZADJE_GUMB = Color.LIGHT_GRAY;

    /** barva napisa na gumbu */
    private static final Color B_NAPIS_GUMB = Color.BLACK;

    /** barva napisa na gumbu, ko je mi"ska nad njim */
    private static final Color B_NAPIS_GUMB_OSVETLJEN = Color.BLUE;

    /** { barva sode vrstice tabele, barva lihe vrstice tabele } */
    private static final Color[] B_VRSTICA_TABELE = { new Color(64, 0, 0), new Color(0, 64, 0) };

    /** [i]: ime igralca z indeksom i */
    private String[] imenaIgralcev;

    /** [i]: to"cke, ki jih je igralec z indeksom i prejel v pravkar
     * zaklju"ceni partiji */
    private int[] dobljeneTocke;

    /** [i]: skupno "stevilo to"ck, ki jih je igralec z indeksom i zbral
     * doslej */
    private int[] skupneTocke;

    /** true natanko tedaj, ko smo z mi"sko na gumbu */
    private boolean miskaNadGumbom;

    /** true natanko tedaj, ko je gumb pritisnjen */
    private boolean gumbPritisnjen;

    /** omejevalni okvir gumba */
    private Rectangle2D okvirGumba;

    /**
     * Ustvari objekt, ki predstavlja plo"s"co.
     */
    public OsrednjaPloscaRezultati() {
        this.setBackground(Razno.B_SPLOSNO_OZADJE_SREDINA);
        this.imenaIgralcev = null;
        this.miskaNadGumbom = false;
        this.gumbPritisnjen = false;
        this.okvirGumba = new Rectangle2D.Double();
    }

    /**
     * Nastavi poslu"salca za mi"skine klike in mi"skine premike.
     */
    public void inicializiraj() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Nari"se vsebino plo"s"ce.
     */
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Razno.nastaviKakovostRisanja(g);
        super.paintComponent(g);

        OsrednjaPloscaRezultati np = OsrednjaPloscaRezultati.this;
        if (np.imenaIgralcev == null) {
            return;
        }

        double wPlosca = (double) this.getWidth();
        double hPlosca = (double) this.getHeight();

        // izra"cuna "sirino tabele
        g.setFont(Razno.vrniPisavo());
        FontMetrics fm = g.getFontMetrics();
        int maksDolzinaIndeksa = IntStream.range(0, Konstante.ST_IGRALCEV)
            .map(i -> fm.stringWidth(String.format("[%d]", i))).max().getAsInt();
        int maksDolzinaImena = Arrays.stream(np.imenaIgralcev)
            .mapToInt(fm::stringWidth).max().getAsInt();
        int maksDolzinaDobljenihTock = Arrays.stream(np.dobljeneTocke)
            .map(t -> fm.stringWidth(String.format("+%d", t))).max().getAsInt();
        int maksDolzinaSkupnihTock = Arrays.stream(np.skupneTocke)
            .map(t -> fm.stringWidth(Integer.toString(t))).max().getAsInt();
        int wPresledek = fm.stringWidth(STR_RAZMIK);

        double wTabela = maksDolzinaIndeksa + maksDolzinaImena +
            maksDolzinaDobljenihTock + maksDolzinaSkupnihTock + 5 * wPresledek;
        double hVrstica = R_H_TABELA_PLOSCA * hPlosca / (Konstante.ST_IGRALCEV + 1);
        double hTabela = (Konstante.ST_IGRALCEV + 1) * hVrstica;
        double xTabela = (wPlosca - wTabela) / 2;
        double yTabela = (hPlosca - hTabela) / 2;

        double[] wRazdelek = {
            maksDolzinaIndeksa,
            maksDolzinaImena,
            maksDolzinaDobljenihTock,
            maksDolzinaSkupnihTock,
        };

        double y = yTabela;
        Razno.Poravnava[] poravnave = {
            Razno.Poravnava.SREDINSKA,
            Razno.Poravnava.LEVA,
            Razno.Poravnava.DESNA,
            Razno.Poravnava.DESNA,
        };

        // nari"se tabelo
        for (int i = 0;  i < Konstante.ST_IGRALCEV;  i++) {
            Rectangle2D okvir = new Rectangle2D.Double(xTabela, y, wTabela, hVrstica);
            g.setColor(B_VRSTICA_TABELE[i % 2]);
            g.fill(okvir);

            Rectangle2D[] podokvirji = {
                new Rectangle2D.Double(xTabela + wPresledek, y, wRazdelek[0], hVrstica),
                new Rectangle2D.Double(xTabela + wRazdelek[0] + 2 * wPresledek, y, wRazdelek[1], hVrstica),
                new Rectangle2D.Double(xTabela + wRazdelek[0] + wRazdelek[1] + 3 * wPresledek, y, wRazdelek[2], hVrstica),
                new Rectangle2D.Double(xTabela + wRazdelek[0] + wRazdelek[1] + wRazdelek[2] + 4 * wPresledek, y, wRazdelek[3], hVrstica)
            };
            String[] vsebina = {
                String.format("[%d]", i),
                np.imenaIgralcev[i],
                "+" + Integer.toString(np.dobljeneTocke[i]),
                Integer.toString(np.skupneTocke[i])
            };
            g.setColor(Razno.B_OSNOVNA_PISAVA);
            for (int j = 0;  j < podokvirji.length;  j++) {
                Razno.izdelajNapis(g, vsebina[j], podokvirji[j], poravnave[j]);
            }
            y += hVrstica;
        }

        // nari"se gumb (izgleda kot dodatna vrstica tabele)
        this.okvirGumba = new Rectangle2D.Double(xTabela, y, wTabela, hVrstica);
        g.setColor(B_OZADJE_GUMB);
        int xGumb = ri(this.okvirGumba.getX());
        int yGumb = ri(this.okvirGumba.getY());
        int wGumb = ri(this.okvirGumba.getWidth());
        int hGumb = ri(this.okvirGumba.getHeight());
        g.draw3DRect(xGumb, yGumb, wGumb, hGumb, !this.gumbPritisnjen);
        g.fill3DRect(xGumb, yGumb, wGumb, hGumb, !this.gumbPritisnjen);
        g.setColor(this.miskaNadGumbom ? B_NAPIS_GUMB_OSVETLJEN : B_NAPIS_GUMB);
        Razno.izdelajNapis(g, GUMB_NOVA_IGRA_NAPIS, this.okvirGumba);
    }

    /**
     * Prika"ze rezultate v tabeli.
     * @param imenaIgralcev imenaIgralcev[i] = ime igralca i
     * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je igralec i
     *    dobil v tej partiji
     * @param skupneTocke skupneTocke[i] = skupne to"cke, ki jih igralec i
     *    trenutno ima
     */
    public void prikazi(String[] imenaIgralcev, int[] dobljeneTocke, int[] skupneTocke) {
        this.imenaIgralcev = imenaIgralcev;
        this.dobljeneTocke = dobljeneTocke;
        this.skupneTocke = skupneTocke;
        this.repaint();
    }

    /**
     * Odzove se na pritisk gumba --- pri"cne novo partijo.
     */
    private void odzivNaPritiskGumba() {
        Most.vrni().novaPartija();
    }

    /** Vrne true natanko v primeru, "ce se podana to"cka nahaja v okviru
     * gumba. */
    private boolean jeZnotrajGumba(Point tocka) {
        return this.okvirGumba.contains(tocka);
    }

    /**
     * Odzivnik na premik mi"ske: posodabljamo le osvetlitev.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        this.miskaNadGumbom = this.jeZnotrajGumba(e.getPoint());
        this.repaint();
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
        if (this.jeZnotrajGumba(e.getPoint())) {
            this.gumbPritisnjen = true;
            this.repaint();
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
        if (this.gumbPritisnjen && this.jeZnotrajGumba(e.getPoint())) {
            // spro"zi odziv na gumb
            this.odzivNaPritiskGumba();
        }
        this.gumbPritisnjen = false;
        repaint();
    }
}
