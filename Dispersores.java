package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Método auxiliar para combinar de forma Little Endian.
     * @param a byte a.
     * @param b byte b.
     * @param c byte c.
     * @param d byte d.
     * @return entero en little endian.
    */
    private static int combinaLittleEndian(byte a, byte b, byte c, byte d) {
        return ((d & 0xFF) << 24) | ((c & 0xFF) << 16) | ((b & 0xFF) << 8)
                | ((a & 0xFF));
    }

    /**
     * Método auxiliar para combinar de forma Big Endian.
     * @param a byte a.
     * @param b byte b.
     * @param c byte c.
     * @param d byte d.
     * @return entero en Big endian.
    */
    private static int combinaBigEndian(byte a, byte b, byte c, byte d) {
        return ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8)
                | ((d & 0xFF));
    }

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int r = 0;
        int i = 0;
        int longitud = llave.length;
        while (longitud >= 4) {
            r ^= combinaBigEndian(llave[i++], llave[i++], llave[i++], llave[i++]);
            longitud -= 4;
        }
        switch (longitud) {
            case 3:
                r ^= combinaBigEndian(llave[i++], llave[i++], llave[i++], (byte) 0x00);
                break;
            case 2:
                r ^= combinaBigEndian(llave[i++], llave[i++], (byte) 0x00, (byte) 0x00);
                break;
            case 1:
                r ^= combinaBigEndian(llave[i++], (byte) 0x00, (byte) 0x00, (byte) 0x00);
                break;
        }
        return r;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int longitud = llave.length;
        int a, b, c;
        a = b = 0x9E3779B9;
        c = 0xFFFFFFFF;
        int offset = 0;
        while (longitud >= 12) {
            a += combinaLittleEndian(llave[offset], llave[offset + 1], llave[offset + 2], llave[offset + 3]);
            b += combinaLittleEndian(llave[offset + 4], llave[offset + 5], llave[offset + 6], llave[offset + 7]);
            c += combinaLittleEndian(llave[offset + 8], llave[offset + 9], llave[offset + 10], llave[offset + 11]);
            a -= b; a -= c; a ^= (c >>> 13);
            b -= c; b -= a; b ^= (a << 8);
            c -= a; c -= b; c ^= (b >>> 13);

            a -= b; a -= c; a ^= (c >>> 12);
            b -= c; b -= a; b ^= (a << 16);
            c -= a; c -= b; c ^= (b >>> 5);

            a -= b; a -= c; a ^= (c >>> 3);
            b -= c; b -= a; b ^= (a << 10);
            c -= a; c -= b; c ^= (b >>> 15);
            offset += 12;
            longitud -= 12;
        }
        c += llave.length;
        switch (longitud) {
            case 11: c += ( (llave[offset + 10] & 0xFF)  << 24);
            case 10: c += ( (llave[offset + 9] & 0xFF)  << 16);
            case  9: c += ( (llave[offset + 8] & 0xFF)  << 8);

            case  8: b += ( (llave[offset + 7] & 0xFF)  << 24);
            case  7: b += ( (llave[offset + 6] & 0xFF)  << 16);
            case  6: b += ( (llave[offset + 5] & 0xFF)  << 8);
            case  5: b += (llave[offset + 4] & 0xFF);

            case  4: a += ( (llave[offset + 3] & 0xFF)  << 24);
            case  3: a += ( (llave[offset + 2] & 0xFF)  << 16);
            case  2: a += ( (llave[offset + 1] & 0xFF)  << 8);
            case  1: a += (llave[offset] & 0xFF);
        }
        a -= b; a -= c; a ^= (c >>> 13);
        b -= c; b -= a; b ^= (a << 8);
        c -= a; c -= b; c ^= (b >>> 13);

        a -= b; a -= c; a ^= (c >>> 12);
        b -= c; b -= a; b ^= (a << 16);
        c -= a; c -= b; c ^= (b >>> 5);

        a -= b; a -= c; a ^= (c >>> 3);
        b -= c; b -= a; b ^= (a << 10);
        c -= a; c -= b; c ^= (b >>> 15);
        return c;
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;
        for (int x = 0; x < llave.length; x++)
            h += (h << 5) + (llave[x] & 0xFF);
        return h;
    }
}
