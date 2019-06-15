package s63180224;

import skupno.Stroj;
import skupno.Karta;
import skupno.MnozicaKart;
import skupno.Konstante;
import java.util.Random;
import java.util.Arrays;

public class Stroj_Qlearner implements Stroj {
	
	private final Random rand;
	private MnozicaKart vRoki;
	private double[][][] Qtabela;	//[stih][barva][vrednost]
	private static final double ALPHA = 0.8;	//update za vsako prejsnje stanje;
	private double EPSILON = 0.00002; //procent s katerim bo izbral max vrednost
	private int[][] akcije; 
	private int zacetnik;
	private int polozaj;
	private Karta prva;
	//private Karta[] karteNaMizi;
	private int stPartije;
	
	//konstruktor
	public Stroj_Qlearner() {
		Qtabela = new double[13][4][13];
			 akcije = new int[2][13];	//katera karta je bila izbrana [0]=barva [1]=vrednost  13=stkart
		this.rand = new Random();
		//karteNaMizi = new Karta[4];
		stPartije = 0;
    }

    @Override
	public void novaPartija(int polozaj, MnozicaKart karte){
		vRoki = karte;
		this.polozaj = polozaj;
		stPartije++;
	}

	@Override
	public void pricetekStiha(int zacetnik){
		this.zacetnik = zacetnik;
	}

	@Override
	public void sprejmiPotezo(int akter, Karta karta){
		if(akter == zacetnik)
			prva = karta;
	}

	@Override
	public Karta izberiPotezo(long preostaliCas){
		MnozicaKart ustrezne = veljavnaPoteza();
		Karta izbrana = null;
		//rabim vedit v kakem vrstnem redu so "vRoki" da lahk update-am Qtabelo
		//razporejeni so po barvni nato znotraj tega po vrednosti
		
		//if(random.num > EPSILON) -> pick random card ELSE pick max
		//if vRoki.length == 1 (pomeni zadni stih) -> update Qtabelo in povecaj EPSILON
		//if stPartije == 10000 -> print Qtabelo
		
		if(rand.nextDouble()>EPSILON){
			izbrana = ustrezne.izberiNakljucno(this.rand); 
		} else {
			Karta[] ustrezneTab = ustrezne.vTabelo();
			int[][] ustrezneAkcije = new int[2][ustrezne.steviloKart()];

			for(int i = 0; i<ustrezne.steviloKart(); i++){
				ustrezneAkcije[0][i] = ustrezneTab[i].vrniBarvo();
				ustrezneAkcije[1][i] = ustrezneTab[i].vrniVrednost();
			}
			int[] izbranBV = Qmax(ustrezneAkcije);
			
			izbrana = Karta.objekt(Karta.bv2indeks(izbranBV[0],izbranBV[1]));
		}
		
	
		akcije[0][13-vRoki.steviloKart()] = izbrana.vrniBarvo();
		akcije[1][13-vRoki.steviloKart()] = izbrana.vrniVrednost();
	
		/*if(stPartije >= 9999 || stPartije == 4999)
			System.out.println(Arrays.deepToString(Qtabela));*/

		vRoki.odstrani(izbrana);
		return izbrana;
		//OPOMBA: moram vklucit se kartenamizi		
    }

    @Override
	public void rezultat(int[] tocke){
		if(tocke[polozaj] != 0) {
			posodobiQtabelo(tocke[polozaj]);
		} else {
			posodobiQtabelo(-tocke[(polozaj+1)%4]);
		}
	
		EPSILON += 0.00002;
	}

	private MnozicaKart veljavnaPoteza() {
		if (this.zacetnik == this.polozaj) {
            return vRoki;
        }

        int barvaPrve = prva.vrniBarvo();

        MnozicaKart ujemajoce = vRoki.karteVBarvi(barvaPrve);
        if (!ujemajoce.jePrazna()) {
            return ujemajoce;
        }


        MnozicaKart aduti = vRoki.karteVBarvi(Konstante.BARVA_ADUTA);
        if (!aduti.jePrazna()) {
            return aduti;
        }

        return vRoki;
	}

	private int[] Qmax(int[][] ustrAkcije) {
		int ix = ustrAkcije[0][0], iy = ustrAkcije[1][0];
		double maxQval = 0.0;
		
		int stKart = ustrAkcije[0].length;
		for(int i = 0; i<stKart; i++){
			int x = ustrAkcije[0][i];
			int y = ustrAkcije[1][i];
			double zbr = Qtabela[13-stKart][x][y-2];

			if(zbr > maxQval){
				maxQval = zbr;
				ix = x; iy = y;
			}
		}
		
		return new int[] {ix,iy};
	}

	private void posodobiQtabelo(int rezTocke) {
		
		for(int i = 12; i>=0; i--){
			int z = i; int x = akcije[0][i]; int y = akcije[1][i]-2; 
			Qtabela[z][x][y] += potenca(ALPHA,13-i)*rezTocke;
			//System.out.println(z + " " + x + " " + (y+2) + " : " + Qtabela[z][x][y]);
		}

	}

	private double potenca(double st, int pot){
		double produkt = 1;
		for(int i = 0; i<pot; i++){
			produkt *= st;		
		}
		return produkt;
	}
}
