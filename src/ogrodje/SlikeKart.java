
package ogrodje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import skupno.Karta;
import static ogrodje.Razno.ri;

/**
 * Vsebuje stati"cne metode za dostop do slik kart oziroma za risanje umetnih
 * slik ("ce slike kart ne obstajajo).
 */
public class SlikeKart {

    /** podimenik, ki vsebuje imenike s slikovnimi datotekami (vsak od teh
     * imenikov vsebuje slike dolo"cene velikosti) */
    private static final String IMENIK_SLIKE = "slike";

    /** format imenika, ki vsebuje datoteke s slikami dolo"cene velikosti */
    private static final String FORMAT_IMENA_SLIK = "v%03d";

    /** kon"cnica posameznih slikovnih datotek */
    private static final String SLIKOVNE_DATOTEKE_KONCNICA = ".png";

    /** razmerje med vi"sino in "sirino karte */
    public static final double R_VISINA_SIRINA = 1.5;

    /** najmanj"sa shranjena "sirina kart */
    private static final int MIN_SIRINA = 20;

    /** najve"cja shranjena "sirina kart */
    private static final int MAKS_SIRINA = 400;

    /** korak pove"cevanja shranjene "sirine (razlika med "sirinami slik v
     * zaporednih imenikih) */
    private static final int KORAK_SIRINE = 20;

    /** umetne slike: razmerje med odmikom obrobe od roba karte in "sirino karte */
    private static final double R_ODMIK_OBROBE_SIRINA = 0.01;

    /** umetne slike: razmerje med debelino obrobe karte in "sirino karte */
    private static final double R_DEBELINA_OBROBE_SIRINA = 0.01;

    /** razmerje med debelino "srafure zadnje strani karte in "sirino karte */
    private static final double R_DEBELINA_SRAFURE_SIRINA = 0.005;

    /** umetne slike: razmerje med odmikom napisa od roba karte in "sirino karte */
    private static final double R_ODMIK_NAPISA_SIRINA = 0.02;

    /** razmerje med vodoravnim (ali navpi"cnim) razmakom med sosednjima
     * "srafurnima "crtama in "sirino karte */
    private static final double R_RAZMAK_SRAFURE_SIRINA = 0.05;

    /** umetne slike: barva ozadja karte */
    private static final Color B_OZADJE = new Color(224, 224, 224);

    /** umetne slike: barva obrobe karte */
    private static final Color B_OBROBA = new Color(64, 64, 64);

    /** umetne slike: barva za srce in karo */
    private static final Color B_SRCE_KARO = new Color(224, 0, 0);

    /** umetne slike: barva za pik in kri"z */
    private static final Color B_PIK_KRIZ = new Color(0, 0, 0);

    /** barva ozadja zadnje strani karte */
    private static final Color B_OZADJE_ZADNJA_STRAN = new Color(224, 224, 224);

    /** barva "srafure na zadnji strani karte */
    private static final Color B_SRAFURA = new Color(0, 0, 128);

    /** simboli za srce, pik, karo in kri"z */
    private static final char[] ZNAK_BARVE = {'\u2665', '\u2660', '\u2666', '\u2663'};

    /** "sirina karte (ena od shranjenih) */
    private static int s_sirinaKarte = 0;

    /** vi"sina karte (= ri(R_VISINA_SIRINA * s_sirinaKarte)) */
    private static int s_visinaKarte = 0;

    /**
     * KARTA_2_SLIKA.get(sirina).get(karta):
     *    slika "sirine `sirina', ki prikazuje karto `karta'
     */
    private static final Map<Integer, Map<Karta, Image>> KARTA_2_SLIKA = new HashMap<>();

    /**
     * Nastavi vi"sino karte.  Dejanska vi"sina karte se bo izra"cunala kot
     * zaokro"zen R_VISINA_SIRINA-kratnik najbli"zje shranjene "sirine.
     */
    public static void nastaviVisinoKarte(double hKarta) {
        nastaviSirinoKarte(hKarta / R_VISINA_SIRINA);
    }

    /**
     * Nastavi "sirino karte na shranjeno "sirino, ki je najbli"zja podani
     * "zeleni "sirini.
     */
    public static void nastaviSirinoKarte(double wKarta) {
        s_sirinaKarte = zaokroziSirino((int) wKarta);
        s_visinaKarte = ri(R_VISINA_SIRINA * s_sirinaKarte);
    }

    /**
     * Vrne "sirino karte.
     */
    public static int vrniSirinoKarte() {
        return s_sirinaKarte;
    }

    /**
     * Vrne vi"sino karte.
     */
    public static int vrniVisinoKarte() {
        return s_visinaKarte;
    }

    /**
     * Pridobi oziroma izdela sliko podane karte.  "Ce obstaja datoteka s
     * shranjeno sliko, prebere sliko iz datoteke, sicer pa nari"se umetno
     * sliko.
     * @param skrita true: karta je skrita; false: karta je vidna
     */
    public static Image pridobiSliko(Karta karta, boolean skrita) {
        if (skrita) {
            return narisiZadnjoStranKarte(s_sirinaKarte);
        }

        // poglej v slovar; "ce slika "ze obstaja, jo uporabi
        if (KARTA_2_SLIKA.containsKey(s_sirinaKarte) && 
                KARTA_2_SLIKA.get(s_sirinaKarte).containsKey(karta)) {
            return KARTA_2_SLIKA.get(s_sirinaKarte).get(karta);
        }

        Image slika = null;

        // preveri, ali obstaja datoteka; "ce obstaja, preberi sliko iz nje
        File datoteka = new File(sirina2imenik(s_sirinaKarte), karta.toString() + SLIKOVNE_DATOTEKE_KONCNICA);
        if (datoteka.isFile()) {
            try {
                slika = ImageIO.read(datoteka);
            } catch (IOException ex) {
            }
        }

        // "ce slike ne moremo prebrati iz datoteke, jo nari"semo
        if (slika == null) {
            slika = narisiKarto(karta, s_sirinaKarte);
        }

        // shrani sliko v slovar
        if (!KARTA_2_SLIKA.containsKey(s_sirinaKarte)) {
            KARTA_2_SLIKA.put(s_sirinaKarte, new HashMap<>());
        }
        KARTA_2_SLIKA.get(s_sirinaKarte).put(karta, slika);

        return slika;
    }

    /**
     * Nari"se umetno podobo podane karte v pomnilni"sko sliko.
     * @param sirina "sirina slike
     */
    private static Image narisiKarto(Karta karta, int sirina) {
        int barva = karta.vrniBarvo();
        int vrednost = karta.vrniVrednost();

        double dSirina = (double) sirina;
        double dVisina = R_VISINA_SIRINA * dSirina;
        BufferedImage slika = new BufferedImage(sirina, ri(dVisina), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = slika.createGraphics();
        Razno.nastaviKakovostRisanja(g);

        g.setColor(B_OZADJE);
        g.fill(new Rectangle2D.Double(0.0, 0.0, dSirina, dVisina));

        // obroba
        double dOdmikObrobe = R_ODMIK_OBROBE_SIRINA * dSirina;
        g.setColor(B_OBROBA);
        g.setStroke(new BasicStroke((float) (R_DEBELINA_OBROBE_SIRINA * dSirina)));
        g.draw(new Rectangle2D.Double(dOdmikObrobe, dOdmikObrobe, dSirina - 2 * dOdmikObrobe, dVisina - 2 * dOdmikObrobe));

        // napisi (barva in vrednost)
        g.setFont(Razno.vrniPisavo());
        double dOdmikNapisa = dSirina * R_ODMIK_NAPISA_SIRINA;
        FontMetrics fm = g.getFontMetrics();
        if (barva % 2 == 0) {
            g.setColor(B_SRCE_KARO);
        } else {
            g.setColor(B_PIK_KRIZ);
        }
        int hPisava = fm.getAscent() + 1;
        String strBarva = Character.toString(ZNAK_BARVE[barva]);
        String strVrednost = Karta.VREDNOST_V_NAPIS[vrednost];
        double wBarva = fm.stringWidth(strBarva);
        double wVrednost = fm.stringWidth(strVrednost);
        double wNapis = Math.max(wBarva, wVrednost);
        double dBarva = (wNapis - wBarva) / 2;
        double dVrednost = (wNapis - wVrednost) / 2;
        g.drawString(strVrednost, ri(dOdmikNapisa + dVrednost), ri(dOdmikNapisa + hPisava));
        g.drawString(strBarva, ri(dOdmikNapisa + dBarva), ri(dOdmikNapisa + 2 * hPisava));
        g.drawString(strVrednost, ri(dSirina + dVrednost - wNapis - dOdmikNapisa), ri(dVisina - dOdmikNapisa - 1));
        g.drawString(strBarva, ri(dSirina + dBarva - wNapis - dOdmikNapisa), ri(dVisina - dOdmikNapisa - 1 - hPisava));
        return slika;
    }

    /**
     * Nari"se zadnjo stran karte v pomnilni"sko sliko.
     * @param sirina "sirina slike
     */
    private static Image narisiZadnjoStranKarte(int sirina) {
        double dSirina = (double) sirina;
        double dVisina = R_VISINA_SIRINA * dSirina;
        BufferedImage slika = new BufferedImage(sirina, ri(dVisina), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = slika.createGraphics();
        Razno.nastaviKakovostRisanja(g);

        g.setColor(B_OZADJE_ZADNJA_STRAN);
        g.fill(new Rectangle2D.Double(0.0, 0.0, dSirina, dVisina));

        // "srafura
        double dOdmikObrobe = R_ODMIK_OBROBE_SIRINA * dSirina;
        Rectangle2D rcRez = new Rectangle2D.Double(dOdmikObrobe, dOdmikObrobe, dSirina - 2 * dOdmikObrobe, dVisina - 2 * dOdmikObrobe);

        g.setClip(rcRez);
        g.setColor(B_SRAFURA);
        double razmak = R_RAZMAK_SRAFURE_SIRINA * sirina;
        double x = -dVisina;
        g.setStroke(new BasicStroke((float) (R_DEBELINA_SRAFURE_SIRINA * dSirina)));
        while (x < dSirina) {
            g.draw(new Line2D.Double(x, 0.0, x + dVisina, dVisina));
            x += razmak;
        }

        // obroba
        g.setClip(null);
        g.setColor(B_OBROBA);
        g.setStroke(new BasicStroke((float) (R_DEBELINA_OBROBE_SIRINA * dSirina)));
        g.draw(rcRez);
        return slika;
    }

    /**
     * Vrne imenik, v katerem so shranjene slike s podano "sirino.
     */
    private static File sirina2imenik(int sirina) {
        return new File(IMENIK_SLIKE, String.format(FORMAT_IMENA_SLIK, sirina));
    }

    /**
     * Za podano "zeleno "sirino vrne najbli"zjo shranjeno "sirino slik kart.
     */
    private static int zaokroziSirino(int zelenaSirina) {
        if (zelenaSirina < MIN_SIRINA) {
            return MIN_SIRINA;
        }
        return Math.min(MAKS_SIRINA, 
                Math.max(MIN_SIRINA, zelenaSirina / KORAK_SIRINE * KORAK_SIRINE));
    }
}
