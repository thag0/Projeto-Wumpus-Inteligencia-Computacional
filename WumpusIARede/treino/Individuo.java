package treino;

import rna.RedeNeural;

public class Individuo implements Cloneable{
   public RedeNeural rede;
   public boolean vivo;

   //calculo do fitness
   public int fitness;
   public int passosDados;
   public int ouroColetado;
   public int jogoGanho;
   public int flechaAcertada;
   public int batidasParede;

   public Individuo(){
      this.fitness = 0;
      this.passosDados = 0;
      this.ouroColetado = 0;
      this.jogoGanho = 0;
      this.flechaAcertada = 0;
      this.batidasParede = 0;
   }


   public void setFitness(int fitness){
      this.fitness = fitness;
   }

   //retornar instancias diferentes para cada objeto clonado
   @Override
   protected Individuo clone(){
      try {
         Individuo clone = (Individuo) super.clone();

         return clone;
      }catch(CloneNotSupportedException e){
         // Trate a exceção de clone não suportado, se necessário
         return null;
      }
   }
   
}
