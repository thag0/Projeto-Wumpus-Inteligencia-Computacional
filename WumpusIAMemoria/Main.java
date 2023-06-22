import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class Main{
    //partes do mapa
    static int tamanhoMapa;
    static int quantidadePoco;
    static int quantidadeWumpus;
    static String mapa[][];
    static String mapaPosicoes[][];
    static String mapaSensacoes[][];

    //simulações
    static double tempoAtualizacao = 0.9f;
    static int rodadaAtual = 0;
    static int rodadas = 100;
    static int[] movimentosFeitos = new int[4];

    //elementos
    static Agente agente;
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

    public static void main(String[] args){
		limparConsole();
		// System.out.print("Tamanho do mapa: ");
		// try{
		// 	tamanhoMapa = Integer.parseInt(pegarEntrada());
		// }catch(Exception e){ tamanhoMapa = 4; }
        tamanhoMapa = 4;

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
        agente.preencherMemoria(agente.getX(), agente.getY(), mapaSensacoes);//primeira iteração
        try{
            while(rodadaAtual <= rodadas){
                imprimirPartida();

                calcularSensacoes();
                verificarSensacoes();
                moverAgente();
                verificarColisao();
                verificarJogoGanho();

                Thread.sleep((long) (1000 * tempoAtualizacao));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void moverAgente(){
        boolean automatico = false;

        if(automatico){
            agente.calcularMovimento(tamanhoMapa, movimentosFeitos, mapaSensacoes);
        
        }else{
            tempoAtualizacao = 0;
            try{
				String direcao = pegarEntrada();
                switch(direcao){
                    case "w": agente.mover("norte"); break;
                    case "s": agente.mover("sul"); break;
                    case "a": agente.mover("oeste"); break;
                    case "d": agente.mover("leste"); break;
                    case "n": novaPartida(); break;
                }
                agente.preencherMemoria(agente.getX(), agente.getY(), mapaSensacoes);
            }catch(Exception e){}
        }

    }


    //controle sobre reinicio de partida
    public static void novaPartida(){
        gerarEntidadesFixas();
        calcularMapaSensacoes();
        rodadaAtual++;
    }


    private static void imprimirPartida(){
        limparConsole();
        System.out.println("Rodada atual: " + rodadaAtual);

        mostrarMapa();
        System.out.println("x0: " + agente.ultimaPosicao[0] + " y0: " + agente.ultimaPosicao[1]);
        System.out.println("x: " + agente.getX() + " y: " + agente.getY());
        //mostrarMapaPosicoes();
        //mostrarMapaSensacoes();

        for(int i = 0; i < tamanhoMapa; i++){
            for(int j = 0; j < tamanhoMapa; j++){
                System.out.print("[" + agente.memoriaSensacoes[i][j] + "]");
            }
            System.out.println();
        }

        System.out.println("----------------------");
        for(int i = 0; i < tamanhoMapa; i++){
            for(int j = 0; j < tamanhoMapa; j++){
                System.out.print("[" + agente.memoriaElementos[i][j] + "]");
            }
            System.out.println();
        }

        System.out.println("Status do agente");
        agente.getInformacoes();
    }


    //geração
    //posições baseadas no exemplo
    public static void gerarEntidadesFixas(){
        if(agente == null) agente = new Agente(xInicialAgente, yInicialAgente, tamanhoMapa);
        else{
            int pontos = agente.getPontos();
            int jogosGanhos = agente.getJogosGanhos();
            int mortes = agente.getMortes();
            int quedasEmPocos = agente.getQuedasEmPocos();
            int flechasAcertadas = agente.getFlechasAcertadas();
            int mortesPeloWumpus = agente.getMortesPeloWumpus();

            agente = new Agente(xInicialAgente, yInicialAgente, tamanhoMapa);
            agente.setPontos(pontos);
            agente.setJogosGanhos(jogosGanhos);
            agente.setMortes(mortes);
            agente.setQuedasEmPocos(quedasEmPocos);
            agente.setFlechasAcertadas(flechasAcertadas);
            agente.setMortesPeloWumpus(mortesPeloWumpus);
        }  

        mapa2();
    }


    public static void mapa1(){
        wumpus.clear();
        wumpus.add(new Wumpus(1, 0));
        ouro = new Ouro(0, 3);
    
        pocos.clear();
        pocos.add(new Poco(0, 1));
        pocos.add(new Poco(2, 1));
        pocos.add(new Poco(1, 3));
    }


    public static void mapa2(){
        wumpus.clear();
        wumpus.add(new Wumpus(3, 3));

        ouro = new Ouro(0, 0);

        pocos.clear();
        pocos.add(new Poco(2, 2));  
    }


    public static void gerarEntidadesAleatorias(){
        //verificar instancia existente e tratar para nao perder valores de simulaçoes passadas
        if(agente == null) agente = new Agente(xInicialAgente, yInicialAgente, tamanhoMapa);
        else{
            int pontos = agente.getPontos();
            int jogosGanhos = agente.getJogosGanhos();
            int mortes = agente.getMortes();
            int quedasEmPocos = agente.getQuedasEmPocos();
            int flechasAcertadas = agente.getFlechasAcertadas();
            int mortesPeloWumpus = agente.getMortesPeloWumpus();
			int flechasAtiradas = agente.getFlechasAtiradas();

            agente = new Agente(xInicialAgente, yInicialAgente, tamanhoMapa);
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
        wumpus.clear();
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

                if(entidadeExistente(agente, linha, coluna)) conteudo += agente.getSimbolo();                
                else if(wumpusExistente(linha, coluna)){
                    for(int i = 0; i < wumpus.size(); i++){
                        if(wumpus.get(i).getStatus()){
                            conteudo += wumpus.get(i).getSimbolo();
                            break;
                        }
                    }
                }
                else if(entidadeExistente(ouro, linha, coluna) && !ouro.getColetado()) conteudo += ouro.getSimbolo();
                else if(pocoExistente(linha, coluna)) conteudo += pocos.get(0).getSimbolo();
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
        for(int i = 0; i < pocos.size(); i++) if(x == pocos.get(i).getX() && y == pocos.get(i).getY()) return true;
        return false;
    }


    public static boolean wumpusExistente(int x, int y){
        for(int i = 0; i < wumpus.size(); i++) if(x == wumpus.get(i).getX() && y == wumpus.get(i).getY()) return true;
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
            for(int contador = 0; contador < 4; contador++){
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
                if((x >= 0) && (x < tamanhoMapa) && (y >= 0) && (y < tamanhoMapa) && wumpus.get(i).getStatus()){//posição dentro do mapa
                    if(!mapaSensacoes[x][y].contains(wumpus.get(i).getSensacao())){
                        mapaSensacoes[x][y] += " " + wumpus.get(i).getSensacao();
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


    public static void calcularSensacoes(){
        String sensacoes = mapaSensacoes[agente.getX()][agente.getY()];
        agente.setSensacoes(sensacoes);
        System.out.println("Sentindo: {" + agente.getSensacoes() + "  }");
    }


    //reações
    public static void verificarSensacoes(){
		int retornoAtirar = -1;//valor fora dos retorno de atirar()
        //atirar no wumpus
        int indiceWumpus = 0;
        for(int i = 0; i < wumpus.size(); i++){
            if(agente.getSensacoes().contains(wumpus.get(i).getSensacao()) && (agente.getFlechas() > 0)){       
                System.out.println(); 
                retornoAtirar = agente.atirar(wumpus.get(i), tamanhoMapa, mapaPosicoes);
                indiceWumpus = i;
                break;
            }
        }


		switch(retornoAtirar){
			case 0: System.out.println("..."); break; //errou o tiro
			case 1: System.out.println("Ouve um eco"); break; // atirou num poço
			
			case 2://atirou no wumpus
				wumpus.get(indiceWumpus).setStatus(false);
            	agente.setFlechasAcertadas(agente.getFlechasAcertadas()+1);
				calcularMapaSensacoes();
				
				imprimirPartida();
				wumpus.get(indiceWumpus).gritar();
			break;
		}

        //pegar ouro
        if(entidadeExistente(ouro, agente.getX(), agente.getY()) && (!ouro.getColetado())){
            agente.PegarOuro(ouro, tamanhoMapa);
			calcularMapaSensacoes();

			imprimirPartida();
			System.out.println("Ouro pego");
        }
    }


    public static void verificarColisao(){
        //colisao com wumpus
        for(int i = 0; i < wumpus.size(); i++){
            if(wumpusExistente(agente.getX(), agente.getY()) && (wumpus.get(i).getStatus())){
                agente.setMortes(agente.getMortes()+1);
                agente.setMortesPeloWumpus(agente.getMortesPeloWumpus()+1);
                novaPartida();
                return;
            } 
        }    
        
        //colisão com os poços
        if(pocoExistente(agente.getX(), agente.getY())){
            agente.setQuedasEmPocos(agente.getQuedasEmPocos()+1);
            agente.setMortes(agente.getMortes()+1);
            novaPartida();
            return;
        }
    }


    public static void verificarJogoGanho(){
        if((agente.getX() == xInicialAgente) && (agente.getY() == yInicialAgente) && (agente.getOuroColetado())){
            agente.setJogosGanhos(agente.getJogosGanhos()+1);
            novaPartida();
        }
    }
}