package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase privada para iteradores de gráficas. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
          iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
          return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
          return iterador.next().get();
        }
    }

    /* Vértices para gráficas; implementan la interfaz VerticeGrafica */
    private class Vertice implements VerticeGrafica<T> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La lista de vecinos del vértice. */
        public Lista<Vertice> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
          this.elemento = elemento;
          this.color = color.NINGUNO;
          vecinos = new Lista<>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
          return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
          return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
          return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
          return vecinos;
        }
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
      vertices = new Lista<>();
      aristas = 0;

    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
      return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
      return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
      if(elemento == null || this.contiene(elemento))
        throw new IllegalArgumentException();
      Vertice aux = new Vertice(elemento);
      vertices.agrega(aux);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
      Vertice u = verticeAudicion(vertice(a));
      Vertice v = verticeAudicion(vertice(b));
      if(u == v || u.vecinos.contiene(v) && v.vecinos.contiene(u))
        throw new IllegalArgumentException();
      u.vecinos.agrega(v);
      v.vecinos.agrega(u);
      aristas++;
    }


    /**
     * Método auxiliar para hacer una audición a vertice
     * @param vertice vértice a ser audicionado.
     * @return vertice vértice.
     */
    private Vertice verticeAudicion(VerticeGrafica<T> vertice){
      return (Vertice) vertice;
    }
    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
      Vertice u = verticeAudicion(vertice(a));
      Vertice v = verticeAudicion(vertice(b));
      if(!(u.vecinos.contiene(v) && v.vecinos.contiene(u)))
        throw new IllegalArgumentException();
      u.vecinos.elimina(v);
      v.vecinos.elimina(u);
      aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <tt>true</tt> si el elemento está contenido en la gráfica,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for(Vertice aux : vertices)
          if(aux.elemento.equals(elemento))
            return true;
        return false;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
      if(!(this.contiene(elemento)))
        throw new NoSuchElementException();
      Vertice aux = verticeAudicion(vertice(elemento));
      /** Recorremos los vértices y dentro sus vecinos */
      for(Vertice u : vertices)
        for(Vertice v : u.vecinos)
          if(v.equals(aux)){
            u.vecinos.elimina(aux);
            aristas--;
          }
      vertices.elimina(aux);

    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <tt>true</tt> si a y b son vecinos, <tt>false</tt> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
      if(!this.contiene(a) || !this.contiene(b))
        throw new NoSuchElementException();
      Vertice u = verticeAudicion(vertice(a));
      Vertice v = verticeAudicion(vertice(b));
      return u.vecinos.contiene(v) && v.vecinos.contiene(u);
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        for(Vertice aux : vertices)
          if(aux.elemento.equals(elemento))  return aux;
        throw new NoSuchElementException();
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
      if(new Vertice(null).getClass() != vertice.getClass())
        throw new IllegalArgumentException();
        Vertice v = verticeAudicion(vertice);
        v.color = color;
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
      paraCadaVertice((v) -> setColor(v, Color.ROJO));
      Cola<Vertice> cola = new Cola<>();
      Vertice v = vertices.getPrimero();
      v.color = Color.NEGRO;
      cola.mete(v);
      while(!(cola.esVacia()))
        for(Vertice u : cola.saca().vecinos)
          if(u.color == Color.ROJO){
            u.color = Color.NEGRO;
            cola.mete(u);
          }
        for(Vertice z : vertices)
          if(z.color != Color.NEGRO)
            return false;
        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
      for(Vertice aux : vertices)
        accion.actua(aux);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
      Cola<Grafica<T>.Vertice> cola = new Cola<>();
      recorrerla(elemento, accion, cola);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
      Pila<Grafica<T>.Vertice> pila = new Pila<>();
      recorrerla(elemento, accion, pila);
    }

    /**
      * Método auxiliar que recorre la gráfica en el orden deseado
      * @param elemento elemento inicial / actual
      * @param accion accion a tomarse en cada vértice
      * @param m instancia de la clase MeteSaca (pila/cola)
      * @throws NoSuchElementException si el elemento no está en la gráfica
      */
    private void recorrerla(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Grafica<T>.Vertice> m){
      /** Si no hay vértice que contenga al elemento */
      if(!(this.contiene(elemento))) throw new NoSuchElementException();
      Vertice aux = verticeAudicion(vertice(elemento));
      m.mete(aux);
      while(!(m.esVacia())){
        Vertice ayuda = m.saca();
        setColor(ayuda, Color.ROJO);
        accion.actua(ayuda);
        for(Vertice v : ayuda.vecinos)
          if(v.color != Color.ROJO){
            setColor(v, Color.ROJO);
            m.mete(v);
          }
      }
      paraCadaVertice(vertice -> setColor(vertice, Color.NINGUNO));
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
      return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
      Lista<Vertice> visitados = new Lista<>();
      String s = "";
      for(Vertice v : vertices){
        for(Vertice u : v.vecinos)
          if(!(visitados.contiene(u))){
            s += String.format("(%s, %s)", v.elemento, u.elemento);
            if(vertices.getUltimo() != v) s += ", ";
          }
          visitados.agrega(v);
      }
      return s;
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param o el objeto con el que hay que comparar.
     * @return <tt>true</tt> si la gráfica es igual al objeto recibido;
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)o;
        if(getElementos() != grafica.getElementos() || aristas != grafica.getAristas())
          return false;
        boolean p = false;
        Vertice v;
        for(Vertice u : vertices){
          if(!(grafica.contiene(u.elemento)))
            return false;
          v = verticeAudicion(grafica.vertice(u.elemento));
          for(Vertice n : u.vecinos)
            for(Vertice a : v.vecinos)
              if(n.elemento == a.elemento){
                p = true;
                break;
              }
        }
        return p;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
