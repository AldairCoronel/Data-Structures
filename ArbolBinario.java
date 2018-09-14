package mx.unam.ciencias.edd;

import java.util.NoSuchElementException;

/**
 * <p>Clase abstracta para árboles binarios genéricos.</p>
 *
 * <p>La clase proporciona las operaciones básicas para árboles binarios, pero
 * deja la implementación de varias en manos de las subclases concretas.</p>
 */
public abstract class ArbolBinario<T> implements Coleccion<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class Vertice implements VerticeArbolBinario<T> {

        /** El elemento del vértice. */
        public T elemento;
        /** El padre del vértice. */
        public Vertice padre;
        /** El izquierdo del vértice. */
        public Vertice izquierdo;
        /** El derecho del vértice. */
        public Vertice derecho;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public Vertice(T elemento) {
          this.elemento = elemento;
        }

        /**
         * Nos dice si el vértice tiene un padre.
         * @return <tt>true</tt> si el vértice tiene padre,
         *         <tt>false</tt> en otro caso.
         */
        @Override public boolean hayPadre() {
          return padre != null;
        }

        /**
         * Nos dice si el vértice tiene un izquierdo.
         * @return <tt>true</tt> si el vértice tiene izquierdo,
         *         <tt>false</tt> en otro caso.
         */
        @Override public boolean hayIzquierdo() {
          return izquierdo != null;
        }

        /**
         * Nos dice si el vértice tiene un derecho.
         * @return <tt>true</tt> si el vértice tiene derecho,
         *         <tt>false</tt> en otro caso.
         */
        @Override public boolean hayDerecho() {
          return derecho != null;
        }

        /**
         * Regresa el padre del vértice.
         * @return el padre del vértice.
         * @throws NoSuchElementException si el vértice no tiene padre.
         */
        @Override public VerticeArbolBinario<T> padre() {
          if(!hayPadre())
            throw new NoSuchElementException();
          return padre;
        }

        /**
         * Regresa el izquierdo del vértice.
         * @return el izquierdo del vértice.
         * @throws NoSuchElementException si el vértice no tiene izquierdo.
         */
        @Override public VerticeArbolBinario<T> izquierdo() {
          if(!hayIzquierdo())
            throw new NoSuchElementException();
          return izquierdo;
        }

        /**
         * Regresa el derecho del vértice.
         * @return el derecho del vértice.
         * @throws NoSuchElementException si el vértice no tiene derecho.
         */
        @Override public VerticeArbolBinario<T> derecho() {
          if(!hayDerecho())
            throw new NoSuchElementException();
          return derecho;
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
          int izq_aux =  hayIzquierdo() ? izquierdo.altura() : -1;
          int der_aux = hayDerecho() ? derecho.altura() : -1;
          return 1 + Math.max(izq_aux, der_aux);
        }

        /**
         * Regresa la profundidad del vértice.
         * @return la profundidad del vértice.
         */
        @Override public int profundidad() {
          if(!(hayPadre()))
            return 0;
          return 1 + padre.profundidad();
        }

        /**
         * Regresa el elemento al que apunta el vértice.
         * @return el elemento al que apunta el vértice.
         */
        @Override public T get() {
          return elemento;
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>. Las clases que extiendan {@link Vertice} deben
         * sobrecargar el método {@link Vertice#equals}.
         * @param o el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link Vertice}, su elemento es igual al elemento de éste
         *         vértice, y los descendientes de ambos son recursivamente
         *         iguales; <code>false</code> en otro caso.
         */
        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked") Vertice vertice = (Vertice)o;
            if(!(elemento.equals(vertice.elemento))) return false;
            boolean der_aux = (derecho != null) ? derecho().equals(vertice.derecho) : true;
            boolean izq_aux = (izquierdo != null) ? izquierdo().equals(vertice.izquierdo) : true;
            return izq_aux && der_aux;
        }

        /**
         * Regresa una representación en cadena del vértice.
         * @return una representación en cadena del vértice.
         */
        public String toString() {
          return String.valueOf(elemento);
        }
    }

    /** La raíz del árbol. */
    protected Vertice raiz;
    /** El número de elementos */
    protected int elementos;

    /**
     * Constructor sin parámetros. Tenemos que definirlo para no perderlo.
     */
    public ArbolBinario() {}

    /**
     * Construye un árbol binario a partir de una colección. El árbol binario
     * tendrá los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario.
     */
    public ArbolBinario(Coleccion<T> coleccion) {
      for(T el : coleccion)
        agrega(el);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link Vertice}. Para
     * crear vértices se debe utilizar este método en lugar del operador
     * <code>new</code>, para que las clases herederas de ésta puedan
     * sobrecargarlo y permitir que cada estructura de árbol binario utilice
     * distintos tipos de vértices.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    protected Vertice nuevoVertice(T elemento) {
        return new Vertice(elemento);
    }

    public int profundidad() {
           return profundidad(raiz);
       }

       private int profundidad(Vertice vertice) {
           if (vertice == null)
               return -1;
           return 1 + Math.max(profundidad(vertice.izquierdo), profundidad(vertice.derecho));
       }
       
    /**
     * Regresa la altura del árbol. La altura de un árbol es la altura de su
     * raíz.
     * @return la altura del árbol.
     */
    public int altura() {
      return (raiz != null) ? raiz.altura() : -1;
    }




    /**
     * Regresa el número de elementos que se han agregado al árbol.
     * @return el número de elementos en el árbol.
     */
    @Override public int getElementos() {
      return elementos;
    }

    /**
     * Nos dice si un elemento está en el árbol binario.
     * @param elemento el elemento que queremos comprobar si está en el árbol.
     * @return <code>true</code> si el elemento está en el árbol;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
      return busca(elemento) != null;
    }

    /**
     * Busca el vértice de un elemento en el árbol. Si no lo encuentra regresa
     * <tt>null</tt>.
     * @param elemento el elemento para buscar el vértice.
     * @return un vértice que contiene el elemento buscado si lo encuentra;
     *         <tt>null</tt> en otro caso.
     */
    public VerticeArbolBinario<T> busca(T elemento) {
        return buscaAux(raiz, elemento);
    }

    protected Vertice buscaAux(Vertice vertice, T elemento) {
        if(vertice == null)
          return null;
        if (vertice.get().equals(elemento))
            return vertice;
        Vertice izq = buscaAux(vertice.izquierdo, elemento);
        Vertice der = buscaAux(vertice.derecho, elemento);
        return izq != null ? izq : der;
    }




    /**
     * Regresa el vértice que contiene la raíz del árbol.
     * @return el vértice que contiene la raíz del árbol.
     * @throws NoSuchElementException si el árbol es vacío.
     */
    public VerticeArbolBinario<T> raiz() {
      if(raiz == null)
        throw new NoSuchElementException();
      return raiz;
    }

    /**
     * Nos dice si el árbol es vacío.
     * @return <code>true</code> si el árbol es vacío, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
      return raiz == null;
    }

    /**
     * Limpia el árbol de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
      raiz = null;
      elementos = 0;
    }

    /**
     * Compara el árbol con un objeto.
     * @param o el objeto con el que queremos comparar el árbol.
     * @return <code>true</code> si el objeto recibido es un árbol binario y los
     *         árboles son iguales; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked")
            ArbolBinario<T> arbol = (ArbolBinario<T>)o;
        if(elementos != arbol.elementos) return false;
        if(raiz == null && arbol.raiz == null) return true;
        if(raiz == null && arbol.raiz != null) return false;
        return raiz.equals(arbol.raiz());
    }

    protected boolean esHijoIzquierdo(Vertice v){
      return v.padre.hayIzquierdo() && v.padre.izquierdo == v;
    }

    protected boolean esHijoDerecho(Vertice v){
      return v.padre.hayDerecho() && v.padre.derecho == v;
    }

    /**
     * Regresa una representación en cadena del árbol.
     * @return una representación en cadena del árbol.
     */
    @Override public String toString() {
        if (raiz == null)
            return "";
        int t = altura() + 1;
        int[] a = new int[t];
        for (int i = 0; i < altura() + 1; i++)
            a[i] = 0;
        return toString(raiz, 0, a);
    }

    private String dibujaEspacios(int l, int[] a) {
        String s = "";
        for (int i = 0; i <= l - 1; i++)
            s += (a[i] == 1) ? "│  " : "   ";
        return s;
    }

    private String toString(Vertice v, int l, int[] a) {
        String s = v.toString() + '\n';
        a[l] = 1;
        if (v.izquierdo != null && v.derecho != null) {
            s += dibujaEspacios(l, a);
            s += "├─›";
            s += toString(v.izquierdo, l + 1, a);
            s += dibujaEspacios(l, a);
            s += "└─»";
            a[l] = 0;
            s += toString(v.derecho, l + 1, a);
        }
        else if (v.izquierdo != null) {
            s += dibujaEspacios(l, a);
            s += "└─›";
            a[l] = 0;
            s += toString(v.izquierdo, l + 1, a);
        }
        else if (v.derecho != null) {
            s += dibujaEspacios(l, a);
            s += "└─»";
            a[l] = 0;
            s += toString(v.derecho, l + 1, a);
        }
        return s;
    }


    /**
     * Convierte el vértice (visto como instancia de {@link
     * VerticeArbolBinario}) en vértice (visto como instancia de {@link
     * Vertice}). Método auxiliar para hacer esta audición en un único lugar.
     * @param vertice el vértice de árbol binario que queremos como vértice.
     * @return el vértice recibido visto como vértice.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         Vertice}.
     */
    protected Vertice vertice(VerticeArbolBinario<T> vertice) {
        return (Vertice)vertice;
    }
}
