public class Wumpus extends Entidade{
    private boolean status;

    public Wumpus(int posX, int posY){
        this.simbolo = "W";
        this.sensacao = "Fedor";
        this.posX = posX;
        this.posY = posY;
        this.status = true;
    }


    public void gritar(){
        System.out.println("*grito*");
    }


    public boolean getStatus(){
        return this.status;
    }


    public void setStatus(boolean status){
        this.status = status;
    }
    
}
