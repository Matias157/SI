package sistema;

import problema.*;
import ambiente.*;
import arvore.TreeNode;
import arvore.fnComparator;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

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
    int plan[];
    double custo = 0;
    static int ct = -1;
    int solucao = -1;
           
    public Agente(Model m) {
        this.model = m; 
        // Posiciona o agente fisicamente no ambiente na posicao inicial
        model.setPos(8,0);

        problema.defEstIni(8, 0);
        problema.defEstObj(2, 8);
        problema.criarLabirinto(9, 9); 
        problema.crencaLabir.porParedeVertical(0, 1, 0);
        problema.crencaLabir.porParedeVertical(0, 0, 1);
        problema.crencaLabir.porParedeVertical(5, 8, 1);
        problema.crencaLabir.porParedeVertical(5, 5, 2);
        problema.crencaLabir.porParedeVertical(8, 8, 2);
        problema.crencaLabir.porParedeHorizontal(4, 7, 0);
        problema.crencaLabir.porParedeHorizontal(7, 7, 1);
        problema.crencaLabir.porParedeHorizontal(3, 5, 2);
        problema.crencaLabir.porParedeHorizontal(3, 5, 3);
        problema.crencaLabir.porParedeHorizontal(7, 7, 3);
        problema.crencaLabir.porParedeVertical(6, 7, 4);
        problema.crencaLabir.porParedeVertical(5, 6, 5);
        problema.crencaLabir.porParedeVertical(5, 7, 7);
    }
    
     /**
     * Agente escolhe qual acao será executada em um ciclo de raciocinio.
     * Observar que o agente executa somente uma acao por ciclo.
     */
    public int deliberar() {               
        //  contador de acoes
        while(solucao != 0 && solucao != 1 && solucao != 2){
            System.out.println("\nEscolha uma estrategia: \n");
            System.out.println("0 - Custo uniforme\n");
            System.out.println("1 - A* com h1\n");
            System.out.println("2 - A* com h2");          
            
            Scanner scanner = new Scanner(System.in);
            solucao = scanner.nextInt();
            
            if (this.solucao == 0){
                System.out.println("Opcao escolhida: Custo uniforme\n");
                plan = bCustoUniforme();
            }
            else if (this.solucao == 1){
                System.out.println("Opcao escolhida: A* com H1\n");
                plan = A_estrelah1();
            }
            else if (this.solucao == 2){
                System.out.println("Opcao escolhida: A* com H2\n");
                plan = A_estrelah2();
            }
            else{
                System.out.println("Opcao invalida.\n");
            }
        }

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
        System.out.print("ct = " + ct + " de " + plan.length + "." + " Ação escolhida = ");
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

    public int[] bCustoUniforme(){
        TreeNode raiz = new TreeNode(null);
        raiz.setGnHn(0, 0);
        raiz.setState(sensorPosicao());
        int i, j;
        List<Integer> solucao = new ArrayList<>();
        int [] plano;
        TreeNode filho;
        TreeNode noAtual = raiz;
        boolean achouSolucao = false;
        boolean estadoVisitado;
        final List<Estado> estadosVisitados = new ArrayList<>();
        int [] acoesPossiveis;
        Estado proxEstado;
        fnComparator comparador = new fnComparator();
        PriorityQueue fronteira = new PriorityQueue(comparador);
        
        fronteira.add(noAtual);
        int iteracoes = 0;
        int count = 1;

        while(!achouSolucao){
            noAtual = (TreeNode) fronteira.remove();
            acoesPossiveis = problema.acoesPossiveis(noAtual.getState());
            if (problema.testeObjetivo(noAtual.getState()))
                achouSolucao = true;
            
            estadosVisitados.add(noAtual.getState());
            
            for (i = 0; i < acoesPossiveis.length; i++){
                if (acoesPossiveis[i] != -1){   
                    proxEstado = problema.suc(noAtual.getState(), i);
                    estadoVisitado = false;
                    
                    for (j=0; j < estadosVisitados.size(); j++) {
                        if(proxEstado.igualAo(estadosVisitados.get(j))){
                            estadoVisitado = true;
                        }
                    }
                    
                    if (!estadoVisitado){
                        filho = noAtual.addChild();
                        filho.setState(proxEstado);
                        filho.setAction(i);
                        filho.setGnHn(problema.obterCustoAcao(noAtual.getState(), i, proxEstado) + noAtual.getGn(), 0);
                        count += 1;
                        fronteira.add(filho);
                    }                        
                }    
            }
            iteracoes += 1;
        }
        
        while(noAtual.getParent() != null)
        {
            solucao.add(noAtual.getAction());
            noAtual = noAtual.getParent();
        }
        
        plano = new int[solucao.size()];
        for(i  = 0; i < solucao.size(); i++)
            plano[i] = solucao.get(solucao.size()-i-1);
        
        return plano;
    }

    public int[] A_estrelah1(){
        TreeNode raiz = new TreeNode(null);
        raiz.setGnHn(0, 0);
        raiz.setState(sensorPosicao());
        int i, j;
        List<Integer> solucao = new ArrayList<>();
        int [] plano;
        TreeNode filho;
        TreeNode noAtual = raiz;
        boolean achouSolucao = false;
        boolean estadoVisitado;
        final List<Estado> estadosVisitados = new ArrayList<>();
        int [] acoesPossiveis;
        Estado proxEstado;
        fnComparator comparador = new fnComparator();
        PriorityQueue fronteira = new PriorityQueue(comparador);
        
        fronteira.add(noAtual);
        int iteracoes = 0;
        int count = 1;

        while(!achouSolucao){
            noAtual = (TreeNode) fronteira.remove();
            acoesPossiveis = problema.acoesPossiveis(noAtual.getState());
            if (problema.testeObjetivo(noAtual.getState()))
                achouSolucao = true;
            
            estadosVisitados.add(noAtual.getState());
            
            for (i = 0; i < acoesPossiveis.length; i++){
                if (acoesPossiveis[i] != -1){   
                    proxEstado = problema.suc(noAtual.getState(), i);
                    estadoVisitado = false;
                    
                    for (j=0; j < estadosVisitados.size(); j++) {
                        if(proxEstado.igualAo(estadosVisitados.get(j)))
                            estadoVisitado = true;
                    }
                    
                    if (!estadoVisitado){
                        filho = noAtual.addChild();
                        filho.setState(proxEstado);
                        filho.setAction(i);
                        float hn = (float)Math.sqrt((noAtual.getState().getLin()-problema.estObj.getLin())*(noAtual.getState().getLin()-problema.estObj.getLin())+(noAtual.getState().getCol()-problema.estObj.getCol())*(noAtual.getState().getCol()-problema.estObj.getCol()));
                        filho.setGnHn(problema.obterCustoAcao(noAtual.getState(), i, proxEstado) + noAtual.getGn(), hn);
                        count += 1;
                        fronteira.add(filho);
                    }                        
                }    
            }
            iteracoes += 1;
        }
        
        while(noAtual.getParent() != null)
        {
            solucao.add(noAtual.getAction());
            noAtual = noAtual.getParent();
        }
        
        plano = new int[solucao.size()];
        for(i  = 0; i < solucao.size(); i++)
            plano[i] = solucao.get(solucao.size()-i-1);
        
        return plano;
    }

    public int[] A_estrelah2(){
        TreeNode raiz = new TreeNode(null);
        raiz.setGnHn(0, 0);
        raiz.setState(sensorPosicao());
        int i, j;
        List<Integer> solucao = new ArrayList<>();
        int [] plano;
        TreeNode filho;
        TreeNode noAtual = raiz;
        boolean achouSolucao = false;
        boolean estadoVisitado;
        final List<Estado> estadosVisitados = new ArrayList<>();
        int [] acoesPossiveis;
        Estado proxEstado;
        fnComparator comparador = new fnComparator();
        PriorityQueue fronteira = new PriorityQueue(comparador);
        
        fronteira.add(noAtual);
        int iteracoes = 0;
        int count = 1;

        while(!achouSolucao){
            noAtual = (TreeNode) fronteira.remove();
            acoesPossiveis = problema.acoesPossiveis(noAtual.getState());
            if (problema.testeObjetivo(noAtual.getState()))
                achouSolucao = true;
            
            estadosVisitados.add(noAtual.getState());
            
            for (i = 0; i < acoesPossiveis.length; i++){
                if (acoesPossiveis[i] != -1){   
                    proxEstado = problema.suc(noAtual.getState(), i);
                    estadoVisitado = false;
                    
                    for (j=0; j < estadosVisitados.size(); j++) {
                        if(proxEstado.igualAo(estadosVisitados.get(j)))
                            estadoVisitado = true;
                    }
                    
                    if (!estadoVisitado){
                        filho = noAtual.addChild();
                        filho.setState(proxEstado);
                        filho.setAction(i);
                        float hn;
                        hn = 0f;
                        filho.setGnHn(problema.obterCustoAcao(noAtual.getState(), i, proxEstado) + noAtual.getGn(), hn);
                        count += 1;
                        fronteira.add(filho);
                    }                        
                }    
            }
            iteracoes += 1;
        }
        
        while(noAtual.getParent() != null)
        {
            solucao.add(noAtual.getAction());
            noAtual = noAtual.getParent();
        }
        
        plano = new int[solucao.size()];
        for(i  = 0; i < solucao.size(); i++)
            plano[i] = solucao.get(solucao.size()-i-1);
        
        return plano;
    }
}
