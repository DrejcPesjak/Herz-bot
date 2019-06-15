package s63180224;

import skupno.Stroj;
import skupno.Karta;
import skupno.MnozicaKart;
import skupno.Konstante;
import java.util.Random;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileNotFoundException;


public class Stroj_Qlearner2_1 implements Stroj {

	private final Random rand;
	private MnozicaKart vRoki;
	private float[][][][] Qtabela;	//[moja_karta][jaz+1][soigralec][jaz-1]
	private static final float ALPHA = 0.8f;	//update za vsako prejsnje stanje;
	private float EPSILON = 0.0000066f; //procent s katerim bo izbral max vrednost
	private short[][] akcije; //[stevilkaStiha][indeksi 4 kart]
	private int zacetnik;
	private int polozaj;
	private short[] karteNaMizi;
	private int stPartije; //za printanje Qtabele po (50000) partiji

	//konstruktor
	public Stroj_Qlearner2_1() {
		Qtabela = new float[52][52][52][52];
			// akcije = new short[13][4];	//13 stihov, po 4 karte
		this.rand = new Random();
		stPartije = 0;
    }

    @Override
	public void novaPartija(int polozaj, MnozicaKart karte){
		//reset akcije
		akcije = new short[13][4];

		for(int i = 0; i<13; i++){
			for(int j = 0; j<4; j++){
				akcije[i][j] = -1;
			}
		}

		vRoki = karte;
		this.polozaj = polozaj;
		stPartije++;
	}

	@Override
	public void pricetekStiha(int zacetnik){
		this.zacetnik = zacetnik;
		karteNaMizi = new short[]{-1,-1,-1,-1};
	}

	@Override
	public void sprejmiPotezo(int akter, Karta karta){
		int shraniIx = (akter+4-polozaj)%4;		
		karteNaMizi[shraniIx] = (short)karta.indeks();

		//System.out.println(Arrays.toString(karteNaMizi));
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

		if(rand.nextFloat()>EPSILON){
			izbrana = ustrezne.izberiNakljucno(this.rand);
		} else {
			Karta[] ustrezneTab = ustrezne.vTabelo();
			int[] ustrezneAkcije = new int[ustrezne.steviloKart()];

			//zgradis tabelo vrednosti in barv
			for(int i = 0; i<ustrezne.steviloKart(); i++){
				ustrezneAkcije[i] = ustrezneTab[i].indeks();
			}

			izbrana = Karta.objekt(Qmax(ustrezneAkcije));
		}

		karteNaMizi[0] = (short)izbrana.indeks();

		for(int i = 0; i < karteNaMizi.length; i++) {
			akcije[13-vRoki.steviloKart()][i] = karteNaMizi[i];
		}
		//System.out.println(Arrays.toString(karteNaMizi));

		vRoki.odstrani(izbrana);
		return izbrana;
    }

    @Override
	public void rezultat(int[] tocke){
		if(tocke[polozaj] != 0) { //ce zmagam
			posodobiQtabelo(tocke[polozaj]);
		} else {
			posodobiQtabelo(-tocke[(polozaj+1)%4]);
		}

		try{
			if(stPartije >= 200000)
				printQtabela();
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}	

		EPSILON += 0.0000066f;
	}

	private MnozicaKart veljavnaPoteza() {
		if (this.zacetnik == this.polozaj) {
            return vRoki;
        }

		int prvi = (zacetnik+4-polozaj)%4;
		//System.out.println(Arrays.toString(karteNaMizi));
		//System.out.println(prvi + " = (" + polozaj+ "+4-"+zacetnik+") mod4");
        int barvaPrve = Karta.objekt((int)karteNaMizi[prvi]).vrniBarvo();

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

	public int Qmax(int[] ustrAkcije) {
		float maxQval = 0; int iX = ustrAkcije[0];

		if(polozaj == zacetnik) {
			//jaz sem zacetni => na mizi ni nobene druge karte => preglej cel blok za vsako ustrezno
			for(int i = 0; i<ustrAkcije.length; i++){
				for(int j = 0; j<52; j++){
					for(int z = 0; z<52; z++){
						for(int t = 0; t<52; t++){
							float trenutniQ = Qtabela[ustrAkcije[i]][j][z][t];

							if(trenutniQ > maxQval){
								maxQval= trenutniQ;
								iX = ustrAkcije[i];
							}
						}
					}
				}
			}

		} else if((zacetnik+1)%4 == polozaj) {
			for(int i = 0; i<ustrAkcije.length; i++){
				for(int j = 0; j<52; j++){
					for(int z = 0; z<52; z++){
						float trenutniQ = Qtabela[ustrAkcije[i]][j][z][karteNaMizi[3]];

						if(trenutniQ > maxQval){
							maxQval= trenutniQ;
							iX = ustrAkcije[i];
						}

					}
				}
			}
		} else if((zacetnik+2)%4 == polozaj) {
			for(int i = 0; i<ustrAkcije.length; i++){
				for(int j = 0; j<52; j++){
					float trenutniQ = Qtabela[ustrAkcije[i]][j][karteNaMizi[2]][karteNaMizi[3]];

					if(trenutniQ > maxQval){
						maxQval= trenutniQ;
						iX = ustrAkcije[i];
					}
				}
			}
		} else {
			for(int x : ustrAkcije){
				float trenutniQ = Qtabela[x][karteNaMizi[1]][karteNaMizi[2]][karteNaMizi[3]];

				if(trenutniQ > maxQval){
					maxQval= trenutniQ;
					iX = x;
				}
			}
		}
		return iX;
	}

	private void posodobiQtabelo(int rezTocke) {
		//System.out.println(Arrays.deepToString(akcije));
		for(int x = 12; x>=0; x--){
			int limitaI = akcije[x][1]==-1 ? 52:akcije[x][1]+1;
				int spodnjaMejaI = limitaI==-1 ? 0:limitaI-1;

			for(int i = spodnjaMejaI; i<limitaI; i++){
				
				int limitaJ = akcije[x][2]==-1 ? 52:akcije[x][2]+1;
					int spodnjaMejaJ = limitaJ==-1 ? 0:limitaJ-1;

				for(int j = spodnjaMejaJ; j<limitaJ; j++){

					int limitaZ = akcije[x][3]==-1 ? 52:akcije[x][3]+1;
						int spodnjaMejaZ = limitaZ==-1 ? 0:limitaZ-1;

					for(int z = spodnjaMejaZ; z<limitaZ; z++){

						Qtabela[akcije[x][0]][i][j][z] += potenca(ALPHA,13-x)*rezTocke;
						
						//System.out.println(limitaI +", "+spodnjaMejaI +", "+ limitaJ +", "+ spodnjaMejaJ +", "+limitaZ +", "+spodnjaMejaZ);
						//System.out.println(Qtabela[akcije[x][0]][i][j][z]);
					}
				
				}
			
			}

		} 

	}

	private float potenca(float st, int pot){
		float produkt = 1;
		for(int i = 0; i<pot; i++){
			produkt *= st;
		}
		return produkt;
	}

	private void printQtabela() throws FileNotFoundException {
		try{			
			PrintWriter writer = new PrintWriter("tabela.txt");
			writer.print("{");
			for(int cetrta = 0; cetrta < 52; cetrta++) {
				writer.print("{");
				for(int tretja = 0; tretja < 52; tretja++) {
					writer.print("{");
					for(int druga = 0; druga < 52; druga++) {
						writer.print("{");
						for(int prva = 0; prva < 52; prva++) {
							if(prva != 51) {
								writer.print(Qtabela[0][0][druga][prva] + "f, ");
							} else {
								if(druga != 51)
									writer.print(Qtabela[cetrta][tretja][druga][prva] + "f}, ");
								else
									writer.print(Qtabela[cetrta][tretja][druga][prva] + "f}");
							}
						}
					}
					if(tretja != 51)
						writer.print("}, ");
					else
						writer.print("}");
				}
				if(cetrta != 51)
					writer.print("}, ");
				else
					writer.print("}");
			}
			writer.println("}");
			writer.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}

	}
}
