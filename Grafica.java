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

    /* Vertices para gráficas; implementan la interfaz ComparableIndexable y
     * VerticeGrafica */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* El diccionario de vecinos del vértice. */
        public Diccionario<T, Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
          this.elemento = elemento;
          this.color = color.NINGUNO;
          vecinos = new Diccionario<>();
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

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
          this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
          return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
          if (distancia > vertice.distancia)
            return 1;
          else if (distancia < vertice.distancia)
            return -1;
          return 0;
        }

    }

    /* Vecinos para gráficas; un vecino es un vértice y el peso de la arista que
     * los une. Implementan VerticeGrafica. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
          this.vecino = vecino;
          this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
          return vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
          return vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
          return vecino.color;
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
          return vecino.vecinos;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica.Vertice v, Grafica.Vecino a);
    }

    /* Vértices. */
    private Diccionario<T, Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
      vertices = new Diccionario<>();
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
      vertices.agrega(aux.elemento, aux);
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
      if (sonVecinos(a, b) || a.equals(b))
        throw new IllegalArgumentException();
      Vecino x = new Vecino(v, 1);
      Vecino y = new Vecino(u, 1);
      u.vecinos.agrega(x.get(), x);
      v.vecinos.agrega(y.get(), y);
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
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
      Vertice u = verticeAudicion(vertice(a));
      Vertice v = verticeAudicion(vertice(b));
      if (!(contiene(a)) || !(contiene(b)))
        throw new NoSuchElementException();
      if (sonVecinos(a, b) || a.equals(b) || peso < 0.0)
        throw new IllegalArgumentException();
      Vecino x = new Vecino(v, peso);
      Vecino y = new Vecino(u, peso);
      u.vecinos.agrega(x.get(), x);
      v.vecinos.agrega(y.get(), y);
      aristas++;
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
      if (!(sonVecinos(a, b)) || a.equals(b))
        throw new IllegalArgumentException();
      Vecino x = null;
      Vecino y = null;
      for (Vecino i : u.vecinos)
        if (i.vecino.equals(v))
          x = i;
      for (Vecino j : v.vecinos)
        if (j.vecino.equals(u))
          y = j;
      u.vecinos.elimina(x.get());
      v.vecinos.elimina(y.get());
      aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <tt>true</tt> si el elemento está contenido en la gráfica,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (Vertice aux : vertices)
          if (aux.elemento.equals(elemento))
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
      if (!(this.contiene(elemento)))
        throw new NoSuchElementException();
      Vertice aux = verticeAudicion(vertice(elemento));
      /** Recorremos los vértices y dentro sus vecinos */
      for (Vertice u : vertices)
        for (Vecino v : u.vecinos)
          if (v.vecino.equals(aux)){
            u.vecinos.elimina(v.get());
            aristas--;
          }
      vertices.elimina(aux.get());

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
      if (!this.contiene(a) || !this.contiene(b))
        throw new NoSuchElementException();
      Vertice u = verticeAudicion(vertice(a));
      Vertice v = verticeAudicion(vertice(b));
      for (Vecino x : u.vecinos)
        if (x.vecino.equals(v))
          return true;
      return false;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
      if (!(contiene(a)) || !(contiene(b)))
        throw new NoSuchElementException();
      if (!(sonVecinos(a, b)))
        throw new IllegalArgumentException();
      Vertice x = verticeAudicion(vertice(a));
      Vertice y = verticeAudicion(vertice(b));
      for (Vecino aux : x.vecinos)
        if (aux.vecino.equals(y))
          return aux.peso;
      return -1;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
      if (!(contiene(a)) || !(contiene(b)))
        throw new NoSuchElementException();
      if (!(sonVecinos(a, b)))
        throw new IllegalArgumentException();
      Vertice x = verticeAudicion(vertice(a));
      Vertice y = verticeAudicion(vertice(b));
      for (Vecino aux : x.vecinos)
        if (aux.vecino.equals(y))
          aux.peso = peso;
      for (Vecino aux : y.vecinos)
        if (aux.vecino.equals(x))
          aux.peso = peso;
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
      if (vertice == null || !Vecino.class.isInstance(vertice) && !Vertice.class.isInstance(vertice))
        throw new IllegalArgumentException();
      if (Vecino.class.isInstance(vertice)) {
        Vecino v = (Vecino) vertice;
        v.vecino.color = color;
      }
      else {
        Vertice v = verticeAudicion(vertice);
        v.color = color;
      }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
      paraCadaVertice((v) -> setColor(v, Color.ROJO));
      Cola<Vertice> cola = new Cola<>();
      Vertice v = (Vertice) vertices.iterator().next();
      v.color = Color.NEGRO;
      cola.mete(v);
      while(!(cola.esVacia()))
        for(Vecino u : cola.saca().vecinos)
          if(u.vecino.color == Color.ROJO){
            u.vecino.color = Color.NEGRO;
            cola.mete(u.vecino);
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
        for(Vecino v : ayuda.vecinos)
          if(v.vecino.color != Color.ROJO){
            setColor(v.vecino, Color.ROJO);
            m.mete(v.vecino);
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
        for(Vecino u : v.vecinos)
          if(!(visitados.contiene(u.vecino)))
            s += String.format("(%s, %s), ", v.elemento, u.vecino.elemento);
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
          for(Vecino n : u.vecinos)
            for(Vecino a : v.vecinos)
              if(n.vecino.elemento == a.vecino.elemento){
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

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <tt>a</tt> y
     *         <tt>b</tt>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */

    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
      Lista<VerticeGrafica<T>> lista = new Lista<>();
      Vertice s = verticeAudicion(vertice(origen));
      if (origen.equals(destino)) {
          lista.agrega(s);
          return lista;
      }
      Vertice t = verticeAudicion(vertice(destino));
      setVertices(s);
      Cola<Grafica<T>.Vertice> cola = new Cola<>();
      cola.mete(s);
      while (!(cola.esVacia())) {
        Vertice u = cola.saca();
        for (Vecino v : u.vecinos) {
          if (Double.compare(v.vecino.distancia, Double.MAX_VALUE) == 0) {
            v.vecino.distancia = u.distancia + 1;
            cola.mete(v.vecino);
          }
        }
      }
      if (Double.compare(t.distancia, Double.MAX_VALUE) == 0)
        return lista;
      lista.agrega(t);
      buscaTrayectoria(lista, t, s, false);
      return lista.reversa();
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <tt>origen</tt> y
     *         el vértice <tt>destino</tt>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
      Lista<VerticeGrafica<T>> lista = new Lista<>();
      Vertice s = verticeAudicion(vertice(origen));
      Vertice t = verticeAudicion(vertice(destino));
      if (origen.equals(destino)) {
          lista.agrega(s);
          return lista;
      }
      settea(vertices);
      s.distancia = 0;
      Lista<Vertice> aux = auxMonticulo(vertices);
      MonticuloMinimo<Vertice> monticulo = new MonticuloMinimo<>(aux);
      while (!(monticulo.esVacia())) {
        Vertice u = monticulo.elimina();
        for (Vecino v : u.vecinos) {
          if (Double.compare(v.vecino.distancia, Double.MAX_VALUE) == 0 ||
              Double.compare(u.distancia + v.peso, v.vecino.distancia) < 0) {
            v.vecino.distancia = u.distancia + v.peso;
            monticulo.reordena(v.vecino);
          }
        }
      }
      if (Double.compare(t.distancia,  Double.MAX_VALUE) == 0)
        return lista;
      lista.agrega(t);
      buscaTrayectoria(lista, t, s, true);
      return lista.reversa();
    }

    private void settea(Diccionario<T, Vertice> vertices) {
        for (Vertice aux : vertices)
            aux.distancia = Double.MAX_VALUE;
    }

    private Lista<Vertice> auxMonticulo(Diccionario<T, Vertice> diccionario) {
        Lista<Vertice> aux = new Lista<Vertice>();
        for (Vertice v : diccionario)
            aux.agrega(v);
        return aux;
    }

    private void buscaTrayectoria(Lista<VerticeGrafica<T>> lista, Vertice u, Vertice s, Boolean dijkstra) {
        for (Vecino v : u.vecinos)
          if (u.distancia - (dijkstra ? v.peso : 0) == v.vecino.distancia  + (dijkstra ? 0 :  1)) {
            lista.agrega(v.vecino);
            buscaTrayectoria(lista, v.vecino, s, dijkstra);
        }
    }


    private void setVertices(Vertice v) {
      for (Vertice x : vertices)
        x.distancia = Double.MAX_VALUE;
      v.distancia = 0;
    }
}
