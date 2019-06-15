
package skupno;

import java.util.Arrays;
import java.util.Random;

/**
 * Razred za testiranje razredov skupno.Karta in skupno.MnozicaKart.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("=======");
        System.out.println(" Karta ");
        System.out.println("=======");
        System.out.println();

        Karta srcevKralj = Karta.sK;
        Karta pikovaDama = Karta.objekt(1, 12);
        Karta krizevaTrojka = Karta.objekt(50);
        Karta karinAs = Karta.objekt("aA");

        Karta[] karte = {srcevKralj, pikovaDama, krizevaTrojka, karinAs};
        for (int i = 0;  i < karte.length;  i++) {
            System.out.printf("Karta: %s%n", karte[i]);
            System.out.printf("Barva: %d%n", karte[i].vrniBarvo());
            System.out.printf("Vrednost: %d%n", karte[i].vrniVrednost());
            System.out.printf("Indeks: %d%n", karte[i].indeks());
            System.out.printf("Adut? %b%n", karte[i].jeAdut());
            System.out.println();
        }

        System.out.println("=========");
        System.out.println(" Mnozica ");
        System.out.println("=========");
        System.out.println();

        System.out.println("<< konstruktorji, vsiPrimerkiBarve, vsiPrimerkiVrednosti >>");
        MnozicaKart prazna = MnozicaKart.PRAZNA;
        MnozicaKart prazna2 = new MnozicaKart();
        MnozicaKart cetvorka = new MnozicaKart(srcevKralj, pikovaDama, krizevaTrojka, karinAs);
        MnozicaKart kopijaCetvorke = new MnozicaKart(cetvorka);
        Karta[] tabela = { Karta.p5, Karta.s9, Karta.r6, Karta.pJ, Karta.a4, Karta.s9 };
        MnozicaKart izTabele = new MnozicaKart(tabela);  // ponavljanja niso problem ...
        MnozicaKart kare = MnozicaKart.vsiPrimerkiBarve(Karta.KARO);
        MnozicaKart fantje = MnozicaKart.vsiPrimerkiVrednosti(Karta.FANT);
        System.out.println("prazna: " + prazna);
        System.out.println("prazna2: " + prazna2);
        System.out.println("cetvorka: " + cetvorka);
        System.out.println("kopijaCetvorke: " + kopijaCetvorke);
        System.out.println("izTabele: " + izTabele);
        System.out.println("kare: " + kare);
        System.out.println("fantje: " + fantje);
        System.out.println();

        System.out.println("<< vsebuje, dodaj, odstrani, steviloKart, jePrazna >>");
        MnozicaKart nova = new MnozicaKart(cetvorka);
        System.out.println(nova);
        System.out.println(nova.steviloKart());
        System.out.println(nova.vsebuje(Karta.r9));
        System.out.println(nova.jePrazna());
        nova.dodaj(Karta.r9);
        nova.dodaj(Karta.sK);      // ni"c se ne zgodi, "ce dodamo karto, ki je "ze v mno"zici
        nova.odstrani(Karta.pQ);
        nova.odstrani(Karta.aA);    
        System.out.println(nova);
        System.out.println(nova.steviloKart());
        System.out.println(nova.vsebuje(Karta.r9));
        System.out.println(nova.jePrazna());
        nova.odstrani(Karta.sK);
        nova.odstrani(Karta.r3);
        nova.odstrani(Karta.r9);
        nova.odstrani(Karta.sK);  // ni"c se ne zgodi, "ce odstranimo karto, ki je ni v mno"zici
        System.out.println(nova);
        System.out.println(nova.steviloKart());
        System.out.println(nova.vsebuje(Karta.r9));
        System.out.println(nova.jePrazna());
        System.out.println();

        System.out.println("<< presek, unija, komplement, razlika >>");
        MnozicaKart rdece = MnozicaKart.vsiPrimerkiBarve(Karta.SRCE).unija(kare);
        MnozicaKart crne = rdece.komplement();
        MnozicaKart visoke = fantje
            .unija(MnozicaKart.vsiPrimerkiVrednosti(Karta.DAMA))
            .unija(MnozicaKart.vsiPrimerkiVrednosti(Karta.KRALJ))
            .unija(MnozicaKart.vsiPrimerkiVrednosti(Karta.AS));
        MnozicaKart nizke = visoke.komplement();
        MnozicaKart visokeRdece = visoke.presek(rdece);
        MnozicaKart nizkeCrne = nizke.presek(crne);
        MnozicaKart visokeRdeceBrezCetvorke = visokeRdece.razlika(cetvorka);
        System.out.println(rdece);
        System.out.println(crne);
        System.out.println(visoke);
        System.out.println(nizke);
        System.out.println(visokeRdece);
        System.out.println(nizkeCrne);
        System.out.println(visokeRdeceBrezCetvorke);
        System.out.println();

        System.out.println("<< karteVBarvi, karteVVrednosti >>");
        System.out.println(visoke.karteVBarvi(Karta.KARO));
        System.out.println(cetvorka.karteVBarvi(Karta.PIK));
        System.out.println(rdece.karteVBarvi(Karta.KRIZ));
        System.out.println(visoke.karteVVrednosti(Karta.DAMA));
        System.out.println(cetvorka.karteVVrednosti(Karta.TROJKA));
        System.out.println(nizke.karteVVrednosti(Karta.AS));
        System.out.println();

        System.out.println("<< izberiNakljucno >>");
        Random generator = new Random(12345);
        for (int i = 0;  i < 10;  i++) {
            System.out.println(visokeRdece.izberiNakljucno(generator));
        }
        System.out.println();

        System.out.println("<< vTabelo >>");
        Karta[] karte2 = cetvorka.vTabelo();
        System.out.println(Arrays.toString(karte2));
        System.out.println();

        System.out.println("<< iterator (sprehod po množici) >>");
        for (Karta karta: cetvorka) {
            System.out.print(karta + " ");
        }
        System.out.println();
        for (Karta karta: nizkeCrne) {
            System.out.print(karta + " ");
        }
        System.out.println();
        System.out.println();

        System.out.println("<< najmanjsiIndeks, najvecjiIndeks >>");
        MnozicaKart mnozica = new MnozicaKart();
        for (int i = 1;  i <= 10;  i++) {
            mnozica.dodaj(Karta.objekt(generator.nextInt(Konstante.ST_KART)));
        }
        System.out.println(mnozica);
        System.out.println("Najmanjši indeks: " + mnozica.najmanjsiIndeks());
        System.out.println("Največji indeks: " + mnozica.najvecjiIndeks());

        // spro"zimo izjemo
        try {
            System.out.println(MnozicaKart.PRAZNA.najmanjsiIndeks());
        } catch (MnozicaKart.PraznaMnozicaException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
