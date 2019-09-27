/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ag;

import static ag.Operadores.selecionarPorRoleta;
import agNRainhas.AGNRainhas;

/**
 * Esta classe é a principal. Contém as configurações para execução do
 * algoritmo.
 *
 * @author Tacla (UTFPR, Curitiba)
 */
public class AG implements ConfigAG {

    // ESTRUTURAS PARA EXECUÇÃO DO AG
    /**
     * pais e filhos de uma iteração no vetor m de cromossomos; metade inicial
     * do vetor eh para os pais, metade final para os descendentes
     */
    protected Cromossomo crom[] = new Cromossomo[2 * TAM_POP];
    /**
     * armazena o fitness dos melhores
     */
    private float[] fitness = new float[TAM_POP];
    /**
     * estrutura auxiliar para classificar individuos com quick sort
     */
    private Cromossomo cromAux[] = new Cromossomo[2 * TAM_POP];

    /**
     * penalização: escolhe se é fitness com penalização (caso true). Caso
     * false, escolhe fitness+reparação
     */
    private static final boolean penalizacao = true;

    /**
     * Configura o AG e cria uma população inicial contendo TAM_POP cromossomos
     * preenchidas com itens aleatórios.
     *
     */
    public AG() {

        // zera o contador de chamada ao metodo de calcular fitness
        Cromossomo.ctChamadasFitness=0L;
        
        // cria uma populacao inicial de pais 
        for (int i = 0; i < TAM_POP; i++) {
            crom[i] = new Cromossomo();
            crom[i].inicializarCromossomo();
        }
    }

    /**
     * Realiza classificação dos indivíduos em ordem decrescente de fitness
     *
     * @param lowerIndex índice inferior
     * @param higherIndex índice superior
     */
    private void quickSort(int lowerIndex, int higherIndex) {
        int i = lowerIndex;
        int j = higherIndex;

        // calcula o pivot = meio do array
        Cromossomo pivot = cromAux[lowerIndex + (higherIndex - lowerIndex) / 2];
        while (i <= j) {

            while (cromAux[i].fitness > pivot.fitness) {
                i++;
            }
            while (cromAux[j].fitness < pivot.fitness) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j) {
            quickSort(lowerIndex, j);
        }
        if (i < higherIndex) {
            quickSort(i, higherIndex);
        }
    }

    /**
     * Método auxiliar ao quickSort
     *
     * @param i mochila a ser trocada
     * @param j mochila a ser trocada
     */
    private void exchangeNumbers(int i, int j) {
        Cromossomo temp = cromAux[i];
        cromAux[i] = cromAux[j];
        cromAux[j] = temp;
    }

    /**
     * Sort Classifica os cromossomos (vetor crom[]) em ordem decrescente de
     * fitness
     */
    public void sort() {
        if (crom == null || crom.length == 0) {
            return;
        }
        this.cromAux = crom;
        quickSort(0, crom.length - 1);
    }

    /**
     * Executa uma vez o Algoritmo Genético até que MAX_GERACOES sejam criadas.
     *
     * @return retorna a 1a. melhor mochila encontrada
     */
    public Cromossomo executarAG() {
        int geracao = 0;
        int[] sel;
        float melhorFit = (float) 0.0;
        Cromossomo melhorCrom = null;

        do {
            // seleciona por roleta individuos para reproducao entre a populacao
            for (int i = 0; i < TAM_POP; i++) {
                fitness[i] = crom[i].fitness;
            }
            sel = selecionarPorRoleta(fitness, TAM_POP);

            // crossover na populacao - faz crossover par a par na ordem da
            // selecao da roleta e gera os filhos na segunda metade do vetor crom[]
            int a, b; // indices para guardar os dois filhos dos pais que cruzam
            int j = 0;
            while (j < TAM_POP) {
                // Faz crossover com os selecionados da populacao.
                // Cada crossover gera dois novos individuos

                // primeiro, clonamos os pais:
                a = j + TAM_POP;
                b = j + TAM_POP + 1;
                crom[a] = crom[sel[j]].clonar();   // clona individuo 1
                crom[b] = crom[sel[j + 1]].clonar(); // clona individuo 2

                // cruzamos os pais (clonados), modificando-os 
                // e, assim geramos os filhos a e b
                Operadores.crossoverUmPonto(crom[a].bits, crom[b].bits, PROB_CROSS);
                j += 2;
            }
            // mutamos os filhos e recalculamos fitness
            for (int i = TAM_POP; i < 2 * TAM_POP; i++) {
                Operadores.mutar(crom[i].bits, AG.PROB_MUT);

                // UM DOS DOIS: reparacao XOR penalizacao
                // fitness com reparação de indivíduos infactíveis
                crom[i].calcularFitness();

            }

            // Selecao da nova populacao: seleciona os TAM_POP melhores de m[]
            // e coloca-os nas posicoes iniciais de m[] = nova populacao
            sort();
            if (crom[0].fitness > melhorFit) {
                melhorFit = crom[0].fitness;
                melhorCrom = crom[0];
            }
            geracao++;
            
            // Para coletar dados geracao a geracao de uma execucao especifica descomentar...
            //System.out.println("Ger: "+ geracao + " Melhor: " + crom[0].imprimirCSV());
            
        } while (geracao < MAX_GERACOES && melhorFit != (float) AGNRainhas.MAX_FIT);
        return melhorCrom;
    }
}
