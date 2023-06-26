package entidade;
import java.util.ArrayList;

import rna.RedeNeural;

public class Agente extends Entidade{

    private int pontos;
    private int jogosGanhos;

    private int mortes;
    private int quedasEmPocos;
    private int mortesPeloWumpus;

    private int flechas;
    private int flechasAtiradas;
    private int flechasAcertadas;
    public int passosDados = 0;

    private boolean ouroColetado = false;
    private boolean matouWumpus = false;
    private String direcoes[]= {"norte", "sul", "oeste", "leste"};
    private String sensacoes = "";

    //adaptar pra usar rede e treinar
    public RedeNeural rede;
    public boolean vivo;
    public int fitness = 0;
    public int rodadasJogadas = 0;

    //variaveis de controle da saída da rede
    boolean movimentoAceito = false;
    int flechaAcertada = 0;
    boolean pegouOuro = false;
    int batidasParede = 0;
    double[] saidaRede;

    public int mapaAndado[][];//evitar redes que andam em circulos

    //cópia do mapa
    int tamanhoMapa;
    public ArrayList<Poco> pocosMapa = new ArrayList<>();
    public ArrayList<Wumpus> wumpusMapa = new ArrayList<>();
    public Ouro ouroMapa;
    public String mapaSensacoes[][];


    public Agente(int posX, int posY, int nEntrada, int nOcultas, int nSaida, int qtdOcultas, int tamanhoMapa, String[][] mapaSensacoes){
        this.simbolo = "A";
        this.posX = posX;
        this.posY = posY;

        this.pontos = 0;
        this.jogosGanhos = 0;
        this.mortes = 0;
        this.flechas = 1;
        this.flechasAcertadas = 0;
        this.flechasAtiradas = 0;
        this.quedasEmPocos = 0;
        this.mortesPeloWumpus = 0;

        //informações para o treino
        this.vivo = true;

        this.rede = new RedeNeural(nEntrada, nOcultas, nSaida, qtdOcultas);
        this.rede.configurarFuncaoAtivacao(5, 8);
        this.rede.compilar();
        saidaRede = new double[this.rede.saida.neuronios.length];

        this.tamanhoMapa = tamanhoMapa;
        this.mapaAndado = new int[tamanhoMapa][tamanhoMapa];
        for(int i = 0; i < tamanhoMapa; i++){
            for(int j = 0; j < tamanhoMapa; j++){
                mapaAndado[i][j] = 0;
            }
        }

        this.mapaSensacoes = mapaSensacoes;
    }


    public void getPropriedades(){
        System.out.println("Flechas restantes: " + this.flechas);
        System.out.println("Flechas acertadas: " + this.flechasAcertadas);
        System.out.println("Flechas atiradas: " + this.flechasAtiradas);

        System.out.println("\nMortes: " + this.mortes);
        System.out.println("Mortes pelo Wumpus: " + this.mortesPeloWumpus);
        System.out.println("Quedas em poços: " + this.quedasEmPocos);

        System.out.println("\nOuro coletado: " + this.pontos);
        System.out.println("Jogos ganhos: " + this.jogosGanhos);
    }


    public void getInformacoesRede(){
        System.out.println("Vivo: " + this.vivo);
        System.out.println("Fitness: " + this.fitness);
        System.out.println("Passos:" + passosDados);
        System.out.println("RodadasJogadas:" + rodadasJogadas);
        for(int i = 0; i < this.rede.saida.neuronios.length; i++){
            System.out.println("N" + i + ": " + this.rede.saida.neuronios[i].saida);
        }
    }


    //uso de rede neural na tomada de decisões
    //a rede vai receber os dados do ambiente e definir uma saída
    public boolean calcularAcao(double[] dadosAmbiente){

        //controle do tempo de treino
        if(!this.vivo) return true;
        if(this.rodadasJogadas > 30) return true;

        this.rede.calcularSaida(dadosAmbiente);

        flechaAcertada = 0;//valor fora dos retornos da função atirar
        movimentoAceito = false;
        pegouOuro = false;
        
        //pegar os dados da saída da rede
        saidaRede = this.rede.obterSaida();
        int indice;
        for(indice = 0; indice < saidaRede.length; indice++){
            if(saidaRede[indice] > 0) break;
        }

        switch(indice){
            //movimentação---
            case 0:
                movimentoAceito = validarAcao((this.getX()-1), this.getY(), "norte");
                if(movimentoAceito) mover("norte");    
            break;

            case 1:
                movimentoAceito = validarAcao((this.getX()+1), this.getY(), "sul");
                if(movimentoAceito) mover("sul");            
            break;

            case 2:
                movimentoAceito = validarAcao(this.getX(), (this.getY()-1), "oeste");
                if(movimentoAceito) mover("oeste");
            break;

            case 3:
                movimentoAceito = validarAcao(this.getX(), (this.getY()+1), "leste");
                if(movimentoAceito) mover("leste");
            break;

            //tiro---
            case 4:
                flechaAcertada = atirar(this.wumpusMapa, "norte");
            break;

            case 5:
                flechaAcertada = atirar(this.wumpusMapa, "sul");
            break;

            case 6:
                flechaAcertada = atirar(this.wumpusMapa, "oeste");
            break;

            case 7:
                flechaAcertada = atirar(this.wumpusMapa, "leste");    
            break;

            //ouro---
            case 8:
                pegouOuro = pegarOuro(ouroMapa);
            break;
        }

        calcularFitness();

        rodadasJogadas++;
        return false;//agente não morreu
    }


    private void calcularFitness(){
        //evitar do agente andar muito em circulos
        //recompensar mais agentes que exploram o mapa
        if(movimentoAceito) this.fitness += (30 - (mapaAndado[posX][posY] * 5));
        else{
            this.batidasParede++;
            this.fitness -= (int)(5 * batidasParede);
        }

        if(flechaAcertada == 1) this.fitness += 1500;//atirou e matou
        else if(flechaAcertada == -1) this.fitness -= 100;// atirou sem ter flecha
        else if(flechaAcertada == -2) this.fitness -= 50;//atirou mas errou
        else if(flechaAcertada == -3) this.fitness -= 150;// atirou na parede

        if(pegouOuro){
            this.fitness += 2000;
            for(int i = 0; i < mapaAndado.length; i++){
                for(int j = 0; j < mapaAndado.length; j++){
                    mapaAndado[i][j] = 0;
                }
            }
        }
        else this.fitness -= 10;//pegou numa casa sem ouro
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
        passosDados++;
        mapaAndado[this.posX][this.posY] += 1;
    }


    public boolean pegarOuro(Ouro ouro){
        if(!ouro.getColetado()){
            if((this.getX() == ouro.getX()) && (this.getY() == ouro.getY())){
                ouro.setColetado(true);
                this.pontos++;
                this.ouroColetado = true;
                return true;
            }
        }
        return false;
    }


    /**
     * 
     * @param wumpus lista de Wumpus do agente
     * @param direcao
     * @return -3 atirou na parede, -2 atirou mas errou, -1 não tem flecha, 1 atirou e matou
     */
    public int atirar(ArrayList<Wumpus> wumpus, String direcao){
        if(this.flechas < 1) return -1;//sem flecha

        int x, y;
        boolean acaoValidada = false;

        x = this.posX;
        y = this.posY;
        
        switch(direcao){
            case "norte": x -= 1; break;//norte
            case "sul": x += 1; break;//sul
            case "oeste": y -= 1; break;//oeste
            case "leste": y += 1; break;//leste
        }

        acaoValidada = validarAcao(x, y, direcao);
        if(!acaoValidada) return -3;//atirou nas paredes


        this.flechas --;
        this.flechasAtiradas++;

        for(int i = 0; i < wumpus.size(); i++){
            if((wumpus.get(i).getStatus()) && (x == wumpus.get(i).getX()) && (y == wumpus.get(i).getY())){                
                wumpus.get(i).setStatus(false);
                this.matouWumpus = true;
                return 1;
            }
        }

        return -2;//atirou errado
    }



    private boolean validarAcao(int x, int y, String direcao){
        if((direcao.equalsIgnoreCase(direcoes[0])) && (x >= 0)) return true;// norte
        if((direcao.equalsIgnoreCase(direcoes[1])) && (x < this.tamanhoMapa)) return true;//sul
        if((direcao.equalsIgnoreCase(direcoes[2])) && (y >= 0)) return true;//oeste
        if((direcao.equalsIgnoreCase(direcoes[3])) && (y < this.tamanhoMapa)) return true;//leste
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


    public boolean getMatouWumpus(){
        return this.matouWumpus;
    }


    public void setMatouWumpus(boolean matouWumpus){
        this.matouWumpus = matouWumpus;
    }
}