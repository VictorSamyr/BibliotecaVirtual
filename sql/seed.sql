-- Carga inicial (Seed) dos 10 livros clássicos do acervo original de 2021.
-- Corrige os caminhos de imagem divergentes do script original (AQuedaDoMorcegoV1.jpg e InvincibleUltimateCollectionV3.jpg)
-- e unifica INSERT + UPDATE em instruções atômicas.

INSERT INTO livros (titulo, genero, autor, ano_lancamento, imagem, sinopse) VALUES
(
    'A culpa é das estrelas',
    'Romance',
    'John Green',
    2012,
    'assets/covers/ACulpaEDasEstrelas.jpg',
    'Hazel é uma paciente terminal. Ainda que, por um milagre da medicina, seu tumor tenha encolhido bastante — o que lhe dá a promessa de viver mais alguns anos —, o último capítulo de sua história foi escrito no momento do diagnóstico. Mas em todo bom enredo há uma reviravolta, e a de Hazel se chama Augustus Waters, um garoto bonito que certo dia aparece no Grupo de Apoio a Crianças com Câncer. Juntos, os dois vão preencher o pequeno infinito das páginas em branco de suas vidas.'
),
(
    'A Queda do Morcego - Volume 1',
    'História em quadrinhos',
    'DC Comics',
    1993,
    'assets/covers/AQuedaDoMorcegoV1.jpg',
    'Os inimigos mais mortais do Cavaleiro das Trevas escaparam do Asilo Arkham! Coringa, Duas-Caras, Chapeleiro Louco, Charada, Hera Venenosa, Espantalho, Crocodilo, Vaga-Lume e Zsasz… Um por um, Batman deve enfrentá-los em um combate perigoso. Mas, escondido no meio desse caos, está a ameaça mais perigosa de todas: Bane!'
),
(
    'Blade Runner',
    'Ficção científica',
    'Philip K. Dick',
    1968,
    'assets/covers/BladeRunner.jpg',
    'Rick Deckard é um caçador de recompensas, vivendo em uma San Francisco decadente, coberta pela poeira radioativa que dizimou inúmeras espécies de animais e plantas. Um novo trabalho pode ser o ponto de virada para melhorar seu padrão de vida e realizar seu sonho de consumo: uma ovelha de verdade, para substituir a réplica elétrica que ele cria em casa. Para isso, Deckard precisa perseguir e aposentar seis androides que estão foragidos, se passando por humanos.'
),
(
    'Como Fazer Amigos e Influenciar Pessoas',
    'Auto-Ajuda',
    'Dale Carnegie',
    1936,
    'assets/covers/ComoFazerAmigosEInfluenciarPessoas.jpg',
    'O guia clássico e definitivo para relacionar-se com as pessoas. Mais de setenta anos depois de sua primeira edição e após mais de 50 milhões de exemplares vendidos, a obra segue sendo uma das principais referências sobre relacionamentos no âmbito profissional ou pessoal, fornecendo princípios práticos de comunicação e liderança.'
),
(
    'Crepúsculo',
    'Romance',
    'Stephenie Meyer',
    2005,
    'assets/covers/Crepusculo.jpg',
    'Isabella Swan chega à nublada e chuvosa cidadezinha de Forks — último lugar onde gostaria de viver. Tenta se adaptar à vida provinciana na qual aparentemente todos se conhecem e se habituar a morar com um pai com quem nunca conviveu. Em seu destino está Edward Cullen: belo, misterioso e guardião de um segredo sobrenatural.'
),
(
    'Invincible: Ultimate Collection 3',
    'História em quadrinhos',
    'Robert Kirkman / Image Comics',
    2007,
    'assets/covers/InvincibleUltimateCollectionV3.jpg',
    'Este volume coleta a batalha intensa de Invincible contra o vilão Angstrom Levy, seu reencontro com seu pai distante e o confronto decisivo contra os Viltrumites.'
),
(
    'It: a Coisa',
    'Suspense',
    'Stephen King',
    1986,
    'assets/covers/ItACoisa.jpg',
    'Durante as férias escolares de 1958, em Derry, pacata cidadezinha do Maine, Bill, Richie, Stan, Mike, Eddie, Ben e Beverly aprenderam o real sentido da amizade, do amor, da confiança e do medo. Naquele verão, eles enfrentaram pela primeira vez a Coisa, um ser sobrenatural que deixou marcas profundas na cidade. Quase trinta anos depois, os amigos voltam a se encontrar para cumprir uma promessa.'
),
(
    'Neuromancer',
    'Ficção científica',
    'William Gibson',
    1984,
    'assets/covers/Neuromancer.png',
    'No futuro, existe a matriz: uma alucinação consensual digital na qual a humanidade se conecta ao ciberespaço. Case era um dos melhores cowboys de console até tentar enganar seus patrões, que danificaram seu sistema nervoso. Nos subúrbios de Chiba City, ele é recrutado por Molly e Armitage para uma última missão impossível.'
),
(
    'O Horror de Dunwich',
    'Suspense',
    'H.P. Lovecraft',
    1929,
    'assets/covers/OHorrorDeDunwich.jpg',
    'Em 1913, no isolado vilarejo de Dunwich, Lavinia Whateley dá à luz Wilbur, uma criança de crescimento anormal. Em seus estudos ao lado do avô, Wilbur recorre ao Necronomicon para dar continuidade a um ritual ancestral que culmina nos eventos conhecidos como o Horror de Dunwich.'
),
(
    'O Poder do Hábito',
    'Auto-Ajuda',
    'Charles Duhigg',
    2012,
    'assets/covers/OPoderDoHabito.jpg',
    'Com base em centenas de estudos acadêmicos e entrevistas com cientistas e executivos, Charles Duhigg explora como o loop do hábito (deixa, rotina e recompensa) funciona no cérebro humano e como a transformação de hábitos impacta a saúde, a produtividade e as organizações.'
);
