
package ogrodje;

import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

import skupno.Karta;

/**
 * Objekt tega razreda predstavlja osrednjo plo"s"co.  Ta med partijo
 * prikazuje teko"ci "stih in imena igralcev, ob koncu partije pa
 * razpredelnico z rezultati.  To dose"zemo tako, da izdelamo dve plo"s"ci
 * (eno za "stih in eno za rezultate) in ju postavimo na razporejevalnik
 * CardLayout.  S pomo"cjo razporejevalnikove metode `show' lahko nato
 * izbiramo, katera plo"s"ca se prika"ze.  (Vedno je prikazana ena sama
 * plo"s"ca.)
 */
public class OsrednjaPlosca extends JPanel {

    /** identifikator plo"s"ce, ki prikazuje teko"ci "stih */
    private static final String STIH = "stih";

    /** identifikator plo"s"ce, ki prikazuje rezultate */
    private static final String REZULTATI = "rezultati";

    /** plo"s"ca, ki prikazuje teko"ci "stih */
    private OsrednjaPloscaStih ploscaStih;

    /** plo"s"ca, ki prikazuje rezultate ob koncu partije */
    private OsrednjaPloscaRezultati ploscaRezultati;

    /**
     * Izdela objekt, ki predstavlja osrednjo plo"s"co.
     */
    public OsrednjaPlosca() {
        this.setBackground(Razno.B_SPLOSNO_OZADJE_SREDINA);
        this.setLayout(new CardLayout());
        this.ploscaStih = new OsrednjaPloscaStih();
        this.ploscaRezultati = new OsrednjaPloscaRezultati();
        this.add(ploscaStih, STIH);
        this.add(ploscaRezultati, REZULTATI);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                OsrednjaPlosca pl = OsrednjaPlosca.this;
                pl.ploscaStih.setBounds(0, 0, pl.getWidth(), pl.getHeight());
                pl.ploscaRezultati.setBounds(0, 0, pl.getWidth(), pl.getHeight());
            }
        });
    }

    /**
     * Podplo"s"cama po"slje sporo"cilo, naj se inicializirata.
     */
    public void inicializiraj() {
        this.ploscaStih.inicializiraj();
        this.ploscaRezultati.inicializiraj();
    }

    /**
     * Prika"ze plo"s"co za prikaz teko"cega "stiha.
     */
    public void prikaziStih() { 
        CardLayout razporejevalnik = (CardLayout) this.getLayout();
        razporejevalnik.show(this, STIH);
    }

    /**
     * Plo"s"ci za prikaz "stiha sporo"ci, da se je pri"cel nov "stih.
     */
    public void novStih(int zacetnik) {
        this.ploscaStih.novStih(zacetnik);
    }

    /**
     * Plo"s"ci za prikaz "stiha sporo"ci, naj doda karto.
     */
    public void dodajKarto(Karta karta) {
        this.ploscaStih.dodajKarto(karta);
    }

    /**
     * Izvede `animacijo' pobiranja "stiha.
     * @param pokliciPoAnimaciji funkcija, ki se pokli"ce po zaklju"cku
     *    animacije
     */
    public void animirajZakljucekStiha(Runnable pokliciPoAnimaciji) {
        this.ploscaStih.animirajZakljucekStiha(pokliciPoAnimaciji);
    }

    /**
     * Prika"ze plo"s"co za prikaz rezultatov.
     * @param imenaIgralcev imenaIgralcev[i] = ime igralca i
     * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je igralec i
     *    dobil v tej partiji
     * @param skupneTocke skupneTocke[i] = skupne to"cke, ki jih igralec i
     *    trenutno ima
     */
    public void prikaziRezultate(String[] imenaIgralcev, int[] dobljeneTocke, int[] skupneTocke) {
        CardLayout razporejevalnik = (CardLayout) this.getLayout();
        razporejevalnik.show(this, REZULTATI);
        this.ploscaRezultati.prikazi(imenaIgralcev, dobljeneTocke, skupneTocke);
    }
}
