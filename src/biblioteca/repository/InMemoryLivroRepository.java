package biblioteca.repository;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositório em memória pré-carregado com os 10 livros do acervo original de 2021.
 * Permite executar e validar a aplicação sem depender de uma instância ativa do PostgreSQL.
 */
public final class InMemoryLivroRepository implements LivroRepository {
    private final List<Livro> livros = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public InMemoryLivroRepository() {
        this(true);
    }

    public InMemoryLivroRepository(boolean carregarAcervoPadrao) {
        if (carregarAcervoPadrao) {
            carregarSeedPadrao();
        }
    }

    @Override
    public synchronized Livro salvar(Livro livro) {
        int id = livro.getId() > 0 ? livro.getId() : sequence.incrementAndGet();
        sequence.updateAndGet(atual -> Math.max(atual, id));
        Livro persistido = livro.withId(id);
        livros.removeIf(item -> item.getId() == id);
        livros.add(persistido);
        return persistido;
    }

    @Override
    public synchronized List<Livro> listarTodos() {
        return List.copyOf(livros);
    }

    @Override
    public synchronized Optional<Livro> buscarPorId(int id) {
        return livros.stream().filter(l -> l.getId() == id).findFirst();
    }

    @Override
    public synchronized List<Livro> buscarPorGenero(Genero genero) {
        if (genero == null) {
            return listarTodos();
        }
        return livros.stream()
                .filter(l -> l.getGenero() == genero)
                .toList();
    }

    @Override
    public synchronized List<Livro> buscarPorTermo(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        String q = termo.trim().toLowerCase(Locale.ROOT);
        return livros.stream()
                .filter(l -> l.getTitulo().toLowerCase(Locale.ROOT).contains(q)
                        || l.getAutor().toLowerCase(Locale.ROOT).contains(q)
                        || l.getGenero().getRotulo().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    @Override
    public synchronized boolean removerPorId(int id) {
        return livros.removeIf(l -> l.getId() == id);
    }

    private void carregarSeedPadrao() {
        salvar(new Livro(
                0,
                "A culpa é das estrelas",
                "John Green",
                Genero.ROMANCE,
                2012,
                "Hazel é uma paciente terminal. Ainda que, por um milagre da medicina, seu tumor tenha encolhido bastante — o que lhe dá a promessa de viver mais alguns anos —, o último capítulo de sua história foi escrito no momento do diagnóstico. Mas em todo bom enredo há uma reviravolta, e a de Hazel se chama Augustus Waters, um garoto bonito que certo dia aparece no Grupo de Apoio a Crianças com Câncer. Juntos, os dois vão preencher o pequeno infinito das páginas em branco de suas vidas.",
                "assets/covers/ACulpaEDasEstrelas.jpg"
        ));
        salvar(new Livro(
                0,
                "A Queda do Morcego - Volume 1",
                "DC Comics",
                Genero.HISTORIA_EM_QUADRINHOS,
                1993,
                "Os inimigos mais mortais do Cavaleiro das Trevas escaparam do Asilo Arkham! Coringa, Duas-Caras, Chapeleiro Louco, Charada, Hera Venenosa, Espantalho, Crocodilo, Vaga-Lume e Zsasz… Um por um, Batman deve enfrentá-los em um combate perigoso. Mas, escondido no meio desse caos, está a ameaça mais perigosa de todas: Bane!",
                "assets/covers/AQuedaDoMorcegoV1.jpg"
        ));
        salvar(new Livro(
                0,
                "Blade Runner",
                "Philip K. Dick",
                Genero.FICCAO_CIENTIFICA,
                1968,
                "Rick Deckard é um caçador de recompensas, vivendo em uma San Francisco decadente, coberta pela poeira radioativa que dizimou inúmeras espécies de animais e plantas. Para melhorar seu padrão de vida, Deckard precisa perseguir e aposentar seis androides foragidos que se passam por humanos.",
                "assets/covers/BladeRunner.jpg"
        ));
        salvar(new Livro(
                0,
                "Como Fazer Amigos e Influenciar Pessoas",
                "Dale Carnegie",
                Genero.AUTO_AJUDA,
                1936,
                "O guia clássico e definitivo para relacionar-se com as pessoas. Mais de setenta anos depois de sua primeira edição e com mais de 50 milhões de exemplares vendidos, a obra apresenta técnicas e princípios fundamentais para comunicação interpessoal e liderança.",
                "assets/covers/ComoFazerAmigosEInfluenciarPessoas.jpg"
        ));
        salvar(new Livro(
                0,
                "Crepúsculo",
                "Stephenie Meyer",
                Genero.ROMANCE,
                2005,
                "Isabella Swan chega à nublada e chuvosa cidadezinha de Forks — último lugar onde gostaria de viver. Tenta se adaptar à vida provinciana e se habituar a morar com o pai. Em seu destino está Edward Cullen: belo, misterioso e guardião de um segredo sobrenatural.",
                "assets/covers/Crepusculo.jpg"
        ));
        salvar(new Livro(
                0,
                "Invincible: Ultimate Collection 3",
                "Robert Kirkman / Image Comics",
                Genero.HISTORIA_EM_QUADRINHOS,
                2007,
                "Este volume coleta a batalha intensa de Invincible com o vilão Angstrom Levy, seu reencontro com seu pai distante e a batalha decisiva contra os Viltrumites.",
                "assets/covers/InvincibleUltimateCollectionV3.jpg"
        ));
        salvar(new Livro(
                0,
                "It: a Coisa",
                "Stephen King",
                Genero.SUSPENSE,
                1986,
                "Durante as férias escolares de 1958, em Derry, pacata cidadezinha do Maine, Bill, Richie, Stan, Mike, Eddie, Ben e Beverly aprenderam o real sentido da amizade, da confiança e do medo ao enfrentarem pela primeira vez a Coisa. Quase trinta anos depois, os amigos voltam a se encontrar.",
                "assets/covers/ItACoisa.jpg"
        ));
        salvar(new Livro(
                0,
                "Neuromancer",
                "William Gibson",
                Genero.FICCAO_CIENTIFICA,
                1984,
                "No futuro, existe a matriz: uma alucinação consensual digital na qual a humanidade se conecta ao ciberespaço. Case era um dos melhores cowboys de console até tentar enganar seus patrões. Nos subúrbios de Tóquio e Chiba City, ele e Molly embarcam em uma missão cheia de mistérios.",
                "assets/covers/Neuromancer.png"
        ));
        salvar(new Livro(
                0,
                "O Horror de Dunwich",
                "H.P. Lovecraft",
                Genero.SUSPENSE,
                1929,
                "Em 1913, no vilarejo de Dunwich, Lavinia Whateley dá à luz Wilbur, uma criança de desenvolvimento incomum. Em seus estudos ao lado do avô, Wilbur recorre ao Necronomicon para dar continuidade a um ritual familiar que atinge seu ápice com o Horror de Dunwich.",
                "assets/covers/OHorrorDeDunwich.jpg"
        ));
        salvar(new Livro(
                0,
                "O Poder do Hábito",
                "Charles Duhigg",
                Genero.AUTO_AJUDA,
                2012,
                "Com base em centenas de artigos acadêmicos e entrevistas com mais de 300 cientistas e executivos, Charles Duhigg mostra como os hábitos funcionam no cérebro humano e como transformá-los impacta a saúde, a produtividade e o sucesso organizacional.",
                "assets/covers/OPoderDoHabito.jpg"
        ));
    }
}
