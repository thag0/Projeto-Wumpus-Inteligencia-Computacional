public class Entidade{
    protected String simbolo;
    protected String sensacao;
    
    protected int posX;
    protected int posY;

    public String getSensacao(){
        return sensacao;
    }

    //get set
    public int getX(){
        return this.posX;
    }

    
    public int getY(){
        return this.posY;
    }


    public String getSimbolo(){
        return this.simbolo;
    }
}
