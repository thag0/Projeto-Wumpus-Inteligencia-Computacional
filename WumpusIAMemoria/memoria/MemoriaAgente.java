package memoria;

public class MemoriaAgente{
   public Casa[][] mapa; 
   public int ultimoX;
   public int ultimoY;
   public int tamanhoMapa;

   public MemoriaAgente(int tamanhoMapa){
      this.tamanhoMapa = tamanhoMapa;
      mapa = new Casa[tamanhoMapa][tamanhoMapa];

      for(int i = 0; i < mapa.length; i++){
         for(int j = 0; j < mapa.length; j++){
            mapa[i][j] = new Casa();
         }
      }
   }
}
