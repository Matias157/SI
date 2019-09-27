/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag;

import agNRainhas.AGNRainhas;
import bits.Bits;
import java.util.BitSet;
import java.util.Random;
import java.lang.*;

/**
 * Permite implementar um cromossomo com codificacao binaria. O cromossomo eh
 * subdividido em varios genes sendo cada um dos genes decompostos em locus. O
 * programador pode atribuir valor a um gene
 *
 * @author tacla
 */
public class Cromossomo implements ConfigAG {

    /**
     * fitness do cromossomo
     */
    public float fitness;

    // DESEMPENHO
    /**
     * Contagem de chamadas ao metodo de fitness - acumula para todos os
     * cromossomos
     */
    public static long ctChamadasFitness = 0;

    /**
     * codificacao binaria do cromossomo
     */
    public BitSet bits;

    public Cromossomo() {
        this.bits = new BitSet(ConfigAG.NUM_BITS);
    }

    public Cromossomo clonar() {
        Cromossomo clone = new Cromossomo();
        for (int i = 0; i < ConfigAG.NUM_BITS; i++) {
            clone.bits.set(i, this.bits.get(i));
        }
        clone.fitness = this.fitness;
        return clone;
    }

    /**
     * Lê o cromossomo cujo indice eh index. <br>
     * Ex.: um cromossomo  com 3 genes de 2 locus cada: [11,10,01]. <br>
     * O index zero corresponde ao gene mais a direita. Logo, <ul>
     * <li>lerGene(0) retorna 1L</li>
     * <li>lerGene(1) retorna 2L</li>
     * <li>lerGene(2) retorna 3L</li> </ul>
     * @param index
     * @return valor long do gene
     */
    public long lerGene(int index) {
        return Bits.getGroupLong(index, this.bits, ConfigAG.NUM_LOCUS);
    }

    /**
     * Permite setar o valor de um gene utilizando um valor definido em long.<br>
     * Ex.: um cromossomo zerado com 3 genes de 2 locus cada: [00,00,00]
     * <br>
     * O index zero corresponde ao gene mais a direita. <br>
     * Logo, ao fazer chamadas sucessivas ao setarGene como indicado abaixo obtera<ul>
     * <li>setarGene(0, 1L) resulta em [00,00,01]</li>
     * <li>setarGene(1, 2L) retorna em [00,10,01]</li>
     * <li>setarGene(2, 3L) retorna em [11,10,01]</li> </ul>
     *
     * @param index o indice do gene a ser setado
     * @param valor o valor a ser atribuido ao gene
     *
     */
    public void setarGene(int index, long valor) {
        Bits.setGroupLong(index, this.bits, ConfigAG.NUM_LOCUS, valor);
    }

    /**
     * Imprime uma variável do tipo BitSet     *
     * @param titulo título que aparecerá no printf
     */
    public void imprimir(String titulo) {
        System.out.println("--- " + titulo + " --- ");
        System.out.println(this.fitness + "," + Bits.printDecCSV(bits, ConfigAG.NUM_LOCUS, ConfigAG.NUM_BITS));
        System.out.println("");
    }

    /**
     * Imprime cromossomo no formato 'valores separados por virgula' 
     * @return string fitness,gene 0,...,gene n, tal que gene 0 a n corresponde
     * ao valor em decimal de cada gene
     */
    public String imprimirCSV() {
        return this.fitness + "," + Bits.printDecCSV(bits, ConfigAG.NUM_LOCUS, ConfigAG.NUM_BITS);
    }

    /**
     * so conserta para um num aleatorio de 0 a 9 caso haja algum gene 
     * infactivel
     */
    private void reparar() {
        int gene = infactivel();
        Random gerador = new Random();
        gerador.setSeed(System.currentTimeMillis());
        while(gene > -1){
            this.setarGene(gene, gerador.nextInt(10)); 
            gene = infactivel();
        }
    }

    /**
     * se o cromossomo tiver genes infactiveis (como um numero maior que o total
     * de linhas) retorna o numero do gene infactivel, se nao retorna -1
     */
    private int infactivel() {
        int i;
        for(i = 0; i < ConfigAG.NUM_GENES; i++){        
            if(this.lerGene(i) > AGNRainhas.NUM_RAINHAS - 1){
                return i;
            }
        }
        return -1;
    }

    /**
     * Calcular fitness: dependente do problema. Se solucao for infactivel e
     * flag PENALIZACAO=true entao penalizar o fitness; caso contrario, reparar
     * o individuo
     */
    protected void calcularFitness() {
        int i, j, genei, genej;
        ctChamadasFitness++;

        if (!ConfigAG.PENALIZACAO) {
            this.reparar();
        } else if (this.infactivel() >= 0) {
            this.fitness = 0;
            return;
        }

        // CALCULAR FITNESS PARA SITUACOES NORMAIS
        this.fitness = AGNRainhas.MAX_FIT;
        
        //*********************Calculando fitness                                //      |_|_|_|_|_|_|_|_|_|_|
        // Cada gene (rainha) perde 1 sempre estiver sob ataque em uma           //      |_|_|_|_|_|_|_|_|_|_|
        // das 4 direcoes (linha, coluna, diagonais crescente e decrescente      //      |_|_|_|_|_|_|_|_|_|1|
        // as colunas nao precisam ser testadas pois a modelagem do problema ja  //      |_|_|_|_|_|_|_|_|_|_|
        // garante que so havera uma rainha em cada coluna                       //      |_|_|_|_|_|_|_|_|_|_|
                                                                                 //      |_|_|_|_|_|_|_|_|_|_|
        for(i = 0; i < ConfigAG.NUM_GENES; i++){                                 //      |_|_|_|_|_|_|_|_|_|_|
            genei = (int)this.lerGene(i); 
            for(j = 0; j < ConfigAG.NUM_GENES; j++){                             //      |_|_|_|_|_|_|_|_|_|_|
                if(j != i){                                                      //      |_|_|_|_|_|_|_|_|_|_|
                    // confere se ha mais uma rainha (j) na mesma linha que a que    
                    // esta sendo verificada (i)                                     
                    genej = (int)this.lerGene(j);                                //      |_|_|_|_|_|_|_|_|_|_|
                    if(genei == genej){ 
                        this.fitness--;
                    }
                    //confere se ha uma rainha (j) nas diagonais
                    // sim, isso da certo
                    if(Math.abs(j - i) == Math.abs(genej - genei)){
                        this.fitness--;
                    }
                }
            }
        }
    }

    /**
     * Preencher um cromossomo com valores iniciais (depende do problema)
     */
    public void inicializarCromossomo() {
        Random gerador = new Random();
        gerador.setSeed(System.currentTimeMillis());
        int i;
        for(i = 0; i < ConfigAG.NUM_GENES; i++){
            this.setarGene(i, gerador.nextLong());
        }
    }
}
