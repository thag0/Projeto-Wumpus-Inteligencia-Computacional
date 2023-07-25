import java.util.HashMap;
import java.util.Random;

import memoria.MemoriaAgente;

public class Agente extends Entidade{

    private int proximoMovimento;
    private Random random;

    private int pontos;
    private int jogosGanhos;

    private int mortes;
    private int quedasEmPocos;
    private int mortesPeloWumpus;

    private int flechas;
    private int flechasAtiradas;
    private int flechasAcertadas;

    private boolean ouroColetado = false;
    private String direcoes[]= {"norte", "sul", "oeste", "leste"};
    private String sensacoes = "";//sensações da casa atual

    //memoria
    MemoriaAgente memoria;
    int[] casaNorte = new int[2];
    int[] casaSul = new int[2];
    int[] casaOeste = new int[2];
    int[] casaLeste = new int[2];
    HashMap<Character, Integer> contadorCaracteres = new HashMap<>();
    int[] possiveisElementos = new int[2];

    public Agente(int posX, int posY, int tamanhoMapa){
        this.simbolo = "A";
        this.posX = posX;
        this.posY = posY;
        random = new Random();

        this.pontos = 0;
        this.jogosGanhos = 0;
        this.mortes = 0;
        this.flechas = 1;
        this.flechasAcertadas = 0;
        this.flechasAtiradas = 0;
        this.quedasEmPocos = 0;
        this.mortesPeloWumpus = 0;

        this.memoria = new MemoriaAgente(tamanhoMapa);
        this.memoria.mapa[tamanhoMapa-1][0].vezesAndadas = 1;

        this.memoria.ultimoX = tamanhoMapa-1;
        this.memoria.ultimoY = 0;
    }


    public void getInformacoes(){
        System.out.println("Flechas restantes: " + this.flechas);
        System.out.println("Flechas acertadas: " + this.flechasAcertadas);
        System.out.println("Flechas atiradas: " + this.flechasAtiradas);

        System.out.println("\nMortes: " + this.mortes);
        System.out.println("Mortes pelo Wumpus: " + this.mortesPeloWumpus);
        System.out.println("Quedas em poços: " + this.quedasEmPocos);

        System.out.println("\nOuro coletado: " + this.pontos);
        System.out.println("Jogos ganhos: " + this.jogosGanhos);
    }


    public void imprimirMapa(){
        System.out.println("-- Mapa de sensações --");
        for(int i = 0; i < this.memoria.mapa.length; i++){
            for(int j = 0; j < this.memoria.mapa.length; j++){
                System.out.print("[" + this.memoria.mapa[i][j].sensacao + "]");
            }
            System.out.println();
        }
        System.out.println("-- Mapa de elementos --");
        for(int i = 0; i < this.memoria.mapa.length; i++){
            for(int j = 0; j < this.memoria.mapa.length; j++){
                System.out.print("[" + this.memoria.mapa[i][j].elemento + "]");
            }
            System.out.println();
        }
        System.out.println("-- Mapa de casas andadas --");
        for(int i = 0; i < this.memoria.mapa.length; i++){
            for(int j = 0; j < this.memoria.mapa.length; j++){
                System.out.print("[" + this.memoria.mapa[i][j].vezesAndadas + "]");
            }
            System.out.println();
        }
        System.out.println();
    }


    //funções lógicas    
    public void calcularMovimento(int tamanhoMapa, int[] movimentosFeitos, String[][] mapaSensacoes, String[][] mapaPosicoes){
        preencherMemoria(this.getX(), this.getY(), mapaSensacoes, mapaPosicoes);//guardar casa segura
        consultarMemoria();
        int[] novoMovimento = new int[3];
        boolean acaoValidada = false;
        boolean podeMover;

        while(true){
            podeMover = false;
            novoMovimento[0] = this.posX;
            novoMovimento[1] = this.posY;
            novoMovimento[2] = random.nextInt(4);
            
            switch(novoMovimento[2]){
                case 0: novoMovimento[0] -= 1; break;//norte
                case 1: novoMovimento[0] += 1; break;//sul
                case 2: novoMovimento[1] -= 1; break;//oeste
                case 3: novoMovimento[1] += 1; break;//leste
            }

            acaoValidada = validarAcao(novoMovimento[0], novoMovimento[1], tamanhoMapa, direcoes[novoMovimento[2]]);

            if(acaoValidada){
                if(mapaPosicoes[this.getX()][this.getY()].contains("centro")){
                   // podeMover = casaSegura(novoMovimento);
                    
                    if(this.memoria.mapa[this.getX()][this.getY()].vezesAndadas > 5){
                        
                        if(contadorElementos("S") > 2){
                            if(this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.contains("S") ){
                                podeMover = true;
                                break;
                            }else if(casaWumpus(novoMovimento)) podeMover = false;
                            else if(casaPoco(novoMovimento)) podeMover = false;
                          

                        }else if(contadorElementos("S") == 2){
                      
                            int cPoco = contadorElementos("P");
                            int cWumpus = contadorElementos("W");
                            
                            if(cPoco > 1 || (cPoco > 0 && cWumpus > 0)){
                                podeMover = casaSegura(novoMovimento);
                
                            }else if(this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.isBlank() ||
                                this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.isEmpty()){
                                podeMover = true;
                                break;
                            }else if(casaWumpus(novoMovimento)) podeMover = false;
                            else if(casaPoco(novoMovimento)) podeMover = false;
                            else if(!this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.contains("S")){
                                    podeMover = true;
                                    break;
                            }   
                        }
                 
                        else if(contadorElementos("S") == 1){
                            if((this.memoria.ultimoX != novoMovimento[0] && this.memoria.ultimoY != novoMovimento[1])){
                                podeMover = false;

                            }else if(casaWumpus(novoMovimento)) podeMover = false;
                            else if(casaPoco(novoMovimento)) podeMover = false;
                            else{
                                podeMover = true;
                                break;
                            }
                                
                        }
                        
                        // while(true){
                        //     if(!this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.contains("S")){
                        //         podeMover = true;
                        //         break;
                            
                        //     }else{
                        //         if(casaWumpus(novoMovimento)) podeMover = false;
                        //         else if(casaPoco(novoMovimento)) podeMover = false;
                        //         else{
                        //             podeMover = true;
                        //         }    
                                
                        //         break;
                        //     }
                        //}
                    
                    }else podeMover = casaSegura(novoMovimento);  

                }else if(mapaPosicoes[this.getX()][this.getY()].contains("parede") && this.memoria.mapa[this.getX()][this.getY()].podeMover){
                    int cPoco = contadorElementos("P");

                    int cWumpus = contadorElementos("W");
                    
                    if(cPoco > 1 || (cPoco > 0 && cWumpus > 0)){
                        this.memoria.mapa[this.getX()][this.getY()].podeMover = false;
                    }
                    
                    if(this.memoria.mapa[this.getX()][this.getY()].vezesAndadas > 5){
                        while(true){
                            if(this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.isBlank() ||
                                this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.isEmpty()){
                                podeMover = true;
                                break;
                            
                            }else{
                                if(casaWumpus(novoMovimento)) podeMover = false;
                                else if(casaPoco(novoMovimento)) podeMover = false;
                                else{
                                    podeMover = true;
                                }    
                                
                                break;
                            }
                        }
                    
                    }else podeMover = casaSegura(novoMovimento);

                }else {
                    podeMover = casaSegura(novoMovimento);
                    if(!podeMover){
                        while(true){
                            novoMovimento = sortearProximoMovimento(this.getX(), this.getY());
                            if(validarAcao(novoMovimento[0], novoMovimento[1], tamanhoMapa, direcoes[novoMovimento[2]])) break;
                        }
                        podeMover = true;
                     
                    }
                }
                if(podeMover) break;
            }
        }

        System.out.println("\nmovendo " + direcoes[novoMovimento[2]]);
        mover(direcoes[novoMovimento[2]]);
        memoria.mapa[this.getX()][this.getY()].vezesAndadas += 1;
        movimentosFeitos[novoMovimento[2]]++;
    }


    public boolean casaSegura(int[] novoMovimento){
        if(this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.contains("S")) return true;
        return false;
    }


    public boolean casaPoco(int[] novoMovimento){
        if(this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.contains("P")) return true;
        return false;
    }


    public boolean casaWumpus(int[] novoMovimento){
        if(this.memoria.mapa[novoMovimento[0]][novoMovimento[1]].elemento.contains("W")) return true;
        return false;
    }


    public int contadorElementos(String elemento){
        int contador = 0;
        
        if(casaNorte[0] != -1){
            if(this.memoria.mapa[casaNorte[0]][casaNorte[1]].elemento.contains(elemento)) contador++;
        }
        if(casaSul[0] != -1){
            if(this.memoria.mapa[casaSul[0]][casaSul[1]].elemento.contains(elemento)) contador++;
        }
        if(casaOeste[0] != -1){
            if(this.memoria.mapa[casaOeste[0]][casaOeste[1]].elemento.contains(elemento)) contador++;
        }
        if(casaLeste[0] != -1){
            if(this.memoria.mapa[casaLeste[0]][casaLeste[1]].elemento.contains(elemento)) contador++;
        }

        return contador;
    }


    public int[] sortearProximoMovimento(int x, int y){
        int[] proximoMovimento = new int[3];
        int novoX = x, novoY = y;
        
        int movimento = random.nextInt(4);
        switch(movimento){
            case 0: novoX -= 1; break;//norte
            case 1: novoX += 1; break;//sul
            case 2: novoY -= 1; break;//oeste
            case 3: novoY += 1; break;//leste    
        }

        proximoMovimento[0] = novoX;
        proximoMovimento[1] = novoY;
        proximoMovimento[2] = movimento;//direção

        return proximoMovimento;
    }


    public int[] preencherPossiveisElementos(int[] casaAdjascente, int[] possiveisElementos){
        if(casaAdjascente[0] != -1){
            if (this.memoria.mapa[casaAdjascente[0]][casaAdjascente[1]].nFedor > 1){
                possiveisElementos[0] ++;
            }
            if (this.memoria.mapa[casaAdjascente[0]][casaAdjascente[1]].nBrisa > 1){
                possiveisElementos[1] ++;
            }
        }

        return possiveisElementos;
    }

    
    public void preencherCasasAdjascentes(){
        int x = this.getX();
        int y = this.getY();

        //casa norte
        if(x-1 >= 0){
          casaNorte[0] = x-1;
          casaNorte[1] = y;  
        
        }else{
            casaNorte[0] = -1;
            casaNorte[1] = -1;
        }
        
        //casa sul
        if(x+1 < this.memoria.tamanhoMapa){
            casaSul[0] = x+1;
            casaSul[1] = y;  
        
        }else{
            casaSul[0] = -1;
            casaSul[1] = -1;
        }

        //casa oeste
        if(y-1 >= 0){
          casaOeste[0] = x;
          casaOeste[1] = y-1;  
        
        }else{
            casaOeste[0] = -1;
            casaOeste[1] = -1;
        }

        //casa leste
        if(y+1 < this.memoria.tamanhoMapa){
          casaLeste[0] = x;
          casaLeste[1] = y+1;  
        
        }else{
            casaLeste[0] = -1;
            casaLeste[1] = -1;
        }
    }


    public void mover(String direcao){
        this.memoria.ultimoX = posX;
        this.memoria.ultimoY = posY;

        switch(direcao){
            case "norte":
                posX -= 1;
            break;
            case "sul":
                posX += 1;
            break;
            case "oeste":
                posY -= 1;
            break;
            case "leste":
                posY += 1;
            break;
        }
    }


    public void PegarOuro(Ouro ouro, int tamanhoMapa){
        ouro.setColetado(true);
        this.pontos++;
        ouroColetado = true;
    }


    public void preencherMemoria(int x, int y, String[][] mapaSensacoes, String[][] mapaPosicoes){
        preencherMapaSensacao(this.memoria.ultimoX, this.memoria.ultimoY, "S");
        //preencherMapaSensacao(x, y, "S");

        preencherMapaElementos(this.memoria.ultimoX, this.memoria.ultimoY, "S");
      
        //caso o agente não sinta nada, todas as casas em volta são seguras
        //preencher casas em volta
        if(mapaSensacoes[x][y].isBlank()){
            if(x-1 >= 0){//norte
                preencherMapaSensacao(x-1, y, "S");
                preencherMapaElementos(x-1, y, "S");
            }
            if(x+1 < mapaSensacoes.length){//sul
                preencherMapaSensacao(x+1, y, "S");
                preencherMapaElementos(x+1, y, "S");
            } 
            if(y-1 >= 0){//oeste
                preencherMapaSensacao(x, y-1, "S");
                preencherMapaElementos(x, y-1, "S");
            } 
            if(y+1 < mapaSensacoes.length){//leste
                preencherMapaSensacao(x, y+1, "S");
                preencherMapaElementos(x, y+1, "S");
            }            
        }

        //verificar cantos
        if(this.getX()==0 && this.getY() == 0){//canto superior esquerdo
            if(this.sensacoes.contains("Brisa")){
                if(acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("B")){//direita
                    preencherMapaElementos(this.getX(), this.getY()+1, "P");
                }
                if(acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("B")){//baixo
                    preencherMapaElementos(this.getX()+1, this.getY(), "P");
                }
            }
        }
        if(this.getX()==0 && this.getY() == this.memoria.tamanhoMapa-1){//canto superior direito
            if(this.sensacoes.contains("Brisa")){
                if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("B")){//esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "P");
                }
                if(acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("B")){//baixo
                    preencherMapaElementos(this.getX()+1, this.getY(), "P");
                }
            }
        }
        if(this.getX()==this.memoria.tamanhoMapa-1 && this.getY() == this.memoria.tamanhoMapa-1){//canto inferior direito
            if(this.sensacoes.contains("Brisa")){
                if(acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("B")){//cima
                    preencherMapaElementos(this.getX()-1, this.getY(), "P");
                }
                if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("B")){//esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "P");
                }
            }
        }

        //verificar paredes
        preencherCasasAdjascentes();
        int cSeguro = contadorElementos("S");
        if(cSeguro > 1 && this.sensacoes.contains("Brisa")){
            int[][] posicoesAdjascentes = {casaNorte, casaSul, casaOeste, casaLeste};
            for(int i = 0; i < 4; i++){
                if(mapaPosicoes[this.getX()][this.getY()].contains("parede")){
                    // if(validarAcao(casaNorte[0], casaNorte[1], this.memoria.tamanhoMapa, "norte")){
                    //     if(!this.memoria.mapa[casaNorte[0]][casaNorte[1]].elemento.contains("S")){
                    //         preencherMapaElementos(casaNorte[0], casaNorte[1], "P");
                    //     }
                    // }
                    if(validarAcao(posicoesAdjascentes[i][0], posicoesAdjascentes[i][1], this.memoria.tamanhoMapa, "norte")){
                        if(!this.memoria.mapa[posicoesAdjascentes[i][0]][posicoesAdjascentes[i][1]].elemento.contains("S")){
                            preencherMapaElementos(posicoesAdjascentes[i][0], posicoesAdjascentes[i][1], "P");
                        }
                    }
                }
            }
        }


        if(mapaSensacoes[x][y].contains("Brilho")){
            preencherMapaSensacao(x, y, "O");
            preencherMapaElementos(x, y, "O");
        }

        //preencher sensações
        String[] sensacoes = {"F", "B"};
        for(int i = 0; i < sensacoes.length; i++){
            if(mapaSensacoes[x][y].contains(sensacoes[i])){
                if(x-1 >= 0){//norte
                    if(!acessarMemoriaSensacoes(x-1, y).contains("S") && !acessarMemoriaSensacoes(x-1, y).contains(sensacoes[i])){//verificar se a casa não é segura
                        if((!acessarMemoriaElementos(x-1, y).contains("P")) || (!acessarMemoriaElementos(x-1, y).contains("W"))){
                            //adicionar sensação na casa
                            String conteudo = acessarMemoriaSensacoes(x-1, y);
                            conteudo += sensacoes[i];
                            preencherMapaSensacao(x-1, y, conteudo);
                        }
                    }
                }

                if(x+1 < mapaSensacoes.length){
                    if(!acessarMemoriaSensacoes(x+1, y).contains("S") && !acessarMemoriaSensacoes(x+1, y).contains(sensacoes[i])){
                        if((!acessarMemoriaElementos(x+1, y).contains("P")) || (!acessarMemoriaElementos(x+1, y).contains("W"))){
                            String conteudo = acessarMemoriaSensacoes(x+1, y);
                            conteudo += sensacoes[i];
                            preencherMapaSensacao(x+1, y, conteudo);
                        }
                    }
                }

                if(y-1 >= 0){
                    if(!acessarMemoriaSensacoes(x, y-1).contains("S") && !acessarMemoriaSensacoes(x, y-1).contains(sensacoes[i])){
                        if((!acessarMemoriaElementos(x, y-1).contains("P")) || (!acessarMemoriaElementos(x, y-1).contains("W"))){
                            String conteudo = acessarMemoriaSensacoes(x, y-1);
                            conteudo += sensacoes[i];
                            preencherMapaSensacao(x, y-1, conteudo);
                        }
                    }
                }

                if(y+1 < mapaSensacoes.length){
                    if(!acessarMemoriaSensacoes(x, y+1).contains("S") && !acessarMemoriaSensacoes(x, y+1).contains(sensacoes[i])){
                        if((!acessarMemoriaElementos(x, y+1).contains("P")) || (!acessarMemoriaElementos(x, y+1).contains("W"))){
                            String conteudo = acessarMemoriaSensacoes(x, y+1);
                            conteudo += sensacoes[i];
                            preencherMapaSensacao(x, y+1, conteudo);
                        }
                    }
                }
            }
        }
        varrerMapa();
    }


    public void varrerMapa(){
         for(int i = 0; i < this.memoria.tamanhoMapa; i++){
            for(int j = 0; j < this.memoria.tamanhoMapa; j++){
                // if(acessarMemoriaElementos(i, j).contains("P")) preencherMapaSensacao(i, j, "P");
                // else if(acessarMemoriaElementos(i, j).contains("W")) preencherMapaSensacao(i, j, "W");
            }
        }       
    }


    public void consultarMemoria(){
        
    }


    //acessar dados da memória
    public void preencherMapaSensacao(int x, int y, String sensacao){
        this.memoria.mapa[x][y].sensacao = sensacao;
    }


    public void preencherMapaElementos(int x, int y, String elemento){
        this.memoria.mapa[x][y].elemento = elemento;
    }


    public String acessarMemoriaSensacoes(int x, int y){
        return this.memoria.mapa[x][y].sensacao;
    }

    
    public String acessarMemoriaElementos(int x, int y){
        return this.memoria.mapa[x][y].elemento;
    }
    

    /**
     * 
     * @param monstro
     * @param tamanhoMapa
     * @return 0 errou o tiro, 1 atirou no poço, 2 matou o wumpus
     */
    public int atirar(Wumpus wumpus, int tamanhoMapa, String[][] mapaPosicoes){
        int x, y, valorRetorno = 0;
        boolean acaoValidada = false;

        while(true){
            x = this.posX;
            y = this.posY;
            proximoMovimento = random.nextInt(4);
            
            switch(proximoMovimento){
                case 0: x -= 1; break;//norte
                case 1: x += 1; break;//sul
                case 2: y -= 1; break;//oeste
                case 3: y += 1; break;//leste
            }

            acaoValidada = validarAcao(x, y, tamanhoMapa, direcoes[proximoMovimento]);            
            if(acaoValidada && !acessarMemoriaSensacoes(x, y).contains("S")) break;
        }

        System.out.println("atirando " + direcoes[proximoMovimento]);
        this.flechas --;
        this.flechasAtiradas++;

        if(this.getX() == this.memoria.tamanhoMapa-1 && this.getY() == 0){//verificar casa do agente
            if(acertouWumpus(wumpus, x, y)){
                preencherMapaSensacao(x, y, "S");
                preencherMapaElementos(x, y, "S");

                if(this.sensacoes.contains("Brisa")){//preencher poço na casa restante
                    if(!this.memoria.mapa[this.getX()-1][this.getY()].sensacao.contains("S")){//verificar norte
                        preencherMapaSensacao(this.getX()-1, this.getY(), "P");
                        preencherMapaElementos(this.getX()-1, this.getY(), "P");
                    
                    }else if(!this.memoria.mapa[this.getX()][this.getY()+1].sensacao.contains("S")){//verificar leste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    }
                }

                valorRetorno = 2;
                return valorRetorno;

            }else{//sem escolha
                this.posX = x;
                this.posY = y;

                valorRetorno = 0;
                return valorRetorno;
            }
        }
        //verificar paredes
        else if((this.getY() == 0) && (this.getX() > 0) && (this.getX() < tamanhoMapa-1)){//parede esquerda
            if(acertouWumpus(wumpus, x, y)){
                preencherMapaSensacao(x, y, "S");
                preencherMapaElementos(x, y, "S");
                
                if(this.sensacoes.contains("Brisa")){//preencher poço na casa restante
                    if(!this.memoria.mapa[this.getX()-1][this.getY()].sensacao.contains("S")){//verificar norte
                        preencherMapaSensacao(this.getX()-1, this.getY(), "P");
                        preencherMapaElementos(this.getX()-1, this.getY(), "P");
                    
                    }else if(!this.memoria.mapa[this.getX()+1][this.getY()].sensacao.contains("S")){//verificar sul
                        preencherMapaSensacao(this.getX()+1, this.getY(), "P");
                        preencherMapaElementos(this.getX()+1, this.getY(), "P");
                    
                    }else if(!this.memoria.mapa[this.getX()][this.getY()+1].sensacao.contains("S")){//verificar leste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    }
                }

                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!acessarMemoriaElementos(x, y).contains("P")){
                    preencherMapaElementos(x, y, "P");
                    this.memoria.mapa[x][y].p = true;                    
                }

                //preencher o wumpus na posição restante
                if(acessarMemoriaElementos(this.getX()-1, this.getY()).isBlank()){//cima
                    preencherMapaElementos(this.getX()-1, this.getY(), "W");
                    this.memoria.mapa[this.getX()-1][this.getY()].w = true;
                } 
                if(acessarMemoriaElementos(this.getX()+1, this.getY()).isBlank()){//baixo
                    preencherMapaElementos(this.getX()+1, this.getY(), "W");
                    this.memoria.mapa[this.getX()+1][this.getY()].w = true;
                }       
                if(acessarMemoriaElementos(this.getX(), this.getY()+1).isBlank()){//direita
                    preencherMapaElementos(this.getX(), this.getY()+1, "W");
                    this.memoria.mapa[this.getX()][this.getY()+1].w = true;
                }

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                preencherMapaSensacao(x, y, "S");
                if(acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S") && acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S")){
                    preencherMapaElementos(this.getX(), this.getY()+1, "W");
                    this.memoria.mapa[this.getX()][this.getY()+1].w = true;
                    
                }else if(acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S") && acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S")){
                    preencherMapaElementos(this.getX()-1, this.getY(), "W");
                    this.memoria.mapa[this.getX()-1][this.getY()].w = true;
                    
                }else if(acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S") && acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S")){
                    preencherMapaElementos(this.getX()+1, this.getY(), "W");
                    this.memoria.mapa[this.getX()+1][this.getY()].w = true;
                }

                return valorRetorno;
            }
        
        }else if(this.getX() == 0 && (this.getY()>0) && (this.getY()<tamanhoMapa-1)){//parede cima
            if(acertouWumpus(wumpus, x, y)){
                preencherMapaSensacao(x, y, "S");
                preencherMapaElementos(x, y, "S");

                if(this.sensacoes.contains("Brisa")){//preencher poço na casa restante
                    if(this.memoria.mapa[this.getX()][this.getY()-1].sensacao.contains("B")){//verificar oeste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    
                    }else if(this.memoria.mapa[this.getX()+1][this.getY()].sensacao.contains("B")){//verificar sul
                        preencherMapaSensacao(this.getX()+1, this.getY(), "P");
                        preencherMapaElementos(this.getX()+1, this.getY(), "P");
                    
                    }else if(this.memoria.mapa[this.getX()][this.getY()+1].sensacao.contains("B")){//verificar leste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    }
                }

                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!acessarMemoriaElementos(x, y).contains("P")){
                    preencherMapaElementos(x, y, "P");
                    this.memoria.mapa[x][y].p = true;                    
                }

                //preencher o wumpus na posição restante
                if(acessarMemoriaElementos(this.getX()+1, this.getY()).isBlank()){//baixo
                    preencherMapaElementos(this.getX()+1, this.getY(), "W");
                    this.memoria.mapa[this.getX()+1][this.getY()].w = true;
                }
                if(acessarMemoriaElementos(this.getX(), this.getY()-1).isBlank()){//esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "W");
                    this.memoria.mapa[this.getX()][this.getY()-1].w = true;
                } 
                if(acessarMemoriaElementos(this.getX(), this.getY()+1).isBlank()){//direita
                    preencherMapaElementos(this.getX(), this.getY()+1, "W");
                    this.memoria.mapa[this.getX()][this.getY()+1].w = true;
                }

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                preencherMapaSensacao(x, y, "S");

                if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S") && acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S")){
                    preencherMapaElementos(this.getX()+1, this.getY(), "W");   
                    this.memoria.mapa[this.getX()+1][this.getY()].w = true;                
                    
                }else if(acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S") && acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S")){
                    preencherMapaElementos(this.getX(), this.getY()-1, "W");
                    this.memoria.mapa[this.getX()][this.getY()-1].w = true;
                    
                }else if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S") && acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S")){
                    preencherMapaElementos(this.getX(), this.getY()+1, "W");
                    this.memoria.mapa[this.getX()][this.getY()+1].w = true;
                }
                return valorRetorno;
            }
            

        }else if((this.getX() == tamanhoMapa-1) && (this.getY() > 0) && (this.getY() < tamanhoMapa-1)){//parede baixo
            if(acertouWumpus(wumpus, x, y)){
                preencherMapaSensacao(x, y, "S");
                preencherMapaElementos(x, y, "S");

                if(this.sensacoes.contains("Brisa")){//preencher poço na casa restante
                    if(this.memoria.mapa[this.getX()][this.getY()-1].sensacao.contains("B")){//verificar oeste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    
                    }else if(this.memoria.mapa[this.getX()-1][this.getY()].sensacao.contains("B")){//verificar norte
                        preencherMapaSensacao(this.getX()-1, this.getY(), "P");
                        preencherMapaElementos(this.getX()-1, this.getY(), "P");
                    
                    }else if(this.memoria.mapa[this.getX()][this.getY()+1].sensacao.contains("B")){//verificar leste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    }
                }

                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!acessarMemoriaElementos(x, y).contains("P")){
                    preencherMapaElementos(x, y, "P");
                    this.memoria.mapa[x][y].p = true;
                }

                //preencher o wumpus na posição restante
                if(acessarMemoriaElementos(this.getX()-1, this.getY()).isBlank()){//cima
                    preencherMapaElementos(this.getX()-1, this.getY(), "W");
                    this.memoria.mapa[this.getX()-1][this.getY()].w = true;
                } 
                if(acessarMemoriaElementos(this.getX(), this.getY()-1).isBlank()){//esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "W");
                    this.memoria.mapa[this.getX()][this.getY()-1].w = true;
                } 
                if(acessarMemoriaElementos(this.getX(), this.getY()+1).isBlank()){//direita
                    preencherMapaElementos(this.getX(), this.getY()+1, "W");
                    this.memoria.mapa[this.getX()][this.getY()+1].w = true;
                } 

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                preencherMapaSensacao(x, y, "S");//errou o tiro e não matou nem ouviu eco

                if(acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S") && acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S")){
                    //seguro cima e seguro direita
                    //monstro esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "W");  
                    this.memoria.mapa[this.getX()][this.getY()-1].w = true;                 
                    
                }else if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S") && acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S")){
                    //seguro esquerda e seguro direita
                    //montro cima
                    preencherMapaElementos(this.getX()-1, this.getY(), "W");
                    this.memoria.mapa[this.getX()-1][this.getY()].w = true;
                    
                }else if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S") && acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S")){
                    //seguro esquerda e seguro cima
                    //monstro direita
                    preencherMapaElementos(this.getX(), this.getY()+1, "W");
                    this.memoria.mapa[this.getX()][this.getY()+1].w = true;
                }
                return valorRetorno;
            }
            

        }else if((this.getY() == tamanhoMapa-1) && (this.getX() > 0) && (this.getX() < tamanhoMapa-1)){//parede direita
            if(acertouWumpus(wumpus, x, y)){
                preencherMapaSensacao(x, y, "S");
                preencherMapaElementos(x, y, "S");

                if(this.sensacoes.contains("Brisa")){//preencher poço na casa restante
                    if(this.memoria.mapa[this.getX()][this.getY()-1].sensacao.contains("B")){//verificar oeste
                        preencherMapaSensacao(this.getX(), this.getY()+1, "P");
                        preencherMapaElementos(this.getX(), this.getY()+1, "P");
                    
                    }else if(this.memoria.mapa[this.getX()-1][this.getY()].sensacao.contains("B")){//verificar norte
                        preencherMapaSensacao(this.getX()-1, this.getY(), "P");
                        preencherMapaElementos(this.getX()-1, this.getY(), "P");
                    
                    }else if(this.memoria.mapa[this.getX()+1][this.getY()].sensacao.contains("B")){//verificar sul
                        preencherMapaSensacao(this.getX()+1, this.getY(), "P");
                        preencherMapaElementos(this.getX()+1, this.getY(), "P");
                    }
                }

                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!acessarMemoriaElementos(x, y).contains("P")){
                    preencherMapaElementos(x, y, "P");
                    this.memoria.mapa[x][y].p = true;                    
                }

                //preencher o wumpus na posição restante
                if(acessarMemoriaElementos(this.getX()-1, this.getY()).isBlank()){//cima
                    preencherMapaElementos(this.getX()-1, this.getY(), "W");
                    this.memoria.mapa[this.getX()-1][this.getY()].w = true;
                } 
                if(acessarMemoriaElementos(this.getX()+1, this.getY()).isBlank()){//baixo
                    preencherMapaElementos(this.getX()+1, this.getY(), "W");
                    this.memoria.mapa[this.getX()+1][this.getY()].w = true;
                } 
                if(acessarMemoriaElementos(this.getX(), this.getY()-1).isBlank()){//esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "W");
                    this.memoria.mapa[this.getX()][this.getY()-1].w = true;
                } 

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                preencherMapaSensacao(x, y, "S");//errou o tiro e não matou nem ouviu eco

                if(acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S") && acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S")){
                    //seguro cima e seguro baixo
                    //monstro esquerda
                    preencherMapaElementos(this.getX(), this.getY()-1, "W");
                    this.memoria.mapa[this.getX()][this.getY()-1].w = true;                 
                    
                }else if(acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S") && acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S")){
                    //seguro esquerda e seguro baixo
                    //montro cima
                    preencherMapaElementos(this.getX()-1, this.getY(), "W");
                    this.memoria.mapa[this.getX()-1][this.getY()].w = true;
                    
                }else if(acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S") && acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S")){
                    //seguro cima e seguro esquerda
                    //monstro baixo
                    preencherMapaElementos(this.getX()+1, this.getY(), "W");
                    this.memoria.mapa[this.getX()+1][this.getY()].w = true;
                }

                return valorRetorno;
            }
        }

        
        if((this.getX() > 0) && (this.getX() < tamanhoMapa-1) && (this.getY() > 0) && (this.getY() < tamanhoMapa-1)){//centro
            if(acertouWumpus(wumpus, x, y)){
                preencherMapaSensacao(x, y, "S");
                preencherMapaElementos(x, y, "S");
                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!acessarMemoriaElementos(x, y).contains("P")){
                    preencherMapaElementos(x, y, "P");
                    this.memoria.mapa[x][y].p = true;                    
                }

                //verificar as quatro casas adjascentes
                varrerMapa();
                preencherMapaSensacao(x, y, "P");

                //preencher o wumpus nas outras casas que podem conter ele
                if(!acessarMemoriaSensacoes(this.getX()-1, this.getY()).contains("S")){//norte
                    String conteudo = acessarMemoriaSensacoes(this.getX()-1, this.getY());
                    conteudo += "F";
                    preencherMapaSensacao(this.getX()-1, this.getY(), conteudo);
                }
                if(!acessarMemoriaSensacoes(this.getX()+1, this.getY()).contains("S")){//sul
                    String conteudo = acessarMemoriaSensacoes(this.getX()+1, this.getY());
                    conteudo += "F";
                    preencherMapaSensacao(this.getX()+1, this.getY(), conteudo);
                }
                if(!acessarMemoriaSensacoes(this.getX(), this.getY()-1).contains("S")){//sul
                    String conteudo = acessarMemoriaSensacoes(this.getX(), this.getY()-1);
                    conteudo += "F";
                    preencherMapaSensacao(this.getX(), this.getY()-1, conteudo);
                }
                if(!acessarMemoriaSensacoes(this.getX(), this.getY()+1).contains("S")){//sul
                    String conteudo = acessarMemoriaSensacoes(this.getX(), this.getY()+1);
                    conteudo += "F";
                    preencherMapaSensacao(this.getX(), this.getY()+1, conteudo);
                }
                valorRetorno = 1;
                return valorRetorno;
            
            }else{


            }
        }

        // if(acertouWumpus(wumpus, x, y)){//acertou wumpus
        //     memoriaSensacoes[x][y] = "S";
        //     valorRetorno = 2;
        // }

        return valorRetorno;
    }


    private boolean acertouWumpus(Wumpus wumpus, int x, int y){
        if((wumpus.getX() == x) && (wumpus.getY() == y)){
            wumpus.setStatus(false); 
            return true;
        }
        return false;
    }


    private boolean validarAcao(int x, int y, int tamanhoMapa, String direcao){
        if((direcao.equalsIgnoreCase(direcoes[0])) && (x >= 0)) return true;// norte
        else if((direcao.equalsIgnoreCase(direcoes[1])) && (x < tamanhoMapa)) return true;//sul
        else if((direcao.equalsIgnoreCase(direcoes[2])) && (y >= 0)) return true;//oeste
        else if((direcao.equalsIgnoreCase(direcoes[3])) && (y < tamanhoMapa)) return true;//leste
        return false;
    }


    //getters e setters
    public int getPontos(){
        return this.pontos;
    }


    public void setPontos(int pontos){
        this.pontos = pontos;
    }


    public int getJogosGanhos(){
        return this.jogosGanhos;
    }


    public void setJogosGanhos(int jogosGanhos){
        this.jogosGanhos = jogosGanhos;
    }


    public int getMortes(){
        return this.mortes;
    }


    public void setMortes(int mortes){
        this.mortes = mortes;
    }


    public int getFlechas(){
        return this.flechas;
    }

    
    public void setFlechas(int flechas){
        this.flechas = flechas;
    }


    public int getFlechasAtiradas(){
        return this.flechasAtiradas;
    }


    public void setFlechasAtiradas(int flechasAtiradas){
        this.flechasAtiradas = flechasAtiradas;
    }


    public String getSensacoes(){
        return this.sensacoes;
    }


    public void setSensacoes(String sensacao){
        this.sensacoes = sensacao;
    }


    public boolean getOuroColetado(){
        return this.ouroColetado;
    }

    
    public void setOuroColetado(boolean ouroColetado){
        this.ouroColetado = ouroColetado;
    }


    public int getFlechasAcertadas(){
        return this.flechasAcertadas;
    }


    public void setFlechasAcertadas(int flechasAcertadas){
        this.flechasAcertadas = flechasAcertadas;
    }


    public int getQuedasEmPocos(){
        return this.quedasEmPocos;
    }


    public void setQuedasEmPocos(int quedasEmPocos){
        this.quedasEmPocos = quedasEmPocos;
    }


    public int getMortesPeloWumpus(){
        return this.mortesPeloWumpus;
    }


    public void setMortesPeloWumpus(int mortesPeloWumpus){
        this.mortesPeloWumpus = mortesPeloWumpus;
    }
}