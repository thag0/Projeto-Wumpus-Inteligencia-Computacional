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


    public Agente(int posX, int posY){
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
    public void calcularMovimento(int tamanhoMapa, int[] movimentosFeitos){
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


    /**
     * 
     * @param monstro
     * @param tamanhoMapa
     * @return 0 errou o tiro, 1 atirou no poço, 2 matou o wumpus
     */
    public int atirar(Wumpus wumpus, int tamanhoMapa){
        int x, y, valorRetorno = 0;
        boolean acaoValidada = false;

        while(true){
            x = this.posX;
            y = this.posY;
            proximoMovimento = random.nextInt(4);
            
            switch(proximoMovimento){
                case 0://norte
                    x -= 1;
                break;
                
                case 1://sul
                    x += 1;
                break;
    
                case 2://oeste
                    y -= 1;
                break;
    
                case 3://leste
                    y += 1;
                break;
            }

            acaoValidada = validarAcao(x, y, tamanhoMapa, direcoes[proximoMovimento]);
            if(acaoValidada) break;
        }

        System.out.println("atirando " + direcoes[proximoMovimento]);
        this.flechas --;
        this.flechasAtiradas++;

        if((wumpus.getX() == x) && (wumpus.getY() == y)){
            valorRetorno = 2;

        }else if(Main.pocoExistente(x, y)){
            valorRetorno = 1;
        }

        return valorRetorno;
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
