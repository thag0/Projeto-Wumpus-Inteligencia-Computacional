import java.util.HashMap;
import java.util.Random;

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
    private String sensacoes = "";

    //memoria
    String memoriaSensacoes[][];
    String memoriaElementos[][];
    HashMap<Character, Integer> contadorCaracteres = new HashMap<>();
    public int ultimaPosicao[] = new int[2];


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

        memoriaSensacoes = new String[tamanhoMapa][tamanhoMapa];
        memoriaElementos = new String[tamanhoMapa][tamanhoMapa];
        ultimaPosicao[0] = tamanhoMapa-1;
        ultimaPosicao[1] = 0;
        for(int i = 0; i < tamanhoMapa; i++){
            for(int j = 0; j < tamanhoMapa; j++){
                memoriaSensacoes[i][j] = "";
                memoriaElementos[i][j] = "";
            }
        }
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


    //funções lógicas    
    public void calcularMovimento(int tamanhoMapa, int[] movimentosFeitos, String[][] mapaSensacoes){
        preencherMemoria(this.getX(), this.getY(), mapaSensacoes);//guardar casa segura
        consultarMemoria();
        int x, y;
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
            if(acaoValidada) break;
        }

        System.out.println("\nmovendo " + direcoes[proximoMovimento]);
        mover(direcoes[proximoMovimento]);
        movimentosFeitos[proximoMovimento]++;
    }


    public void mover(String direcao){
        ultimaPosicao[0] = posX;
        ultimaPosicao[1] = posY;

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


    public void preencherMemoria(int x, int y, String[][] mapaSensacoes){
        memoriaSensacoes[ultimaPosicao[0]][ultimaPosicao[1]] = "S";
        memoriaSensacoes[x][y] = "S";

        memoriaElementos[x][y] = "S";

        //caso o agente não sinta nada, todas as casas em volta são seguras
        if(mapaSensacoes[x][y].isBlank()){
            if(x-1 >= 0) memoriaSensacoes[x-1][y] = "S";//norte 
            if(x+1 < mapaSensacoes.length) memoriaSensacoes[x+1][y] = "S";//sul
            if(y-1 >= 0) memoriaSensacoes[x][y-1] = "S";//oeste
            if(y+1 < mapaSensacoes.length) memoriaSensacoes[x][y+1] = "S";//leste           
        }

        if(mapaSensacoes[x][y].contains("Brilho")){
            memoriaSensacoes[x][y] += "O";
            memoriaElementos[x][y] += "O";
        }

        //preencher sensações
        String[] sensacoes = {"F", "B"};
        for(int i = 0; i < sensacoes.length; i++){
            if(mapaSensacoes[x][y].contains(sensacoes[i])){
                if(x-1 >= 0){
                    if(!memoriaSensacoes[x-1][y].contains("S")){
                      if(!memoriaElementos[x-1][y].contains("P") || !memoriaElementos[x-1][y].contains("W")) memoriaSensacoes[x-1][y] += sensacoes[i];
                    }
                } 

                if(x+1 < mapaSensacoes.length){
                    if(!memoriaSensacoes[x+1][y].contains("S")){
                        System.out.println("a");
                        if(!memoriaElementos[x+1][y].contains("P") || !memoriaElementos[x+1][y].contains("W")) memoriaSensacoes[x+1][y] += sensacoes[i];
                    }
                }

                if(y-1 >= 0){
                    if(!memoriaSensacoes[x][y-1].contains("S")){
                        if(!memoriaElementos[x][y-1].contains("P") || !memoriaElementos[x][y-1].contains("W")) memoriaSensacoes[x][y-1] += sensacoes[i];
                    }
                }

                if(y+1 < mapaSensacoes.length){
                    if(!memoriaSensacoes[x][y+1].contains("S")){
                        if(!memoriaElementos[x][y+1].contains("P") || !memoriaElementos[x][y+1].contains("W")) memoriaSensacoes[x][y+1] += sensacoes[i];
                    } 
                }
            }
        }

        //preencher mapa de elementos
        char fedor = 'F';
        char brisa = 'B';
        int contFedor = 0;
        int contBrisa = 0;
        for(int i = 0; i < memoriaSensacoes.length; i++){
            for(int j = 0; j < memoriaSensacoes.length; j++){
                
                if(!memoriaSensacoes[i][j].isBlank()){
                    contadorCaracteres.clear();
                    contFedor = 0;
                    contBrisa = 0;
                    for(int k = 0; k < memoriaSensacoes[i][j].length(); k++){
                        char c = memoriaSensacoes[i][j].charAt(k);
                        if(c == fedor) contFedor++;
                        if(c == brisa) contBrisa++;
                    }

                    if((contBrisa > contFedor) && (contBrisa > 2)){
                        memoriaElementos[i][j] += "P";
                        memoriaSensacoes[i][j] = memoriaSensacoes[i][j].replaceAll("B", "");
                    
                    
                    }else if((contFedor > contBrisa) && (contFedor > 2)){
                        memoriaElementos[i][j] += "W";
                        memoriaSensacoes[i][j] = memoriaSensacoes[i][j].replaceAll("F", "");
                    }
                }

            }
        }
    }


    public void consultarMemoria(){
        
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
            if(acaoValidada && !memoriaSensacoes[x][y].contains("S")) break;
        }

        System.out.println("atirando " + direcoes[proximoMovimento]);
        this.flechas --;
        this.flechasAtiradas++;

        //verificar paredes
        if((this.getY() == 0) && (this.getX() > 0) && (this.getX() < tamanhoMapa-1)){//parede esquerda
            if(acertouWumpus(wumpus, x, y)){
                memoriaSensacoes[x][y] = "S";
                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!memoriaElementos[x][y].contains("P")) memoriaElementos[x][y] = "P";

                //preencher o wumpus na posição restante
                if(memoriaElementos[this.getX()-1][this.getY()].isBlank()) memoriaElementos[this.getX()-1][this.getY()] = "W";//cima
                if(memoriaElementos[this.getX()+1][this.getY()].isBlank()) memoriaElementos[this.getX()+1][this.getY()] = "W";//baixo
                if(memoriaElementos[this.getX()][this.getY()+1].isBlank()) memoriaElementos[this.getX()][this.getY()+1] = "W";//direita

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                memoriaSensacoes[x][y] = "S";
                if(memoriaSensacoes[this.getX()+1][this.getY()].contains("S") && memoriaSensacoes[this.getX()-1][this.getY()].contains("S")){
                    memoriaElementos[this.getX()][this.getY()+1] = "W";
                
                }else if(memoriaSensacoes[this.getX()][this.getY()+1].contains("S") && memoriaSensacoes[this.getX()+1][this.getY()].contains("S")){
                    memoriaElementos[this.getX()-1][this.getY()] = "W";
                
                }else if(memoriaSensacoes[this.getX()-1][this.getY()].contains("S") && memoriaSensacoes[this.getX()][this.getY()+1].contains("S")){
                    memoriaElementos[this.getX()+1][this.getY()] = "W";
                }

                return valorRetorno;
            }

        
        }else if(this.getX() == 0 && (this.getY()>0) && (this.getY()<tamanhoMapa-1)){//parece cima
            if(acertouWumpus(wumpus, x, y)){
                memoriaSensacoes[x][y] = "S";
                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!memoriaElementos[x][y].contains("P")) memoriaElementos[x][y] = "P";

                //preencher o wumpus na posição restante
                if(memoriaElementos[this.getX()+1][this.getY()].isBlank()) memoriaElementos[this.getX()+1][this.getY()] = "W";//baixo
                if(memoriaElementos[this.getX()][this.getY()-1].isBlank()) memoriaElementos[this.getX()][this.getY()-1] = "W";//esquerda
                if(memoriaElementos[this.getX()][this.getY()+1].isBlank()) memoriaElementos[this.getX()][this.getY()+1] = "W";//direita

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                memoriaSensacoes[x][y] = "S";
                if(memoriaSensacoes[this.getX()][this.getY()-1].contains("S") && memoriaSensacoes[this.getX()][this.getY()+1].contains("S")){
                    memoriaElementos[this.getX()+1][this.getY()] = "W";
                
                }else if(memoriaSensacoes[this.getX()+1][this.getY()].contains("S") && memoriaSensacoes[this.getX()][this.getY()+1].contains("S")){
                    memoriaElementos[this.getX()][this.getY()-1] = "W";
                
                }else if(memoriaSensacoes[this.getX()][this.getY()-1].contains("S") && memoriaSensacoes[this.getX()+1][this.getY()].contains("S")){
                    memoriaElementos[this.getX()][this.getY()+1] = "W";
                }

                return valorRetorno;
            }
            

        }else if((this.getX() == tamanhoMapa-1) && (this.getY() > 0) && (this.getY() < tamanhoMapa-1)){//parede baixo
            if(acertouWumpus(wumpus, x, y)){
                memoriaSensacoes[x][y] = "S";//posição que atirou agora é segura
                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!memoriaElementos[x][y].contains("P")) memoriaElementos[x][y] = "P";

                //preencher o wumpus na posição restante
                if(memoriaElementos[this.getX()-1][this.getY()].isBlank()) memoriaElementos[this.getX()-1][this.getY()] = "W";//cima
                if(memoriaElementos[this.getX()][this.getY()-1].isBlank()) memoriaElementos[this.getX()][this.getY()-1] = "W";//esquerda
                if(memoriaElementos[this.getX()][this.getY()+1].isBlank()) memoriaElementos[this.getX()][this.getY()+1] = "W";//direita

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                memoriaSensacoes[x][y] = "S";
                if(memoriaSensacoes[this.getX()-1][this.getY()].contains("S") && memoriaSensacoes[this.getX()][this.getY()+1].contains("S")){
                    memoriaElementos[this.getX()][this.getY()-1] = "W";
                
                }else if(memoriaSensacoes[this.getX()][this.getY()-1].contains("S") && memoriaSensacoes[this.getX()][this.getY()+1].contains("S")){
                    memoriaElementos[this.getX()-1][this.getY()] = "W";
                
                }else if(memoriaSensacoes[this.getX()][this.getY()-1].contains("S") && memoriaSensacoes[this.getX()][this.getY()-1].contains("S")){
                    memoriaElementos[this.getX()][this.getY()+1] = "W";
                }

                return valorRetorno;
            }
            

        }else if((this.getY() == tamanhoMapa-1) && (this.getX() > 0) && (this.getX() < tamanhoMapa-1)){//parede direita
            if(acertouWumpus(wumpus, x, y)){
                memoriaSensacoes[x][y] = "S";//posição que atirou agora é segura
                valorRetorno = 2;
                return valorRetorno;
            
            }else if(Main.pocoExistente(x, y)){
                if(!memoriaElementos[x][y].contains("P")) memoriaElementos[x][y] = "P";

                //preencher o wumpus na posição restante
                if(memoriaElementos[this.getX()-1][this.getY()].isBlank()) memoriaElementos[this.getX()-1][this.getY()] = "W";//cima
                if(memoriaElementos[this.getX()+1][this.getY()].isBlank()) memoriaElementos[this.getX()+1][this.getY()] = "W";//baixo
                if(memoriaElementos[this.getX()][this.getY()-1].isBlank()) memoriaElementos[this.getX()][this.getY()-1] = "W";//esquerda

                valorRetorno = 1;
                return valorRetorno;
            
            }else{
                memoriaSensacoes[x][y] = "S";
                if(memoriaSensacoes[this.getX()-1][this.getY()].contains("S") && memoriaSensacoes[this.getX()+1][this.getY()].contains("S")){
                    memoriaElementos[this.getX()][this.getY()-1] = "W";
                
                }else if(memoriaSensacoes[this.getX()][this.getY()-1].contains("S") && memoriaSensacoes[this.getX()+1][this.getY()].contains("S")){
                    memoriaElementos[this.getX()-1][this.getY()] = "W";
                
                }else if(memoriaSensacoes[this.getX()-1][this.getY()].contains("S") && memoriaSensacoes[this.getX()][this.getY()-1].contains("S")){
                    memoriaElementos[this.getX()+1][this.getY()] = "W";
                }

                return valorRetorno;
            }
        }

        
        if((this.getX() > 0) && (this.getX() < tamanhoMapa-1) && (this.getY() > 0) && (this.getY() < tamanhoMapa-1)){//centro

        }


        if(acertouWumpus(wumpus, x, y)){//acertou wumpus
            memoriaSensacoes[x][y] = "S";
            valorRetorno = 2;
        
        }else{

        }

        return valorRetorno;
    }


    private boolean acertouWumpus(Wumpus wumpus, int x, int y){
        if((wumpus.getX() == x) && (wumpus.getY() == y)) return true;
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
