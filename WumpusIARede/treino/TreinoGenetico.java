package treino;

import java.util.ArrayList;
import java.util.Random;

import entidade.Agente;
import rna.RedeNeural;

public class TreinoGenetico{
   public int tamanhoPopulacao;
   public int individuosVivos = 0;
   public ArrayList<Agente> individuos;
   public double mediaPesos = 0;

   public int geracaoAtual = 0;

   //não deixar stagnar
   public int geracoesStagnadas = 0;
   public int ultimoMelhorFitness = 0;
   boolean aumentarAleatoriedade = false;
   

   int i, j, k;//contadores
   Random random = new Random();

   public TreinoGenetico(int tamanhoPopulacao){
      this.tamanhoPopulacao = tamanhoPopulacao;

      individuos = new ArrayList<Agente>();
   }


   public Agente gerarIndividuo(
      int tamanhoMapa,
      int qtdNeuroniosEntrada,
      int qtdNeuroniosOcultas,
      int qtdNeuroniosSaida,
      int qtdOcultas,
      String[][] mapaSensacoes
      ){

      return new Agente(tamanhoMapa-1, 0, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, tamanhoMapa, mapaSensacoes);
   }


   public void carregarIndividuo(Agente individuo){
      individuo.rede.configurarFuncaoAtivacao(5, 2);
      individuos.add(individuo);
      individuosVivos++;
   }


   public void ajustarPouplacao(int tamanhoMapa, int qtdNeuroniosEntrada, int qtdNeuroniosOcultas, int qtdNeuroniosSaida, int qtdOcultas, String[][] mapaSensacoes){
      System.out.println("Ajustando população");

      double mediaFitness = calcularMediaFitness();
      double desvioPadraoFitness = calcularDesvioPadraoFitness();

      Agente melhorAgente = escolherMelhorIndividuo();
      RedeNeural melhorRede = melhorAgente.rede;

      //tratar gerações sem melhora
      if(melhorAgente.fitness == ultimoMelhorFitness) geracoesStagnadas++;
      else{
         ultimoMelhorFitness = melhorAgente.fitness;
         geracoesStagnadas = 0;
      }

      if(geracoesStagnadas > 10){//randomizar mais os pesos
         aumentarAleatoriedade = true;
         geracoesStagnadas = 0;
      }

      individuos.clear();
      for(i = 0; i < tamanhoPopulacao; i++){
         Agente novoAgente = gerarIndividuo(tamanhoMapa, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, mapaSensacoes);
         novoAgente.rede = melhorRede.clone();
         ajustarPesos(novoAgente.rede, mediaFitness, desvioPadraoFitness, aumentarAleatoriedade); 
         carregarIndividuo(novoAgente);    
      }

      aumentarAleatoriedade = false;
      geracaoAtual++;
   }


   private double calcularMediaFitness(){
      double fitnessTotal = 0.0;
      for(i = 0; i < tamanhoPopulacao; i++){
         fitnessTotal += this.individuos.get(i).fitness;
      }
      return (double) (fitnessTotal / tamanhoPopulacao);
   }


   private double calcularDesvioPadraoFitness(){
      double mediaFitness = calcularMediaFitness();
      double somaDiferencasQuadrado = 0.0;
      
      for (int i = 0; i < tamanhoPopulacao; i++) {
         double diferenca = individuos.get(i).fitness - mediaFitness;
         somaDiferencasQuadrado += Math.pow(diferenca, 2);
      }
      
      double desvioPadrao = Math.sqrt(somaDiferencasQuadrado / tamanhoPopulacao);
      return desvioPadrao;
   }


   public void ajustarPesos(RedeNeural rede, double mediaFitness, double desvioPadraoFitness, boolean aumentarAleatoriedade){
      //percorrer camada de entrada
      //percorrer neuronios da camada de entrada
      for(int i = 0; i < rede.entrada.neuronios.length; i++){
         //percerrer pesos de cada neuronio da camada de entrada
         for(int j = 0; j < rede.entrada.neuronios[i].pesos.length; j++){
            rede.entrada.neuronios[i].pesos[j] += novoValorAleatorio(mediaFitness, desvioPadraoFitness, aumentarAleatoriedade);
         }
      }

      //percorrer camadas ocultas
      for(int i = 0; i < rede.ocultas.length; i++){
         //percorrer neuronios da camada oculta
         for(int j = 0; j < rede.ocultas[i].neuronios.length; j++){
            //percorrer pesos de cada neuronio da camada oculta
            for(int k = 0; k < rede.ocultas[i].neuronios[j].pesos.length; k++){
               rede.ocultas[i].neuronios[j].pesos[k] += novoValorAleatorio(mediaFitness, desvioPadraoFitness, aumentarAleatoriedade);
            }
         }
      }
   }


   private double novoValorAleatorio(double mediaFitness, double desvioPadraoFitness, boolean aumentarAleatoriedade){
      double valor = random.nextGaussian() * (Math.abs(mediaFitness) / 2.0);
      valor /= 100;

      if(aumentarAleatoriedade) valor += random.nextDouble(-100, 100);

      mediaPesos = valor;//feedback na tela da rede
      return valor;
   }


   public Agente escolherMelhorIndividuo(){
      int melhorValor = 0;
      int indice = 0;
   
      for (i = 0; i < individuos.size(); i++){
         if(i == 0){
            melhorValor = individuos.get(i).fitness;
            indice = i;
         
         }else if(individuos.get(i).fitness > melhorValor){
            indice = i;
            melhorValor = individuos.get(i).fitness;
         }
      }
      return individuos.get(indice);
   }


   public Agente melhorIndividuoVivo(){
      int melhorValor = 0;
      int indice = 0;
   
      for (i = 0; i < individuos.size(); i++){
         if(individuos.get(i).vivo){
            if(i == 0){
               melhorValor = individuos.get(i).fitness;
               indice = i;
            
            }else if(individuos.get(i).fitness > melhorValor){
               indice = i;
               melhorValor = individuos.get(i).fitness;
            }
         }
      }
      return individuos.get(indice);
   }
}
