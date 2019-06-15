
package ogrodje; 

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

/**
 * Vsebuje razne stati"cne drobnarije.
 */
public class Razno {

    /**
     * Seznam konstant, ki predstavljajo poravnavo besedila znotraj okvir"cka.
     */
    public static enum Poravnava {
        LEVA, DESNA, SREDINSKA;
    }

    /** tipi"cno razmerje med fm.getHeight() in fm.getAscent()
     * (fm je objekt razreda FontMetrics) */
    public static final double R_FONT_HEIGHT_ASCENT = 1.25;

    /** razmerje med "sirino statusne plo"s"ce in polovico "sirine krovne
     * plo"s"ce */
    public static final double R_W_STATUSNA_KROVNA = 0.975;

    /** predpostavljena maksimalna dol"zina imena (brez predpone
     * sXXXXXXXX.Stroj_) */
    public static final int MAKS_DOLZINA_IMENA = 16;

    /** konstanta, ki predstavlja neveljaven "cas */
    public static final long NEVELJAVEN_CAS = Long.MIN_VALUE;

    /** univerzalna pisava v programu (velikost se lahko spreminja) */
    private static Font s_pisava = new Font("SansSerif", Font.PLAIN, 12);

    /** splo"sna barva pisave */
    public static final Color B_OSNOVNA_PISAVA = new Color(208, 208, 208);

    /** splo"sna barva ozadja */
    public static final Color B_SPLOSNO_OZADJE = new Color(0, 0, 64);

    /** barva ozadja sredinske plo"s"ce */
    public static final Color B_SPLOSNO_OZADJE_SREDINA = new Color(0, 0, 32);

    /** ozadje plo"s"c za igralca 0 in 2 */
    private static final Color B_OZADJE_IGRALCA_02 = new Color(64, 0, 0);

    /** ozadje plo"s"c za igralca 1 in 3 */
    private static final Color B_OZADJE_IGRALCA_13 = new Color(0, 64, 0);

    /** B_OZADJE_IGRALCA[i] = ozadje plo"s"c za igralca i */
    public static final Color[] B_OZADJE_IGRALCA = {
        B_OZADJE_IGRALCA_02, B_OZADJE_IGRALCA_13,
        B_OZADJE_IGRALCA_02, B_OZADJE_IGRALCA_13
    };

    /** 
     * Vrne celo "stevilo, ki je najbli"zje "stevilu x.
     */
    public static int ri(double x) {
        return (int) Math.round(x);
    }

    /**
     * Vrne univerzalno pisavo v programu.
     */
    public static Font vrniPisavo() {
        return s_pisava;
    }

    /**
     * Nastavi velikost univerzalne pisave na podano velikost.
     */
    public static void nastaviVelikostPisave(double velikost) {
        s_pisava = s_pisava.deriveFont((float) velikost);
    }

    /**
     * V podanem grafi"cnem kontekstu vklju"ci antialiasing.
     */
    public static void nastaviKakovostRisanja(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Na sredino podanega okvirja nari"se podani napis.
     */
    public static void izdelajNapis(Graphics g, String napis, Rectangle2D okvir) {
        izdelajNapis(g, napis, okvir, Poravnava.SREDINSKA);
    }

    /**
     * V podani okvir nari"se podani napis.
     * @param poravnava poravnava napisa znotraj okvirja (leva, desna ali
     *    sredinska)
     */
    public static void izdelajNapis(Graphics g, String napis, 
            Rectangle2D okvir, Poravnava poravnava) {

        FontMetrics fm = g.getFontMetrics();
        double wNapis = (double) fm.stringWidth(napis);
        double hNapis = (double) fm.getAscent();

        double x = okvir.getX();
        switch (poravnava) {
            case SREDINSKA:
                x += (okvir.getWidth() - wNapis) / 2;
                break;

            case DESNA:
                x += okvir.getWidth() - wNapis;
                break;
        }
        double y = okvir.getY() + (okvir.getHeight() + hNapis) / 2;
        g.drawString(napis, ri(x), ri(y));
    }
}
