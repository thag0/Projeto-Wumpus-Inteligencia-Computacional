package render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class PainelGraficoBarras extends JPanel{
   final int largura = 600;
   final int altura = 400;

   //dados
   int y[];//dados 
   int x;//quantidade de elementos

   //dados do gráfico
   int maiorValor = 0;
   double escala = 0;
   int x0 = largura/2;
   int y0 = altura/2;

   public PainelGraficoBarras(){
      setVisible(true);
      setPreferredSize(new Dimension(largura, altura));
      setDoubleBuffered(true);

      setBackground(new Color(30, 30, 30));
   }


   public void desenhar(int[] y){
      if(y.length > this.largura){
         int diferencaTamanho = y.length - this.largura;
         int[] novoY = new int[this.largura];
         
         for(int i = novoY.length-1; i >= 0; i--){
            novoY[i] = y[i+diferencaTamanho];
         }
         
         this.y = novoY;
         this.x = novoY.length;
      
      }else{
         this.y = y;
         this.x = y.length;
      }      


      //pergar maior valor da lista
      int maiorValor = 0;
      int menorValor = 0;

      for(int i = 0; i < y.length; i++){
         if(y[i] > maiorValor) maiorValor = y[i];
         if(y[i] < menorValor) menorValor = y[i];
      }

      int maiorAbsoluto;
      maiorAbsoluto = Math.max(menorValor, maiorValor);

      escala = (double) ((altura-y0) / maiorAbsoluto);

      repaint();
   }


   @Override
   protected void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      graficoBarras(g2);

      g2.dispose();
   }


   private void graficoBarras(Graphics2D g2){
      int xDesenho = 0;
      int yDesenho = 0;
      int larguraDesenho = (this.largura / y.length);
      int alturaBarra = 0;

      for(int i = 0; i < x; i++){
         alturaBarra = (int) (Math.abs(y[i]) * escala);

         if(y[i] >= 0) yDesenho = y0 - alturaBarra;
         else yDesenho = y0;

         if(y[i] > 0) g2.setColor(Color.green);
         else g2.setColor(Color.red);
         g2.fillRect(xDesenho, yDesenho, larguraDesenho, alturaBarra);

         xDesenho += larguraDesenho;
      }

      //linha horizontal onde y = 0
      g2.setColor(Color.white);
      g2.drawLine(0, y0, largura, y0);
   }
}
