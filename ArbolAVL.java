package mx.unam.ciencias.edd;

/**
 * <p>Clase para árboles AVL.</p>
 *
 * <p>Un árbol AVL cumple que para cada uno de sus vértices, la diferencia entre
 * la áltura de sus subárboles izquierdo y derecho está entre -1 y 1.</p>
 */
public class ArbolAVL<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices de árboles AVL. La única diferencia
     * con los vértices de árbol binario, es que tienen una variable de clase
     * para la altura del vértice.
     */
    protected class VerticeAVL extends Vertice {

        /** La altura del vértice. */
        public int altura;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeAVL(T elemento) {
          super(elemento);
          altura = 0;
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
            return altura;
        }

        /**
         * Regresa una representación en cadena del vértice AVL.
         * @return una representación en cadena del vértice AVL.
         */
        @Override public String toString() {
          return String.format("%s %d/%d", elemento.toString(), altura, balance(this));
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param o el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeAVL}, su elemento es igual al elemento de éste
         *         vértice, los descendientes de ambos son recursivamente
         *         iguales, y las alturas son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked") VerticeAVL vertice = (VerticeAVL)o;
          return (altura == vertice.altura && super.equals(o));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolAVL() {
      super();
    }

    /**
     * Construye un árbol AVL a partir de una colección. El árbol AVL tiene los
     * mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol AVL.
     */
    public ArbolAVL(Coleccion<T> coleccion) {
      super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeAVL}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeAVL(elemento);
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol girándolo como
     * sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
      super.agrega(elemento);
      rebalancear(verticeAVL(ultimoAgregado.padre));
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y gira el árbol como sea necesario para rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
      /** Buscamos el elemento */
      VerticeAVL vertice = verticeAVL(super.busca(elemento, raiz));
      /** Si no está, terminamos */
      if(vertice == null)
        return;
      /**
        * Si nuestro vértice tiene dos hijos distintos de null
        * intercambiamos el vértice con el máximo del subárbol izquierdo
        */
      if(vertice.izquierdo != null && vertice.derecho != null){
        /**
         * Intercambio el contenido del vértice, así no tengo que
         * molestarme con las referencias
         */
        VerticeAVL auxiliar = vertice;
        vertice = verticeAVL(super.maxSubArbol(vertice.izquierdo));
        auxiliar.elemento = vertice.elemento;
      }
      /** Si el vértice es una hoja */
      if(vertice.izquierdo == null && vertice.derecho == null)
        eliminarHoja(vertice);
      else
        subirHijo(vertice);
      rebalancear(verticeAVL(vertice.padre));
      elementos--;
    }

    /**
     * Método auxiliar para eliminar una hoja
     * @param vertice vértice hoja
     */
    private void eliminarHoja(VerticeAVL vertice){
      /** En caso de que sea la raíz */
      if(vertice == raiz)
        raiz = ultimoAgregado = null;
      /** Si no, debemos desconectarlo del árbol */
      else if(esHijoDerecho(vertice))
        vertice.padre.derecho = null;
      else
        vertice.padre.izquierdo = null;
    }

    /**
      * Método auxiliar para subir al hijo del vértice al lugar del vértice
      */
    private void subirHijo(VerticeAVL vertice){
      if(vertice.izquierdo != null){
        /** Primero chequemos si no es la raíz */
        if(vertice == raiz){
          raiz = vertice.izquierdo;
          raiz.padre = null;
        }
        else{
          vertice.izquierdo.padre = vertice.padre;
          if(esHijoIzquierdo(vertice))
            vertice.padre.izquierdo = vertice.izquierdo;
          else
            vertice.padre.derecho = vertice.izquierdo;
        }
      }
      else{
        /** Primero chequemos si no es la raíz */
        if(vertice == raiz){
          raiz = raiz.derecho;
          raiz.padre = null;
        }
        else{
          vertice.derecho.padre = vertice.padre;
          if(esHijoDerecho(vertice))
            vertice.padre.derecho = vertice.derecho;
          else
            vertice.padre.izquierdo = vertice.derecho;
        }
      }
    }

    /**
     * Método auxiliar que me da el balance del vértice
     * @param vertice vértice a sacar balance
     * @return balance el balance de ese vértice
     */
    private int balance(VerticeAVL vertice){
      return obtenerAltura(verticeAVL(vertice.izquierdo)) - obtenerAltura(verticeAVL(vertice.derecho));
    }

    /**
     * Método auxiliar para rebalancear el Árbol AVL
     * @param vertice vértice AVL
     */
    private void rebalancear(VerticeAVL vertice){
      /** Caso base */
      if(vertice == null)
        return;
      /** Actualizamos la altura */
      actualizarAltura(vertice);
      if(balance(vertice) == -2){
        /** Están cruzados */
        if(balance(verticeAVL(vertice.derecho)) == 1)
          giraDerechaAVL(verticeAVL(vertice.derecho));
        giraIzquierdaAVL(vertice);
      }
      else if(balance(vertice) == 2) {
        /** Están cruzados */
        if(balance(verticeAVL(vertice.izquierdo)) == -1)
          giraIzquierdaAVL(verticeAVL(vertice.izquierdo));
        giraDerechaAVL(vertice);
      }
      rebalancear(verticeAVL(vertice.padre));
    }

    /**
      * Método que hace el giro izquierdo AVL
      * @param vertice vértice AVL
      */
    private void giraIzquierdaAVL(VerticeAVL vertice){
        super.giraIzquierda(vertice);
        actualizarAltura(vertice);
        actualizarAltura(verticeAVL(vertice.padre));
    }


    /**
      * Método que hace el giro derecho AVL
      * @param vertice vértice AVL
      */
    private void giraDerechaAVL(VerticeAVL vertice){
        super.giraDerecha(vertice);
        actualizarAltura(vertice);
        actualizarAltura(verticeAVL(vertice.padre));
    }

    /**
     * Método que nos ayuda para actualizar la altura
     * @param vertice vértice AVL
     */
    private void actualizarAltura(VerticeAVL vertice){
      vertice.altura = cambiarAltura(vertice);
    }

    /**
     * Método auxiliar para cambiar la altura del vértice en el rebalanceo
     * @param vertice vértice AVL
     * @return altura nueva altura
     */
    private int cambiarAltura(VerticeAVL vertice){
      return 1 + Math.max(obtenerAltura(verticeAVL(vertice.izquierdo)), obtenerAltura(verticeAVL(vertice.derecho)));
    }
    /**
     * Método auxiliar que obtiene la altura del vértice AVL
     * @param vertice vértice del cual buscamos la altura
     * @return altura la altura de ese vértice
     */
    private int obtenerAltura(VerticeAVL vertice){
      return (vertice == null) ? -1 : verticeAVL(vertice).altura;
    }

    /**
     * Método auxiliar que hace la audición de VerticeArbolBinario A VerticeAVL
     * @param vertice VerticeArbolBinario
     * @return vertice VerticeAVL
     */
    private VerticeAVL verticeAVL(VerticeArbolBinario<T> vertice){
      return (VerticeAVL)vertice;
    }
    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la derecha por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la izquierda por el " +
                                                "usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la izquierda por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la derecha por el " +
                                                "usuario.");
    }
}
