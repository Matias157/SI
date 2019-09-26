package ag;

import java.util.Random;
import java.util.BitSet;

/**
 * Operadores para Algoritmos Geneticos Canonicos: crossover simples, 
 * crossover de 2 pontos, mutação e seleção por roleta.
 */
public class Operadores {
    /**
     * Seleciona a quantidade pedida de cromossomos pelo método da roleta
     * @param fitness float valor de fitness dos cromossomos candidatos a seleção
     * @param qtdCromossomos quantos cromossomos devem ser selecionados para 
     * criar nova geração
     * @return vetor de int contendo o índice dos cromossomos selecionados. Um
     * cromossomo pode aparecer mais de uma vez. Suponha que temos 5 cromossomos:<br>
     * índice  0 1 2 3 4 5   (identifica o cromossomo) <br>
     * fitness 9 8 1 1 7 3   (fitness float de cada cromossomo)<br>
     * return  1 0 0 4 1 0   (identifica os cromossomos selecionados pela roleta)
     */
    public static int[] selecionarPorRoleta(float fitness[], int qtdCromossomos) {
        int selecao[] = new int[qtdCromossomos];
        float fitnessRel[] = new float[fitness.length];
        float soma = 0;
        Random gerador = new Random();
        gerador.setSeed(System.currentTimeMillis());
        float r;

        for (int i = 0; i < fitness.length; i++) {
            soma += fitness[i];
        }

        for (int i = 0; i < fitness.length; i++) {
            fitnessRel[i] = fitness[i] / soma;
        }
        // seleciona cromossomos na quantidade especificada
        for (int i = 0; i < qtdCromossomos; i++) {
            r = gerador.nextFloat();
            float acumulado = 0;
            int j = -1;
            do {
                j = (j + 1) % fitness.length;
                acumulado += fitnessRel[j];
            } while (acumulado < r);
            selecao[i] = j;
        }
        return selecao;
    }
    /**
     * Seleciona a quantidade pedida de cromossomos pelo método da roleta
     * @param fitness float valor de fitness dos cromossomos candidatos a seleção
     * @param qtdCromossomos quantos cromossomos devem ser selecionados para 
     * criar nova geração
     * @return vetor de int contendo o índice dos cromossomos selecionados. Um
     * cromossomo pode aparecer mais de uma vez. Suponha que temos 5 cromossomos:<br>
     * índice  0 1 2 3 4 5   (identifica o cromossomo) <br>
     * fitness 9 8 1 1 7 3   (fitness int de cada cromossomo)<br>
     * return  1 0 0 4 1 0   (identifica os cromossomos selecionados pela roleta)
     */
    public static int[] selecionarPorRoleta(int fitness[], int qtdCromossomos) {
        int selecao[] = new int[qtdCromossomos];
        int soma = 0;
        Random gerador = new Random();
        int r;

        for (int i = 0; i < fitness.length; i++) {
            soma += fitness[i];
        }
        // seleciona cromossomos na quantidade especificada
        for (int i = 0; i < qtdCromossomos; i++) {
            r = gerador.nextInt(soma);
            int acumulado = 0, j = -1;
            do {
                j = (j + 1) % fitness.length;
                acumulado += fitness[j];
            } while (acumulado < r);
            selecao[i] = j;
        }
        return selecao;
    }

   /**
     * Realiza crossover/cruzamento DOIS PONTOS entre dois cromossomos/indivídiuos
     * Os dois pontos são aleatórios.
     * @param c1 bitset representando o cromossomo 1
     * @param c2 bitset representando o cromossomo 2
     * @param tamBitSet tamanho dos bitsets (assume-se que são iguais)
     * @param pCross probalidade de ocorrer crossover entre dois cromossomos;
     * tipicamente entre 0.7 e 0.8
     */
    public static void crossoverDoisPontos(BitSet c1, BitSet c2, float pCross) {
        // BitSize.length e size nao retornam o tamanho do cromossomo, por isso
        // fui obrigado a colocar tamanho como argumento

        if (pCross <= 0) {
            return;
        }

        Random gerador = new Random();
        float r = gerador.nextFloat();
        if (r > pCross) {
            return;
        }

        // sorteia dois pontos de crossover - os alelos entre [p1, p2] serao
        // trocados
        int p1, p2, p;

        // sorteia dois pontos distintos e, ao mesmo tempo, diferentes de 0 e
        // de tamBitSet - para impedir crossover total = swap      
        do {
            p1 = gerador.nextInt(Cromossomo.NUM_BITS);
            p2 = gerador.nextInt(Cromossomo.NUM_BITS);
            if (p1 > p2) {
                p = p1;
                p1 = p2;
                p2 = p;
            }
        } while ((p1 == p2) || (p1 == 0 && p2 == (Cromossomo.NUM_BITS - 1)));

        // necessario somar ao p2 porque trocarMaterialGenetico exclui o limite superior 
        trocarMaterialGenetico(c1, c2, p1, p2 + 1);

        System.out.println("pontos de crossover = " + p1 + "," + p2);
    }
    
    /**
     * Realiza crossover/cruzamento SIMPLES entre dois cromossomos ou indivídiuos
     * @param c1 bitset representando o cromossomo 1
     * @param c2 bitset representando o cromossomo 2
     * @param pCross probalidade de ocorrer crossover entre dois cromossomos;
     * tipicamente entre 0.7 e 0.8
     */
    public static void crossoverUmPonto(BitSet c1, BitSet c2, float pCross) {
        // BitSize.length e size nao retornam o tamanho do cromossomo, por isso
        // fui obrigado a colocar tamanho como argumento. Realiza crossover com 
        // probabilidade pCross

        if (pCross <= 0) {
            return;
        }

        Random gerador = new Random();
        float r = gerador.nextFloat();
        if (r > pCross) {
            return;
        }

        // sorteia um ponto de crossover
        int p;
        // nao faz crossover na pos 0, pois seria um swap completo
        p = gerador.nextInt(Cromossomo.NUM_BITS - 1) + 1; 
        //System.out.println("ponto de crossover = " + p);
        trocarMaterialGenetico(c1, c2, p, Cromossomo.NUM_BITS);
    }
    /**
     * Realiza efetivamente a troca de material genético de dois cromossomos entre
     * dois pontos p1 e p2
     * @param c1 cromossomo 1 (BitSet)
     * @param c2 cromossomo 2 (BitSet)
     * @param p1 ponto de crossover 1
     * @param p2 ponto de crossover 2
     */
    private static void trocarMaterialGenetico(BitSet c1, BitSet c2, int p1, int p2) {
        // [p1, p2[: crossover entre ponto 1 (inclusive) e ponto 2 exclusivo
        BitSet alelosC1 = c1.get(p1, p2);
        BitSet alelosC2 = c2.get(p1, p2);

        for (int i = p1, j = 0; i < p2; i++, j++) {
            c1.set(i, alelosC2.get(j));
            c2.set(i, alelosC1.get(j));
        }
    }
    
    /**
     * Realiza mutacoes nos alelos do cromossomo c com probabilidade 
     * pMut (por alelo).
     * @param c cromossomo a ser mutado
     * @param pMut probabilidade de mutação (utilizar < 0.05)
     */
    public static void mutar(BitSet c, float pMut) {
        if (pMut <= 0) {
            return;
        }

        Random gerador = new Random();
        float r;
        for (int i = 0; i < Cromossomo.NUM_BITS; i++) {
            r = gerador.nextFloat(); // sorteia um número aleatório
            if (r <= pMut) {
                c.flip(i);           // inverte o bit
            }
        }
    }

    /* TESTES UNITARIOS
     public static void main(String[] args) {
     int[] fit = {1, 4};
     int[] res;

     /* TESTES SELECAO ROLETA
     res = selecionarPorRoleta(fit, 8);

     for (int i = 0; i < res.length; i++) {
     System.out.println("selecao=" + res[i]);
     }

     System.out.println("-- SELECAO FLOAT --");
     float[] fitF = {(float) 15.323, (float) 15.333, (float) 15.2346, (float) 14.12333};
     res = selecionarPorRoleta(fitF, 8);

     for (int i = 0; i < res.length; i++) {
     System.out.println("selecao=" + res[i]);
     }
     */
    // TESTES CROSSOVER 
    /*
     BitSet b1 = new BitSet(8);
     b1.set(0, true);
     b1.set(1, false);
     b1.set(2, true);
     b1.set(3, false);
     b1.set(4, true);
     b1.set(5, false);
     b1.set(6, true);
     b1.set(7, false);
     imprimirBitSet(b1, 8, "CROMOSSOMO 1");

     BitSet b2 = new BitSet(8);
     b2.set(0, false);
     b2.set(1, true);
     b2.set(2, false);
     b2.set(3, true);
     b2.set(4, false);
     b2.set(5, true);
     b2.set(6, false);
     b2.set(7, true);
     imprimirBitSet(b2, 8, "CROMOSSOMO 2");

     crossoverDoisPontos(b1, b2, 8);
     imprimirBitSet(b1, 8, "CROMOSSOMO 1 - depois crossover");
     imprimirBitSet(b2, 8, "CROMOSSOMO 2 - depois crossover");
        
     System.out.println("MUTACAO NO CROMOSSOMO 1");
     System.out.println("Probabilidade: 0.75");
     mutar(b1, 8, (float) 0.75);
     imprimirBitSet(b1, 8, "CROMOSSOMO 1 - depois de mutacao");

     } 
     */
}
