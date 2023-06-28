import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import entidade.Agente;
import entidade.Entidade;
import entidade.Ouro;
import entidade.Poco;
import entidade.Wumpus;
import render.Janela;
import treino.TreinoGenetico;


public class Main{
    //partes do mapa
    static int tamanhoMapa;
    static int quantidadePoco;
    static int quantidadeWumpus;
    static String mapa[][];
    static String mapaPosicoes[][];
    static String mapaSensacoes[][];

    //simulações
    static double tempoAtualizacao = 0.2f;
    static int rodadaAtual = 0;
    static int rodadas = 1000;

    //elementos
    static Agente agente;
    static Agente melhorAgente;
    static Ouro ouro;
    static ArrayList<Wumpus> wumpus = new ArrayList<Wumpus>();
    static ArrayList<Poco> pocos = new ArrayList<Poco>();

    //geração aleatória
    static Random random = new Random();

    //posição inicial padrão do agente
    static int xInicialAgente;
    static int yInicialAgente;

    //controle de geração dos poços
    static boolean paredeOcupada = false;

    static String[] posicoesMapa = {"x", ".", "+"};

    //dados pro treino
    static final int TAMANHO_POPULACAO = 6_000;
    static final int EVOLUCAO_MUTACAO = 1;
    static final int EVOLUCAO_CROSSOVER = 2;
    static int metodoEvolucao = 2;//alterar metodo evolutivo
    
    //dados da rede
    static final int neuroniosEntrada = 10;//10
    static final int neuroniosOcultas = 9;//9
    static final int neuroniosSaida = 9;//9
    static final int quantidadeOcultas = 3;//3

    //informações
    static Janela janela;
    static long redesQueGanharam = 0;

    public static void main(String[] args){
		limparConsole();

        tamanhoMapa = 7;//treinar em mapa fixo

        criarMapas();
        calcularMapaPosicoes();

		novaPartida();

        loopJogo();
    }


	public static String pegarEntrada(){
		String entrada;

		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			entrada = br.readLine();
		
		}catch(Exception e){
			entrada = "";
		}

		return entrada;
	}


    public static void loopJogo(){
        //entrada da rede do agente
        double[] dadosAmbiente = new double[neuroniosEntrada];
        for(int i = 0; i < dadosAmbiente.length; i++) dadosAmbiente[i] = 0;

        TreinoGenetico treinoGenetico = new TreinoGenetico(TAMANHO_POPULACAO);
        boolean individuoMorreu = false;

        //primeira geração
        for(int i = 0; i < treinoGenetico.tamanhoPopulacao; i++){
            treinoGenetico.carregarIndividuo(treinoGenetico.gerarIndividuo(tamanhoMapa, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, mapaSensacoes));
            copiarElementosParaAgente(treinoGenetico.individuos.get(i), wumpus, pocos, ouro);
            calcularMapaSensacoesAgente(treinoGenetico.individuos.get(i));
        }

        melhorAgente = treinoGenetico.individuos.get(0);
        janela = new Janela();
        janela.painel.melhorAgente = melhorAgente;

        int i = 0; //contador
        try{
            while(rodadaAtual < rodadas){

                for(i = 0; i < treinoGenetico.tamanhoPopulacao; i++){//calcular uma ação de cada individuo

                    if(treinoGenetico.individuos.get(i).vivo){
                        calcularMapaSensacoesAgente(treinoGenetico.individuos.get(i));
                        copiarSensacoesParaAgente(treinoGenetico.individuos.get(i));
                        atualizarDados(treinoGenetico.individuos.get(i), dadosAmbiente);
                        
                        individuoMorreu = treinoGenetico.individuos.get(i).calcularAcao(dadosAmbiente);
                        if(!individuoMorreu){
                            individuoMorreu = verificarColisao(treinoGenetico.individuos.get(i));
                        }

                        if(!individuoMorreu){
                            individuoMorreu = verificarJogoGanho(treinoGenetico.individuos.get(i), i);
                        }

                        if(individuoMorreu){
                            treinoGenetico.individuos.get(i).vivo = false;
                            treinoGenetico.individuosVivos--;
                            individuoMorreu = false;
                        }
                    }

                    if(treinoGenetico.individuosVivos < 1) break;
                }

                if(treinoGenetico.individuosVivos < 1){//proxima geração
                    System.out.println("Ajustando população");

                    if(metodoEvolucao == EVOLUCAO_CROSSOVER){
                        treinoGenetico.ajustarPorCrossover(tamanhoMapa, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, mapaSensacoes);
                    
                    }else if(metodoEvolucao == EVOLUCAO_MUTACAO){
                        treinoGenetico.ajustarPorMutacao(tamanhoMapa, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, mapaSensacoes);
                    
                    }else throw new IllegalArgumentException("Método de evolução fora dos parâmetros suportados");

                    novaPartida();
                    for(int j = 0; j < treinoGenetico.tamanhoPopulacao; j++){
                        copiarElementosParaAgente(treinoGenetico.individuos.get(j), wumpus, pocos, ouro);  
                    }
                
                }else{//acompanhar o melhor agente
                    melhorAgente = treinoGenetico.melhorIndividuoVivo();
                    calcularMapaSensacoesAgente(melhorAgente);
                    copiarSensacoesParaAgente(melhorAgente);
                    imprimirPartida(treinoGenetico);
                    janela.desenhar(
                        melhorAgente,
                        treinoGenetico,
                        redesQueGanharam,
                        metodoEvolucao
                    );
    
                    Thread.sleep((long) (1000 * tempoAtualizacao));
                    if(treinoGenetico.geracaoAtual % 30 == 0) System.gc();//limpar lixo
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //controle sobre reinicio de partida
    public static void novaPartida(){
        gerarEntidadesFixas();
        calcularMapaSensacoes();
        //rodadaAtual++;
    }


    public static void imprimirPartida(TreinoGenetico treinoGenetico){
        limparConsole();
        System.out.println("Geração atual: " + treinoGenetico.geracaoAtual);
        System.out.println("Individuos vivos: " + treinoGenetico.individuosVivos + "/" + treinoGenetico.tamanhoPopulacao);

        System.out.println("\nMelhor individuo vivo");
        System.out.println("Fitness: " + melhorAgente.fitness);
        System.out.println("Ouro coletado: " + melhorAgente.getOuroColetado());
        System.out.println("Matou Wumpus: " + melhorAgente.getMatouWumpus());

        mostrarMapa();
        System.out.println("x: " + melhorAgente.getX() + " y: " + melhorAgente.getY());
        System.out.println("Sentindo: {" + melhorAgente.getSensacoes() + "  }");

        Auxiliares.imprimirarApenasSaidasRede(melhorAgente.rede);
    }


    //geração
    public static void gerarEntidadesFixas(){
        if(agente == null) agente = new Agente(xInicialAgente, yInicialAgente, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, tamanhoMapa, mapaSensacoes);
        else{
            int pontos = agente.getPontos();
            int jogosGanhos = agente.getJogosGanhos();
            int mortes = agente.getMortes();
            int quedasEmPocos = agente.getQuedasEmPocos();
            int flechasAcertadas = agente.getFlechasAcertadas();
            int mortesPeloWumpus = agente.getMortesPeloWumpus();

            agente = new Agente(xInicialAgente, yInicialAgente, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, tamanhoMapa, mapaSensacoes);
            agente.setPontos(pontos);
            agente.setJogosGanhos(jogosGanhos);
            agente.setMortes(mortes);
            agente.setQuedasEmPocos(quedasEmPocos);
            agente.setFlechasAcertadas(flechasAcertadas);
            agente.setMortesPeloWumpus(mortesPeloWumpus);
        }  

        wumpus.clear();
        wumpus.add(new Wumpus(1, 4));

        ouro = new Ouro(1, 6);
    
        pocos.clear();
        pocos.add(new Poco(3, 2));
        pocos.add(new Poco(4, 2));
        pocos.add(new Poco(2, 6));
        pocos.add(new Poco(6, 5));
        pocos.add(new Poco(2, 2));
    }


    public static void gerarEntidadesAleatorias(){
        //verificar instancia existente e tratar para nao perder valores de simulaçoes passadas
        if(agente == null) agente = new Agente(xInicialAgente, yInicialAgente, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, tamanhoMapa, mapaSensacoes);
        else{
            int pontos = agente.getPontos();
            int jogosGanhos = agente.getJogosGanhos();
            int mortes = agente.getMortes();
            int quedasEmPocos = agente.getQuedasEmPocos();
            int flechasAcertadas = agente.getFlechasAcertadas();
            int mortesPeloWumpus = agente.getMortesPeloWumpus();
			int flechasAtiradas = agente.getFlechasAtiradas();

            agente = new Agente(xInicialAgente, yInicialAgente, neuroniosEntrada, neuroniosOcultas, neuroniosSaida, quantidadeOcultas, tamanhoMapa, mapaSensacoes);
            agente.setPontos(pontos);
            agente.setJogosGanhos(jogosGanhos);
            agente.setMortes(mortes);
            agente.setQuedasEmPocos(quedasEmPocos);
            agente.setFlechasAcertadas(flechasAcertadas);
            agente.setMortesPeloWumpus(mortesPeloWumpus);
			agente.setFlechasAtiradas(flechasAtiradas);
        }

        //adicionar vários wumpus
        int x, y;
        if(wumpus.size() > 0) wumpus.clear();
        for(int i = 0; i < quantidadeWumpus; i++){
            do{
                x = random.nextInt(tamanhoMapa);
                y = random.nextInt(tamanhoMapa);
            }while(entidadeExistente(agente, x, y));
            wumpus.add(new Wumpus(x, y));
        }


        do{//pode ficar na mesma casa que o wumpus
            x = random.nextInt(tamanhoMapa);
            y = random.nextInt(tamanhoMapa);
        }while(entidadeExistente(agente, x, y) || wumpusExistente(x, y));
        ouro = new Ouro(x, y);
        
        //deletar poços existentes caso seja de uma re execução
        if(pocos.size() > 0) pocos.clear();
        paredeOcupada = false;
        for(int i = 0; i < quantidadePoco; i++){
            while(true){
                x = random.nextInt(tamanhoMapa);
                y = random.nextInt(tamanhoMapa);  

                //posição não ocupada
                if(!entidadeExistente(agente, x, y) && !wumpusExistente(x, y) && !entidadeExistente(ouro, x, y) && !pocoExistente(x, y)){

                    if(paredeOcupada){
                        if(mapaPosicoes[x][y].equalsIgnoreCase(posicoesMapa[0]) || mapaPosicoes[x][y].equalsIgnoreCase(posicoesMapa[2])){//borda, centro
                            pocos.add(new Poco(x, y));
                            break;
                        }
                    }else{
                        if(mapaPosicoes[x][y].equalsIgnoreCase(posicoesMapa[1])){// parede
                            paredeOcupada = true;
                            pocos.add(new Poco(x, y));
                            break;
                        }
                    }
              
                }
                
            }
        }
    }


    public static void criarMapas(){
        if(tamanhoMapa < 3) tamanhoMapa = 3;
		//evitar que o agente nasça em posições erradas
		xInicialAgente = tamanhoMapa-1;
		yInicialAgente = 0;
		quantidadePoco = (int)(((tamanhoMapa * tamanhoMapa)-1)*0.2);
        quantidadeWumpus = (int)(quantidadePoco * 0.4);

        mapa = new String[tamanhoMapa][tamanhoMapa];
        mapaPosicoes = new String[tamanhoMapa][tamanhoMapa];
        mapaSensacoes = new String[tamanhoMapa][tamanhoMapa];

        for (int i = 0; i < tamanhoMapa; i++) {
            for (int j = 0; j < tamanhoMapa; j++){
                mapa[i][j] = " ";
            } 
        }
    }


    //impressão
    public static void mostrarMapa(){
        System.out.println("\nMapa da partida (tamanho " + tamanhoMapa +")");

        String conteudo;
        for(int linha = 0; linha < tamanhoMapa; linha++){
            for(int coluna = 0; coluna < tamanhoMapa; coluna++){
				if(coluna == 0) conteudo = "|";
				else conteudo = " ";

                if(entidadeExistente(melhorAgente, linha, coluna) && melhorAgente.vivo) conteudo += melhorAgente.getSimbolo();         
                else if(wumpusExistente(melhorAgente, linha, coluna)){
                    conteudo += wumpus.get(0).getSimbolo();
                }
                else if(entidadeExistente(melhorAgente.ouroMapa, linha, coluna) && !melhorAgente.ouroMapa.getColetado()) conteudo += ouro.getSimbolo();
                else if(pocoExistente(melhorAgente, linha, coluna)) conteudo += pocos.get(0).getSimbolo();
                else conteudo += mapa[linha][coluna];
                
				if(coluna == tamanhoMapa-1) conteudo += "|";
				else conteudo += " ";
                System.out.print(conteudo);
            }
            System.out.println();
        }
        System.out.println();      
    }


    public static void mostrarMapaPosicoes(){
        System.out.println("\nMapa de posições");

        for (int linha = 0; linha < tamanhoMapa; linha++) {
            for (int coluna = 0; coluna < tamanhoMapa; coluna++){
                System.out.print(" " + mapaPosicoes[linha][coluna] + " ");
            } 
            System.out.println();
        }
    }


    public static void mostrarMapaSensacoes(){
        System.out.println("\nMapa de sensações");

        for (int linha = 0; linha < tamanhoMapa; linha++) {
            for (int coluna = 0; coluna < tamanhoMapa; coluna++){
                System.out.print("[\t" + mapaSensacoes[linha][coluna] + "\t]");
            } 
            System.out.println();
        }
        System.out.println();
    }


    public static void limparConsole(){
    	try{
        	String nomeSistema = System.getProperty("os.name");

            if(nomeSistema.contains("Windows")){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
				return;
        	}else{
        		for (int i = 0; i < 100; i++){
        			System.out.println();
        		}        		
        	}
        }catch(Exception e){
            return;
        }
    }


    //lógica
    public static boolean entidadeExistente(Entidade entidade, int x, int y){
        if(entidade.getX() == x && entidade.getY() == y) return true;
        return false;
    }


    public static boolean pocoExistente(int x, int y){
        for(int i = 0; i < pocos.size(); i++){
            if((x == pocos.get(i).getX()) && (y == pocos.get(i).getY())) return true;
        }
        return false;
    }


    public static boolean pocoExistente(Agente agente, int x, int y){
        for(int i = 0; i < pocos.size(); i++){
            if((x == pocos.get(i).getX()) && (y == pocos.get(i).getY())) return true;
        }
        return false;
    }


    public static boolean wumpusExistente(int x, int y){
        for(int i = 0; i < wumpus.size(); i++){
            if((x == wumpus.get(i).getX()) && (y == wumpus.get(i).getY()) && (wumpus.get(i).getStatus())) return true;
        }
        return false;
    }


    public static boolean wumpusExistente(Agente agente, int x, int y){
        for(int i = 0; i < agente.wumpusMapa.size(); i++){
            if((x == agente.wumpusMapa.get(i).getX()) && (y == agente.wumpusMapa.get(i).getY()) && (agente.wumpusMapa.get(i).getStatus())) return true;
        }
        return false;
    }


    public static void calcularMapaPosicoes(){
        for (int i = 0; i < tamanhoMapa; i++){
            for (int j = 0; j < tamanhoMapa; j++) mapaPosicoes[i][j] = " "; 
        }

        for (int linha = 0; linha < tamanhoMapa; linha++){
            for(int coluna = 0; coluna < tamanhoMapa; coluna++){
                //verificar cantos
                if(linha == 0 && coluna == 0) mapaPosicoes[linha][coluna] = posicoesMapa[0];
                else if(linha == 0 && coluna == (tamanhoMapa-1)) mapaPosicoes[linha][coluna] = posicoesMapa[0];
                else if(linha == (tamanhoMapa-1) && coluna == 0) mapaPosicoes[linha][coluna] = posicoesMapa[0];
                else if(linha == (tamanhoMapa-1) && coluna == (tamanhoMapa-1)) mapaPosicoes[linha][coluna] = posicoesMapa[0];

                //verificar centros
                else if(linha != 0 && coluna != 0 && 
                ((linha+1) < (tamanhoMapa)) && ((coluna+1) < (tamanhoMapa))) mapaPosicoes[linha][coluna] = posicoesMapa[2];

                //verificar paredes
                else mapaPosicoes[linha][coluna] = posicoesMapa[1]; 
            }
        }
    }


    public static void calcularMapaSensacoes(){
        for (int i = 0; i < tamanhoMapa; i++){
            for (int j = 0; j < tamanhoMapa; j++) mapaSensacoes[i][j] = " ";
        }

        if(!ouro.getColetado()) mapaSensacoes[ouro.getX()][ouro.getY()] = " " + ouro.getSensacao();

        int x, y;
        for(int i = 0; i < wumpus.size(); i++){//percorrer cada monstro da lista
            for(int contador = 0; contador < 4; contador++){//percorrer cada direção adjascente
                x = wumpus.get(i).getX();
                y = wumpus.get(i).getY();
    
                //definir direções
                switch(contador){
                    case 0: x -= 1; break;//norte
                    case 1: x += 1; break;//sul
                    case 2: y -= 1; break;//oeste
                    case 3: y += 1; break;//leste
                }
    
                //verificar limites do mapa
                if((x >= 0) && (x < tamanhoMapa) && (y >= 0) && (y < tamanhoMapa)){//posição dentro do mapa
                    if(wumpus.get(i).getStatus()){
                        if(!mapaSensacoes[x][y].contains(wumpus.get(i).getSensacao())){
                            mapaSensacoes[x][y] += " " + wumpus.get(i).getSensacao();
                        }
                    }
                }
            }
        }


        //percorrer cada poço
        for(int contador2 = 0; contador2 < pocos.size(); contador2++){
            //calcular direções
            for(int contador = 0; contador < 4; contador++){
				x = pocos.get(contador2).getX();
				y = pocos.get(contador2).getY();

				switch(contador){
					case 0: x -= 1; break;//norte
					case 1: x += 1; break;//sul
					case 2: y -= 1; break;//oeste
					case 3: y += 1; break;//leste
				}

				//verificar limites do mapa
				if((x >= 0) && (x < tamanhoMapa) && (y >= 0) && (y < tamanhoMapa)){//posição dentro do mapa
					if(!mapaSensacoes[x][y].contains(pocos.get(0).getSensacao())){//indice nao importa
						mapaSensacoes[x][y] += " " + pocos.get(contador2).getSensacao();
					}
				}
            }
        }
    }


    //calcular o mapa sensação pra cada agente separado
    public static void calcularMapaSensacoesAgente(Agente agente){
        for (int i = 0; i < tamanhoMapa; i++){
            for (int j = 0; j < tamanhoMapa; j++) agente.mapaSensacoes[i][j] = " ";
        }

        if(!agente.ouroMapa.getColetado()) agente.mapaSensacoes[agente.ouroMapa.getX()][agente.ouroMapa.getY()] = " " + agente.ouroMapa.getSensacao();

        int x, y;
        for(int i = 0; i < agente.wumpusMapa.size(); i++){//percorrer cada monstro da lista
            for(int contador = 0; contador < 4; contador++){//percorrer cada direção adjascente
                x = agente.wumpusMapa.get(i).getX();
                y = agente.wumpusMapa.get(i).getY();
    
                //definir direções
                switch(contador){
                    case 0: x -= 1; break;//norte
                    case 1: x += 1; break;//sul
                    case 2: y -= 1; break;//oeste
                    case 3: y += 1; break;//leste
                }
    
                //verificar limites do mapa
                if((x >= 0) && (x < tamanhoMapa) && (y >= 0) && (y < tamanhoMapa)){//posição dentro do mapa

                    if(agente.wumpusMapa.get(i).getStatus()){
                        if(!agente.mapaSensacoes[x][y].contains(agente.wumpusMapa.get(i).getSensacao())){
                            agente.mapaSensacoes[x][y] += " " + agente.wumpusMapa.get(i).getSensacao();
                        }
                    }
                }
            }
        }


        //percorrer cada poço
        for(int contador2 = 0; contador2 < agente.pocosMapa.size(); contador2++){
            //calcular direções
            for(int contador = 0; contador < 4; contador++){
				x = agente.pocosMapa.get(contador2).getX();
				y = agente.pocosMapa.get(contador2).getY();

				switch(contador){
					case 0: x -= 1; break;//norte
					case 1: x += 1; break;//sul
					case 2: y -= 1; break;//oeste
					case 3: y += 1; break;//leste
				}

				//verificar limites do mapa
				if((x >= 0) && (x < tamanhoMapa) && (y >= 0) && (y < tamanhoMapa)){//posição dentro do mapa
					if(!agente.mapaSensacoes[x][y].contains(agente.pocosMapa.get(0).getSensacao())){//indice nao importa
						agente.mapaSensacoes[x][y] += " " + agente.pocosMapa.get(contador2).getSensacao();
					}
				}
            }
        }
    }


    public static void copiarSensacoesParaAgente(Agente agente){
        String sensacoes = agente.mapaSensacoes[agente.getX()][agente.getY()];
        agente.setSensacoes(sensacoes);
    }


    public static boolean verificarColisao(Agente agente){
        int i = 0;
        for(i = 0; i < agente.wumpusMapa.size(); i++){
            if((agente.getX() == agente.wumpusMapa.get(i).getX()) && (agente.getY() == agente.wumpusMapa.get(i).getY())){
                agente.fitness -= 1000;
                return true;       
            }
        }
        for(i = 0; i < agente.pocosMapa.size(); i++){
            if((agente.getX() == agente.pocosMapa.get(i).getX()) && (agente.getY() == agente.pocosMapa.get(i).getY())){
                agente.fitness -= 800;
                return true;                
            }
        }
        return false;
    }


    public static boolean verificarJogoGanho(Agente agente, int indiceAgente) throws InterruptedException{
        if(agente.getOuroColetado()){
            if((agente.getX() == xInicialAgente) && (agente.getY() == yInicialAgente)){
                agente.fitness += 4000;

                System.out.println("Agente[" + indiceAgente + "] ganhou a partida");
                Thread.sleep((long)(1000 * 0.01));

                String nomeArquivo = "./melhores-redes/rede-fit-" + agente.fitness + ".dat";
                //salvar apenas novas redes
                //evitar arquivos corrompidos
                if(!(new File(nomeArquivo).exists())){
                    agente.rede.salvarRedeArquivo(nomeArquivo);
                }

                redesQueGanharam++;
                return true;//desligar agente
            }
        }
        return false;
    }


    /**
     * Atualiza os dados do ambiente para o cálculo da rede neural do agente
     */
    public static double[] atualizarDados(Agente agente, double[] dados){
        //calcular cada casa adjascente
        //calcular se as casas estão disponíveis pra andar

        //valores que a rede vai receber
        int caminhoLivre = 10;
        int caminhoBloqueado = -10;

        int sentindoAlgo = 10;
        int sentindoNada = -10;

        int acaoFeita = 10;
        int acaoNaoFeita = -10;
        
        //casas adjascentes disponíveis

        if((agente.getX()-1) >= 0) dados[0] = caminhoLivre;//norte
        else dados[0] = caminhoBloqueado;

        if((agente.getX()+1) < mapa.length) dados[1] = caminhoLivre;//sul
        else dados[1] = caminhoBloqueado;

        if((agente.getY()-1) >= 0) dados[2] = caminhoLivre;//leste
        else dados[2] = caminhoBloqueado;

        if((agente.getY()+1) <  mapa.length) dados[3] = caminhoLivre;//oeste
        else dados[3] = caminhoBloqueado;

        //sensações na casa atual

        if(mapaSensacoes[agente.getX()][agente.getY()].contains("Brilho")) dados[4] = sentindoAlgo;
        else dados[4] = sentindoNada;
        
        if(mapaSensacoes[agente.getX()][agente.getY()].contains("Fedor")) dados[5] = sentindoAlgo;
        else dados[5] = sentindoNada;

        if(mapaSensacoes[agente.getX()][agente.getY()].contains("Brisa")) dados[6] = sentindoAlgo;
        else dados[6] = sentindoNada;

        //status do agente

        if(agente.getOuroColetado()) dados[7] = acaoFeita;
        else dados[7] = acaoNaoFeita;

        if(agente.getMatouWumpus()) dados[8] = acaoFeita;
        else dados[8] = acaoNaoFeita;

        if(agente.getFlechas() > 0) dados[9] = acaoFeita;
        else dados[9] = acaoNaoFeita;

        return dados;
    }


    /**
     * Copia dos elementos do mapa para o agente
     * @param agente
     * @param wumpus
     * @param pocos
     * @param ouro
     */
    public static void copiarElementosParaAgente(Agente agente, ArrayList<Wumpus> wumpus, ArrayList<Poco> pocos, Ouro ouro){
        //adicionar poços
        int i = 0;
        for(i = 0; i < wumpus.size(); i++){
            agente.wumpusMapa.add(new Wumpus(wumpus.get(i).getX(), wumpus.get(i).getY()));
        }

        //adicionar wumpus
        for(i = 0; i < pocos.size(); i++){
            agente.pocosMapa.add(new Poco(pocos.get(i).getX(), pocos.get(i).getY()));
        }

        //adicionar ouro
        agente.ouroMapa = new Ouro(ouro.getX(), ouro.getY());
    }
}