/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agNRainhas;

import ag.AG;
import ag.ConfigAG;
import ag.Cromossomo;

/**
 * Esta classe é a principal. Contém as configurações para execução do
 * algoritmo.
 *
 * @author Tacla (UTFPR, Curitiba)
 *
 */
/**
 * Classe principal do problema
 *
 * @author tacla
 */
public class AGNRainhas implements ConfigAG {

    public static final int NUM_RAINHAS = 10; 
    /** Valor maximo para o fitness */   
    /** Neste problema, eh conhecido - DEFINIR */
    public static final float MAX_FIT = 18; // vlr maximo do fitness (

    public static void main(String[] args) {
        Cromossomo melhor;
        int ctOtimo = 0;
        int ctExec = 0;
        long ctTotalFitness = 0;
        long ctTotalOtimo = 0;
        do {
            AG ag = new AG();
            melhor = ag.executarAG();
            if (melhor != null) {
                //melhor.imprimir("Melhor");
                if (melhor.fitness == MAX_FIT) {
                    ctOtimo++;
                }
            }
            ctExec++;
            ctTotalFitness += Cromossomo.ctChamadasFitness;
            ctTotalOtimo += ctOtimo;
            System.out.println(ctExec + "," + Cromossomo.ctChamadasFitness + "," + melhor.imprimirCSV());

        } while (ctExec < 5000);
        System.out.println("Encontradas " + ctOtimo + " sols otimas em " + MAX_EXECUCOES + " execucoes. Total chamadas ao fitness: " + ctTotalFitness + ". Total dos melhoes fitness " + ctTotalOtimo);
    }
}
