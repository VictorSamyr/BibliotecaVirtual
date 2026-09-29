# Biblioteca Virtual

Aplicação desktop em Java (Swing + JDBC) para catalogação, consulta e recomendação de livros por gêneros literários.

Originalmente desenvolvido em **junho de 2021** como o primeiro projeto deste perfil no GitHub (durante o curso técnico no IFAL, em coautoria com [@NicolasYanB](https://github.com/NicolasYanB)), o repositório foi **refatorado em 2026** para servir como estudo prático de evolução em arquitetura de software, segurança e testabilidade em Java, utilizando apenas a biblioteca padrão do JDK.

---

## Evolução Técnica (2021 → 2026)

A versão original de 2021 continha problemas típicos de projetos iniciais: classes duplicadas na pasta `Library/` e `Library/Biblioteca/`, regras de negócio e consultas SQL acopladas diretamente aos eventos de botões Swing, vulnerabilidades de *SQL Injection* e falhas na derivação de hashes de senha.

| Dimensão | Implementação Original (2021) | Refatoração (2026) |
| :--- | :--- | :--- |
| **Arquitetura** | Classes no *default package* duplicadas em `Library/` e `Library/Biblioteca/`, misturando interface Swing e SQL. | Separação em pacotes (`domain`, `repository`, `security`, `service`, `ui`) com entidades imutáveis e inversão de dependência via interfaces. |
| **Prevenção de SQL Injection** | `UsuarioDao.getUsuarios(String sql)` e `LivroDao.getLivros(String sql)` executavam strings concatenadas (`"SELECT * FROM usuarios WHERE login = '" + login + "'"`) via `Statement`. | Consultas parametrizadas exclusivas com `PreparedStatement` em [`JdbcUsuarioRepository`](src/biblioteca/repository/JdbcUsuarioRepository.java) e [`JdbcLivroRepository`](src/biblioteca/repository/JdbcLivroRepository.java). |
| **Criptografia de Senhas** | `GeradorSHA256` aplicava SHA-256 sem *salt* e convertia o digest via `new BigInteger(1, shaByte).toString(16)`, omitindo zeros à esquerda quando o primeiro byte era `< 0x10`. | [`PasswordHasher`](src/biblioteca/security/PasswordHasher.java) utiliza `PBKDF2WithHmacSHA256` com *salt* aleatório de 128 bits (`SecureRandom`), 65.536 iterações e comparação em tempo constante (`MessageDigest.isEqual`). |
| **Ciclo de Vida de Conexões** | `Connection` aberta no construtor do DAO e mantida sem fechamento em consultas, com credenciais fixas (`postgres/password`) no código. | Uso de `try-with-resources` em todas as operações JDBC e leitura de configuração via variáveis de ambiente em [`DatabaseConfig`](src/biblioteca/repository/DatabaseConfig.java). |
| **Execução Zero-Setup** | Exigia um servidor PostgreSQL local ativo e execução manual prévia de script SQL para abrir a primeira tela. | Repositórios em memória ([`InMemoryLivroRepository`](src/biblioteca/repository/InMemoryLivroRepository.java) e [`InMemoryUsuarioRepository`](src/biblioteca/repository/InMemoryUsuarioRepository.java)) pré-carregados com o acervo padrão permitem executar e testar imediatamente apenas com o JDK. |
| **Interface e Funcionalidades** | Clique nas capas apenas imprimia no console; `TelaLivro` e `TelaAdmin` estavam incompletas; laço fixo de 10 livros lançava `IndexOutOfBoundsException` se o acervo variasse; dois caminhos de imagens no SQL divergiam dos arquivos reais. | Grade dinâmica com `JScrollPane`, busca por título/autor, filtro por gênero, recomendação baseada nos gêneros favoritos do leitor, modal de detalhes do livro ([`TelaLivroDialog`](src/biblioteca/ui/TelaLivroDialog.java)) e painel administrativo funcional ([`TelaAdminDialog`](src/biblioteca/ui/TelaAdminDialog.java)). |
| **Testes Automatizados** | Inexistentes. | Suíte de testes auto-contida em [`BibliotecaTestRunner`](test/biblioteca/BibliotecaTestRunner.java) validando criptografia, regressão do bug de SHA-256, tentativa de injeção, integridade dos ativos e regras de catálogo. |

---

## Estrutura do Projeto

```text
.
├── assets/
│   ├── covers/                 # Capas dos 10 livros do acervo inicial
│   └── icons/                  # Ícones da interface gráfica
├── sql/
│   ├── schema.sql              # DDL PostgreSQL com constraints e suporte a hashes PBKDF2
│   └── seed.sql                # Carga inicial do acervo com caminhos corrigidos
├── src/
│   └── biblioteca/
│       ├── Main.java           # Ponto de entrada (GUI Swing ou demonstração CLI)
│       ├── domain/             # Entidades imutáveis (Livro, Usuario) e enums (Genero, Sexo)
│       ├── repository/         # Interfaces e implementações In-Memory e JDBC (PostgreSQL)
│       ├── security/           # Derivação e verificação de senhas (PBKDF2 + compatibilidade SHA-256)
│       ├── service/            # Regras de autenticação, catálogo, busca e recomendação
│       └── ui/                 # Janelas e diálogos Swing desacoplados da camada de dados
└── test/
    └── biblioteca/
        └── BibliotecaTestRunner.java
```

---

## Como Executar

O projeto utiliza apenas o JDK (validado com **OpenJDK 20+** e **OpenJDK 26**) e não requer ferramentas externas de build para compilação e execução local.

### 1. Compilar o projeto e os testes

Na raiz do repositório:

```bash
javac -encoding UTF-8 -d out $(find src test -name "*.java")
```

No Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force -Path out | Out-Null
$sources = Get-ChildItem -Path src, test -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $sources
```

### 2. Executar a interface gráfica (Modo In-Memory padrão)

```bash
java -cp out biblioteca.Main
```

Contas de demonstração pré-carregadas no modo em memória:
- **Administrador:** login `admin` | senha `admin123`
- **Leitor:** login `leitor` | senha `leitor123`

### 3. Executar a demonstração em linha de comando (Headless / CLI)

```bash
java -cp out biblioteca.Main --cli-demo
```

### 4. Executar a suíte de testes automatizados

```bash
java -cp out biblioteca.BibliotecaTestRunner
```

### 5. Executar com PostgreSQL (Modo JDBC opcional)

Caso deseje persistir os dados em um banco PostgreSQL real (adicionando o driver JDBC do PostgreSQL ao *classpath*):

1. Execute os scripts [`sql/schema.sql`](sql/schema.sql) e [`sql/seed.sql`](sql/seed.sql) no seu banco.
2. Defina as variáveis de ambiente e inicie a aplicação:

```bash
export BIBLIOTECA_STORAGE=jdbc
export DB_URL="jdbc:postgresql://localhost:5432/biblioteca"
export DB_USER="postgres"
export DB_PASSWORD="sua_senha"
java -cp "out:postgresql.jar" biblioteca.Main
```

---

## Limitações Atuais

- No modo padrão (`memory`), os livros e usuários criados durante a sessão ficam em memória e são reiniciados ao fechar a aplicação.
- A recomendação literária utiliza filtragem direta baseada na interseção com o conjunto `generosPreferidos` do usuário, sem ponderação por histórico de leitura ou avaliações.
- Não há controle de empréstimos, exemplares disponíveis ou histórico de leituras concluídas.
- A interface gráfica mantém o estilo visual clássico em Swing de 2021 para preservar a identidade histórica do projeto.

---

## Roadmap

- Persistência local em arquivo (JSON ou SQLite embarcado) para reter alterações entre sessões sem exigir servidor PostgreSQL externo;
- Registro de leituras concluídas, avaliações (1 a 5 estrelas) e ordenação das recomendações por relevância;
- Controle de acesso baseado em papel (`isAdmin()`) restringindo ações de escrita no painel administrativo apenas a administradores;
- Migração opcional para gerenciamento via Maven/Gradle com execução de testes em pipeline CI (GitHub Actions).

---

## Licença

Distribuído sob a licença MIT. Consulte o arquivo [`LICENSE`](LICENSE) para mais informações.
