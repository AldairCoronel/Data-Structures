package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
      Nodo nodoAux = cabeza;
      String stringCadena = "";
      while(nodoAux != null){
        stringCadena += nodoAux.elemento + ",";
        nodoAux = nodoAux.siguiente;
      }
      return stringCadena;
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
      if(elemento == null)
        throw new IllegalArgumentException();
      Nodo nodoAux = new Nodo(elemento);
      if(rabo == null)
        cabeza = rabo = nodoAux;
      else{
        rabo.siguiente = nodoAux;
        rabo = nodoAux;
      }
    }
}
