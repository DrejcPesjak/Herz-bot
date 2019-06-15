
package ogrodje;

import skupno.Karta;

/**
 * Objekti notranjih razredov v tem razredu predstavljajo mo"zne izide partije.
 */
public class Izid {

    /**
     * Abstraktni izhodi"s"cni razred
     */
    public static abstract class Osnova {
        private int[] dobljeneTocke;

        /**
         * Konstruktor.
         * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je prejel
         *    igralec i
         */
        protected Osnova(int[] dobljeneTocke) {
            this.dobljeneTocke = dobljeneTocke;
        }

        /**
         * Vrne tabelo, v kateri element z indeksom i podaja to"cke, ki jih je
         * v partiji prejel igralec z indeksom i.
         */
        public int[] vrniDobljeneTocke() {
            return dobljeneTocke;
        }
    }

    /**
     * Objekt tega razreda predstavlja regularen zaklju"cek partije (partija
     * se je iztekla do konca, ne da bi kateri od igralcev odigral neveljavno
     * potezo ali prekora"cil "casovno omejitev).
     */
    public static class Regularen extends Osnova {

        /**
         * Konstruktor.
         * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je prejel
         *    igralec i
         */
        public Regularen(int[] dobljeneTocke) {
            super(dobljeneTocke);
        }

        @Override
        public String toString() {
            return String.format("regularen");
        }
    }

    /**
     * Objekt tega razreda predstavlja zaklju"cek partije z neveljavno potezo.
     */
    public static class NeveljavnaPoteza extends Osnova {
        private int akter;
        private Karta napacnaKarta;

        /**
         * Konstruktor.
         * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je prejel
         *    igralec i
         * @param akter indeks igralca, ki je izbral neveljavno potezo
         * @param napacnaKarta karta, ki jo je akter posku"sal odvre"ci
         */
        public NeveljavnaPoteza(int[] dobljeneTocke, int akter, Karta napacnaKarta) {
            super(dobljeneTocke);
            this.akter = akter;
            this.napacnaKarta = napacnaKarta;
        }

        @Override
        public String toString() {
            return String.format("igralec [%d] je odigral neveljavno potezo (%s)", this.akter, this.napacnaKarta);
        }
    }

    /**
     * Objekt tega razreda predstavlja zaklju"cek partije s prekora"citvijo
     * "casovne omejitve.
     */
    public static class PrekoracitevCasa extends Osnova {
        private int akter;

        /**
         * Konstruktor.
         * @param dobljeneTocke dobljeneTocke[i] = to"cke, ki jih je prejel
         *    igralec i
         * @param akter indeks igralca, ki je prekora"cil "cas
         */
        public PrekoracitevCasa(int[] dobljeneTocke, int akter) {
            super(dobljeneTocke);
            this.akter = akter;
        }

        @Override
        public String toString() {
            return String.format("igralec [%d] je prekoračil časovno omejitev", this.akter);
        }
    }
}
