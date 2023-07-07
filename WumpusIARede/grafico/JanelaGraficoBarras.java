package grafico;

import javax.swing.JFrame;

public class JanelaGraficoBarras extends JFrame{
   public PainelGraficoBarras painel;
   
   public JanelaGraficoBarras(){
      painel = new PainelGraficoBarras();
      add(painel);
      setTitle("Gráfico de barras");
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      setResizable(false);
   }


   public void desenhar(int[] y){
      painel.desenhar(y);
   }
}
