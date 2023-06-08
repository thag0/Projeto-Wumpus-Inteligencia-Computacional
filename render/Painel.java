package render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entidade.Agente;

public class Painel extends JPanel{
   final int largura = 620;
   final int altura = 540;
   public Agente melhorAgente;
   Graphics2D g2;

   //desenho
   int contador = 0;
   int contador2 = 0;
   int x0 = 110;
   int y0 = 40;
   int x = 0;
   int y = 0;
   int larguraDesenho = 18;
   int alturaDesenho = 18;

   //informações
   double mediaPesos = 0;
   double melhorFitness = 0;
   int geracoesStagnadas = 0;

   int r = 100;
   int g = 150;
   int b = 200;
   int alpha = 255;
   Color corNeuronioAtivo = new Color(r, g, b, alpha);
   Color corNeuronioInativo = new Color(50, 50, 50, alpha);

   public Painel(){
      setBackground(Color.BLACK);
      setPreferredSize(new Dimension(largura, altura));
      setFocusable(true);
      setDoubleBuffered(true);
      setEnabled(true);
      setVisible(true);
   }


   public void desenhar(Agente agente, double pesos, double melhorFitness, int geracoesStagnadas){
      melhorAgente = agente;
      mediaPesos = pesos;
      this.melhorFitness = melhorFitness;
      this.geracoesStagnadas = geracoesStagnadas;
      repaint();
   }

   @Override
   protected void paintComponent(Graphics g){
      super.paintComponent(g);
      g2 = (Graphics2D) g;

      g2.setColor(corNeuronioAtivo);
      g2.drawString(("novo peso médio:  " + (float)(mediaPesos)), 10, 15);

      x = x0;
      y = y0 + alturaDesenho + (altura/2) - (larguraDesenho*melhorAgente.rede.entrada.neuronios.length);
      desenharCamadaEntrada(g2);
      

      x += (larguraDesenho*2);
      y = y0 + (alturaDesenho) + (altura/2) - (larguraDesenho * melhorAgente.rede.ocultas[0].neuronios.length);
      desenharOcultas(g2);

      x = x0 + ((larguraDesenho * 2 * melhorAgente.rede.qtdCamadasOcultas)) + (larguraDesenho * 2);
      y = y0 + alturaDesenho + (altura/2) - (larguraDesenho*melhorAgente.rede.saida.neuronios.length);
      desenharSaida(g2);

      g2.setColor(corNeuronioAtivo);
      x = x0 + 190;
      y = 15;
      g2.drawString(("Gerações stagnadas: " + this.geracoesStagnadas), x, y);

      x += 150;
      g2.drawString(("Último melhor fitness: " + (int)(this.melhorFitness)), x, y);

      g2.dispose();
   }


   private void desenharCamadaEntrada(Graphics2D g2){
      for(contador = 0; contador < melhorAgente.rede.entrada.neuronios.length; contador++){
         
         if(melhorAgente.rede.entrada.neuronios[contador].saida > 0) g2.setColor(corNeuronioAtivo);
         else g2.setColor(corNeuronioInativo);
         
         //direções
         int xTexto = -90;
         int yTexto = 14;
         if(contador == 0) g2.drawString("Norte", (x+xTexto), (y+yTexto));
         if(contador == 1) g2.drawString("Sul", (x+xTexto), (y+yTexto));
         if(contador == 2) g2.drawString("Oeste", (x+xTexto), (y+yTexto));
         if(contador == 3) g2.drawString("Leste", (x+xTexto), (y+yTexto));
         //sentidos
         if(contador == 4) g2.drawString("Brilho", (x+xTexto), (y+yTexto));
         if(contador == 5) g2.drawString("Fedor", (x+xTexto), (y+yTexto));
         if(contador == 6) g2.drawString("Brisa", (x+xTexto), (y+yTexto));

         //informações do agente
         if(contador == 7) g2.drawString("Ouro pego", (x+xTexto), (y+yTexto));
         if(contador == 8) g2.drawString("Matou Wumpus", (x+xTexto), (y+yTexto));
         if(contador == 9) g2.drawString("Tem flecha", (x+xTexto), (y+yTexto));

         g2.fillOval(x, y, larguraDesenho, alturaDesenho);
         y += larguraDesenho + 10;
      }
   }


   private void desenharOcultas(Graphics2D g2){
      for(contador = 0; contador < melhorAgente.rede.ocultas.length; contador++){
         for(contador2 = 0; contador2 < melhorAgente.rede.ocultas[contador].neuronios.length; contador2++){

            if(melhorAgente.rede.ocultas[contador].neuronios[contador2].saida > 0) g2.setColor(corNeuronioAtivo);
            else g2.setColor(corNeuronioInativo); 
            
            g2.fillOval(x, y, larguraDesenho, alturaDesenho);
            y += larguraDesenho + 10;   
         }
         x += (larguraDesenho*2);
         y = y0 + alturaDesenho + (altura/2) - (larguraDesenho*melhorAgente.rede.ocultas[0].neuronios.length);
      }
   }


   private void desenharSaida(Graphics2D g2){
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
         y += larguraDesenho + 10; 
      }
   }
}
