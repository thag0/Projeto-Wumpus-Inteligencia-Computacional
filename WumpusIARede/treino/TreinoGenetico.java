package treino;

import java.util.ArrayList;
import java.util.Random;

import entidade.Agente;
import rna.Camada;
import rna.Neuronio;
import rna.RedeNeural;

public class TreinoGenetico{
   public int tamanhoPopulacao;
   public int individuosVivos = 0;
   public ArrayList<Agente> individuos;
   public double mediaFitness = 0.0;
   public double desvioPadraoFitness = 0.0;

   public final double TAXA_CROSSOVER;
   public final double TAXA_MUTACAO;
   public final boolean ELITISMO;

   public int geracaoAtual = 0;

   //não deixar stagnar
   public int geracoesStagnadas = 0;
   public int ultimoMelhorFitness = 0;
   boolean aumentarAleatoriedade = false;
   

   int i, j, k;//contadores
   Random random = new Random();

   public TreinoGenetico(int tamanhoPopulacao, double taxaCrossover, double taxaMutacao, boolean aplicarElitismo){
      if(taxaCrossover < 0 || taxaCrossover > 1) throw new IllegalArgumentException("A taxa de crossover deve estar entre 0 e 1");
      if(taxaMutacao < 0 || taxaMutacao > 1) throw new IllegalArgumentException("A taxa de mutação deve estar entre 0 e 1");

      this.TAXA_CROSSOVER = taxaCrossover;
      this.TAXA_MUTACAO = taxaMutacao;
      this.ELITISMO = aplicarElitismo;

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
      individuos.add(individuo);
      individuosVivos++;
   }


   public void ajustarPorMutacao(int tamanhoMapa, int qtdNeuroniosEntrada, int qtdNeuroniosOcultas, int qtdNeuroniosSaida, int qtdOcultas, String[][] mapaSensacoes){
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
      if(geracoesStagnadas > 15){
         aumentarAleatoriedade = true;
         geracoesStagnadas = 0;
      }

      //gerar novos individuos
      individuos.clear();
      int indiceInicio = 0;
      if(ELITISMO){//preservar dna do melhor agente
         Agente novoAgente = gerarIndividuo(tamanhoMapa, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, mapaSensacoes);
         novoAgente.rede = melhorRede.clone();
         carregarIndividuo(novoAgente);

         indiceInicio++;
      }

      for(int i = indiceInicio; i < tamanhoPopulacao; i++){
         Agente novoAgente = gerarIndividuo(tamanhoMapa, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, mapaSensacoes);
         novoAgente.rede = melhorRede.clone();
         mutacao(novoAgente.rede, mediaFitness, desvioPadraoFitness, aumentarAleatoriedade);
         carregarIndividuo(novoAgente);    
      }

      aumentarAleatoriedade = false;
      geracaoAtual++;
   }


   public void ajustarPorCrossover(int tamanhoMapa, int qtdNeuroniosEntrada, int qtdNeuroniosOcultas, int qtdNeuroniosSaida, int qtdOcultas, String[][] mapaSensacoes){
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
      int indiceInicio = 0;
      if(ELITISMO){//preservar o dna completo dos melhores individuos
         Agente novoAgente1 = gerarIndividuo(tamanhoMapa, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, mapaSensacoes);
         novoAgente1.rede = agente1.rede.clone();
         carregarIndividuo(novoAgente1);
         indiceInicio++;

         Agente novoAgente2 = gerarIndividuo(tamanhoMapa, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, mapaSensacoes);
         novoAgente2.rede = agente2.rede.clone();
         carregarIndividuo(novoAgente2);
         indiceInicio++;
      }

      for(int i = indiceInicio; i < tamanhoPopulacao; i++){
            Agente novoAgente = gerarIndividuo(tamanhoMapa, qtdNeuroniosEntrada, qtdNeuroniosOcultas, qtdNeuroniosSaida, qtdOcultas, mapaSensacoes);
            RedeNeural novaRede = crossover(agente1.rede.clone(), agente2.rede.clone());
            ajustarPesos(novaRede, 1000);
            novoAgente.rede = novaRede;
            carregarIndividuo(novoAgente);
      }

      geracaoAtual++;
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


   private void mutacao(RedeNeural rede, double mediaFitness, double desvioPadraoFitness, boolean aumentarAleatoriedade){
      int i, j, k;//contadores locais
      if(random.nextDouble() > TAXA_MUTACAO) return;//não aplicar mutação

      //percorrer camada de entrada
      //percorrer neuronios da camada de entrada
      for(i = 0; i < rede.entrada.neuronios.length; i++){
         //percerrer pesos de cada neuronio da camada de entrada
         for(j = 0; j < rede.entrada.neuronios[i].pesos.length; j++){
            rede.entrada.neuronios[i].pesos[j] += novoValorAleatorio(mediaFitness, desvioPadraoFitness, aumentarAleatoriedade);
         }
      }

      //percorrer camadas ocultas
      for(i = 0; i < rede.ocultas.length; i++){
         //percorrer neuronios da camada oculta
         for(j = 0; j < rede.ocultas[i].neuronios.length; j++){
            //percorrer pesos de cada neuronio da camada oculta
            for(k = 0; k < rede.ocultas[i].neuronios[j].pesos.length; k++){
               rede.ocultas[i].neuronios[j].pesos[k] += novoValorAleatorio(mediaFitness, desvioPadraoFitness, aumentarAleatoriedade);
            }
         }
      }
   }


   //Ajusta os pesos com valores já definidos
   private void ajustarPesos(RedeNeural rede, double alcancePesos){
      int i, j, k;//contadores locais

      //percorrer camada de entrada
      //percorrer neuronios da camada de entrada
      for(i = 0; i < rede.entrada.neuronios.length; i++){
         //percerrer pesos de cada neuronio da camada de entrada
         for(j = 0; j < rede.entrada.neuronios[i].pesos.length; j++){
            rede.entrada.neuronios[i].pesos[j] += random.nextDouble((-1 * alcancePesos), alcancePesos);
         }
      }

      //percorrer camadas ocultas
      for(i = 0; i < rede.ocultas.length; i++){
         //percorrer neuronios da camada oculta
         for(j = 0; j < rede.ocultas[i].neuronios.length; j++){
            //percorrer pesos de cada neuronio da camada oculta
            for(k = 0; k < rede.ocultas[i].neuronios[j].pesos.length; k++){
               rede.ocultas[i].neuronios[j].pesos[k] += random.nextDouble((-1 * alcancePesos), alcancePesos);
            }
         }
      }
   }


   private double novoValorAleatorio(double mediaFitness, double desvioPadraoFitness, boolean aumentarAleatoriedade){
      double valor;
      // double valor = random.nextGaussian() * (Math.abs(mediaFitness) / 2);
      // valor /= 100;

      valor = random.nextDouble(-1, 1);
      valor *= 1000;

      if(aumentarAleatoriedade) valor += random.nextDouble(-50, 50);

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