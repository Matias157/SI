package sistema;

import ambiente.*;
import arvore.TreeNode;
import arvore.fnComparator;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author tacla
 */
public class Agente implements PontosCardeais {

    /* referência ao ambiente para poder atuar no mesmo*/
    Model model;
    Problema prob;
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    int plan[];
    Estado peredes[];
    double custo;
    float custoML = 0;
    static int ct = -1;
    int estrategia = 0; //0 = baseline, 1 = J48

    public Agente(Model m, int numLinha, int idcenario){
        String[][][] oponentes = new String[9][9][5]; //0 = peso, 1 = altura, 2 = dentes, 3 = olhos, 4 = resposta
        oponentes = geraCenario(m, numLinha);
        //m.desenhar();
        /*for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                for(int k = 0; k < 5; k++){
                    System.out.print(oponentes[i][j][k] + " ");
                }
                System.out.print("\n");
            }
        }*/
        Estado aux = estAtu;

        plan = buscaCheapestFirst(2);

        ct = -1;
        while(true){
            while(ct < plan.length){
                ct++;
                estAtu = prob.suc(estAtu, plan[ct]);

                if(prob.testeObjetivo(estAtu)){
                    break;
                }

                float custointer = prob.obterCustoAcao(estAtu, plan[ct], prob.suc(estAtu, plan[ct]));

                if(estrategia == 0){
                    if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && baseline()){
                        custointer *= 6;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && !baseline()){
                        custointer *= 3;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && baseline()){
                        custointer *= 1;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && !baseline()){
                        custointer *= 4;
                    }
                }

                else if(estrategia == 1){
                    if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && J48(oponentes, estAtu)){
                        custointer *= 6;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && !J48(oponentes, estAtu)){
                        custointer *= 3;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && J48(oponentes, estAtu)){
                        custointer *= 1;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && !J48(oponentes, estAtu)){
                        custointer *= 4;
                    }
                }
                custoML += custointer;
            }
            if(estrategia == 1)
                break;
            if(estrategia == 0){
                System.out.println("(baseline, " + idcenario + ", " + custoML + ")");
                try {
                    FileWriter fstream1 = null;
                    fstream1 = new FileWriter("/home/alexandre/SI/ML/T06/src/sistema/custobaseline.txt", true);
                    BufferedWriter out1 = new BufferedWriter(fstream1);
                    out1.write(String.valueOf(custoML) + "\n");
                    out1.close();
                } 
                catch(Exception e){
                    System.err.println(e);
                }
                estAtu = aux;
                custoML = 0;
                estrategia = 1;
                ct = -1;
            }
        }
        System.out.println("(J48, " + idcenario + ", " + custoML + ")");
        try {
            FileWriter fstream2 = null;
            fstream2 = new FileWriter("/home/alexandre/SI/ML/T06/src/sistema/custoJ48.txt", true);
            BufferedWriter out2 = new BufferedWriter(fstream2);
            out2.write(String.valueOf(custoML) + "\n");
            out2.close();
        } 
        catch(Exception e){
            System.err.println(e);
        }
    }

    public String[][][] geraCenario(Model m, int numLinha) {
        this.model = m;
        int initLin;
        int initCol;
        int fimLin;
        int fimCol;
        int numLinhas = numLinha;
        String[][][] oponentes = new String[9][9][5];
        Random random = new Random();

        prob = new Problema();
        prob.criarLabirinto(9, 9);
        prob.crencaLabir.porParedeVertical(0, 1, 0);
        prob.crencaLabir.porParedeVertical(0, 0, 1);
        prob.crencaLabir.porParedeVertical(5, 8, 1);
        prob.crencaLabir.porParedeVertical(5, 5, 2);
        prob.crencaLabir.porParedeVertical(8, 8, 2);
        prob.crencaLabir.porParedeHorizontal(4, 7, 0);
        prob.crencaLabir.porParedeHorizontal(7, 7, 1);
        prob.crencaLabir.porParedeHorizontal(3, 5, 2);
        prob.crencaLabir.porParedeHorizontal(3, 5, 3);
        prob.crencaLabir.porParedeHorizontal(7, 7, 3);
        prob.crencaLabir.porParedeVertical(6, 7, 4);
        prob.crencaLabir.porParedeVertical(5, 6, 5);
        prob.crencaLabir.porParedeVertical(5, 7, 7);

        while(true){
            initLin = random.nextInt(9);
            initCol = random.nextInt(9);
            if(prob.crencaLabir.parede[initLin][initCol] == 0)
                break;
        }
        m.setPos(initLin, initCol);

        Estado ini = this.sensorPosicao();
        prob.defEstIni(ini.getLin(), ini.getCol());

        this.estAtu = prob.estIni;

        while(true){
            fimLin = random.nextInt(9);
            fimCol = random.nextInt(9);
            if(fimLin != initLin && fimCol != initCol)
                if(prob.crencaLabir.parede[fimLin][fimCol] == 0)
                    break;
        }
        prob.defEstObj(fimLin, fimCol);

        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(prob.crencaLabir.parede[i][j] == 0){
                    if((i == initLin && j == initCol) || (i == fimLin && j == fimCol)){}
                    else {
                        //System.out.println("(" + i + ", " + j + ")");
                        for(int k = 0; k < 5; k++){
                            oponentes[i][j][k] = linhaPorNumero(numLinhas)[k];
                        }
                        //System.out.println(Arrays.toString(linhaPorNumero(numLinhas)));
                        numLinhas++;
                    }
                }
            }
        }
        return(oponentes);
    }

    public String[] linhaPorNumero(int num){
        String[] valores = null;
        String str = "vazia";
        try{
            FileInputStream fstream = new FileInputStream("/home/alexandre/SI/ML/T06/src/sistema/Oponentes.txt");
            DataInputStream in = new DataInputStream (fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            for (int lineNumber = 0; lineNumber < 5500; lineNumber++) {
                if (lineNumber == num)
                    str = br.readLine();
                else
                    br.readLine();
            }
            //System.out.println(str);
            valores = str.split(",");
            //System.out.println(Arrays.toString(valores));
            in.close();
        }
        catch(Exception e){
            System.err.println(e);
        }
        return(valores);
    }

    public boolean baseline(){ //true = gentil, false = nao gentil
        Random random = new Random();
        int val = random.nextInt(2); 
        if(val == 0)
            return(false);
        else
            return(true);
    }

    /*  
        J48 pruned tree
        ------------------
        dentes = normais
        |   corolhos = escura: S (177.0)
        |   corolhos = clara: S (165.0)
        |   corolhos = vermelha
        |   |   massa <= 100.25
        |   |   |   altura <= 1.82: N (28.0)
        |   |   |   altura > 1.82: S (43.0)
        |   |   massa > 100.25: N (111.0)
        dentes = afiados
        |   massa <= 99.57
        |   |   altura <= 1.81: N (62.0/1.0)
        |   |   altura > 1.81: S (118.0)
        |   massa > 99.57: N (296.0)
    */
    public boolean J48(String[][][] oponentes, Estado estAtu){ //true = gentil, false = nao gentil
        if(oponentes[estAtu.getLin()][estAtu.getCol()][2].contains("normais")){
            if(oponentes[estAtu.getLin()][estAtu.getCol()][3].contains("escura")){
                return(true);
            }
            else if(oponentes[estAtu.getLin()][estAtu.getCol()][3].contains("clara")){
                return(true);
            }
            else if(oponentes[estAtu.getLin()][estAtu.getCol()][3].contains("vermelha")){
                if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][0]) <= 100.25){
                    if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][1]) <= 1.82){
                        return(false);
                    }
                    else if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][1]) > 1.82){
                        return(true);
                    }
                }
                else if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][0]) > 100.25){
                    return(false);
                }
            }
        }
        else if(oponentes[estAtu.getLin()][estAtu.getCol()][2].contains("afiados")){
            if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][0]) <= 99.57){
                if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][1]) <= 1.81){
                    return(false);
                }
                else if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][1]) > 1.81){
                    return(true);
                }
            }
            else if(Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][0]) > 99.57){
                return(false);
            }
        }
        System.out.println("BUGO");
        return(true);
    }

    public void printPlano() {
        System.out.println("--- PLANO ---");
        for (int i = 0; i < plan.length; i++) {
            System.out.print(acao[plan[i]] + ">");
        }
        System.out.println("FIM\n\n");
    }

    /**
     * Atuador: solicita ao agente 'fisico' executar a acao.
     *
     * @param direcao
     * @return 1 caso movimentacao tenha sido executada corretamente
     */
    public int executarIr(int direcao) {
        model.ir(direcao);
        return 1; // deu certo
    }

    /**
     * Simula um sensor que realiza a leitura da posição atual no ambiente e
     * traduz para um par de coordenadas armazenadas em uma instância da classe
     * Estado.
     *
     * @return Estado um objeto que representa a posição atual do agente no
     * labirinto
     */
    private Estado sensorPosicao() {
        int pos[];
        pos = model.lerPos();
        return new Estado(pos[0], pos[1]);
    }

    public void printExplorados(ArrayList<Estado> expl) {
        System.out.println("--- Explorados --- (TAM: " + expl.size() + ")");
        for (Estado e : expl) {
            System.out.print(e.getString() + " ");
        }
        System.out.println("\n");
    }

    public void printFronteira(ArrayList<TreeNode> front) {
        System.out.println("--- Fronteira --- (TAM=" + front.size() + ")");
        for (TreeNode f : front) {
            String str;
            str = String.format("<%s %.2f+%.2f=%.2f> ", f.getState().getString(),
                    f.getGn(), f.getHn(), f.getFn());
            System.out.print(str);
        }
        System.out.println("\n");
    }

    public int[] montarPlano(TreeNode nSol) {
        int d = nSol.getDepth();
        int sol[] = new int[d];
        TreeNode pai = nSol;

        for (int i = sol.length - 1; i >= 0; i--) {
            sol[i] = pai.getAction();
            pai = pai.getParent();
        }
        return sol;
    }

    /**
     * Implementa uma heurística - a número 1 - para a estratégia A* No caso,
     * hn1 é a distância em colunas do estado passado como argumento até o
     * estado objetivo.
     *
     * @param estado: estado para o qual se quer calcular o valor de hn
     */
    private float hn1(Estado est) {
        return (float) Math.abs(est.getCol() - prob.estObj.getCol());
    }

    /**
     * Implementa uma heurística - a número 2 - para a estratégia A* No caso,
     * hn2 é a distância Euclidiana do estado passado como argumento até o
     * estado objetivo (calculada por Pitágoras).
     *
     * @param estado: estado para o qual se quer calcular o valor de hn
     */
    private float hn2(Estado est) {
        double distCol = Math.abs(est.getCol() - prob.estObj.getCol());
        double distLin = Math.abs(est.getLin() - prob.estObj.getLin());
        return (float) Math.sqrt(Math.pow(distLin, 2) + Math.pow(distCol, 2));
    }

    /**
     * Realiza busca com a estratégia de custo uniforme ou A* conforme escolha
     * realizada na chamada.
     *
     * @param tipo 0=custo uniforme; 1=A* com heurística hn1; 2=A* com hn2
     * @return
     */
    public int[] buscaCheapestFirst(int tipo) {
        // atributos para analise de depenho
        int ctNosArvore = 0; // contador de nos gerados e incluidos na arvore
        // nós que foram inseridos na arvore mas que
        // que não necessitariam porque o estado já
        // foi explorado ou por já estarem na fronteira 
        int ctNosDesprFront = 0;
        int ctNosDesprExpl = 0;

        // Algoritmo de busca
        TreeNode sol = null;     // armazena o nó objetivo
        TreeNode raiz = new TreeNode(null);
        raiz.setState(prob.estIni);
        raiz.setGnHn(0, 0);
        raiz.setAction(-1); // nenhuma acao
        ctNosArvore++;

        // cria FRONTEIRA com estado inicial 
        ArrayList<TreeNode> fronteira = new ArrayList<>(12);
        fronteira.add(raiz);

        // cria EXPLORADOS - lista de estados inicialmente vazia
        ArrayList<Estado> expl = new ArrayList<>(12);

        // estado na inicializacao da arvore de busca
        //System.out.println("\n*****\n***** INICIALIZACAO ARVORE DE BUSCA\n*****\n");
        //System.out.println("\nNós na árvore..............: " + ctNosArvore);
        //System.out.println("Desprezados já na fronteira: " + ctNosDesprFront);
        //System.out.println("Desprezados já explorados..: " + ctNosDesprExpl);
        //System.out.println("Total de nós gerados.......: " + (ctNosArvore + ctNosDesprFront + ctNosDesprExpl));

        while (!fronteira.isEmpty()) {
            //System.out.println("\n*****\n***** Inicio iteracao\n*****\n");
            //printFronteira(fronteira);
            TreeNode nSel = fronteira.remove(0);
            //System.out.println("   Selec. exp.: \n" + nSel.gerarStr() + "\n");

            // teste de objetivo
            if (nSel.getState().igualAo(this.prob.estObj)) {
                sol = nSel;
                //System.out.println("!!! Solução encontrada !!!");
                break;
            }
            expl.add(nSel.getState()); // adiciona estado aos já explorados
            //printExplorados(expl);

            // obtem acoes possiveis para o estado selecionado para expansão
            int acoes[] = prob.acoesPossiveis(nSel.getState());
            // adiciona um filho para cada acao possivel
            for (int ac = 0; ac < acoes.length; ac++) {
                if (acoes[ac] < 0) // a acao não é possível
                {
                    continue;
                }
                // INSERE NÓ FILHO NA ÁRVORE DE BUSCA - SEMPRE INSERE, DEPOIS
                // VERIFICA SE O INCLUI NA FRONTEIRA OU NÃO
                // instancia o filho ligando-o ao nó selecionado (nSel)
                TreeNode filho = nSel.addChild();
                // Obtem estado sucessor pela execução da ação <ac>
                Estado estSuc = prob.suc(nSel.getState(), ac);
                filho.setState(estSuc);
                // custo gn: custo acumulado da raiz ate o nó filho
                float gnFilho;
                gnFilho = nSel.getGn() + prob.obterCustoAcao(nSel.getState(), ac, estSuc);

                switch (tipo) {
                    case 0: // busca custo uniforme
                        filho.setGnHn(gnFilho, (float) 0); // deixa hn zerada porque é busca de custo uniforme  
                        break;
                    case 1: // A* com heurística 1
                        filho.setGnHn(gnFilho, hn1(estSuc));
                        break;
                    case 2: // A* com heurística 2
                        filho.setGnHn(gnFilho, hn2(estSuc));
                        break;
                }

                filho.setAction(ac);

                // INSERE NÓ FILHO NA FRONTEIRA (SE SATISFAZ CONDIÇÕES)
                // Testa se estado do nó filho foi explorado
                boolean jaExplorado = false;
                for (Estado e : expl) {
                    if (filho.getState().igualAo(e)) {
                        jaExplorado = true;
                        break;
                    }
                }
                // Testa se estado do nó filho está na fronteira, caso esteja
                // guarda o nó existente em nFront
                TreeNode nFront = null;
                if (!jaExplorado) {
                    for (TreeNode n : fronteira) {
                        if (filho.getState().igualAo(n.getState())) {
                            nFront = n;
                            break;
                        }
                    }
                }

                // se ainda não foi explorado ...
                if (!jaExplorado) {
                    // e não está na fronteira, então adiciona à fronteira
                    if (nFront == null) {
                        fronteira.add(filho);
                        fronteira.sort(new fnComparator()); // classifica ascendente
                        ctNosArvore++;
                    } else {
                        // se jah estah na fronteira temos que ver se eh melhor 
                        if (nFront.getFn() > filho.getFn()) { // no da fronteira tem custo maior que o filho
                            fronteira.remove(nFront);  // remove no da fronteira: pior
                            nFront.remove(); // retira-se da arvore
                            fronteira.add(filho);      // adiciona o filho que eh melhor
                            fronteira.sort(new fnComparator()); // classifica ascendente
                            // nao soma na arvore porque inclui o melhor e retira o pior
                        } else {
                            // conta como desprezado seja porque o filho eh pior e foi descartado
                            ctNosDesprFront++;

                        }
                    }
                } else {
                    ctNosDesprExpl++;
                }
                // esta contagem de maximos perdeu o sentido porque todos os 
                // nos sao armazenados na arvore de busca. Logo, ultima iteracao
                // contem o maximo de nos na arvore (inclusive com a fronteira
                // e os ja explorados (que tambem estao na arvore)
                /*
                if (fronteira.size() > maxNosFronteira)
                    maxNosFronteira = fronteira.size();
                if (expl.size() > maxNosExplorados)
                    maxNosExplorados = expl.size();
                 */
            }
            //raiz.printSubTree();
            //System.out.println("\nNós na árvore..............: " + ctNosArvore);
            //System.out.println("Desprezados já na fronteira: " + ctNosDesprFront);
            //System.out.println("Desprezados já explorados..: " + ctNosDesprExpl);
            //System.out.println("Total de nós gerados.......: " + (ctNosArvore + ctNosDesprFront + ctNosDesprExpl));
            //System.out.println("Nós desprezados total..........: " + (ctNosDesprFront + ctNosDesprExpl));
            //System.out.println("Máx nós front..: " + maxNosFronteira);
            //System.out.println("Máx nós explor.: " + maxNosExplorados);
        }

        // classifica a fronteira por 
        //Collections.sort(fronteira, new fnComparator());
        if (sol != null) {
            //System.out.println("!!! Solucao encontrada !!!");
            //System.out.println("!!! Custo: " + sol.getGn());
            //System.out.println("!!! Depth: " + sol.getDepth() + "\n");
            //System.out.println("\nNós na árvore..............: " + ctNosArvore);
            //System.out.println("Desprezados já na fronteira: " + ctNosDesprFront);
            //System.out.println("Desprezados já explorados..: " + ctNosDesprExpl);
            //System.out.println("Total de nós gerados.......: " + (ctNosArvore + ctNosDesprFront + ctNosDesprExpl));
            return montarPlano(sol);
        } else {
            System.out.println("### solucao NAO encontrada ###");
            return null;
        }
    }
}
