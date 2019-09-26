/*
 * Esta classe permite trabalhar com bitsets codificados da seguinte forma:
 * 
 */
package bits;

import java.util.BitSet;

/**
 * Class Bits
 *
 * @author
 * https://stackoverflow.com/questions/2473597/bitset-to-and-from-integer-long
 *
 *
 * Permite trabalhar com BitSets nos quais o bit menos significativo ocupa a
 * posicao zero. Permite separar o bitset em grupos de bit - util para
 * algoritmos geneticos. Por exemplo, o BitSet de 8 bits pode ser dividido em
 * dois grupos de 4 bits: 76543210 indice BitSet 00111001 = conteudo BitSet 0011
 * 1001 = separacao em grupos 3 9 = valor decimal de cada grupo (long)
 */
public class Bits {

    /**
     *
     * @param value
     * @return value como BitSet
     */
    public static BitSet convert(long value) {
        BitSet bs = new BitSet();
        int index = 0;
        while (value != 0L) {
            if (value % 2L != 0) {
                bs.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        return bs;
    }

    /**
     *
     * @param bs
     * @return valor do BitSet em long
     */
    public static long convert(BitSet bs) {
        long value = 0L;
        for (int i = 0; i < bs.length(); ++i) {
            value += bs.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    /**
     *
     * @param bs1 BitSet 1 a ser comparado com o 2
     * @param bs2 BitSet 2
     * @return 1 se bs1 > bs2; 0 se bs1 = bs2; -1 se bs1 < bs2
     */
    public static int compare(BitSet bs1, BitSet bs2) {
        return Long.compare(Bits.convert(bs1), Bits.convert(bs2));
    }

    /**
     *
     * @param bs BitSet a ser impresso em comma separate value
     * @param groupSize tamanho de cada agrupamento de bits
     * @param numBits numero total de bits do BitSet
     * @return uma string contendo o valor em binário de cada grupo de bits do
     * bit mais significativo para o menos Exemplo: idx 76543210 (zero eh o
     * menos significativo) bs=[10001111] ==> return "10,00,11,11," para
     * groupSize=2 e numBits=8
     */
    public static String printBinCSV(BitSet bs, int groupSize, int numBits) {
        String strCSV = "";
        for (int i = numBits - 1; i >= 0; i--) {
            if (bs.get(i)) {
                strCSV = strCSV.concat("1");
            } else {
                strCSV = strCSV.concat("0");
            }

            if (i % groupSize == 0) {
                strCSV = strCSV.concat(",");
            }
        }
        return strCSV;
    }

    /**
     *
     * @param index indice do grupo (0=corresponde ao grupo menos significativo)
     * @param bs BitSet
     * @param groupSize tamanho do grupo
     * @return um BitSet contendo o grupo selecionado
     */
    public static BitSet getGroup(int index, BitSet bs, int groupSize) {
        return bs.get(index * groupSize, index*groupSize + groupSize);  //inclusive, exclusive     
    }

    /**
     * Retorna um grupo do BitSet como um valor long
     *
     * @param index indice do grupo (0=corresponde ao grupo menos significativo)
     * @param bs BitSet
     * @param groupSize tamanho do grupo
     * @return um long com o valor do grupo
     */
    public static long getGroupLong(int index, BitSet bs, int groupSize) {
        int start = index * groupSize;
        BitSet g = bs.get(start, start + groupSize);  //inclusive, exclusive     
        return Bits.convert(g);
    }

    /**
     * SetGroupLong: setar um valor long a um grupo do BitSet
     *
     * @param index indice do grupo as ser modificado
     * @param bs bitset
     * @param groupSize tamanho do grupo
     * @param value valor a ser colocado no grupo
     * @return a porcao do bitset com o novo valor
     */
    public static BitSet setGroupLong(int index, BitSet bs, int groupSize, long value) {
        int start = index * groupSize;
        BitSet v = Bits.convert(value);
        for (int i = 0; i < groupSize; i++) {
            bs.set(start + i, v.get(i));
        }
        return bs.get(start, start + groupSize);  //inclusive, exclusive     
    }

    /**
     *
     * @param bs
     * @param groupSize tamanho de um grupo de bits (para agrupar bits)
     * @param numBits numero total de bits do BitSet
     * @return uma string contendo os valores decimais de cada grupo de bits da
     * esquerda para a direita (do bit mais significativo para o menos).
     * Exemplo: idx 76543210 bs=[10 00 11 11] ==> return "2,0,3,3," para
     * groupSize=2 e numBits=8
     */
    public static String printDecCSV(BitSet bs, int groupSize, int numBits) {
        String strCSV = "";
        
        for (int i = numBits; i > 0; i -= groupSize) {
            BitSet pos = bs.get(i - groupSize, i);  //inclusive, exclusive     
            strCSV = strCSV.concat(String.valueOf(Bits.convert(pos)));

            if ((i%groupSize) == 0 && i > groupSize ) {
                strCSV = strCSV.concat(",");
           }
       }
        return strCSV;
    }
}
