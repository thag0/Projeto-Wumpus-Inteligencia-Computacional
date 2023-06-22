public class Ouro extends Entidade{
    private boolean coletado;

    public Ouro(int posX, int posY){
        this.simbolo = "O";
        this.sensacao = "Brilho";
        this.posX = posX;
        this.posY = posY;
        this.coletado = false;
    }


    public boolean getColetado(){
        return this.coletado;
    }


    public void setColetado(boolean coletado){
        this.coletado = coletado;
    }
}
