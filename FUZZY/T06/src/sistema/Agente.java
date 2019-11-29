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
import net.sourceforge.jFuzzyLogic.*;
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
    int estrategia = 1; //0 = baseline, 1 = J48
    String fileName = "/home/alexandre/SI/FUZZY/T06/src/sistema/fuzzy.fcl";
    FIS fis = FIS.load(fileName,true);

    public Agente(final Model m, final int numLinha, final int idcenario){
        String[][][] oponentes = new String[9][9][5]; //0 = peso, 1 = altura, 2 = dentes, 3 = olhos, 4 = resposta

        int conseguiu_empurrar = 0, tentou_empurrar = 0, errou = 0;

        boolean n_empurra;
		final boolean gentil;

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
        final Estado aux = estAtu;

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

                	n_empurra = baseline();

                    if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && n_empurra){
                        custointer *= 6;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && !n_empurra){
                        custointer *= 3;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && n_empurra){
                        custointer *= 1;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && !n_empurra){
                        custointer *= 4;
                    }
                }

                // N -> Rude
                // S -> Gentil
                // true -> n empurra
                // fals -> empurra

                else if(estrategia == 1){

                	n_empurra = J48(oponentes, estAtu);

                	if(!n_empurra){
                		final double peso = Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][0]);
                    	final double altura = Float.parseFloat(oponentes[estAtu.getLin()][estAtu.getCol()][1]);
                    	final double intensidade_minima = peso/altura; //calcula intensidade minima necessaria para empurrar

                        fis.setVariable("peso", peso);
                        fis.setVariable("altura", altura);
                        fis.evaluate();
                        final double intensidade = fis.getVariable("intensidade").getLatestDefuzzifiedValue();

                		//DESCOMENTE ISSO AQUI APRA SABER A DIFERENCA ENTRE AS INTENSIDADES REAL E FUZZY
                		//System.out.println("intensidade_fuzzy: " + intensidade);
                		//System.out.println("intensidade_minima: " + intensidade_minima);

                		tentou_empurrar++;
               
                		if(intensidade > intensidade_minima){ //compara e ve se conseguiu empurrar
                			conseguiu_empurrar++;
                        }
                        else{
                            errou++;
                        }
                	}

                    if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && n_empurra){
                        custointer *= 6;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("N") && !n_empurra){
                        custointer *= 3;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && n_empurra){
                        custointer *= 1;
                    }
                    else if(oponentes[estAtu.getLin()][estAtu.getCol()][4].contains("S") && !n_empurra){
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
                    fstream1 = new FileWriter("/home/alexandre/SI/FUZZY/T06/src/sistema/custobaseline.txt", true);
                    final BufferedWriter out1 = new BufferedWriter(fstream1);
                    out1.write(String.valueOf(custoML) + "\n");
                    out1.close();
                } 
                catch(final Exception e){
                    System.err.println(e);
                }
                estAtu = aux;
                custoML = 0;
                estrategia = 1;
                ct = -1;
            }
        }



        System.out.println("Conseguiu empurrar " + (double)conseguiu_empurrar*100/tentou_empurrar + "% das vezes que tentou.");

        System.out.println("(J48, " + idcenario + ", " + custoML + ")");
        try {
            FileWriter fstream2 = null;
            fstream2 = new FileWriter("/home/alexandre/SI/FUZZY/T06/src/sistema/acertosFuzzy.txt", true);
            final BufferedWriter out2 = new BufferedWriter(fstream2);
            out2.write(String.valueOf(conseguiu_empurrar) + "\n");
            out2.close();
        } 
        catch(final Exception e){
            System.err.println(e);
        }
        try {
            FileWriter fstream2 = null;
            fstream2 = new FileWriter("/home/alexandre/SI/FUZZY/T06/src/sistema/errosFuzzy.txt", true);
            final BufferedWriter out2 = new BufferedWriter(fstream2);
            out2.write(String.valueOf(errou) + "\n");
            out2.close();
        } 
        catch(final Exception e){
            System.err.println(e);
        }
    }

    public String[][][] geraCenario(final Model m, final int numLinha) {
        this.model = m;
        int initLin;
        int initCol;
        int fimLin;
        int fimCol;
        int numLinhas = numLinha;
        final String[][][] oponentes = new String[9][9][5];
        final Random random = new Random();

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

        final Estado ini = this.sensorPosicao();
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

    public String[] linhaPorNumero(final int num){
        String[] valores = null;
        String str = "vazia";
        try{
            final FileInputStream fstream = new FileInputStream("/home/alexandre/SI/ML/T06/src/sistema/Oponentes.txt");
            final DataInputStream in = new DataInputStream (fstream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
        catch(final Exception e){
            System.err.println(e);
        }
        return(valores);
    }

    public boolean baseline(){ //true = gentil, false = nao gentil
        final Random random = new Random();
        final int val = random.nextInt(2); 
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

    public boolean J48(final String[][][] oponentes, final Estado estAtu){ //true = gentil, false = nao gentil
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
    public int executarIr(final int direcao) {
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

    public void printExplorados(final ArrayList<Estado> expl) {
        System.out.println("--- Explorados --- (TAM: " + expl.size() + ")");
        for (final Estado e : expl) {
            System.out.print(e.getString() + " ");
        }
        System.out.println("\n");
    }

    public void printFronteira(final ArrayList<TreeNode> front) {
        System.out.println("--- Fronteira --- (TAM=" + front.size() + ")");
        for (final TreeNode f : front) {
            String str;
            str = String.format("<%s %.2f+%.2f=%.2f> ", f.getState().getString(),
                    f.getGn(), f.getHn(), f.getFn());
            System.out.print(str);
        }
        System.out.println("\n");
    }

    public int[] montarPlano(final TreeNode nSol) {
        final int d = nSol.getDepth();
        final int sol[] = new int[d];
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
    private float hn1(final Estado est) {
        return (float) Math.abs(est.getCol() - prob.estObj.getCol());
    }

    /**
     * Implementa uma heurística - a número 2 - para a estratégia A* No caso,
     * hn2 é a distância Euclidiana do estado passado como argumento até o
     * estado objetivo (calculada por Pitágoras).
     *
     * @param estado: estado para o qual se quer calcular o valor de hn
     */
    private float hn2(final Estado est) {
        final double distCol = Math.abs(est.getCol() - prob.estObj.getCol());
        final double distLin = Math.abs(est.getLin() - prob.estObj.getLin());
        return (float) Math.sqrt(Math.pow(distLin, 2) + Math.pow(distCol, 2));
    }

    /**
     * Realiza busca com a estratégia de custo uniforme ou A* conforme escolha
     * realizada na chamada.
     *
     * @param tipo 0=custo uniforme; 1=A* com heurística hn1; 2=A* com hn2
     * @return
     */
    public int[] buscaCheapestFirst(final int tipo) {
        // atributos para analise de depenho
        int ctNosArvore = 0; // contador de nos gerados e incluidos na arvore
        // nós que foram inseridos na arvore mas que
        // que não necessitariam porque o estado já
        // foi explorado ou por já estarem na fronteira 
        int ctNosDesprFront = 0;
        int ctNosDesprExpl = 0;

        // Algoritmo de busca
        TreeNode sol = null;     // armazena o nó objetivo
        final TreeNode raiz = new TreeNode(null);
        raiz.setState(prob.estIni);
        raiz.setGnHn(0, 0);
        raiz.setAction(-1); // nenhuma acao
        ctNosArvore++;

        // cria FRONTEIRA com estado inicial 
        final ArrayList<TreeNode> fronteira = new ArrayList<>(12);
        fronteira.add(raiz);

        // cria EXPLORADOS - lista de estados inicialmente vazia
        final ArrayList<Estado> expl = new ArrayList<>(12);

        // estado na inicializacao da arvore de busca
        //System.out.println("\n*****\n***** INICIALIZACAO ARVORE DE BUSCA\n*****\n");
        //System.out.println("\nNós na árvore..............: " + ctNosArvore);
        //System.out.println("Desprezados já na fronteira: " + ctNosDesprFront);
        //System.out.println("Desprezados já explorados..: " + ctNosDesprExpl);
        //System.out.println("Total de nós gerados.......: " + (ctNosArvore + ctNosDesprFront + ctNosDesprExpl));

        while (!fronteira.isEmpty()) {
            //System.out.println("\n*****\n***** Inicio iteracao\n*****\n");
            //printFronteira(fronteira);
            final TreeNode nSel = fronteira.remove(0);
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
            final int acoes[] = prob.acoesPossiveis(nSel.getState());
            // adiciona um filho para cada acao possivel
            for (int ac = 0; ac < acoes.length; ac++) {
                if (acoes[ac] < 0) // a acao não é possível
                {
                    continue;
                }
                // INSERE NÓ FILHO NA ÁRVORE DE BUSCA - SEMPRE INSERE, DEPOIS
                // VERIFICA SE O INCLUI NA FRONTEIRA OU NÃO
                // instancia o filho ligando-o ao nó selecionado (nSel)
                final TreeNode filho = nSel.addChild();
                // Obtem estado sucessor pela execução da ação <ac>
                final Estado estSuc = prob.suc(nSel.getState(), ac);
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
                for (final Estado e : expl) {
                    if (filho.getState().igualAo(e)) {
                        jaExplorado = true;
                        break;
                    }
                }
                // Testa se estado do nó filho está na fronteira, caso esteja
                // guarda o nó existente em nFront
                TreeNode nFront = null;
                if (!jaExplorado) {
                    for (final TreeNode n : fronteira) {
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


/*    
    public double intensidade_fuzzy(final double altura, final double peso){
    	
    	final double[] pert_altura = new double[3];
    	final double[] pert_peso = new double[3];
    	final double[] pert_intensidade = new double[3];
        
        final int BAIXA = 0;
        final int MEDIA = 1;
        final int ALTA = 2;
*/
    	/* pertencimentos referentes a ALTURA */
        // altura(baixa)
/*
    	if(altura <= 1.50)
    		pert_altura[BAIXA] = 1.0;
    	else if(altura <= 1.65)
    		pert_altura[BAIXA] = (1.65-altura)/0.15;
    	else 
    		pert_altura[BAIXA] = 0;

    	// altura(media)
    	if(altura <= 1.50)
    		pert_altura[MEDIA] = 0;
    	else if(altura <= 1.65)
    		pert_altura[MEDIA] = (altura-1.5)/0.15;
    	else if(altura <= 1.8)
    		pert_altura[MEDIA] = (1.8-altura)/0.15;
    	else
    		pert_altura[MEDIA] = 0;

    	// altura(alta)
    	if(altura <= 1.65)
    		pert_altura[ALTA] = 0;
    	else if(altura <= 1.8)
    		pert_altura[ALTA] = (altura-1.65)/0.15;
    	else 
    		pert_altura[ALTA] = 1;

*/
    	/* pertencimentos referentes a PESO */
        // PESO(baixa)
/*
    	if(peso <= 50.0)
    		pert_peso[BAIXA] = 1.0;
    	else if(peso <= 80.0)
    		pert_peso[BAIXA] = (80-peso)/30;
    	else 
    		pert_peso[BAIXA] = 0;

    	// peso(media)
    	if(peso <= 50.0)
    		pert_peso[MEDIA] = 0;
    	else if(peso <= 80.0)
    		pert_peso[MEDIA] = (peso-50)/30;
    	else if(peso <= 120)
    		pert_peso[MEDIA] = (120-peso)/40;
    	else
    		pert_peso[MEDIA] = 0;

    	// peso(alta)
    	if(peso <= 80.0)
    		pert_peso[ALTA] = 0;
    	else if(peso <= 120.0)
    		pert_peso[ALTA] = (peso-80)/40;
    	else 
    		pert_peso[ALTA] = 1;
 */

    	/* 
    	REGRAS (tudo and) 	ALTURA 	PESO 	INTENSIDADE
		1					alta 	alta 	alta
		2					baixa 	alta 	alta
		3					baixa 	baixa 	baixa
		4					media 	alta 	alta
		5					media 	media 	media
		6					media 	baixa 	baixa
		7					baixa 	media 	media
		8					alta 	media 	media
		9					alta 	baixa 	baixa

		*/

        // regra 1
/*
		if(pert_altura[ALTA] <= pert_peso[ALTA]){
			pert_intensidade[ALTA] = pert_altura[ALTA];
		}
		else{
			pert_intensidade[ALTA] = pert_peso[ALTA];
		}

		// regra 2
		if(pert_altura[BAIXA] <= pert_peso[ALTA]){
			if(pert_intensidade[ALTA] <= pert_altura[BAIXA])
				pert_intensidade[ALTA] = pert_altura[BAIXA];
		}
		else{
			if(pert_intensidade[ALTA] <= pert_peso[ALTA])
				pert_intensidade[ALTA] = pert_peso[ALTA];
		}

		// regra 3
		if(pert_altura[BAIXA] <= pert_peso[BAIXA]){
			pert_intensidade[BAIXA] = pert_altura[BAIXA];
		}
		else{
			pert_intensidade[BAIXA] = pert_peso[BAIXA];
		}

		// regra 4
		if(pert_altura[MEDIA] <= pert_peso[ALTA]){
			if(pert_intensidade[ALTA] <= pert_altura[MEDIA])
				pert_intensidade[ALTA] = pert_altura[MEDIA];
		}
		else{
			if(pert_intensidade[ALTA] <= pert_peso[ALTA])
				pert_intensidade[ALTA] = pert_peso[ALTA];
		}

		// regra 5
		if(pert_altura[MEDIA] <= pert_peso[MEDIA]){
			pert_intensidade[MEDIA] = pert_altura[MEDIA];
		}
		else{
			pert_intensidade[MEDIA] = pert_peso[MEDIA];
		}

		// regra 6
		if(pert_altura[MEDIA] <= pert_peso[BAIXA]){
			if(pert_intensidade[BAIXA] <= pert_altura[MEDIA])
				pert_intensidade[BAIXA] = pert_altura[MEDIA];
		}
		else{
			if(pert_intensidade[BAIXA] <= pert_peso[BAIXA])
				pert_intensidade[BAIXA] = pert_peso[BAIXA];
		}

		// regra 7
		if(pert_altura[BAIXA] <= pert_peso[MEDIA]){
			if(pert_intensidade[MEDIA] <= pert_altura[BAIXA])
				pert_intensidade[MEDIA] = pert_altura[BAIXA];
		}
		else{
			if(pert_intensidade[MEDIA] <= pert_peso[MEDIA])
				pert_intensidade[MEDIA] = pert_peso[MEDIA];
		}

		// regra 8
		if(pert_altura[ALTA] <= pert_peso[MEDIA]){
			if(pert_intensidade[MEDIA] <= pert_altura[ALTA])
				pert_intensidade[MEDIA] = pert_altura[ALTA];
		}
		else{
			if(pert_intensidade[MEDIA] <= pert_peso[MEDIA])
				pert_intensidade[MEDIA] = pert_peso[MEDIA];
		}

		// regra 9
		if(pert_altura[ALTA] <= pert_peso[BAIXA]){
			if(pert_intensidade[BAIXA] <= pert_altura[ALTA])
				pert_intensidade[BAIXA] = pert_altura[ALTA];
		}
		else{
			if(pert_intensidade[BAIXA] <= pert_peso[BAIXA])
				pert_intensidade[BAIXA] = pert_peso[BAIXA];
		}


		final double intensidade = 0;

		double numerador = 0;
		double denominador = 0;

		final double ponto_baixa_max = 75-25*pert_intensidade[BAIXA];
		final double ponto_media_min = 50+25*pert_intensidade[MEDIA];
		final double ponto_media_max = 100-25*pert_intensidade[MEDIA];
		final double ponto_alta_min = 75+25*pert_intensidade[ALTA];

		double int_baixa = 0;
		double int_alta = 0;
		double int_media = 0;

		for(int i = 0; i < 125; i++){
			// a altura da baixa
			if(i <= ponto_baixa_max){
				int_baixa = pert_intensidade[BAIXA];
			}
			else if(i <= 75){
				int_baixa = 75-25*i;
			}
			else{
				int_baixa = 0;
			}

			// a altura para media
			if(i <= 50){
				int_media = 0;
			}
			else if(i <= ponto_media_min){
				int_media = 50+25*i;
			}
			else if(i <= ponto_media_max){
				int_media = pert_intensidade[MEDIA];
			}
			else if(i <= 100){
				int_media = 100-25*i;
			}
			else{
				int_media = 0;
			}

			// a altura para as altas
			if(i <= 75){
				int_media = 0;
			}
			else if(i <= ponto_alta_min){
				int_alta = 75+25*i;
			}
			else{
				int_alta = pert_intensidade[ALTA];
			}

			// verifica qual dos três gráficos é maior em i e adiciona a conta
			if(int_alta >= int_baixa && int_alta >= int_media){
				numerador += i*int_alta;
				denominador += int_alta;
			}
			else if(int_media >= int_baixa && int_media >= int_alta){
				numerador += i*int_media;
				denominador += int_media;
			}
			else if(int_baixa >= int_alta && int_baixa >= int_media){
				numerador += i*int_baixa;
				denominador += int_baixa;
			}
		}

    	return numerador/denominador;
    }
*/

}
