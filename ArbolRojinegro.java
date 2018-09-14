package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<tt>null</tt>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices de árboles rojinegros. La única
     * diferencia con los vértices de árbol binario, es que tienen un campo para
     * el color del vértice.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        public String toString() {
              String letra = (color == Color.ROJO) ? "R" : "N";
              return String.format("%s{%s}", letra, elemento.toString());
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param o el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)o;
            return (color == vertice.color && super.equals(o));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() {
        super();
    }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
      super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
      return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
      return verticeRojinegro(vertice).color;
    }

    /**
      * Hace el casting de VerticeArbolBinario a VerticeRojinegro
      * @param vertice El vertice arbol binario
      * @return vRN El vertice rojinegros
      */
    private VerticeRojinegro verticeRojinegro(VerticeArbolBinario<T> vertice){
      VerticeRojinegro vRN = (VerticeRojinegro)vertice;
      return vRN;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro auxiliar = verticeRojinegro(ultimoAgregado);
        auxiliar.color = Color.ROJO;
        rebalancearAgrega(auxiliar);
    }

    /**
      * Algoritmo auxiliar recursivo para rebalancear el árbol rojinegro
      * @param vertice vertice rojinegro
      */
    private void rebalancearAgrega(VerticeRojinegro vertice){
      VerticeRojinegro padre, tio, abuelo;
      /** Caso 1
       * El vértice v tiene padre nulo
       * Coloreamos v de negro y terminamos
       */
      if(vertice.padre == null){
        raiz = vertice;
        vertice.color = Color.NEGRO;
        return;
      }
      padre = verticeRojinegro(vertice.padre);
      /** Caso 2
       * El vértice padre es negro
       * Terminamos
       */
      if(padre.color == Color.NEGRO)
        return;
      abuelo = verticeRojinegro(padre.padre);
      /** Caso 3
       * El tío es rojo, padre también es rojo
       * Coloreamos al tío y al padre de negro
       * Coloreamos al abuelo de rojo
       * Hacemos recursión sobre el abuelo
       */
      tio = obtenerTio(padre, abuelo);
      if(tio != null && tio.color == Color.ROJO){
        padre.color = tio.color = Color.NEGRO;
        abuelo.color = Color.ROJO;
        rebalancearAgrega(abuelo);
        return;
      }
      /** Caso 4
       * El vertice y el padre están cruzados
       */
      if(esHijoIzquierdo(padre) ^ esHijoIzquierdo(vertice)){
        if(esHijoIzquierdo(padre))
          super.giraIzquierda(padre);
        else
          super.giraDerecha(padre);
        /** Actualizamos referencias */
        VerticeRojinegro auxiliar = vertice;
        vertice = padre;
        padre = auxiliar;
      }
      /** Caso 5
       * El vértice y el padre no están cruzados
       */
      padre.color = Color.NEGRO;
      abuelo.color = Color.ROJO;
      if(esHijoIzquierdo(vertice))
        super.giraDerecha(abuelo);
      else
        super.giraIzquierda(abuelo);
    }

    /**
      * Algoritmo auxiliar para obtener al tio
      * @param padre vértice padre
      * @param abuelo vértice abuelo
      * @return tio vértice tío
      */
    private VerticeRojinegro obtenerTio(VerticeRojinegro padre, VerticeRojinegro abuelo){
      return esHijoDerecho(padre) ? verticeRojinegro(abuelo.izquierdo)  : verticeRojinegro(abuelo.derecho);
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro vertice = verticeRojinegro(busca(elemento, raiz));
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
          VerticeRojinegro auxiliar = vertice;
          vertice = verticeRojinegro(super.maxSubArbol(vertice.izquierdo));
          auxiliar.elemento = vertice.elemento;
        }
        /** Vértice fanstasma */
        VerticeRojinegro fantasma = null;
        if(vertice.izquierdo == null && vertice.derecho == null){
            fantasma = verticeRojinegro(nuevoVertice(null));
            fantasma.color = Color.NEGRO;
            fantasma.padre = vertice;
            vertice.izquierdo = fantasma;
        }
        /** Vértice hijo */
        VerticeRojinegro hijo;
        hijo = obtenerHijo(vertice);
        /** Desconectamos el vértice a eliminar del resto del árbol */
        subirHijo(vertice);
        /** 3 casos
         * 1) El hijo es rojo y el vértice negro. Coloreamos al hijo de negro y terminamos.
         * 2) El vértice es rojo y el hijo negro. No hacemos nada.
         * 3) El vértice y el hijo son negros. Rebalanceamos sobre h.
         */
        if(esNegro(vertice) && esNegro(hijo)){
          hijo.color = Color.NEGRO;
          rebalancearElimina(hijo);
        }
        else
          hijo.color = Color.NEGRO;
        eliminarFantasma(fantasma);
        /** No olvidemos restar al vértice elimanado */
        elementos--;
    }

    /**
     * Método auxilar para rebalancear el árbol al eliminar un vértice
     * @param vertice vértice negro distinto de null (puede ser fantasma)
     */
    private void rebalancearElimina(VerticeRojinegro vertice){
      /** Caso 1
       * El vértice tiene padre null. Terminamos.
       */
      if(vertice.padre == null){
        vertice.color = Color.NEGRO;
        raiz = vertice;
        return;
      }
      /** Vértice padre */
      VerticeRojinegro padre = verticeRojinegro(vertice.padre);
      /** Vértice hermano */
      VerticeRojinegro hermano = obtenerHermano(vertice);
      /** Caso 2
       * El vértice hermano es rojo y el padre es negro.
       * Coloreamos al padre de rojo, al hermano de negro y giramos sobre el el padre en dirección del vértice.
       */
      if(hermano.color == Color.ROJO){
        padre.color = Color.ROJO;
        hermano.color = Color.NEGRO;
        if(esHijoIzquierdo(vertice))
          super.giraIzquierda(padre);
        else
          super.giraDerecha(padre);
        /** Actualizamos las referencias */
        padre = verticeRojinegro(vertice.padre);
        hermano = obtenerHermano(vertice);
      }
      /** Caso 3
       * El padre, hermano, sobrino izquierdo, sobrino derecho son negros.
       * (Los sobrinos pueden ser nulos)
       * Coloramos al hermano de rojo, hacemos recursión sobre el padre.
       * Terminamos.
       */
      /** Vértice sobrino izquierdo y sobrino derecho */
      VerticeRojinegro sobrinoIzquierdo = verticeRojinegro(hermano.izquierdo);
      VerticeRojinegro sobrinoDerecho = verticeRojinegro(hermano.derecho);
      if (esNegro(padre) && esNegro(hermano) && sobrinosNegros(sobrinoIzquierdo, sobrinoDerecho)) {
        hermano.color = Color.ROJO;
        rebalancearElimina(padre);
        return;
      }
      /** Caso 4
       * Los vértices hermano, sobrino izquierdo y sobrino derecho son negros, el padre es rojo.
       * Coloreamos al hermano de rojo, al padre de negro y terminamos.
       */
       if (esNegro(hermano) && sobrinosNegros(sobrinoIzquierdo, sobrinoDerecho) && padre.color == Color.ROJO) {
                   padre.color = Color.NEGRO;
                   hermano.color = Color.ROJO;
                   return;
               }
      /** Caso 5
       * El vértice es izquierdo y los sobrinos son bicolor (rojo y negro).
       * El vértice es derecho y los sobrinos son bicolor (negro y rojo).
       * Coloramos al hermano de rojo, a su hijo rojo de negro y giramos sobre el hermano en dirección contraria al vértice.
       */
       if(sobrinosCruzados(vertice, sobrinoIzquierdo, sobrinoDerecho) && sonVerticesBicolor(sobrinoIzquierdo, sobrinoDerecho)){
        hermano.color = Color.ROJO;
        if(!esNegro(sobrinoIzquierdo))
          sobrinoIzquierdo.color = Color.NEGRO;
        else
          sobrinoDerecho.color = Color.NEGRO;
        if(esHijoIzquierdo(vertice))
          super.giraDerecha(hermano);
        else
          super.giraIzquierda(hermano);
        /** Preparamos para caso 6 */
        hermano = obtenerHermano(vertice);
        sobrinoIzquierdo = verticeRojinegro(hermano.izquierdo);
        sobrinoDerecho =  verticeRojinegro(hermano.derecho);
       }
       /** Caso 6
        * Vértice es izquierdo y el sobrino derecho rojo.
        * Vértice es derecho y el sobrino izquierdo rojo.
        * Coloreamos al hermano con el mismo color del padre y al padre de negro.
        * Coloreamos al hijo del hermano con dirección contraria al vértice de negro.
        * Giramos sobre el padre en la dirección del vértice.
        */
        hermano.color = padre.color;
        padre.color = Color.NEGRO;
        if(esHijoDerecho(vertice)){
          sobrinoIzquierdo.color = Color.NEGRO;
          super.giraDerecha(padre);
        }
        else{
          sobrinoDerecho.color = Color.NEGRO;
          super.giraIzquierda(padre);
        }
    }

    /**
     * Método auxiliar para checar si el vértice es negro.
     * @param vertice vértice a checar si es negro
     * @return true si el vértice es null o el color es negro.
     */
    private boolean esNegro(VerticeRojinegro vertice) {
        return vertice == null || vertice.color == Color.NEGRO;
    }

    /**
     * Método auxiliar para checar si los sobrinos izquierdo y derecho son negros.
     * @param sobrinoIzquierdo sobrino izquierdo.
     * @param sobrinoDerecho sobrino derecho.
     * @return true si ambos son negros false lo contrario.
     */
    private boolean sobrinosNegros(VerticeRojinegro sobrinoIzquierdo, VerticeRojinegro sobrinoDerecho) {
      return esNegro(sobrinoIzquierdo) && esNegro(sobrinoDerecho);
    }

    /**
     * Método auxiliar para ver si los vertices son bicolor.
     * @param vertice_1 vértice 1
     * @param vertice_2 vértice 2
     * @return true si son bicolor, falso si no lo son.
     */
    private boolean sonVerticesBicolor(VerticeRojinegro vertice_1, VerticeRojinegro vertice_2){
      return esNegro(vertice_1) ^ esNegro(vertice_2);
    }

    /**
     * Método auxiliar para ver si los sobrinos son cruzados.
     * @param vertice vértice
     * @param sobrinoIzquierdo sobrino izquierdo
     * @param sobrinoDerecho sobrino derecho
     * @return true si son sobrinos cruzados, false si no lo son.
     */
    private boolean sobrinosCruzados(VerticeRojinegro vertice, VerticeRojinegro sobrinoIzquierdo, VerticeRojinegro sobrinoDerecho){
      return esNegro(sobrinoDerecho) && esHijoIzquierdo(vertice) || esNegro(sobrinoIzquierdo) && esHijoDerecho(vertice);
    }

    /**
      * Método auxiliar para obtener al hermano de un vértice
      * @param vertice vértice a buscar el hermano
      */
    private VerticeRojinegro obtenerHermano(VerticeRojinegro vertice){
      return (esHijoDerecho(vertice)) ? verticeRojinegro(vertice.padre.izquierdo) : verticeRojinegro(vertice.padre.derecho);
    }

    /**
     * Método auxiliar para eliminar al fantasma
     * @param fantasma el vértice fantasma a eliminar
     */
    private void eliminarFantasma(VerticeRojinegro fantasma){
      if(fantasma != null){
        if(fantasma == raiz)
          raiz = ultimoAgregado = fantasma = null;
        else
          if(esHijoIzquierdo(fantasma))
            fantasma.padre.izquierdo = null;
          else
            fantasma.padre.derecho = null;
      }
    }

    /**
      * Método auxiliar para subir al hijo del vértice al lugar del vértice
      */
    private void subirHijo(VerticeRojinegro vertice){
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
     * Método auxiliar para obtener al hijo del vértice
     * @param vertice el vértice a buscar su hijo
     * @return hijo vértice hijo de vértice
     */
    private VerticeRojinegro obtenerHijo(VerticeRojinegro vertice){
      return (vertice.izquierdo != null) ? verticeRojinegro(vertice.izquierdo) : verticeRojinegro(vertice.derecho);
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
