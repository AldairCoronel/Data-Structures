package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase privada para iteradores de árboles binarios completos. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Constructor que recibe la raíz del árbol. */
        public Iterador() {
          cola = new Cola<>();
          if(raiz != null)
            cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
          return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
          Vertice aux = cola.saca();
          if(aux.izquierdo != null)
            cola.mete(aux.izquierdo);
          if(aux.derecho != null)
            cola.mete(aux.derecho);
          return aux.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
      if(elemento == null)
        throw new IllegalArgumentException();
      Vertice aux = nuevoVertice(elemento);
      elementos++;
      if(raiz == null){
        raiz = aux;
        return;
      }
      Cola<Vertice> cola = new Cola<>();
      cola.mete(raiz);
      while(!cola.esVacia()){
        Vertice x = cola.saca();
        if(x.izquierdo == null){
          x.izquierdo = aux;
          aux.padre = x;
          return;
        }
        if(x.derecho == null){
          x.derecho = aux;
          aux.padre = x;
          return;
        }
        if(x.izquierdo != null)
          cola.mete(x.izquierdo);
        if(x.derecho != null)
          cola.mete(x.derecho);
      }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice aux = vertice(busca(elemento));
        if(aux != null){
            elementos--;
            if(elementos == 0){
                raiz = null;
                return;
            }
            Cola<Vertice> cola = new Cola<>();
            cola.mete(raiz);
            Vertice x = raiz;
            while(!cola.esVacia()){
                if(x.hayIzquierdo())
                    cola.mete(x.izquierdo);
                if(x.hayDerecho())
                    cola.mete(x.derecho);
                x = cola.saca();
                if(cola.esVacia())
                    break;
            }
            T el_aux = x.elemento;
            x.elemento = aux.get();
            aux.elemento = el_aux;
            if(esHijoIzquierdo(x))
                x.padre.izquierdo = null;
            if(esHijoDerecho(x))
                x.padre.derecho = null;
        }
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
      if(elementos == 0)
        return -1;
      return (int)(Math.floor(Math.log(elementos)/Math.log(2)));
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
      if(raiz == null) return;
      Cola<Vertice> cola = new Cola<>();
      cola.mete(raiz);
      while(!cola.esVacia()){
        Vertice aux = cola.saca();
        accion.actua(aux);
        if(aux.izquierdo != null)
          cola.mete(aux.izquierdo);
        if(aux.derecho != null)
          cola.mete(aux.derecho);
      }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
