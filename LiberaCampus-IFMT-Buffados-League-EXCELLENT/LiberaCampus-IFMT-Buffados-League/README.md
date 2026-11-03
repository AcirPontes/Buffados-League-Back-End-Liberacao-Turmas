# LiberaCampus IFMT — Buffados League

**Sistema acadêmico de petições para liberação de turmas do Ensino Médio Integrado**  
IFMT — Campus Cuiabá, Cel. Octayde Jorge da Silva · Projeto acadêmico do 3º ano C de Informática.

> Este é um projeto estudantil independente. Não é um sistema oficial do IFMT, não consulta dados acadêmicos institucionais e não autentica estudantes no SUAP.

## 1. O que foi reconstruído

O projeto deixa de ser um protótipo estático que gravava informações no `localStorage` e passa a ser uma aplicação Java/Spring Boot com persistência relacional, autenticação por perfil, regras de autorização no servidor e histórico de tramitação.

A entidade central do sistema é `Petition` (Petição). A aplicação foi organizada em camadas seguindo o padrão estudado no projeto Restaurante:

```text
Navegador
   │
   ▼
Thymeleaf + HTML + CSS + jQuery + jQuery UI
   │
   ▼
Controllers (rotas HTTP e vínculo com as telas)
   │
   ▼
Services (validação e regras de negócio)
   │
   ▼
Repositories (Spring Data JPA)
   │
   ▼
Entities / Hibernate / Banco H2 ou MySQL
```

## 2. Funcionalidades

### Estudante

- Cadastro público de estudante com matrícula, nome, curso, turma e senha própria deste portal.
- Login com matrícula e senha local.
- Criação e consulta das próprias petições.
- Edição e cancelamento somente enquanto a petição estiver pendente.
- Registro da condição do solicitante: líder, vice-líder ou estudante da turma.
- Histórico de eventos e consulta do estado de cada departamento.

### Administrador

- Contas administrativas provisionadas separadamente; o formulário público nunca permite escolher o perfil `ADMIN`.
- Cada administrador possui um ou mais departamentos atribuídos.
- A fila exibe somente solicitações vinculadas aos departamentos de sua conta.
- O administrador pode iniciar a análise, aprovar ou indeferir a etapa do seu departamento.
- O indeferimento exige justificativa.
- A decisão registra o departamento, o nome do responsável e o horário.

### Tramitação por departamento

| Curso técnico | Encaminhamento automático |
|---|---|
| Informática | DCOM |
| Edificações | DINFRA |
| Agrimensura | DINFRA |
| Eventos | DGH |
| Secretariado | DGH |
| Eletrônica | DEEA |
| Eletrotécnica | DEEA |
| Disciplinas de Base Comum | DABC (adicionado quando selecionado na petição) |

O departamento principal é determinado pelo curso armazenado no cadastro da conta. O solicitante não pode escolher livremente um departamento para desviar a petição. O DABC pode ser acrescentado como área adicional quando a liberação atingir aulas de Base Comum.

Quando uma petição envolve mais de um departamento, cada área mantém sua própria manifestação. O status geral é consolidado assim:

1. **Pendente:** nenhuma área iniciou a análise.
2. **Em análise:** ao menos uma área iniciou a análise ou aprovou parcialmente.
3. **Aprovada:** todas as áreas envolvidas registraram aprovação.
4. **Indeferida:** pelo menos uma área registrou manifestação desfavorável.
5. **Cancelada:** o estudante cancelou a petição antes do início da análise.

Um indeferimento encerra a petição como um todo. As demais etapas deixam de aceitar decisões, evitando manifestações contraditórias depois de uma decisão final.

## 3. Modelo de dados

- `UserAccount`: identidade, matrícula, hash da senha, perfil, curso, turma e departamentos atribuídos aos administradores.
- `Petition`: protocolo, solicitante, turma, curso, data, intervalo de aulas, quantidade estimada, disciplinas afetadas, justificativa e status consolidado.
- `PetitionDepartment`: associação entre petição e departamento, status individual, justificativa, responsável e data da manifestação.
- `PetitionEvent`: trilha de auditoria cronológica, com título, descrição, responsável e horário.

O relacionamento `Petition 1:N PetitionDepartment` é importante: uma solicitação pode depender de manifestações de diferentes departamentos sem duplicar o protocolo ou criar petições desconectadas.

O projeto mantém a imagem do quadro de entidades original em `docs/diagrama-original-equipe.jpg`, inclui a análise da referência em `docs/ANALISE_TECNICA.md`, apresenta o MER e o fluxo em `docs/MODELO_DADOS_E_FLUXO.md` e registra as páginas institucionais consultadas em `docs/FONTES_INSTITUCIONAIS.md`.

## 4. Tecnologias

- Java 21
- Spring Boot 4.1.2
- Spring MVC
- Spring Security
- Spring Data JPA / Hibernate
- Thymeleaf
- Bean Validation
- H2 Database (persistente em arquivo, para desenvolvimento)
- MySQL Connector/J (opção de banco para configuração posterior)
- jQuery 3.7.1
- jQuery UI 1.14.2
- HTML5 e CSS3 responsivo

jQuery e jQuery UI são carregados por WebJars, portanto são resolvidos pelo Maven e servidos pela própria aplicação. A interface utiliza o Datepicker localizado em português, Autocomplete e Dialog.

## 5. Executar localmente

A aplicação usa Spring Boot 4.1.2 e exige JDK 21 ou superior. **Não é necessário instalar o Maven manualmente**: o projeto inclui `mvnw.cmd`, um inicializador para Windows que baixa automaticamente o Apache Maven 3.9.11 para o perfil do usuário, quando necessário.

### Windows — execução com dois cliques

1. Extraia o ZIP inteiro para uma pasta local.
2. Instale o JDK 21 ou superior e confirme que `java -version` funciona no Prompt de Comando.
3. Dê dois cliques em `EXECUTAR-LIBERACAMPUS.bat`.
4. Na primeira execução, mantenha a internet ativa para baixar Maven e dependências.
5. Quando o terminal informar `Started LiberaCampusApplication`, acesse <http://localhost:8080>.

Para gerar o JAR executável e iniciá-lo, use `GERAR-JAR-LIBERACAMPUS.bat`. O artefato será criado em `target/liberacampus-1.0.0.jar`.

### IntelliJ IDEA — terminal integrado

Abra a pasta raiz (onde está o `pom.xml`), abra a aba Terminal e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Para executar os testes e gerar o JAR:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

O Maven Wrapper baixa a distribuição Maven configurada em `.mvn/wrapper/maven-wrapper.properties` e a reutiliza nas execuções seguintes. A inicialização requer internet para baixar Maven e as dependências que ainda não estejam no cache local. Consulte `LEIA-PRIMEIRO.txt` para instruções resumidas.

O H2 Console fica disponível em <http://localhost:8080/h2-console> durante o desenvolvimento (perfil padrão). Credenciais padrão: usuário `sa`, senha em branco. JDBC URL: `jdbc:h2:file:./data/liberacampus`.

### Contas de demonstração

Com `APP_DEMO_ENABLED=true` (padrão no perfil acadêmico local), o sistema cria contas fictícias para apresentação:

| Perfil | Usuário/matrícula | Senha |
|---|---|---|
| Estudante | `202600001` | `Aluno@2026` |
| Administrador geral | `admin` | `Admin@2026` |
| Administrador DCOM | `admin.dcom` | `Admin@2026` |
| Administrador DINFRA | `admin.dinfra` | `Admin@2026` |
| Administrador DABC | `admin.dabc` | `Admin@2026` |
| Administrador DGH | `admin.dgh` | `Admin@2026` |
| Administrador DEEA | `admin.deea` | `Admin@2026` |

Essas contas são exclusivamente fictícias e existem para testar os fluxos. Não devem permanecer habilitadas em uma implantação real.

### Desativar demonstração

Configure `APP_DEMO_ENABLED=false`. Para criar a primeira conta administrativa por variáveis de ambiente, configure antes de iniciar:

```bash
APP_DEMO_ENABLED=false
APP_ADMIN_MATRICULA=usuario-admin
APP_ADMIN_NOME="Nome do responsável"
APP_ADMIN_SENHA="UmaSenhaForteEUnica"
APP_ADMIN_DEPARTAMENTOS=DCOM,DABC
```

Os códigos aceitos em `APP_ADMIN_DEPARTAMENTOS` são `DCOM`, `DINFRA`, `DABC`, `DGH` e `DEEA`. Separe múltiplos códigos por vírgula.

No PowerShell, defina cada variável com `$env:NOME="valor"` antes de executar o Maven.

## 6. SUAP e proteção de credenciais

A tela de login foi desenhada para usar matrícula, mas a autenticação atualmente é **local**, com senha própria do LiberaCampus e armazenamento de hash BCrypt. Ela não verifica a matrícula no SUAP nem deve receber a senha utilizada no SUAP.

Uma integração institucional verdadeira dependeria de autorização do IFMT e de um mecanismo oficial de identidade (por exemplo, OAuth2/OpenID Connect, SAML ou API institucional documentada), incluindo configuração de cliente, URLs de retorno, escopos, política de privacidade e validação de vínculo. Curso, turma e a condição de líder/vice-líder/estudante são declarados no cadastro/formulário e não são verificados contra uma base institucional nesta versão. Não se deve automatizar o login do SUAP, coletar a senha institucional ou fazer scraping do formulário de autenticação.

## 7. Horário escolar / EduPage

O formulário disponibiliza o endereço do horário do IFMT Campus Cuiabá para consulta por turma, professor e sala. A versão entregue não importa automaticamente a grade do EduPage: não foi identificada uma API institucional autorizada para sincronização. Os horários/aulas são informados pelo estudante e conferidos pelo administrador.

## 8. Perfil de produção

Para uma execução de implantação, ative `SPRING_PROFILES_ACTIVE=prod`, configure um banco relacional externo e forneça as variáveis do administrador inicial. O perfil `prod` desabilita o console H2 e os dados fictícios, ativa cache de templates e utiliza `ddl-auto=validate`. Antes disso, crie/versione o schema com Flyway ou Liquibase e confirme a configuração do provedor de identidade.

## 9. Banco MySQL (opcional)

O projeto inclui o conector MySQL. Para usá-lo, configure:

```bash
DB_URL=jdbc:mysql://localhost:3306/liberacampus?createDatabaseIfNotExist=true&serverTimezone=America/Cuiaba
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
DDL_AUTO=update
```

Em implantação institucional, prefira migrações versionadas (Flyway ou Liquibase), usuário de banco com privilégios mínimos, HTTPS, backups, gestão de segredos e `DDL_AUTO=validate` após a criação do schema.

## 10. Segurança implementada e limites

- Hash de senha BCrypt; nunca persiste senha em texto puro.
- Autorização por papel aplicada no Spring Security no servidor.
- O cadastro público sempre cria `STUDENT`.
- Operações de aprovação/indeferimento exigem `ADMIN` e validação de pertencimento ao departamento.
- Estudante consulta somente as próprias petições.
- Administrador consulta petições vinculadas aos seus departamentos.
- Alteração/exclusão por GET não é utilizada; ações de escrita são POST e protegidas por CSRF.
- Indeferimento exige justificativa.
- Histórico de movimentações e controle otimista (`@Version`) na petição.

Para produção, ainda devem ser adicionados: integração oficial de identidade, confirmação de e-mail ou validação de matrícula, política de redefinição de senha, rate limiting, auditoria imutável, backups, testes de segurança, HTTPS e adequação institucional à LGPD. O H2 Console deve ser desativado fora do ambiente local.

## 11. Testes

```bash
mvn test
```

O teste de contexto verifica o mapeamento dos sete cursos aos cinco departamentos. Testes unitários adicionais validam a consolidação de status e a substituição do encaminhamento departamental (por exemplo, incluir ou retirar o DABC enquanto a petição ainda está pendente).

## Equipe

**Buffados League · 3º ano C de Informática**

O arquivo original de equipe e responsabilidades foi preservado como referência no material recebido.
