package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {}

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador) {
        quickSort(arreglo, 0, arreglo.length - 1, comparador);
    }


    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }


    private static <T> void quickSort(T[] a, int s, int t, Comparator<T> c){
      if(t <= s)
        return;
      int i = s+1;
      int j = t;
      while(i < j)
        if(c.compare(a[i] , a[s]) > 0 && c.compare(a[j] , a[s]) <= 0)
          intercambia(a, i++, j--);
        else if(c.compare(a[i] , a[s]) <= 0)
          i++;
        else
          j--;
        if(c.compare(a[i] , a[s]) > 0)
          i--;
        intercambia(a, i, s);
        quickSort(a,  s, i-1, c);
        quickSort(a, i+1, t, c);

    }




    /**
      * intercambia
      * @param <T> tipo del arreglo
      * @param arreglo arreglo
      * @param i indice
      * @param m indice
      */

    private static <T> void intercambia(T[] arreglo, int i, int m){
      if(i == m)
        return;
      T auxiliar = arreglo[i];
      arreglo[i] = arreglo[m];
      arreglo[m] = auxiliar;
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordernar el arreglo.
     */
    public static <T> void
    selectionSort(T[] arreglo, Comparator<T> comparador) {
        for(int i = 0; i < arreglo.length; i++){
          int min = i;
          for(int j = i+1; j < arreglo.length; j++){
            if(comparador.compare(arreglo[j], arreglo[min]) < 0)
              min = j;
          }
          intercambia(arreglo, i, min);
        }
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador) {
        return busquedaBinaria(arreglo, elemento, comparador, 0, arreglo.length-1);
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int
    busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }

    private static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> c, int a, int b){
        if(b < 0 || b < a)
            return -1;
        int m = (a+b) / 2;
        if(c.compare(arreglo[m] , elemento) > 0)
            return busquedaBinaria(arreglo, elemento, c, a, m-1);
        else if(c.compare(arreglo[m] , elemento) < 0)
            return busquedaBinaria(arreglo, elemento, c, m+1, b);
        else
            return m;
    }
}
