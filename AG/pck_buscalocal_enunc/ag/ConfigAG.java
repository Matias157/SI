/*
 * Configuracoes de execucao do AG e dos cromossomos
 */
package ag;

/**
 *
 * @author tacla
 */
public interface ConfigAG {

    // PARÂMETROS DE CONFIGURAÇÃO DO AG 
    /**
     * número máximo de execuções
     */
    public static final int MAX_EXECUCOES = 5000;
    /**
     * tamanho da população = quantidade de indivíduos ou cromossomos
     * OBRIGATORIAMENTE UM NÚMERO PAR >= 2
     */
    public static final int TAM_POP = 20;
    /**
     * critério de parada: máximo de gerações a serem criadas
     */
    public static final int MAX_GERACOES = 50;
    /**
     * probabilidade de crossover entre 2 individuos: tipicamente [70%,80%]
     */
    public static final float PROB_CROSS = (float) 0.7;
    /**
     * probabilidade de mutação de um alelo: deve ser inferior a 5%
     */
    public static final float PROB_MUT = (float) 0.045;

    /**
     * penalização: escolhe se é fitness com penalização (caso true). Caso
     * false, escolhe fitness+reparação
     */
    public static final boolean PENALIZACAO = false;
    /**
     * tamanho do cromossomo em bits
     */
    public static final int NUM_GENES = 10; // Linha de cada rainha (1 por coluna)
    /**
     * numero de locus por gene
     */
    public static final int NUM_LOCUS = 4; // 4 bits
    /**
     * numero de bits necessarios por cromossomo
     */
    public static int NUM_BITS = NUM_GENES * NUM_LOCUS;
}

