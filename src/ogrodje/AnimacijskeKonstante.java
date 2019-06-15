
package ogrodje;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** 
 * Razred vsebuje konstante, ki podajajo "cakalne "case (v milisekundah) pri
 * ``animacijah''.
*/ 
public class AnimacijskeKonstante {

    /** datoteka s "cakalnimi "casi (lahko jo ureja uporabnik) */
    private static final String DATOTEKA = "cakanje.txt";

    /** priro"cna konstanta za dostop do vrednosti CAKANJE_PRED_PREMIKOM_STIHA */
    public static final String PRED_PREMIKOM_STIHA = "CAKANJE_PRED_PREMIKOM_STIHA";

    /** priro"cna konstanta za dostop do vrednosti CAKANJE_PRED_ZAPRTJEM_STIHA */
    public static final String PRED_ZAPRTJEM_STIHA = "CAKANJE_PRED_ZAPRTJEM_STIHA";

    /** priro"cna konstanta za dostop do vrednosti CAKANJE_PO_RACUNALNIKOVI_POTEZI */
    public static final String PO_RACUNALNIKOVI_POTEZI = "CAKANJE_PO_RACUNALNIKOVI_POTEZI";

    /** koliko "casa "cakamo, preden se "stih premakne proti dobitniku */
    private static final int PRIVZETO_CAKANJE_PRED_PREMIKOM_STIHA = 750;

    /** koliko "casa "cakamo, preden se "stih zapre (odstrani z osrednje
     * plo"s"ce) */
    private static final int PRIVZETO_CAKANJE_PRED_ZAPRTJEM_STIHA = 750;

    /** koliko "casa "cakamo, potem ko ra"cunalnik odvr"ze karto */
    private static final int PRIVZETO_CAKANJE_PO_RACUNALNIKOVI_POTEZI = 500;

    /** vedno "cakamo vsaj toliko milisekund ... */
    public static final int LOCLJIVOST_CASOVNIKA = 50;

    /** konstanta2vrednost.get(konstanta) = vrednost za konstanto `konstanta' */
    private Map<String, Integer> konstanta2vrednost;

    /** eden in edini objekt razreda AnimacijskeKonstante v celotnem sistemu */
    private static AnimacijskeKonstante s_animacijskeKonstante = null;

    /** 
     * Inicializira slovar konstanta2vrednost.
     */
    private AnimacijskeKonstante() {
        this.konstanta2vrednost = new HashMap<>();
        this.konstanta2vrednost.put(
                "CAKANJE_PRED_ZAPRTJEM_STIHA",
                PRIVZETO_CAKANJE_PRED_ZAPRTJEM_STIHA);
        this.konstanta2vrednost.put(
                "CAKANJE_PRED_PREMIKOM_STIHA",
                PRIVZETO_CAKANJE_PRED_PREMIKOM_STIHA);
        this.konstanta2vrednost.put(
                "CAKANJE_PO_RACUNALNIKOVI_POTEZI",
                PRIVZETO_CAKANJE_PO_RACUNALNIKOVI_POTEZI);
    }

    /** 
     * Prebere vrednosti konstant iz datoteke DATOTEKA in jih shrani v slovar
     * konstanta2vrednost.
     */
    public static void preberi() {
        s_animacijskeKonstante = new AnimacijskeKonstante();
        boolean izjema = false;

        try (Scanner sc = new Scanner(new File(DATOTEKA))) {
            while (sc.hasNextLine()) {
                String vrstica = sc.nextLine().trim();

                // presko"cimo komentarje
                if (vrstica.isEmpty() || vrstica.startsWith("#") || vrstica.startsWith("//")) {
                    continue;
                }

                // ignoriramo morebitna kon"cna podpi"cja
                while (vrstica.endsWith(";")) {
                    vrstica = vrstica.substring(0, vrstica.length() - 1).trim();
                }

                // razbijemo niz `konstanta = vrednost' na konstanto in
                // vrednost
                String[] komponente = vrstica.split("=");
                String konstanta = komponente[0].trim();
                int vrednost = Integer.parseInt(komponente[komponente.length - 1].trim());
                if (vrednost < 0) {
                    throw new NumberFormatException();
                }
                vrednost = Math.max(vrednost, LOCLJIVOST_CASOVNIKA);
                if (s_animacijskeKonstante.konstanta2vrednost.get(konstanta) == null) {
                    throw new RuntimeException(String.format("konstanta %s ne obstaja", konstanta));
                }
                s_animacijskeKonstante.konstanta2vrednost.put(konstanta, vrednost);
            }
        } catch (FileNotFoundException ex) {
            izjema = true;
            System.err.printf("Ne najdem datoteke %s. Uporabljam privzete čakalne nastavitve.%n", DATOTEKA);
        } catch (NumberFormatException ex) {
            izjema = true;
            System.err.printf("Vrednosti v datoteki %s morajo biti nenegativna cela števila.%n", DATOTEKA);
        } catch (RuntimeException ex) {
            izjema = true;
            System.err.printf("Napaka pri branju datoteke %s: %s%n", DATOTEKA, ex.getMessage());
        }

        if (izjema) {
            System.err.println("Uporabil bom privzete nastavitve čakalnih časov.");
        }
    }

    /**
     * Vrne vrednost podane konstante.
     */
    public static int vrni(String konstanta) {
        return s_animacijskeKonstante.konstanta2vrednost.get(konstanta);
    }
}
