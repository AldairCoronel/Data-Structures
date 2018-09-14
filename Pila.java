package mx.unam.ciencias.edd;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString() {
        Nodo nodoAux = cabeza;
        String stringCadena = "";
        while(nodoAux != null){
          stringCadena += nodoAux.elemento + "\n";
          nodoAux = nodoAux.siguiente;
        }
        return stringCadena;
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if(elemento == null)
          throw new IllegalArgumentException();
        Nodo nodoAux = new Nodo(elemento);
        if(cabeza == null)
          cabeza = rabo = nodoAux;
        else{
          nodoAux.siguiente = cabeza;
          cabeza = nodoAux;
        }
    }
}
