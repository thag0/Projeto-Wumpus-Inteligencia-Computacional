package render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import entidade.Agente;
import rna.RedeNeural;
import treino.TreinoGenetico;

public class Painel extends JPanel{
   final int largura = 700;
   final int altura = 480;
   Graphics2D g2;
   
   public Agente melhorAgente;
   public RedeNeural rede;
   //coordenadas de origem dos neuronios
   ArrayList<Coordenada> coordEntrada = new ArrayList<>();
   ArrayList<ArrayList<Coordenada>> coordOcultas = new ArrayList<>();//lista de lista de coordenadas
   ArrayList<Coordenada> coordSaida = new ArrayList<>();

   //auxilinar na inicialização
   Coordenada c = new Coordenada(0, 0, 0);
   //auxiliar no desenho das conexões
   Coordenada c1;
   Coordenada c2;

   //desenho
   int contador = 0;
   int contador2 = 0;
   int x0 = 120;//posição x base de desenho da rede
   int y0 = 70;//posição y base de desenho da rede
   int x = 0;
   int y = 0;
   int yCamadaEntrada = 0;
   int yCamadaOculta = 0;
   int yCamadaSaida = 0;
   int larguraDesenho = 26;
   int alturaDesenho = larguraDesenho;
   int espacoVerticalEntreNeuronio = 9;
   int espacoHorizontalEntreCamadas = (int)(larguraDesenho * 4);
   String texto = "";

   //informações
   int geracaoAtual = 0;
   double mediaPesos = 0;
   double melhorFitness = 0;
   double mediaFitness = 0;
   int geracoesStagnadas = 0;
   long redesQueGanharam = 0;

   File pastaRedes = new File("./melhores-redes/");

   int r = 145;
   int g = 100;
   int b = 160;
   Color corNeuronioAtivo = new Color(r, g, b);
   Color corNeuronioInativo = new Color((int)(r * 0.35), (int)(g * 0.35), (int)(b * 0.35));

   Color corConexaoAtiva = new Color((int)(r * 0.8), (int)(g * 0.8), (int)(b * 0.8));
   Color corConexaoInativa = new Color(30, 30, 30);

   public Painel(){
      setBackground(Color.black);
      setPreferredSize(new Dimension(largura, altura));
      setFocusable(true);
      setDoubleBuffered(true);
      setEnabled(true);
      setVisible(true);

      //evitar null pointer exception na rede
      rede = new RedeNeural(1, 1, 1, 1);
      rede.compilar();
   }


   public void desenhar(Agente agente, TreinoGenetico treinoGenetico, long redesQueGanharam){
      //melhor agente
      melhorAgente = agente;
      rede = agente.rede;
      
      //treino
      this.geracaoAtual = treinoGenetico.geracaoAtual;
      this.redesQueGanharam = redesQueGanharam;
      
      //estatisticas
      this.melhorFitness = treinoGenetico.ultimoMelhorFitness;
      this.geracoesStagnadas = treinoGenetico.geracoesStagnadas;
      this.mediaFitness = treinoGenetico.mediaFitness;
      repaint();
   }

   
   @Override
   protected void paintComponent(Graphics g){
      super.paintComponent(g);
      g2 = (Graphics2D) g;

      //centralizar o desenho dos neuronios com base na altura da tela, no tamanho dos neuronios das camadas desenhadas
      //incluir o espaçamento estre os neurinios no calculo
      yCamadaEntrada = y0 + (altura/2) - (larguraDesenho * (melhorAgente.rede.entrada.neuronios.length+1)) + (espacoVerticalEntreNeuronio * (melhorAgente.rede.entrada.neuronios.length-1));
      yCamadaOculta = y0 + (altura/2) - (larguraDesenho * (melhorAgente.rede.ocultas[0].neuronios.length+1)) + (espacoVerticalEntreNeuronio * (melhorAgente.rede.ocultas[0].neuronios.length-1));
      yCamadaSaida = y0 + (altura/2) - (larguraDesenho * (melhorAgente.rede.saida.neuronios.length+1)) + (espacoVerticalEntreNeuronio * (melhorAgente.rede.saida.neuronios.length-1));

      //desenhar informações
      g2.setColor(corNeuronioAtivo);
      g2.setFont(getFont().deriveFont(15f));

      //primeira linha
      x = 10;
      y = 20;
      texto = "Geração atual: " + this.geracaoAtual;
      g2.drawString(texto, x, y);

      x += 200;
      texto = "Gerações stagnadas: " + this.geracoesStagnadas;
      g2.drawString(texto, x, y);

      x += 210;
      texto = "Redes que ganharam: " + redesQueGanharam;
      g2.drawString(texto, x, y);

      //segunda linha
      x = 10;
      y = 40;
      texto = "Último melhor fitness: " + (int)(this.melhorFitness);
      g2.drawString(texto, x, y);

      x += 200;
      texto = "Última média fitness: " + (int)(this.mediaFitness);
      g2.drawString(texto, x, y);

      //terceira linha

      x = 10;
      y = 60;
      texto = "Diversidade de redes: " + pastaRedes.listFiles().length + " redes salvas";
      g2.drawString(texto, x, y);

      //desenho para calcular as cooredanas das conexões
      desenharCamadaEntrada(g2);    
      desenharOcultas(g2);
      desenharSaida(g2);
      
      desenharConexoesEntrada(g2);
      desenharConexoesOcultas(g2);
      desenharConexoesSaida(g2);

      g2.dispose();
   }


   private void desenharConexoesEntrada(Graphics2D g2){
      //evitar muitas instanciações
      int i, j;

      //entrada -> primeira oculta
      for(i = 0; i < coordEntrada.size(); i++){//percorrer neuronios da entrada
         for(j = 0; j < coordOcultas.get(0).size()-1; j++){//percorrer a primeira oculta, excluir o bias
            c1 = coordEntrada.get(i);
            c2 = coordOcultas.get(0).get(j);

            if(c1.valor > 0 && c2.valor > 0) g2.setColor(corConexaoAtiva);
            else g2.setColor(corConexaoInativa);
            g2.drawLine(c1.x, c1.y, c2.x-larguraDesenho, c2.y);
         }
      }
      //reforçar as linhas ativas
      for(i = 0; i < coordEntrada.size(); i++){
         for(j = 0; j < coordOcultas.get(0).size()-1; j++){
            c1 = coordEntrada.get(i);
            c2 = coordOcultas.get(0).get(j);

            if(c1.valor > 0 && c2.valor > 0){
               g2.setColor(corConexaoAtiva);
               g2.drawLine(c1.x, c1.y, c2.x-larguraDesenho, c2.y);
            }
         }
      }
   }


   private void desenharConexoesOcultas(Graphics2D g2){
      int i, j, k;
      //primeira oculta -> ultima oculta
      for(i = 0; i < coordOcultas.size()-1; i++){//percorrer ocultas
         for(j = 0; j < coordOcultas.get(i).size(); j++){//percorrer neuronios da camada oculta atual
            for(k = 0; k < coordOcultas.get(i+1).size()-1; k++){//percorrer neuronios da camada oculta na frente, excluir o bias
               c1 = coordOcultas.get(i).get(j);
               c2 = coordOcultas.get(i+1).get(k);

               if(c1.valor > 0 && c2.valor > 0) g2.setColor(corConexaoAtiva);
               else g2.setColor(corConexaoInativa);
               g2.drawLine(c1.x, c1.y, c2.x-larguraDesenho, c2.y);
            }
         }
      }
      //reforçar as linhas ativas
      for(i = 0; i < coordOcultas.size()-1; i++){
         for(j = 0; j < coordOcultas.get(i).size(); j++){
            for(k = 0; k < coordOcultas.get(i+1).size()-1; k++){
               c1 = coordOcultas.get(i).get(j);
               c2 = coordOcultas.get(i+1).get(k);

               if(c1.valor > 0 && c2.valor > 0){
                  g2.setColor(corConexaoAtiva);
                  g2.drawLine(c1.x, c1.y, c2.x-larguraDesenho, c2.y);
               }
            }
         }
      }      
   }


   private void desenharConexoesSaida(Graphics2D g2){
      //ultima oculta -> saída
      int i, j;
      for(i = 0; i < coordOcultas.get(coordOcultas.size()-1).size(); i++){//percorrer neuronios da ultima oculta
         for(j = 0; j < coordSaida.size(); j++){//percorrer neuronios da saida
            c1 = coordOcultas.get(coordOcultas.size()-1).get(i);
            c2 = coordSaida.get(j);

            if(c1.valor > 0 && c2.valor > 0) g2.setColor(corConexaoAtiva);
            else g2.setColor(corConexaoInativa);
            g2.drawLine(c1.x, c1.y, c2.x-larguraDesenho, c2.y);
         }
      }
      //reforçar as linhas ativas
      for(i = 0; i < coordOcultas.get(coordOcultas.size()-1).size(); i++){
         for(j = 0; j < coordSaida.size(); j++){
            c1 = coordOcultas.get(coordOcultas.size()-1).get(i);
            c2 = coordSaida.get(j);

            if(c1.valor > 0 && c2.valor > 0){
               g2.setColor(corConexaoAtiva);
               g2.drawLine(c1.x, c1.y, c2.x-larguraDesenho, c2.y);
            }
         }
      }
   }


   private void desenharCamadaEntrada(Graphics2D g2){
      x = x0;
      y = yCamadaEntrada;
      
      coordEntrada.clear();
      for(contador = 0; contador < rede.entrada.neuronios.length; contador++){
         
         if(rede.entrada.neuronios[contador].saida > 0) g2.setColor(corNeuronioAtivo);
         else g2.setColor(corNeuronioInativo);
         
         int xTexto = -110;
         int yTexto = 14;
         //posições disponíveis
         if(contador == 0) g2.drawString("Norte", (x+xTexto), (y+yTexto));
         else if(contador == 1) g2.drawString("Sul", (x+xTexto), (y+yTexto));
         else if(contador == 2) g2.drawString("Oeste", (x+xTexto), (y+yTexto));
         else if(contador == 3) g2.drawString("Leste", (x+xTexto), (y+yTexto));
         //sentidos
         else if(contador == 4) g2.drawString("Brilho", (x+xTexto), (y+yTexto));
         else if(contador == 5) g2.drawString("Fedor", (x+xTexto), (y+yTexto));
         else if(contador == 6) g2.drawString("Brisa", (x+xTexto), (y+yTexto));

         //informações do agente
         else if(contador == 7) g2.drawString("Ouro pego", (x+xTexto), (y+yTexto));
         else if(contador == 8) g2.drawString("Matou Wumpus", (x+xTexto), (y+yTexto));
         else if(contador == 9) g2.drawString("Tem flecha", (x+xTexto), (y+yTexto));
         else g2.drawString("Bias", (x+xTexto), (y+yTexto));

         //salvar coordenada do centro do desenho do neuronio
         coordEntrada.add(new Coordenada(x+larguraDesenho, y+(larguraDesenho/2), rede.entrada.neuronios[contador].saida));
         g2.fillOval(x, y, larguraDesenho, alturaDesenho);
         y += larguraDesenho + espacoVerticalEntreNeuronio;
      }
   }


   private void desenharOcultas(Graphics2D g2){
      x += espacoHorizontalEntreCamadas;
      y = yCamadaOculta;

      coordOcultas.clear();
      for(contador = 0; contador < rede.ocultas.length; contador++){//percorrer ocultas

         // coordOcultas.get(contador).clear();//limpar lista da camada atual pra não estourar a memória
         coordOcultas.add(new ArrayList<>());
         for(contador2 = 0; contador2 < rede.ocultas[contador].neuronios.length; contador2++){//percorrer neuronios de uma oculta
            
            //salvar coordenada do centro do desenho do neuronio
            coordOcultas.get(contador).add(new Coordenada(x+larguraDesenho, y+(larguraDesenho/2), rede.ocultas[contador].neuronios[contador2].saida));

            if(rede.ocultas[contador].neuronios[contador2].saida > 0) g2.setColor(corNeuronioAtivo);
            else g2.setColor(corNeuronioInativo);
            g2.fillOval(x, y, larguraDesenho, alturaDesenho);
            y += larguraDesenho + espacoVerticalEntreNeuronio;   
         }
         x += espacoHorizontalEntreCamadas;
         y = yCamadaOculta;
      }
   }


   private void desenharSaida(Graphics2D g2){
      y = yCamadaSaida;
      
      coordSaida.clear();
      for(contador = 0; contador < rede.saida.neuronios.length; contador++){
         if(rede.saida.neuronios[contador].saida > 0) g2.setColor(corNeuronioAtivo);
         else g2.setColor(corNeuronioInativo);

         coordSaida.add(new Coordenada(x+larguraDesenho, y+(larguraDesenho/2), rede.saida.neuronios[contador].saida));
         g2.fillOval(x, y, larguraDesenho, alturaDesenho);
         if(contador == 0) g2.drawString("Mover norte", (x+40), (y+13));
         if(contador == 1) g2.drawString("Mover sul", (x+40), (y+13));
         if(contador == 2) g2.drawString("Mover oeste", (x+40), (y+13));
         if(contador == 3) g2.drawString("Mover leste", (x+40), (y+13));
         if(contador == 4) g2.drawString("Atirar norte", (x+40), (y+13));
         if(contador == 5) g2.drawString("Atirar sul", (x+40), (y+13));
         if(contador == 6) g2.drawString("Atirar oeste", (x+40), (y+13));
         if(contador == 7) g2.drawString("Atirar leste", (x+40), (y+13));
         if(contador == 8) g2.drawString("Pegar", (x+40), (y+13));
         y += larguraDesenho + espacoVerticalEntreNeuronio; 
      }
   }
}