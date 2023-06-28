# Treinamento do agente de aprendizagem com uso de Redes Neurais Artificiais

![gifTreinamentoRedes](https://github.com/thag0/Projeto-Wumpus-Inteligencia-Computacional/assets/91092364/83923273-7f41-4aba-901d-ef64dd68a352)

Diretório destinado ao código fonte do algoritmo de treinamento para os agentes do mundo de wumpus

A implementação do agente de aprendizagem foi feita usando redes neurais artificiais, um algortimo que se assemelha ao funcionamento do cérebro humano. Cada rede é composta por camadas e cada camada é composta por neurônios artificiais, o modelo de rede neural criado é baseado no processo de *feedforward*, então alimentamos a rede com os dados do ambiente que foram julgados necessários para e rede aprender, são eles:
 - Posições norte, sul, leste e oeste disponíveis;
 - Sensações na casa atual tais como fedor, brisa e brilho;
 - Alguns atributos do próprio agente que a rede controla como a informação se ele pegou o ouro, matou o wumpus e se ainda tem flecha.

O algoritmo de treino apenas replica o melhor agente de cada geração e faz algumas alterações na rede dele. Os passos mais detalhados são comentados a seguir:
- Criar vários agentes com redes neurais aleatórias;
- Após todos os agentes terminarem de jogar, calculamos as pontuações para cada agente;
- Selecionamos os melhores indivíduos daquela geração, baseado na sua pontuação(fitness);
- De acordo com o método evolutivo utilizado, criamos uma nova população para jogar a partida;
- Repetimos o processo até conseguir um agente que ganhe o jogo.

# Detalhes do algoritmo genético usado
No problema prospoto, utlizamos dois método de evolução para as gerações, mutações e crossover.
Na mutação, nós selecionamos o melhor indivíduo da geração, clonamos a rede neural dele para os seus filhos e cada filho sofrerá uma mutação em casa peso da rede neural dele.
No método de crossover são selecionados os dois melhores agentes da geração

# Cálculo da pontuação do agente
Para evitar comportamentos inesperados, o agente possui algumas variáveis na hora de calcular a sua pontuação(fitness).
- A pontuação do agente é aumentada caso ele explore uma casa que não tinha ido anteriormente, caso ele ande muitas vezes na mesma casa, esse comportamento
  resultará numa penalidade na pontuação dele. O mesmo vale para as vezes que ele tentar andar para fora dos limites do mapa, é calculado quantas vezes ele "bateu
  na parede" e também aplicaremos uma penalização.
- Quando o agente atirar, iremos verificar quatro condições: se o agente acertou o Wumpus, se o gente tentou atirar sem ter flecha, se o agente atirou numa parede(fora
  dos limites do mapa) e se o agente atirou numa casa qualquer sem o montro.
- Caso o agente decida pegar numa casa que contém o ouro, sua pontuação aumentará drasticamente, também aplicaremos uma penalidade caso o agente decida pegar o ouro
  numa casa que não tem o ouro.
- Por último, quando o agente já possuir o ouro e estiver de volta na casa de origem, sua pontuação aumentará drasticamente.

A partir desses critérios, nós tentaremos moldar um comportamento inteligente para o agente, fazendo ele explorar mais o mapa, evitar bater nas paredes, andar menos em círculos, atirar somente quando necessário e pegar o ouro somente quando necessário. Com isso forçamos que o algoritmo de treino replique os genes dos agentes que tiveram as 
melhores decisões baseadas no objetivo imposto.

# Mais informações sobre a rede
Alguns dados mais técnicos da melhor configuração que conseguimos até agora foram:
- Dez neurônios na camada de entrada para os dados do ambiente e do agente;
- Três camadas ocultas com 9 neurônios cada;
- Nove neurônios na camada de saída para cada ação que o agente pode executar;
- Função de ativação usada nas camadas ocultas foi a tangente hiperbólica;
- Função de ativação usada na camada de saída foi a Argmax;
- O valor dos pesos aleatórios da primeira geração de redes varia entre -100 e 100;
- Bias como um neurônio adicional em cada camada, com exceção da camada de saída;
