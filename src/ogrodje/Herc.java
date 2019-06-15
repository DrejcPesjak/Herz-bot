
package ogrodje;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import static ogrodje.Razno.ri;

import skupno.Konstante;

/**
 * Vstopna to"cka v program.
 * <p>
 * Igralci so opredeljeni s polo"zaji oz. indeksi (0: spodaj levo, 1: zgoraj
 * levo, 2: zgoraj desno, 3: spodaj desno).  Isti indeksi se uporabljajo tudi
 * za pripadajo"ce kartne in statusne plo"s"ce.
 */
public class Herc {

    /** naslov okna */
    private static final String IME_PROGRAMA = "Herc";

    /** datoteka, ki vsebuje podatke o polo"zaju in velikosti okna */
    private static final String DATOTEKA_OKNO = "okno.txt";

    /** minimalna "sirina okna */
    private static final int MINIMALNA_SIRINA_OKNA = 800;

    /** minimalna vi"sina okna */
    private static final int MINIMALNA_VISINA_OKNA = 600;

    /** privzeto razmerje med "sirino (oz. vi"sino) okna in "sirino (oz.
     * vi"sino) zaslona */
    private static final double R_OKNO_ZASLON = 0.5;

    /**
     * Vstopna to"cka v program
     */
    public static void main(String[] args) {
        AnimacijskeKonstante.preberi();
        final Seansa seansa = Seansa.izArgumentov(args); 

        if (seansa != null) {
            if (seansa.besedilniVmesnik()) {
                // besedilni na"cin
                Most.ustvari(seansa, null);
                seansa.inicializiraj();
                while (seansa.naslednja() != null) {}

            } else {
                // grafi"cni na"cin
                SwingUtilities.invokeLater(() -> {
                    KrovnaPlosca krovnaPlosca = izdelajGUI();
                    Most.ustvari(seansa, krovnaPlosca);
                    seansa.inicializiraj();
                    Partija partija = seansa.naslednja();
                    krovnaPlosca.posredujPartijo(partija);
                });
            }
        }
    }

    /**
     * Izdela celoten grafi"cni vmesnik (okno in vse komponente).
     */
    private static KrovnaPlosca izdelajGUI() {
        final JFrame okno = new JFrame(IME_PROGRAMA);
        okno.setBounds(okvirOkna(DATOTEKA_OKNO));
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        KrovnaPlosca krovnaPlosca = new KrovnaPlosca(okno);
        krovnaPlosca.inicializiraj();
        okno.add(krovnaPlosca);

        okno.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    // shrani polo"zaj in velikost okna v datoteko DATOTEKA_OKNO
                    Writer pisalnik = new FileWriter(DATOTEKA_OKNO);
                    pisalnik.write(String.format("%d %d %d %d%n", okno.getX(), okno.getY(), okno.getWidth(), okno.getHeight()));
                    pisalnik.close();
                } catch (IOException ex) {
                    System.err.printf("Ne morem odpreti datoteke %s za pisanje.%n", DATOTEKA_OKNO);
                }
            }
        });

        okno.setVisible(true);
        return krovnaPlosca;
    }

    /**
     * Prebere podatke o polo"zaju in velikosti okna iz podane datoteke in
     * vrne omejevalni pravokotnik okna.  "Ce datoteke ne more prebrati, vrne
     * tak pravokotnik, da bo okno postavljeno na sredino zaslona, zavzemalo
     * pa bo v obeh smereh vsaj R_OKNO_ZASLON zaslona.
     */
    private static Rectangle okvirOkna(String datotekaOkno) {
        Dimension velikostZaslona = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max(MINIMALNA_SIRINA_OKNA, ri(R_OKNO_ZASLON * velikostZaslona.width));
        int h = Math.max(MINIMALNA_VISINA_OKNA, ri(R_OKNO_ZASLON * velikostZaslona.height));
        int x = (velikostZaslona.width - w) / 2;
        int y = (velikostZaslona.height - h) / 2;

        try {
            Scanner sc = new Scanner(new File(datotekaOkno));
            String[] podatki = sc.nextLine().split(" ");
            List<Integer> stevila = Stream.of(podatki).map(s -> Integer.parseInt(s)).collect(Collectors.toList());
            x = stevila.get(0);
            y = stevila.get(1);
            w = stevila.get(2);
            h = stevila.get(3);
        } catch (FileNotFoundException | RuntimeException ex) {}
        return new Rectangle(x, y, w, h);
    }
}
