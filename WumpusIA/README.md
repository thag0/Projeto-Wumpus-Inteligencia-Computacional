# Agente reativo com movimentos aleatórios

![gifAgenteAleatorioJogando](https://github.com/thag0/Projeto-Wumpus-Inteligencia-Computacional/assets/91092364/2b6fd475-bd6c-4be7-b184-bb84cf8cdaa6)

Diretório destinado para a etapa 1 e 2 do projeto de desenvolvimento.

Aqui a geração do mapa é completamente aleatória, o tamanho do mapa é definido pelo usuário mas não pode ser menor que 3, sua geração é de forma simétrica, o que significa que o tamanho corresponde a quantidade de linhas e colunas igualmente. A quantidade de elementos depende do tamanho do mapa, como a quantidade de poços e a quantidade de wumpus. Cada mapa possui apenas um ouro. O mapa também possui alguns tratamentos em casos específicos que levem à um jogo impossível para o agente jogar.

O comportamento do agente é quase totalmente aleatório, com exceção na hora de pegar o ouro e atirar a flecha. Nesses casos o agente sempre pega o ouro quando sente brilho e
sempre atira, mas em uma direção aleatória, quando sente fedor.

O programa mostra a informação do mapa que o agente está jogando e vários status sobre as simulações dele.
