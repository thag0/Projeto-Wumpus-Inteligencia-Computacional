package rna;

import java.io.Serializable;
import java.util.Random;

public class Neuronio implements Serializable{
   public double saida;
   public double entrada;
   public double[] pesos;
   public double erro;//implementar backpropagation

   public int qtdLigacoes;
   private Random random = new Random();

   public Neuronio(int qtdLigacoes){
      this.qtdLigacoes = qtdLigacoes;

      pesos = new double[qtdLigacoes];
      for(int i = 0; i < pesos.length; i++){
         pesos[i] = random.nextDouble(-100, 100);
      }

      this.entrada = 0;
      this.saida = 0;
   }
}
