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
     * FAZER
     */
    private void reparar()
    {
        // Procurar uma rainha incorreta e reparar
        // Jeito mais fácil é remover a rainha da posição errada. (Switch 1 para 0).

        // Fazer isso para todas as rainhas incorretas
        /*for(int i = 0; i < ConfigAG.NUM_GENES; i++)
        {
            for(int j = 0; j < ConfigAG.NUM_LOCUS; i++)
            {
                // Situações de rainha errada:
                // Mesma Coluna
                // Mesma Linha
                // Diagonais
                // Acho que dá pra fazer recursivo
            }
        }
        for(int i = 0; i < ConfigAG.NUM_GENES; i++){
            if(Bits.getGroup(i, this.bits, 10).cardinality() > 1){
                System.out.println("Antes");
                System.out.println(Bits.getGroup(i, this.bits, 10));
                for(int j = 1; j < Bits.getGroup(i, this.bits, 10).cardinality(); j++){
                    System.out.println(Bits.getGroup(i, this.bits, 10).nextSetBit(0));
                    Bits.getGroup(i, this.bits, 10).clear(Bits.getGroup(i, this.bits, 10).nextSetBit(0));
                }
                System.out.println("Depois");
                System.out.println(Bits.getGroup(i, this.bits, 10));
            }
        }*/
        imprimir("Antes");
        for(int i = 0; i < ConfigAG.NUM_GENES; i++){
            long gene = lerGene(i);
            BitSet aux = Bits.convert(gene);
            if(aux.cardinality() > 1){
                System.out.println("Antes");
                System.out.println(aux);
                System.out.println(aux.cardinality());
                int card = aux.cardinality();
                for(int j = 1; j < card; j++){
                    System.out.println(aux.nextSetBit(0));
                    aux.clear(aux.nextSetBit(0));
                }
                System.out.println("Depois");
                System.out.println(aux);
                gene = Bits.convert(aux);
                setarGene(i, gene);
            }
        }
        System.out.println("-----------------------------------------------");
        for(int i = 0; i < ConfigAG.NUM_GENES - 1; i++){
            long gene = lerGene(i);
            BitSet aux = Bits.convert(gene);
            for(int j = i; j < ConfigAG.NUM_GENES - 1; j++){
                long prox = lerGene(j + 1);
                BitSet aux2 = Bits.convert(prox);
                if(aux.nextSetBit(0) == aux2.nextSetBit(0)){
                    System.out.println(aux2);
                    aux2.clear(aux2.nextSetBit(0));
                }
            }
        }
        imprimir("Depois");
    }

    /**
     * FAZER
     */
    private int infactivel()
    {
        // Quando ele é infactível?
        // Rainhas em posições que não podem acontecer:
        
        // Mesma Coluna = Mais de um número 1 no gene
        for(int i = 0; i < ConfigAG.NUM_GENES; i++)
        {
            for(int j = 0; j < ConfigAG.NUM_LOCUS; j++)
            {
                // Checar se tem mais que um número 1 no gene
                // se sim, retorna qualquer valor >=0
                // se não, faz nada
                // Acho que dá pra fazer recursivo
            }
        }
        // Mesma Linha = Genes diferentes com número 1 na mesma posição de locus
        for(int i = 0; i < ConfigAG.NUM_GENES; i++)
        {
            for(int j=0; j < ConfigAG.NUM_LOCUS; j++)
            {
                // Checar entre dois genes se tem número 1 no mesmo locus
                // se sim, retorna qualquer valor >=0
                // se não, faz nada   
                // Acho que dá pra fazer recursivo             
            }
        }
        // Mesma diagonal (esse é mais difícil de checar)
        for(int i = 0; i < ConfigAG.NUM_GENES; i++)
        {
            for(int j = 0; j < ConfigAG.NUM_LOCUS; j++)
            {
                // Checar locus -1, locus+1 no gene anterior e próximo gene.
                // Dá para fazer esse -1 na verdade ser um -i, dado o indice. Ver se funciona depois
                // Acho que dá pra fazer recursivo
            }
        }
        return -1;
    }

    /**
     * Calcular fitness: dependente do problema. Se solucao for infactivel e
     * flag PENALIZACAO=true entao penalizar o fitness; caso contrario, reparar
     * o individuo
     */
    protected void calcularFitness()
    {
   
        // Ideias: quanto mais rainhas "certas", maior o fitness. 
        // Aqui, permitimos que cromossomos tenham menos que 10 rainhas.
        ctChamadasFitness++;

        if (!ConfigAG.PENALIZACAO)
        {
            this.reparar();
        } 
        else if (this.infactivel() >= 0) 
        {
            for(int i = 0; i < ConfigAG.NUM_GENES; i++)
            {
                if(lerGene(i) != 0)
                {
                    if(lerGene(i)%2 == 0)
                    {
                        this.fitness++;
                    }
                    else
                    {
                        // Penalização: perde por ter 2 ou mais rainhas na mesma coluna.
                        this.fitness--;
                    }
                }
            }
            return;
        }

        // CALCULAR FITNESS PARA SITUACOES NORMAIS
        this.fitness = 0;
        for (int i = 0; i < ConfigAG.NUM_GENES; i++)
        {
            if(lerGene(i) != 0)
            {
                if(lerGene(i)%2 == 0)
                {
                    this.fitness++;
                }
            }
        }
    }

    /**
     * Preencher um cromossomo com valores iniciais (depende do problema)
     */
    public void inicializarCromossomo()
    {
        // INICIALIZAR CADA GENE DO CROMOSSOMO COM UM VALOR
        /* Aqui, inicializamos o cromossomo com as rainhas nas diagonais*/
        long aux;
        for(int i = 0; i < ConfigAG.NUM_GENES; i++)
        {
            aux = i;
            setarGene(i, aux*2); 
        }
    }
}
