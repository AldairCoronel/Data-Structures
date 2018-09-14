package mx.unam.ciencias.edd;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, mapeando un conjunto de <em>llaves</em> a una colección
 * de <em>valores</em>.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /* Clase para las entradas del diccionario. */
    private class Entrada {

        /* La llave. */
        public K llave;
        /* El valor. */
        public V valor;

        /* Construye una nueva entrada. */
        public Entrada(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Clase privada para iteradores de diccionarios. */
    private class Iterador {

        /* En qué lista estamos. */
        private int indice;
        /* Iterador auxiliar. */
        private Iterator<Entrada> iterador;

        /* Construye un nuevo iterador, auxiliándose de las listas del
         * diccionario. */
        public Iterador() {
            for (int i = 0; i < entradas.length; i++)
                if (entradas[i] != null) {
                    indice = i;
                    iterador = entradas[i].iterator();
                    break;
                }
        }

        /* Nos dice si hay una siguiente entrada. */
        public boolean hasNext() {
            return iterador != null;
        }

        /* Regresa la siguiente entrada. */
        public Entrada siguiente() {
            if (iterador == null)
                throw new NoSuchElementException();
            Entrada entrada = iterador.next();
            if (!iterador.hasNext()) {
                /** Bandera */
                boolean nul = true;
                for (int i = indice + 1; i < entradas.length; i++)
                    if (entradas[i] != null) {
                        indice = i;
                        iterador = entradas[i].iterator();
                        nul = false;
                        break;
                    }
                if (nul)
                    iterador = null;
            }
            return entrada;
        }
    }

    /* Clase privada para iteradores de llaves de diccionarios. */
    private class IteradorLlaves extends Iterador
        implements Iterator<K> {

        /* Construye un nuevo iterador de llaves del diccionario. */
        public IteradorLlaves() {
            new Iterador();
        }

        /* Regresa el siguiente elemento. */
        @Override public K next() {
            return super.siguiente().llave;
        }
    }

    /* Clase privada para iteradores de valores de diccionarios. */
    private class IteradorValores extends Iterador
        implements Iterator<V> {

        /* Construye un nuevo iterador de llaves del diccionario. */
        public IteradorValores() {
            new Iterador();
        }

        /* Regresa el siguiente elemento. */
        @Override public V next() {
            return super.siguiente().valor;
        }
    }

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Capacidad mínima; decidida arbitrariamente a 2^6. */
    private static final int MINIMA_CAPACIDAD = 64;

    /* Dispersor. */
    private Dispersor<K> dispersor;
    /* Nuestro diccionario. */
    private Lista<Entrada>[] entradas;
    /* Número de valores. */
    private int elementos;

    /* Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked")
    private Lista<Entrada>[] nuevoArreglo(int n) {
        return (Lista<Entrada>[])Array.newInstance(Lista.class, n);
    }

    /**
     * Construye un diccionario con una capacidad inicial y dispersor
     * predeterminados.
     */
    public Diccionario() {
        this(MINIMA_CAPACIDAD, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial definida por el
     * usuario, y un dispersor predeterminado.
     * @param capacidad la capacidad a utilizar.
     */
    public Diccionario(int capacidad) {
        this(capacidad, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial predeterminada, y un
     * dispersor definido por el usuario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(Dispersor<K> dispersor) {
        this(MINIMA_CAPACIDAD, dispersor);
    }

    /**
     * Construye un diccionario con una capacidad inicial y un método de
     * dispersor definidos por el usuario.
     * @param capacidad la capacidad inicial del diccionario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(int capacidad, Dispersor<K> dispersor) {
        this.dispersor = dispersor;
        int c = (capacidad < MINIMA_CAPACIDAD) ? calcularCapacidad(MINIMA_CAPACIDAD)
                                               : calcularCapacidad(capacidad);
        entradas = nuevoArreglo(c);
    }

    private int calcularCapacidad(int capacidad) {
        int potencia = 1;
        for (int i = 0; potencia <= capacidad*2; i++)
            potencia = (int)Math.pow(2, i);
        return potencia;
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si
     * la llave ya había sido utilizada antes para agregar un valor, el
     * diccionario reemplaza ese valor con el recibido aquí.
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException si la llave o el valor son nulos.
     */
     public void agrega(K llave, V valor) {
         if (llave == null || valor == null)
             throw new IllegalArgumentException();
         elementos = agrega(llave, valor, elementos, entradas);
         /** Si sobrepasa la maxima carga */
         if (carga() > MAXIMA_CARGA) {
             Lista<Entrada>[] arregloAuxiliar = nuevoArreglo(entradas.length * 2);
             int aux = 0;
             for (int i = 0; i < entradas.length; i++)
                 if (entradas[i] != null)
                     for (Entrada entrada : entradas[i])
                         aux = agrega(entrada.llave, entrada.valor, aux, arregloAuxiliar);
             elementos = aux;
             entradas = arregloAuxiliar;
         }
     }

     /**
      * Metodo auxiliar para agregar.
      * @param llave llave.
      * @param valor valor.
      * @param elementos entero.
      * @param arreglo arreglo.
      * @return entero n;
      */
     private int agrega(K llave, V valor, int elementos, Lista<Entrada>[] arreglo) {
         int indice = obtenerIndice(llave, arreglo);
         if (arreglo[indice] == null) {
             arreglo[indice] = new Lista<Entrada>();
             arreglo[indice].agrega(new Entrada(llave, valor));
             elementos++;
         }
         else {
             boolean repetidas = false;
             for (Entrada entrada : arreglo[indice]) {
                 if (entrada.llave.equals(llave)) {
                     entrada.valor = valor;
                     repetidas = true;
                     break;
                 }
             }
             if (repetidas == false) {
                 arreglo[indice].agrega(new Entrada(llave, valor));
                 elementos++;
             }
         }
         return elementos;
     }

    /**
     * Metodo para obtener indice.
     * @param llave llave.
     * @param entradas arreglo.
     * @return indice.
     */
    private int obtenerIndice(K llave, Lista<Entrada>[] entradas) {
        int mascaraDispersora = entradas.length - 1;
        return dispersor.dispersa(llave) & mascaraDispersora;
    }

    /**
     * Regresa el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no está en el diccionario.
     */
    public V get(K llave) {
        if (llave == null)
            throw new IllegalArgumentException();
        int indice = obtenerIndice(llave, entradas);
        if (entradas[indice] == null)
            throw new NoSuchElementException();
        for (Entrada entrada : entradas[indice])
            if (entrada.llave.equals(llave))
                return entrada.valor;
        throw new NoSuchElementException();
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <tt>true</tt> si la llave está en el diccionario,
     *         <tt>false</tt> en otro caso.
     */
    public boolean contiene(K llave) {
        if (llave == null)
            return false;
        int indice = obtenerIndice(llave, entradas);
        if (entradas[indice] == null)
            return false;
        for (Entrada entrada : entradas[indice])
            if (entrada.llave.equals(llave))
                return true;
        return false;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor a eliminar.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no se encuentra en
     *         el diccionario.
     */
    public void elimina(K llave) {
        if (llave == null)
            throw new IllegalArgumentException();
        int indice = obtenerIndice(llave, entradas);
        if (entradas[indice] == null)
            throw new NoSuchElementException();
        boolean esta = false;
        for (Entrada entrada : entradas[indice])
            if (entrada.llave.equals(llave)) {
                entradas[indice].elimina(entrada);
                elementos--;
                esta = true;
                if (entradas[indice].getElementos() == 0)
                    entradas[indice] = null;
            }
        if (esta == false)
            throw new NoSuchElementException();
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
        if (elementos == 0)
            return 0;
        int colisiones = 0;
        for (int i = 0; i < entradas.length; i++)
            if (entradas[i] != null)
                colisiones += entradas[i].getElementos();
        return colisiones - 1;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave que tenemos
     * en el diccionario.
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        int colisionMaxima = 0;
        for (int i = 0; i < entradas.length; i++)
            if (entradas[i] != null)
                colisionMaxima = (entradas[i].getElementos() > colisionMaxima) ?
                                  entradas[i].getElementos() : colisionMaxima;
        return colisionMaxima - 1;
    }

    /**
     * Nos dice la carga del diccionario.
     * @return la carga del diccionario.
     */
    public double carga() {
        return ((double) elementos) / entradas.length;
    }

    /**
     * Regresa el número de entradas en el diccionario.
     * @return el número de entradas en el diccionario.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si el diccionario es vacío.
     * @return <code>true</code> si el diccionario es vacío, <code>false</code>
     *         en otro caso.
     */
    public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el diccionario de elementos, dejándolo vacío.
     */
    public void limpia() {
        entradas = nuevoArreglo(entradas.length);
        elementos = 0;
    }

    /**
     * Regresa una representación en cadena del diccionario.
     * @return una representación en cadena del diccionario.
     */
    @Override public String toString() {
        Iterador iterador = new Iterador();
        if (elementos == 0)
            return "{}";
        String aux = "{ ";
        while (iterador.hasNext()){
            Entrada entrada = iterador.siguiente();
            aux += "'" + entrada.llave + "': '" + entrada.valor + "', ";
        }
        return aux + "}";
    }

    /**
     * Nos dice si el diccionario es igual al objeto recibido.
     * @param o el objeto que queremos saber si es igual al diccionario.
     * @return <code>true</code> si el objeto recibido es instancia de
     *         Diccionario, y tiene las mismas llaves asociadas a los mismos
     *         valores.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") Diccionario<K, V> d =
            (Diccionario<K, V>)o;
        if (d.getElementos() != elementos)
            return false;
        if (elementos == 0 && d.getElementos() == 0)
            return true;
        for (int i = 0; i < entradas.length; i++)
            if (entradas[i] != null)
                for (Entrada entrada : entradas[i]) {
                    V valorAuxiliar = null;
                    try {
                        valorAuxiliar = d.get(entrada.llave);
                    }
                    catch (Exception e){
                        return false;
                    }
                    if (!(entrada.valor.equals(valorAuxiliar)))
                        return false;
                }
        return true;
    }

    /**
     * Regresa un iterador para iterar las llaves del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar las llaves del diccionario.
     */
    public Iterator<K> iteradorLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar los valores del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar los valores del diccionario.
     */
    @Override public Iterator<V> iterator() {
        return new IteradorValores();
    }
}
