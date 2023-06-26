# Treinamento do agente de aprendizagem com uso de redes neurais artificiais

![gifTreinamentoRedes](https://github.com/thag0/Projeto-Wumpus-Inteligencia-Computacional/assets/91092364/be02580f-317b-48e2-a4bc-efb38afcea5c)

Diretório destinado ao código fonte do algoritmo de treinamento para os agentes do mundo de wumpus

A implementação do agente de aprendizagem foi feita usando redes neurais artificiais, um algortimo que se assemelha ao funcionamento do cérebro humano. Cada rede é composta por camadas 
e cada camada é composta por neurônios artificiais, o modelo de rede neural criado é baseado no processo de *feedforward*, então alimentamos a rede com os dados do ambiente que foram: 
posições norte, sul, leste e oeste disponíveis, sensações na casa atual tais como fedor, brisa e brilho, e alguns atributos do próprio agente que a rede controla como a informação se ele 
pegou o ouro, matou o wumpus e se ainda tem flecha.

O algoritmo de treino apenas replica o melhor agente de cada geração e faz algumas alterações na rede dele. Os passos mais detalhados são comentados a seguir:

- Criar vários agentes com redes neurais aleatórias;
- Quando todos os agentes morrerem, calculamos o agente com maior pontuação;
- Clonamos a rede neural do melhor agente para ela ser usada na próxima geração;
- Os indivíduos da nova geração irão receber a rede neural do último melhor agente, mas com pequenas modificações aleatórias com o objetivo de gerar um agente ainda melhor;
- Repetimos o processo até conseguir um agente que ganhe o jogo.

# Mais informações sobre a rede
Alguns dados mais técnicos da melhor configuração que conseguimos até agora foram:
- Dez neurônios na camada de entrada para os dados do ambiente e do agente;
- Três camadas ocultas com 9 neurônios cada;
- Nove neurônios na camada de saída para cada ação que o agente pode executar;
- Função de ativação usada nas camadas ocultas foi a tangente hiperbólica;
- Função de ativação usada na camada de saída foi a Argmax;
- O valor dos pesos aleatórios da primeira geração de redes varia entre -100 e 100;
- Bias como um neurônio adicional em cada camada, com exceção da camada de saída;
