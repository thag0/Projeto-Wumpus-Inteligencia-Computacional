# Agente baseado em memória

![gifAgenteMemoria](https://github.com/thag0/Projeto-Wumpus-Inteligencia-Computacional/assets/91092364/562448e3-bd67-48de-9d2c-06af3656bf22)

Diretório destinado ao código fonte do agente com uso de memória.

Arquivos do projeto da etapa 3.

Aqui o agente já começa a ter um comportamento mais inteligente, ele tem a capacidade de mapear as casas ao redor tendo a sensação da casa que ele está atualmente. Cada casa que o agente chegar e não morrer será mapeada por ele como S (segura), a partir disso ele começa a criar um "mapa mental" do ambiente que joga. O agente irá andar e basear suas deduções dos elementos do mapa (poços, Wumpus e ouro) baseado na sensação sentida na casa atual dele, o mesmo mapeará todas as casa ao redor (com exceção das casas já mapeadas como segura) de acordo com a sensação que ele sentir.

Com base nisso, temos diversos tratamentos específicos de acordo com as sensações sentidas. Mais especificamente quando o agente for atirar, foram tratados diversos casos variando desde quais sensações ele está sentido, bem como a posição relativa dele no mapa (canto, parede, centro).

Apesar de termos várias idéias de implementação e exaustivamente tentar lidar com muitos casos isolados, não conseguimos fazer o agente ganhar nenhuma partida mesmo cuidando com muitos casos. O agente nessa etapa tem um comportamento "medroso", anda muito nas casas seguras, e evita arriscar quando precisa sair da sua zona de conforto. Resumidamente não conseguimos fazer uma boa generalização da ideia de um comportamento mais inteligente para o agente.
