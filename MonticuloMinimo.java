package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase privada para iteradores de montículos. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
          if(indice >= elementos)
            throw new NoSuchElementException();
          return get(indice++); //Regresamos el elementos en el indice e
                                //incrementamos.
        }
    }

    /* Clase estática privada para poder implementar HeapSort. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
          this.elemento = elemento;
          indice = -1;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
          return indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
          this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
          return elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
      arbol = nuevoArreglo(100);
      elementos = 0;
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
      this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
      arbol = nuevoArreglo(n);
      elementos = n;
      int indice = 0;
      for (T e : iterable) {
        arbol[indice] = e;
        e.setIndice(indice++);
      }
      for (int x = (int) Math.floor(elementos / 2) -1; x >= 0; x--)
        acomodaAbajo(x);
    }

    private void acomodaArriba(int x) {
      //Chequemos que el indice sea válido.
      if (x < 0 || x >= elementos)
        return;
      int indicePadre = (x - 1) / 2;
      if (indicePadre < 0)
        return;
      T v = arbol[x];
      T padre = arbol[indicePadre];
      if (padre.compareTo(v) > 0) {
        intercambia(x, indicePadre);
        acomodaArriba(indicePadre);
      }
    }

    private void acomodaAbajo(int x) {
      //Chequemos que no sea hoja, si lo es terminamos.
      if (2 * x + 1 >= elementos)
        return;
      int hijoMenor = obtenerHijoMenor(x);
      T v = arbol[x];
      T vMenor = arbol[hijoMenor];
      if (v.compareTo(vMenor) > 0) {
        intercambia(x, hijoMenor);
        acomodaAbajo(hijoMenor);
      }
    }

    private void intercambia(int x, int y) {
      T e = arbol[x];
      arbol[x] = arbol[y];
      arbol[x].setIndice(x);
      arbol[y] = e;
      arbol[y].setIndice(y);
    }

    private int obtenerHijoMenor(int x){
      T arbolIzq = arbol[2 * x + 1];
      T arbolDer = null;
      if (2 * x + 2 < elementos)
        arbolDer = arbol[2 * x + 2];
      if (arbolDer == null)
        return 2 * x + 1;
      if (arbolIzq.compareTo(arbolDer) < 0)
        return 2 * x + 1;
      else if (arbolIzq.compareTo(arbolDer) > 0)
        return 2 * x + 2;
      else
        return 2 * x + 1;
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
      if (elementos == arbol.length) {
        T[] arregloAux = nuevoArreglo(2 * elementos);
        for (int x = 0; x < elementos; x++)
          arregloAux[x] = arbol[x];
        arbol = arregloAux;
      }
      arbol[elementos] = elemento;
      elemento.setIndice(elementos);
      acomodaArriba(elementos++);
    }


    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina() {
      if (elementos == 0)
        throw new IllegalStateException();
      T raiz = arbol[0];
      intercambia(0, --elementos);
      acomodaAbajo(0);
      raiz.setIndice(-1);
      return raiz;
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
        if (elemento.getIndice() < 0 || elemento.getIndice() >= elementos)
          return;
        int x = elemento.getIndice();
        T aux = arbol[x];
        intercambia(x, --elementos);
        arbol[elementos] = null;
        reordena(arbol[x]);
        aux.setIndice(-1);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
      if (elemento.getIndice() < 0 || elemento.getIndice() >= elementos)
        return false;
      return arbol[elemento.getIndice()].equals(elemento);
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <tt>true</tt> si ya no hay elementos en el montículo,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean esVacia() {
      return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
      elementos = 0;
      for (int x = 0; x < elementos; x++)
        arbol[x] = null;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento) {
      if (elemento == null)
        return;
      acomodaAbajo(elemento.getIndice());
      acomodaArriba(elemento.getIndice());
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
      return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
      if (i < 0 || i >= elementos)
        throw new NoSuchElementException();
      return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
      String s = "";
      for (int x = 0; x < elementos; x++)
        s += String.format("%s, ", arbol[x]);
      return s;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param o el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)o;
        if (elementos != monticulo.getElementos())
          return false;
        if (esVacia() && monticulo.esVacia())
          return true;
        int x = 0;
        for (T e : arbol)
          if (e.compareTo(monticulo.get(x++)) != 0)
            return false;
        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
      Lista<Adaptador<T>> l1 = new Lista<>();
      Lista<T> l2 = new Lista<>();
      for (T e : coleccion)
        l1.agrega(new Adaptador<>(e));
      MonticuloMinimo<Adaptador<T>> arbolAux = new MonticuloMinimo<>(l1);
      while (arbolAux.getElementos() > 0) {
        Adaptador<T> elementoAdaptador = arbolAux.elimina();
        l2.agrega(elementoAdaptador.elemento);
      }
      return l2;
    }
}
