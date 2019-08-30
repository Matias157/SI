package sistema;

import problema.*;
import ambiente.*;
import arvore.TreeNode;
import arvore.fnComparator;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;

/**
 *
 * @author tacla
 */
public class Agente implements PontosCardeais {
    /* referência ao ambiente para poder atuar no mesmo*/
    Problema problema = new Problema(); 
    Labirinto labirinto;
    Model model;
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    int plan[] = {N, N, N, N, L, L, L, S, S, S, S, L, L, L, L, L, N, N, N, N, N, N};
    double custo = 0;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m; 
        // Posiciona o agente fisicamente no ambiente na posicao inicial
        model.setPos(8,0);

        labirinto = model.labir;

        problema.defEstIni(8, 0);
        problema.defEstObj(2, 8);
        problema.crencaLabir = labirinto;
    }
    
     /**
     * Agente escolhe qual acao será executada em um ciclo de raciocinio.
     * Observar que o agente executa somente uma acao por ciclo.
     */
    public int deliberar() {               
        //  contador de acoes
        ct++;

        if(problema.testeObjetivo(sensorPosicao())){
            System.out.println("****************** fim do ciclo ***********************");
            return -1;
        }

        // @todo T1: perceber por meio do sensor a posicao atual e imprimir
        System.out.println("*************** inicio do ciclo ***********************");
        System.out.println("estado atual: (" + sensorPosicao().getLin() + ", " + sensorPosicao().getCol() + ")");
        System.out.println("sucessor: (" + problema.suc(sensorPosicao(), plan[ct]).getLin() + ", " + problema.suc(sensorPosicao(), plan[ct]).getCol() + ")");
        System.out.print("ações possiveis: { ");
        if(problema.acoesPossiveis(sensorPosicao())[0] == 1){
            System.out.print("N ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[1] == 1){
            System.out.print("NE ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[2] == 1){
            System.out.print("L ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[3] == 1){
            System.out.print("SE ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[4] == 1){
            System.out.print("S ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[5] == 1){
            System.out.print("SO ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[6] == 1){
            System.out.print("O ");
        }
        if(problema.acoesPossiveis(sensorPosicao())[7] == 1){
            System.out.print("NO ");
        }
        System.out.println("}");
        System.out.print("ct = " + ct + " de 21." + " Ação escolhida = ");
        if(plan[ct] == 0){
            System.out.println("N");
        }
        else if(plan[ct] == 1){
            System.out.println("NE");
        }
        else if(plan[ct] == 2){
            System.out.println("L");
        }
        else if(plan[ct] == 3){
            System.out.println("SE");
        }
        else if(plan[ct] == 4){
            System.out.println("S");
        }
        else if(plan[ct] == 5){
            System.out.println("SO");
        }
        else if(plan[ct] == 6){
            System.out.println("O");
        }
        else{
            System.out.println("NO");   
        }
        System.out.println("custo até o momento (com a ação escolhida): " + custo);
        System.out.println("*******************************************************");
        // @todo T1: a cada acao escolher uma acao {N, NE, L, SE, S, SO, O, NO}
        if(plan[ct] == N || plan[ct] == S || plan[ct] == L || plan[ct] == O){
            custo++;
        }
        else{
            custo+=1.5;
        }

        executarIr(plan[ct]);
        
        return 1; // Se retornar -1, encerra o agente
    }

    /**
    * Atuador: executa 'fisicamente' a acao Ir
    * @param direcao um dos pontos cardeais
    */
    public int executarIr(int direcao) {
        //@todo T1 - invocar metodo do Model - atuar no ambiente
        model.ir(direcao);
        return 1; // deu certo
    }

    /**
     * Simula um sensor que realiza a leitura da posição atual no ambiente e
     * traduz para um par de coordenadas armazenadas em uma instância da classe
     * Estado.
     * @return Estado contendo a posição atual do agente no labirinto 
     */
    public Estado sensorPosicao() {
    	int coord[] = model.lerPos();
        //@todo T1 - sensor deve ler a posicao do agente no labirinto (environment)
        return new Estado(coord[0],coord[1]);
    }
    
}
