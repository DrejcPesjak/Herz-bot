
package ogrodje;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.function.DoubleUnaryOperator;
import javax.swing.JPanel;
import javax.swing.Timer;

import skupno.Karta;
import skupno.Konstante;
import static ogrodje.Razno.ri;

/**
 * Objekt tega razreda predstavlja plo"s"co za prikaz teko"cega "stiha.  Ta
 * plo"s"ca je ena od `kartic' (elementov razporejevalnika CardLayout)
 * na osrednji plo"s"ci.
 */
public class OsrednjaPloscaStih extends JPanel {

    /** razmerje med koli"cino R in vi"sino plo"s"ce, kjer je R razmik med
     * vrhom (oz.  dnom) plo"s"ce in zgornjima (oz. spodnjima) kartama v
     * "stihu */
    private static final double R_ROB_VISINA = 0.025;

    /** razmerje med odmikom karte od sredine plo"s"ce v smeri x in "sirino
     * karte */
    private static final double REL_ODMIK_KARTE_X = 0.50;

    /** razmerje med odmikom karte od sredine plo"s"ce v smeri y in vi"sino
     * karte */
    private static final double REL_ODMIK_KARTE_Y = 0.50;

    /** pri animaciji pobiranja "stiha: razmerje med premikom "stiha v smeri
     * dobitnika "stiha in kraj"so stranico plo"s"ce */
    private static final double R_PREMIK_STIHA_PLOSCA = 0.25;

    /** teko"ci "stih, prikazan na plo"s"ci this */
    private Stih stih;

    /** igralec, ki je pravkar pobral "stih (pomembno pri animaciji "stiha) */
    private int animiranDobitnikStiha;

    /**
     * Ustvari objekt, ki predstavlja plo"s"co.
     */
    public OsrednjaPloscaStih() {
        this.setBackground(Razno.B_SPLOSNO_OZADJE_SREDINA);
        this.stih = null;
        this.animiranDobitnikStiha = -1;
    }

    /** Ne naredi ni"cesar. */
    public void inicializiraj() {
    }

    /**
     * Po"cisti plo"s"co.
     */
    private void pocisti() {
        this.stih = new Stih(0);
        this.repaint();
    }

    /**
     * Po"cisti plo"s"co in nastavi za"cetnika "stiha.
     * @param zacetnik indeks igralca, ki pri"cne "stih
     */
    public void novStih(int zacetnik) {
        this.stih = new Stih(zacetnik);
        this.repaint();
    }

    /**
     * Doda karto v "stih in na plo"s"co.
     */
    public void dodajKarto(Karta karta) {
        this.stih.dodaj(karta);
        if (this.stih.steviloKart() < Konstante.ST_IGRALCEV) {
            this.stih.naprej();
        }
        this.repaint();
    }

    /**
     * Izvede `animacijo' pobiranja "stiha.
     * @param pokliciPoAnimaciji funkcija, ki se pokli"ce po zaklju"cku
     *    animacije
     */
    public void animirajZakljucekStiha(Runnable pokliciPoAnimaciji) {
        // ``animiraj'' dobitek "stiha
        Timer casovnik = new Timer(AnimacijskeKonstante.vrni(AnimacijskeKonstante.PRED_PREMIKOM_STIHA), (e) -> {
            this.animiranDobitnikStiha = this.stih.dobitnik();

            // ``animiraj'' pospravljanje "stiha
            Timer casovnik2 = new Timer(AnimacijskeKonstante.vrni(AnimacijskeKonstante.PRED_ZAPRTJEM_STIHA), (e2) -> {
                this.animiranDobitnikStiha = -1;
                this.pocisti();
                pokliciPoAnimaciji.run();
            });
            casovnik2.setRepeats(false);
            casovnik2.start();
            this.repaint();
        });
        casovnik.setRepeats(false);
        casovnik.start();
    }

    /**
     * Nari"se vsebino plo"s"ce.
     * @param pokliciPoAnimaciji funkcija, ki se pokli"ce po zaklju"cku
     *    animacije
     */
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Razno.nastaviKakovostRisanja(g);
        super.paintComponent(g);

        if (this.stih == null || this.stih.steviloKart() == 0)  {
            return;
        }

        double wPlosca = (double) this.getWidth();
        double hPlosca = (double) this.getHeight();
        double wKarta = SlikeKart.vrniSirinoKarte();
        double hKarta = SlikeKart.vrniVisinoKarte();

        double xSredina = (wPlosca - wKarta) / 2;
        double ySredina = (hPlosca - hKarta) / 2;

        // dolo"ci polo"zaje kart v "stihu
        double[] xKarta = {
            xSredina - REL_ODMIK_KARTE_X * wKarta,
            xSredina - REL_ODMIK_KARTE_X * wKarta,
            xSredina + REL_ODMIK_KARTE_X * wKarta,
            xSredina + REL_ODMIK_KARTE_X * wKarta,
        };

        double hRob = R_ROB_VISINA * hPlosca;
        DoubleUnaryOperator omeji = (h) ->
            Math.max(hRob, Math.min(hPlosca - hKarta - hRob, h));

        double[] yKarta = {
            omeji.applyAsDouble(ySredina + REL_ODMIK_KARTE_Y * hKarta),
            omeji.applyAsDouble(ySredina - REL_ODMIK_KARTE_Y * hKarta),
            omeji.applyAsDouble(ySredina - REL_ODMIK_KARTE_Y * hKarta),
            omeji.applyAsDouble(ySredina + REL_ODMIK_KARTE_Y * hKarta),
        };

        if (this.animiranDobitnikStiha >= 0) {
            // uporabi koordinatno transformacijo, da prika"ze"s "stih na
            // premaknjeni lokaciji
            double premik = R_PREMIK_STIHA_PLOSCA * Math.min(wPlosca, hPlosca);
            double x = premik;
            double y = premik;
            double[] dx = {-x, -x, x, x};
            double[] dy = {y, -y, -y, y};
            g.setTransform(AffineTransform.getTranslateInstance(
                        dx[this.animiranDobitnikStiha], dy[this.animiranDobitnikStiha]));
        }

        // nari"si karte, ki tvorijo "stih
        int i = this.stih.zacetnik();
        for (Karta karta: this.stih) {
            Image slikaKarte = SlikeKart.pridobiSliko(karta, false);
            g.drawImage(slikaKarte, ri(xKarta[i]), ri(yKarta[i]), null);
            i = (i + 1) % Konstante.ST_IGRALCEV;
        }
    }
}
