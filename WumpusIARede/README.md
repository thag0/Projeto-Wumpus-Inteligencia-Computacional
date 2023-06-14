# Treinamento do agente de aprendizagem com uso de redes neurais artificiais

Diretório destinado ao código fonte do algoritmo de treinamento para os agentes do mundo de wumpus

- Criar vários agentes com redes neurais aleatórias
- Quando todos os agentes morrerem, calculamos o agente com maior pontuação
- Clonamos a rede neural do melhor agente para ela ser usada na próxima geração
- Os indivíduos da nova geração irão receber a rede neural do último melhor agente, mas com pequenas modificações aleatórias com o objetivo de gerar um agente ainda melhor
- Repetimos o processo até conseguir um agente que ganhe o jogo
