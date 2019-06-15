package s63180224;

import skupno.Stroj;
import skupno.Karta;
import skupno.MnozicaKart;
import skupno.Konstante;

public class Stroj_Novinec implements Stroj {

	private int polozaj;
	private int zacetnik;
	private MnozicaKart trenutneKarte;
	private Karta[] karteNaMizi;

	//konstruktor
	public Stroj_Novinec() {
		karteNaMizi = new Karta[4];
    }


	@Override
	public void novaPartija(int polozaj, MnozicaKart karte){
		this.polozaj = polozaj;
		trenutneKarte = karte;
	}

	@Override
	public void pricetekStiha(int zacetnik){
		this.zacetnik = zacetnik;
	}

	@Override
	public void sprejmiPotezo(int akter, Karta karta){
		karteNaMizi[akter] = karta;
	}

	@Override
	public Karta izberiPotezo(long preostaliCas){

		if(polozaj == zacetnik) {
			//ce prvi polagam karto
			MnozicaKart nova = trenutneKarte.razlika(MnozicaKart.vsiPrimerkiBarve(0));
			if(!nova.jePrazna()) {
				Karta k = maxKarta(nova);
				trenutneKarte.odstrani(k);
				return k;
			}
			Karta k = maxKarta(trenutneKarte);
			trenutneKarte.odstrani(k);
			return k;


		} else if(polozaj == (zacetnik +1)%4) {
			//ce drugi polagam karto
			Karta prva = karteNaMizi[zacetnik];
			int barva = prva.vrniBarvo();

			MnozicaKart ujemajoce = trenutneKarte.karteVBarvi(barva);
        	if (!ujemajoce.jePrazna()) {	//ce imam originalno barvo
				Karta max = maxKarta(ujemajoce);
				if(max.vrniVrednost() > prva.vrniVrednost()){
					trenutneKarte.odstrani(max);
					return max;
				} else {
					Karta min = minKarta(ujemajoce);
					trenutneKarte.odstrani(min);
					return min;
				}
			}

			MnozicaKart aduti = trenutneKarte.karteVBarvi(0);
			if(!aduti.jePrazna()) { //drgac izberem srce
				Karta k = maxKarta(aduti);
				trenutneKarte.odstrani(k);
				return k;

			} else {
				Karta min = minKarta(trenutneKarte);
				trenutneKarte.odstrani(min);
				return min;
			}



		} else if(polozaj == (zacetnik +2)%4) {
			//ce tretji polagam karto
			Karta prva = karteNaMizi[zacetnik];
			int barva = prva.vrniBarvo();

			MnozicaKart ujemajoce = trenutneKarte.karteVBarvi(barva);
        	if (!ujemajoce.jePrazna()) {	//ce imam originalno barvo
				Karta max = maxKarta(ujemajoce);
				int mVr = max.vrniVrednost();

				if(mVr > prva.vrniVrednost() &&
					mVr > karteNaMizi[(zacetnik +1)%4].vrniVrednost() &&
					prva.vrniVrednost() < karteNaMizi[(zacetnik +1)%4].vrniVrednost()){

					trenutneKarte.odstrani(max);
					return max;

				} else {
					Karta min = minKarta(ujemajoce);
					trenutneKarte.odstrani(min);
					return min;
				}
			}

			MnozicaKart aduti = trenutneKarte.karteVBarvi(0);
			if(!aduti.jePrazna()) { //drgac izberem srce
				Karta k = maxKarta(aduti);
				if(k.vrniVrednost()>karteNaMizi[(zacetnik +1)%4].vrniVrednost() &&
					karteNaMizi[(zacetnik +1)%4].vrniBarvo() == 0) {

					trenutneKarte.odstrani(k);
					return k;

				} else {
					Karta min = minKarta(aduti);
					trenutneKarte.odstrani(min);
					return min;
				}

			} else {
				Karta min = minKarta(trenutneKarte);
				trenutneKarte.odstrani(min);
				return min;
			}



		} else {
			//ce zadni polagam karto
			Karta prva = karteNaMizi[zacetnik];
			int barva1 = prva.vrniBarvo();

			Karta druga = karteNaMizi[(zacetnik +1)%4];
			int barva2 = prva.vrniBarvo();

			Karta tretja = karteNaMizi[(zacetnik +2)%4];
			int barva3 = prva.vrniBarvo();

			//ce je soigralec ze zmagal
			if(barva1==barva2 && prva.vrniVrednost() < druga.vrniVrednost()) {
				if((barva2 == barva3 && druga.vrniVrednost() > tretja.vrniVrednost()) || barva3 != 0) {
					Karta k = odigrajSlabo(trenutneKarte, barva1);
					trenutneKarte.odstrani(k);
					return k;
				} else { //moram zmagati
					Karta k = odigrajDobro(trenutneKarte, barva1);
					trenutneKarte.odstrani(k);
					return k;
				}

			} else if(barva1 != 0 && barva2 == 0) {
				if((barva2 == barva3 && druga.vrniVrednost() > tretja.vrniVrednost()) || barva3 != 0) {
					Karta k = odigrajSlabo(trenutneKarte, barva1);
					trenutneKarte.odstrani(k);
					return k;
				} else { //moram zmagati
					Karta k = odigrajDobro(trenutneKarte, barva1);
					trenutneKarte.odstrani(k);
					return k;
				}

			} else { //moram zmagati
				Karta k = odigrajDobro(trenutneKarte, barva1);
				trenutneKarte.odstrani(k);
				return k;
			}
		}
	}

	@Override
	public void rezultat(int[] tocke){
		
	}

	private Karta maxKarta(MnozicaKart abc) {
		Karta[] karte = abc.vTabelo();

		int maxVrednost = 0; int maxIx = 0;

		for(int i = 0; i<karte.length; i++) {
			if(karte[i].vrniVrednost()>maxVrednost) {
				maxVrednost = karte[i].vrniVrednost();
				maxIx = i;
			}
		}

		return karte[maxIx];
	}

	private Karta minKarta(MnozicaKart abc) {
		Karta[] karte = abc.vTabelo();

		int minVrednost = 20; int minIx = 0;

		for(int i = 0; i<karte.length; i++) {
			if(karte[i].vrniVrednost()<minVrednost) {
				minVrednost = karte[i].vrniVrednost();
				minIx = i;
			}
		}
		
		return karte[minIx];
	}

	private Karta odigrajSlabo(MnozicaKart neki, int barva) {

		MnozicaKart ujemajoce = neki.karteVBarvi(barva);
		if (!ujemajoce.jePrazna()) {
			return minKarta(ujemajoce);
		}

		MnozicaKart aduti = neki.karteVBarvi(0);
		if(!aduti.jePrazna()) {
			return minKarta(aduti);
		}

		return minKarta(neki);

	}

	private Karta odigrajDobro(MnozicaKart neki, int barva) {
		MnozicaKart ujemajoce = neki.karteVBarvi(barva);
		if (!ujemajoce.jePrazna()) {
			Karta max = maxKarta(ujemajoce);
			Karta predzadnji = karteNaMizi[(zacetnik+2)%4];
			if(max.vrniVrednost() > karteNaMizi[zacetnik].vrniVrednost()){
				if(barva==predzadnji.vrniBarvo() && max.vrniVrednost() > predzadnji.vrniVrednost()) {
					return max;
				} else
					return minKarta(ujemajoce);
			} else
				return minKarta(ujemajoce);
		}

		MnozicaKart aduti = neki.karteVBarvi(0);
		if(!aduti.jePrazna()) {
			Karta max = maxKarta(aduti);
			Karta predzadnji = karteNaMizi[(zacetnik+2)%4];
			if(predzadnji.vrniBarvo()==0 && max.vrniVrednost() > predzadnji.vrniVrednost()) {
				return max;
			} else
				return minKarta(aduti);
		}

		return minKarta(neki);
	}

}
