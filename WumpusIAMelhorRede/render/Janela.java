package render;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import entidade.Agente;
import treino.TreinoGenetico;

public class Janela extends JFrame{

   public Painel painel = new Painel();

   public Janela(){
      try{
         BufferedImage icone = ImageIO.read(new File("./imagens/inteligencia-artificial.png"));
         setIconImage(icone);
      }catch(Exception e){}
      
      setTitle("Treino Genético");
      add(painel);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      pack();
      setResizable(false);
      setLocationRelativeTo(null);
   }


   public void desenhar(Agente agente, TreinoGenetico treinoGenetico, long redesQueGanharam, int metodoEvolucao){
      painel.desenhar(agente, treinoGenetico, redesQueGanharam, metodoEvolucao);
   }
}
