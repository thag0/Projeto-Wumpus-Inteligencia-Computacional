package render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JPanel;

import entidade.Agente;
import treino.TreinoGenetico;

public class Painel extends JPanel{
   final int largura = 510;
   final int altura = 420;
   public Agente melhorAgente;
   Graphics2D g2;

   //desenho
   int contador = 0;
   int contador2 = 0;
   int x0 = 120;//posição x base de desenho da rede
   int y0 = 50;//posição y base de desenho da rede
   int x = 0;
   int y = 0;
   int yCamadaEntrada = 0;
   int yCamadaOculta = 0;
   int yCamadaSaida = 0;
   int larguraDesenho = 22;
   int alturaDesenho = larguraDesenho;
   int espacoVerticalEntreNeuronio = 8;
   int espacoHorizontalEntreCamadas = (larguraDesenho * 2);
   String texto = "";

   //informações
   int geracaoAtual = 0;
   double mediaPesos = 0;
   double melhorFitness = 0;
   double mediaFitness = 0;
   int geracoesStagnadas = 0;
   long redesQueGanharam = 0;

   File pastaRedes = new File("./melhores-redes/");

   int r = 150;
   int g = 110;
   int b = 180;
   Color corNeuronioAtivo = new Color(r, g, b);
   Color corNeuronioInativo = new Color((int)(r * 0.35), (int)(g * 0.35), (int)(b * 0.35));

   public Painel(){
      setBackground(Color.BLACK);
      setPreferredSize(new Dimension(largura, altura));
      setFocusable(true);
      setDoubleBuffered(true);
      setEnabled(true);
      setVisible(true);
   }


   public void desenhar(Agente agente, TreinoGenetico treinoGenetico, long redesQueGanharam){
      //melhor agente
      melhorAgente = agente;
      
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
      g2.setFont(getFont().deriveFont(14f));

      //primeira linha
      x = 10;
      y = 20;
      texto = "Geração atual: " + this.geracaoAtual;
      g2.drawString(texto, x, y);

      x += 200;
      texto = "Gerações stagnadas: " + this.geracoesStagnadas;
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
      texto = "Redes que ganharam: " + redesQueGanharam;
      g2.drawString(texto, x, y);

      x += 200;
      texto = "Diversidade de redes: " + pastaRedes.listFiles().length + " redes salvas";
      g2.drawString(texto, x, y);

      //desenhar camadas
      desenharCamadaEntrada(g2);    
      desenharOcultas(g2);
      desenharSaida(g2);

      g2.dispose();
   }


   private void desenharCamadaEntrada(Graphics2D g2){
      x = x0;
      y = yCamadaEntrada;
      
      for(contador = 0; contador < melhorAgente.rede.entrada.neuronios.length; contador++){
         
         if(melhorAgente.rede.entrada.neuronios[contador].saida > 0) g2.setColor(corNeuronioAtivo);
         else g2.setColor(corNeuronioInativo);
         
         //direções
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

         g2.fillOval(x, y, larguraDesenho, alturaDesenho);
         y += larguraDesenho + espacoVerticalEntreNeuronio;
      }
   }


   private void desenharOcultas(Graphics2D g2){
      x += espacoHorizontalEntreCamadas;
      y = yCamadaOculta;

      for(contador = 0; contador < melhorAgente.rede.ocultas.length; contador++){
         for(contador2 = 0; contador2 < melhorAgente.rede.ocultas[contador].neuronios.length; contador2++){

            if(melhorAgente.rede.ocultas[contador].neuronios[contador2].saida > 0) g2.setColor(corNeuronioAtivo);
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
      
      for(contador = 0; contador < melhorAgente.rede.saida.neuronios.length; contador++){
         if(melhorAgente.rede.saida.neuronios[contador].saida > 0) g2.setColor(corNeuronioAtivo);
         else g2.setColor(corNeuronioInativo);

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
