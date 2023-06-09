package render;

import javax.swing.JFrame;

import entidade.Agente;

public class Janela extends JFrame{

   public Painel painel = new Painel();

   public Janela(){
      setTitle("Melhor rede");
      add(painel);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      pack();
      setResizable(false);
      setLocationRelativeTo(null);
   }


   public void desenhar(Agente agente){
      painel.desenhar(agente);
   }
}
