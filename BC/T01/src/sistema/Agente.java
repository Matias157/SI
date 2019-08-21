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
    Model model;
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    int plan[];
    double custo;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m; 
        
        // Posiciona o agente fisicamente no ambiente na posicao inicial
        model.setPos(8,0);
    }
    
     /**
     * Agente escolhe qual acao será executada em um ciclo de raciocinio.
     * Observar que o agente executa somente uma acao por ciclo.
     */
    public int deliberar() {               
        //  contador de acoes
        ct++;
        // @todo T1: perceber por meio do sensor a posicao atual e imprimir
        System.out.println("posição atual: (" + sensorPosicao().getLin() + ", " + sensorPosicao().getCol() + ")");
        // @todo T1: a cada acao escolher uma acao {N, NE, L, SE, S, SO, O, NO}
        if(sensorPosicao().getLin() > 4 && sensorPosicao().getCol() == 0)
        	executarIr(N); //executar a acao escolhida
        else if(sensorPosicao().getCol() < 3 && sensorPosicao().getLin() == 4)
        	executarIr(L);
        else if(sensorPosicao().getLin() < 8 && sensorPosicao().getCol() == 3)
        	executarIr(S);
        else if(sensorPosicao().getCol() < 8 && sensorPosicao().getLin() == 8)
        	executarIr(L);
        else if(sensorPosicao().getLin() > 2 && sensorPosicao().getCol() == 8)
        	executarIr(N);
        else
        	return(-1);
        
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
