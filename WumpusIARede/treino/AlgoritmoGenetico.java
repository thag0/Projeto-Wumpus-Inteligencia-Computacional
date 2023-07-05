package treino;

import java.util.ArrayList;
import java.util.Random;

import entidade.Agente;
import rna.Camada;
import rna.Neuronio;
import rna.RedeNeural;

public class AlgoritmoGenetico{
   public int tamanhoPopulacao;
   public int individuosVivos = 0;
   public ArrayList<Agente> individuos;
   public double mediaFitness = 0.0;
   public double desvioPadraoFitness = 0.0;

   public final double TAXA_CROSSOVER;
   public final double TAXA_MUTACAO;

   public int geracaoAtual = 0;

   //não deixar stagnar
   public int geracoesStagnadas = 0;
   public int ultimoMelhorFitness = 0;
   boolean aumentarAleatoriedade = false;
   

   int i, j, k;//contadores
   Random random = new Random();

   public AlgoritmoGenetico(int tamanhoPopulacao, double taxaCrossover, double taxaMutacao){
      if(taxaCrossover < 0 || taxaCrossover > 1) throw new IllegalArgumentException("A taxa de crossover deve estar entre 0 e 1");
      if(taxaMutacao < 0 || taxaMutacao > 1) throw new IllegalArgumentException("A taxa de mutação deve estar entre 0 e 1");

      this.TAXA_CROSSOVER = taxaCrossover;
      this.TAXA_MUTACAO = taxaMutacao;

      this.tamanhoPopulacao = tamanhoPopulacao;

      individuos = new ArrayList<Agente>();
   }


   public Agente gerarIndividuo(int tamanhoMapa, int nEntrada, int nOcultas, int nSaida, int qOcultas, String[][] mapaSensacoes){
      return new Agente(tamanhoMapa-1, 0, nEntrada, nOcultas, nSaida, qOcultas, tamanhoMapa, mapaSensacoes);
   }


   public void carregarIndividuo(Agente individuo){
      individuos.add(individuo);
      individuosVivos++;
   }


   public void ajustarPorMutacao(int tamanhoMapa, int nEntrada, int nOculta, int nSaida, int qOcultas, String[][] mapaSensacoes){
      mediaFitness = calcularMediaFitness();
      desvioPadraoFitness = calcularDesvioPadraoFitness();

      //pegando o melhor agente da geração
      Agente melhorAgente = escolherMelhorIndividuo();
      RedeNeural melhorRede = melhorAgente.rede;

      //tratar gerações sem melhora
      if(melhorAgente.fitness == ultimoMelhorFitness) geracoesStagnadas++;
      else{
         ultimoMelhorFitness = melhorAgente.fitness;
         geracoesStagnadas = 0;
      }

      //randomizar mais os pesos
      //evitar que as gerações fiquem muito tempo sem melhorar
      if(geracoesStagnadas > 20){
         aumentarAleatoriedade = true;
         geracoesStagnadas = 0;
      }

      //gerar novos individuos
      individuos.clear();
      for(int i = 0; i < tamanhoPopulacao; i++){
         Agente novoAgente = gerarIndividuo(tamanhoMapa, nEntrada, nOculta, nSaida, qOcultas, mapaSensacoes);
         novoAgente.rede = melhorRede.clone();
         mutacao(novoAgente.rede);
         carregarIndividuo(novoAgente);    
      }

      aumentarAleatoriedade = false;
      geracaoAtual++;
   }


   public void ajustarPorCrossover(int tamanhoMapa, int nEntrada, int nOculta, int nSaida, int qOcultas, String[][] mapaSensacoes){
      mediaFitness = calcularMediaFitness();
      desvioPadraoFitness = calcularDesvioPadraoFitness();
      
      Agente agente1 = escolherMelhorIndividuo();
      Agente agente2 = escolherSegundoMelhorIndividuo();

      if(agente1.fitness == ultimoMelhorFitness) geracoesStagnadas++;
      else{
         ultimoMelhorFitness = agente1.fitness;
         geracoesStagnadas = 0;
      }

      individuos.clear();
      for(int i = 0; i < tamanhoPopulacao; i++){
         Agente novoAgente = gerarIndividuo(tamanhoMapa, nEntrada, nOculta, nSaida, qOcultas, mapaSensacoes);
         RedeNeural novaRede = crossover(agente1.rede.clone(), agente2.rede.clone());
         novoAgente.rede = novaRede;
         ajustarPesos(novoAgente.rede, 1000);
         carregarIndividuo(novoAgente);
      }

      geracaoAtual++;
   }
   
   
   private void mutacao(RedeNeural rede){
      if(random.nextDouble() > TAXA_MUTACAO) return;//não aplicar mutação
      int i;//contador local

      for(Neuronio neuronio : rede.entrada.neuronios){
         for(i = 0; i < neuronio.pesos.length; i++){
            neuronio.pesos[i] += novoValorAleatorio();
         }
      }

      for(Camada camada : rede.ocultas){
         for(Neuronio neuronio : camada.neuronios){
            for(i = 0; i < neuronio.pesos.length; i++){
               neuronio.pesos[i] += novoValorAleatorio();
            }            
         }
      }
   }


   private RedeNeural crossover(RedeNeural rede1, RedeNeural rede2){
      ArrayList<Neuronio> vetorRede1 = redeParaVetor(rede1);
      ArrayList<Neuronio> vetorRede2 = redeParaVetor(rede2);
      if(vetorRede1.size() != vetorRede2.size()) throw new IllegalArgumentException("As redes 1 e 2 possuem tamanhos diferentes");

      if(random.nextDouble() > TAXA_CROSSOVER) return rede1.clone();

      int tamanhoDNA = vetorRede1.size();
      int indiceCorte = random.nextInt(tamanhoDNA);//ponto de separação da combinação
      
      //combinando os genes
      ArrayList<Neuronio> vetorCombinado = new ArrayList<>();
      for(int i = 0; i < vetorRede1.size(); i++){
         if(i < indiceCorte) vetorCombinado.add(vetorRede1.get(i));
         else vetorCombinado.add(vetorRede2.get(i));
      }
      
      int nEntrada = rede1.entrada.neuronios.length-1;//excluir o bias
      int nOcultas = rede1.ocultas[0].neuronios.length-1;//excluir o bias
      int nSaida = rede1.saida.neuronios.length;
      int qOcultas = rede1.ocultas.length;
      RedeNeural redeCombinada = vetorParaRede(vetorCombinado, nEntrada, nOcultas, nSaida, qOcultas);

      return redeCombinada;
   }


   private ArrayList<Neuronio> redeParaVetor(RedeNeural rede){
      ArrayList<Neuronio> neuronios = new ArrayList<>();
      
      //camada de entrada
      for(Neuronio neuronio : rede.entrada.neuronios){
         neuronios.add(neuronio);
      }
      //camadas ocultas
      for(Camada oculta : rede.ocultas){
         for(Neuronio neuronio : oculta.neuronios){
            neuronios.add(neuronio);
         }
      }
      //camada de saídas
      for(Neuronio neuronio : rede.saida.neuronios){
         neuronios.add(neuronio);
      }

      return neuronios;
   }


   private RedeNeural vetorParaRede(ArrayList<Neuronio> neuronios, int nEntrada, int nOcultas, int nSaida, int qOcultas){
      RedeNeural rede = new RedeNeural(nEntrada, nOcultas, nSaida, qOcultas);
      rede.configurarFuncaoAtivacao(5, 8);
      rede.compilar();

      int indice = 0;//correspondente ao indice da lista de neuronios
      //camada de entrada
      for(int i = 0; i < nEntrada; i++){
         rede.entrada.neuronios[i] = neuronios.get(indice);
         indice++;
      }

      //camadas ocultas
      for(int i = 0; i < qOcultas; i++){
         for(int j = 0; j < nOcultas; j++){
            rede.ocultas[i].neuronios[j] = neuronios.get(indice);
            indice++;
         }
      }

      //camada de saída
      for(int i = 0; i < nSaida; i++){
         rede.saida.neuronios[i] = neuronios.get(indice);
         indice++;
      }

      return rede;
   }


   private double calcularMediaFitness(){
      double fitnessTotal = 0.0;
      for(int i = 0; i < tamanhoPopulacao; i++){
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


   //Ajusta os pesos com valores já definidos
   private void ajustarPesos(RedeNeural rede, double alcancePesos){
      if(alcancePesos < 0) throw new IllegalArgumentException("O valor de alcance dos pesos deve ser maior que zero");
      
      int cont;//contador local
      for(Neuronio neuronio : rede.entrada.neuronios){
         for(cont = 0; cont < neuronio.pesos.length; cont++){
            neuronio.pesos[cont] += random.nextDouble((-1 * alcancePesos), alcancePesos); 
         }
      }

      for(Camada camada : rede.ocultas){
         for(Neuronio neuronio : camada.neuronios){
            for(cont = 0; cont < neuronio.pesos.length; cont++){
               neuronio.pesos[cont] += random.nextDouble((-1 * alcancePesos), alcancePesos);    
            }
         }
      }
   }


   private double novoValorAleatorio(){
      double valor;
      // double valor = random.nextGaussian() * (Math.abs(mediaFitness) / 2);
      // valor /= 100;

      valor = random.nextDouble(-1, 1);
      valor *= 1000;

      if(aumentarAleatoriedade) valor += random.nextDouble(-500, 500);

      return valor;
   }


   public Agente escolherMelhorIndividuo(){
      int melhorValor = this.individuos.get(0).fitness;
      int indice = 0;
   
      for (i = 0; i < tamanhoPopulacao; i++){
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


   public Agente escolherSegundoMelhorIndividuo(){
      int melhorFitness = 0;
      int segundoMelhorFitness = 0;
      int indiceSegundoMelhor = 0;

      //procurar melhor individuo
      for(int i = 0; i < tamanhoPopulacao; i++){
         if(i == 0){
            melhorFitness = this.individuos.get(i).fitness;
         
         }else if(this.individuos.get(i).fitness > melhorFitness){
            melhorFitness = this.individuos.get(i).fitness;
         }
      }

      //procurar segundo melhor
      int fitness = 0;
      for(int i = 0; i < tamanhoPopulacao; i++){
         fitness = this.individuos.get(i).fitness;
         if((fitness < melhorFitness) && (fitness > segundoMelhorFitness)){
            indiceSegundoMelhor = i;
            segundoMelhorFitness = fitness;
         }
      }
   
      return this.individuos.get(indiceSegundoMelhor);
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