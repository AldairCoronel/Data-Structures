package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase Nodo privada para uso interno de la clase Lista. */
    private class Nodo {
        /* El elemento del nodo. */
        public T elemento;
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nodo con un elemento. */
        public Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase Iterador privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nuevo iterador. */
        public Iterador() {
	         siguiente = cabeza;
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
	         return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
          if(hasNext()){
            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior.elemento;
          }
          throw new NoSuchElementException();
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
          return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
          if(hasPrevious()){
              siguiente = anterior;
              anterior = anterior.anterior;
              return siguiente.elemento;
         }
         throw new NoSuchElementException();
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
          anterior = null;
          siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
          anterior = rabo;
          siguiente = null;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
        return getLongitud();
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
      return getLongitud() == 0;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
	     agregaFinal(elemento);
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
      if(elemento == null)
    	    throw new IllegalArgumentException();
      Nodo n = new Nodo(elemento);
    	if(esVacia())
    	    cabeza = rabo = n;
    	else{
    	    n.anterior = rabo;
    	    rabo = n;
    	    n.anterior.siguiente = n;
    	}
    	longitud++;
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
      if(elemento == null)
        throw new IllegalArgumentException();
      Nodo n = new Nodo(elemento);
      if(esVacia())
        cabeza = rabo = n;
      else{
        n.siguiente = cabeza;
        cabeza = n;
        n.siguiente.anterior = n;
      }
      longitud++;
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
      if(elemento == null)
        throw new IllegalArgumentException();
      if(i <= 0)
        agregaInicio(elemento);
      else if(i >= getLongitud())
        agregaFinal(elemento);
      else{
        Nodo n = new Nodo(elemento);
        Nodo t = cabeza;
        int contador = 0;
        while(t != null){
          if(i <= contador++){
            n.siguiente = t;
            n.anterior = t.anterior;
            t.anterior.siguiente = n;
            t.anterior = n;
            longitud++;
            return;
          }
          t = t.siguiente;
        }
      }
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
     /**
      * Elimina un elemento de la lista. Si el elemento no está contenido en la
      * lista, el método no la modifica.
      * @param elemento el elemento a eliminar.
      */
     @Override public void elimina(T elemento) {
         if (longitud == 1) {
             limpia();
             return;
         }
         Iterador it = new Iterador();
         while (it.hasNext()) {
             Nodo n = it.siguiente;
             if (elemento.equals(it.next())) {
                 if (n.anterior != null && n.siguiente != null) {
                     n.anterior.siguiente = n.siguiente;
                     n.siguiente.anterior = n.anterior;
                 }
                 else if (n.anterior == null && n.siguiente != null) {
                     cabeza = cabeza.siguiente;
                     cabeza.anterior = null;
                 }
                 else if (n.anterior != null && n.siguiente == null) {
                     rabo = rabo.anterior;
                     rabo.siguiente = null;
                 }
                 longitud--;
                 return;
             }
         }
     }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
      if(esVacia())
        throw new NoSuchElementException();
      T elementoAux = cabeza.elemento;
      if(getLongitud() == 1)
        cabeza = rabo = null;
      else{
        cabeza = cabeza.siguiente;
        cabeza.anterior = null;
      }
      longitud--;
      return elementoAux;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
      if(esVacia())
        throw new NoSuchElementException();
      T elementoAux = rabo.elemento;
      if(getLongitud() == 1)
        rabo = cabeza = null;
      else{
        rabo = rabo.anterior;
        rabo.siguiente = null;
      }
      longitud--;
      return elementoAux;
    }

    /**
    * Busca un nodo en la lista. -Auxiliar-
    * @param elemento el elemnto que queremos saber si está en la lista.
    * @return el nodo del elemento en la lista.
    */

    private Nodo buscaNodo(T elemento) {
      Nodo t = cabeza;
      while(t != null){
        if(t.elemento.equals(elemento))
          return t;
        t = t.siguiente;
      }
      return null;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <tt>true</tt> si <tt>elemento</tt> está en la lista,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return buscaNodo(elemento) != null;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
      Lista<T> listaAyuda = new Lista<>();
      Nodo c = cabeza;
      while(c != null){
        listaAyuda.agregaInicio(c.elemento);
        c = c.siguiente;
      }
      return listaAyuda;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
      Lista<T> listaAyuda = new Lista<>();
      Nodo c = cabeza;
      while(c != null){
        listaAyuda.agregaFinal(c.elemento);
        c = c.siguiente;
      }
      return listaAyuda;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
      cabeza = rabo = null;
      longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
      if(esVacia())
        throw new NoSuchElementException();
      return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
      if(esVacia())
        throw new NoSuchElementException();
      return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
      if(i < 0 || i >= getLongitud())
        throw new ExcepcionIndiceInvalido();
      Nodo t = cabeza;
      int iesimo = 0;
      while(iesimo++ != i){
        t = t.siguiente;
      }
      return t.elemento;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si
     *         el elemento no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
      Nodo t = cabeza;
      int iesimo = 0;
      while(t != null){
        if(t.elemento.equals(elemento))
          return iesimo;
        iesimo++;
        t = t.siguiente;
     }
     return -1;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
      Nodo t = cabeza;
      String stringCadena = "[";
      while(t != null){
        stringCadena += t.elemento;
        t = t.siguiente;
        if(t != null)
          stringCadena += ", ";

      }
      stringCadena += "]";
      return stringCadena;
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param o el objeto con el que hay que comparar.
     * @return <tt>true</tt> si la lista es igual al objeto recibido;
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass())
          return false;
      @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)o;
      if(getLongitud() != lista.getLongitud())
        return false;
      Nodo n = lista.cabeza;
      for(T elemento : this){
        if(!(elemento.equals(n.elemento)))
          return false;
        n = n.siguiente;
      }
      return true;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }




    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
      return mergeSort(this, comparador);
    }



    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a,b) -> a.compareTo(b));
    }



    private Lista<T> mergeSort(Lista<T> l, Comparator<T> comparador){
      if(l.longitud < 2)
        return copia();
      Lista<T> li = new Lista<T>();
      Lista<T> ld = new Lista<T>();
      int mitad = l.longitud/2, i = 0;
      Nodo auxiliar = cabeza;
      while(auxiliar != null){
        if(i < mitad)
          li.agregaFinal(auxiliar.elemento);
        else
          ld.agregaFinal(auxiliar.elemento);
        auxiliar = auxiliar.siguiente;
        i++;
      }
      li = li.mergeSort(li, comparador);
      ld = ld.mergeSort(ld, comparador);
      return mezcla(li, ld, comparador);
    }

    private Lista<T> mezcla(Lista<T> li, Lista<T> ld, Comparator<T> comparador){
      Lista<T> listaOrdenada = new Lista<T>();
      int c;
      while( !li.esVacia() && !ld.esVacia() ){
        c = comparador.compare(li.getPrimero(), ld.getPrimero());
        if(c <= 0){
          listaOrdenada.agregaFinal(li.getPrimero());
          li.eliminaPrimero();
        }
        else if(c > 0){
          listaOrdenada.agregaFinal(ld.getPrimero());
          ld.eliminaPrimero();
        }
        else{
          listaOrdenada.agregaFinal(li.getPrimero());
          listaOrdenada.agregaFinal(ld.getPrimero());
          li.eliminaPrimero();
          ld.eliminaPrimero();
        }
      }
      while(li.cabeza != null){
        listaOrdenada.agregaFinal(li.cabeza.elemento);
        li.eliminaPrimero();
      }
      while(ld.cabeza != null){
        listaOrdenada.agregaFinal(ld.cabeza.elemento);
        ld.eliminaPrimero();
      }
      return listaOrdenada;
    }


    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <tt>true</tt> si elemento está contenido en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        for(T el : this)
            if(comparador.compare(elemento, el) == 0)
                return true;
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <tt>true</tt> si el elemento está contenido en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
