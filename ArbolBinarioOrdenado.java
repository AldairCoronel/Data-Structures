package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase privada para iteradores de árboles binarios ordenados. */
    private class Iterador implements Iterator<T> {

    /* Pila para recorrer los vértices en DFS in-order. */
    private Pila<Vertice> pila;

    /* Construye un iterador con el vértice recibido. */
    public Iterador() {
      pila = new Pila<>();
      if(raiz != null){
        pila.mete(raiz);
        Vertice verticeAyuda = raiz;
        while(verticeAyuda.hayIzquierdo()){
          pila.mete(verticeAyuda.izquierdo);
          verticeAyuda = verticeAyuda.izquierdo;
        }
	    }
    }

    /* Nos dice si hay un elemento siguiente. */
    @Override public boolean hasNext() {
      return !pila.esVacia();
    }

    /* Regresa el siguiente elemento en orden DFS in-order. */
    @Override public T next() {
  	  Vertice aux = pila.saca();
  	  if(aux.hayDerecho()){
  	    pila.mete(aux.derecho);
  	    Vertice ayuda = aux.derecho;
  	    while(ayuda.hayIzquierdo()){
  	      pila.mete(ayuda.izquierdo);
  	      ayuda = ayuda.izquierdo;
  	    }
  	  }
  	  return aux.elemento;
          }
      }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if(elemento == null) throw new IllegalArgumentException();
        Vertice aux = nuevoVertice(elemento);
        ultimoAgregado = aux;
        elementos++;
        if(raiz == null)
            raiz = aux;
        else
            agrega(raiz, aux);
    }

    private void agrega(Vertice x, Vertice y){
        if(y.get().compareTo(x.get()) <= 0){
            if(x.izquierdo == null){
                x.izquierdo = y;
                y.padre = x;
                return;
            }
            else
                agrega(x.izquierdo, y);
        }
        else{
            if(x.derecho == null){
                x.derecho = y;
                y.padre = x;
                return;
            }
            else
                agrega(x.derecho, y);
        }
    }


    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento){
      Vertice aux = vertice(busca(elemento));
      if(aux != null) {
          elementos--;
          if(aux.izquierdo != null && aux.derecho != null)
          aux = intercambiaEliminable(aux);
          eliminaVertice(aux);
      }
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
      Vertice maximo = maxSubArbol(vertice.izquierdo);
      T e = vertice.elemento;
      vertice.elemento = maximo.elemento;
      maximo.elemento = e;
      return maximo;
    }


    public Vertice maxSubArbol(Vertice x){
      if(x.derecho == null)
        return x;
      return maxSubArbol(x.derecho);
    }



    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
      Vertice x = (vertice.izquierdo != null && vertice.derecho == null)
              ? vertice.izquierdo : vertice.derecho;
      if(x != null)
        x.padre = vertice.padre;
      if(vertice.padre != null){
        if(esHijoIzquierdo(vertice))
          vertice.padre.izquierdo = x;
        else
          vertice.padre.derecho = x;
        return;
      }
      raiz = x;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <tt>null</tt>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <tt>null</tt> en otro caso.
     */
     @Override public VerticeArbolBinario<T> busca(T elemento) {
         if(elemento == null || esVacia())
           return null;
         return busca(elemento, raiz);
     }


     public VerticeArbolBinario<T> busca(T elemento, VerticeArbolBinario<T> vertice) {
       if(elemento == null || vertice == null)
         return null;
       if(elemento.compareTo(vertice.get()) == 0)
         return vertice;
       else if(elemento.compareTo(vertice.get()) > 0 && vertice.hayDerecho())
         return busca(elemento, vertice.derecho());
       else if (vertice.hayIzquierdo())
         return busca(elemento, vertice.izquierdo());
       return null;
     }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
      Vertice aux_q = vertice(vertice);
      if(aux_q.izquierdo != null){
        Vertice aux_p = aux_q.izquierdo;
        aux_p.padre = aux_q.padre;
        if(aux_q.hayPadre()){
          if(esHijoIzquierdo(aux_q))
            aux_q.padre.izquierdo = aux_p;
          else
            aux_q.padre.derecho = aux_p;
        }
        else
          raiz = aux_p;
        aux_q.padre = aux_p;
        aux_q.izquierdo = aux_p.derecho;
        if(aux_p.derecho != null)
          aux_p.derecho.padre = aux_q;
        aux_p.derecho = aux_q;
      }
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        Vertice aux_q = vertice(vertice);
        if(aux_q.derecho != null){
          Vertice aux_p = aux_q.derecho;
          aux_p.padre = aux_q.padre;
          if(aux_q.hayPadre()){
            if(esHijoDerecho(aux_q))
              aux_q.padre.derecho = aux_p;
            else
              aux_q.padre.izquierdo = aux_p;
          }
          else
            raiz = aux_p;
          aux_q.padre = aux_p;
          aux_q.derecho = aux_p.izquierdo;
          if(aux_p.izquierdo != null)
            aux_p.izquierdo.padre = aux_q;
          aux_p.izquierdo = aux_q;
        }
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(accion, raiz);
    }

    private void dfsPreOrder(AccionVerticeArbolBinario<T> accion, Vertice n){
      if(n == null) return;
      accion.actua(n);
      dfsPreOrder(accion, n.izquierdo);
      dfsPreOrder(accion, n.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
      dfsInOrder(accion, raiz);
    }

    private void dfsInOrder(AccionVerticeArbolBinario<T> accion, Vertice n){
      if(n == null) return;
      dfsInOrder(accion, n.izquierdo);
      accion.actua(n);
      dfsInOrder(accion, n.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
      dfsPostOrder(accion, raiz);
    }

    private void dfsPostOrder(AccionVerticeArbolBinario<T> accion, Vertice n){
      if(n == null) return;
      dfsPostOrder(accion, n.izquierdo);
      dfsPostOrder(accion, n.derecho);
      accion.actua(n);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
