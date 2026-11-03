# Análise técnica e decisões de arquitetura

## 1. O projeto Restaurante como referência

O Restaurante apresenta a sequência didática clássica de uma aplicação Spring: `ProdutoController` recebe as requisições, `ProdutoService` executa as operações, `ProdutoRepository` estende `JpaRepository`, `Produto` representa a entidade e `ProdutoDTO` transporta os dados para a aplicação. As telas Thymeleaf apresentam formulário de cadastro/edição e listagem.

Essa separação é o principal conceito a ser reaproveitado. O novo projeto conserva o fluxo Controller → Service → Repository → JPA e Thymeleaf, mas substitui a entidade Produto por um domínio que representa a tramitação de petições.

### Pontos positivos preservados

- Camadas de responsabilidade claramente identificáveis.
- Uso de Spring Data JPA para operações CRUD.
- Integração servidor/tela por Thymeleaf.
- Entidade Java convertida pelo Hibernate para persistência relacional.
- Modelo simples o suficiente para ser explicado e defendido em sala.

### Problemas encontrados na referência

1. `ProdutoController` usa `@Autowired` em atributo. A versão reconstruída usa injeção por construtor.
2. `ProdutoService` também injeta Repository em atributo. A nova implementação usa dependências `final` e construtores.
3. A rota `GET /produtos/excluir/{id}` modifica dados. No novo sistema, cancelamentos e decisões são `POST`, com CSRF.
4. O `ProdutoService` lança `RuntimeException` genérica para registro inexistente. O novo serviço utiliza erros de regra de negócio e de autorização mais específicos.
5. Não há autenticação ou autorização no Restaurante. O LiberaCampus utiliza Spring Security e perfis.
6. Não há Bean Validation no DTO de referência. Os formulários do novo sistema aplicam validação no servidor.
7. A classe `ProdutoDTO` está no pacote genérico `dto`, fora do pacote-base da aplicação. Os DTOs novos ficam em `br.edu.ifmt.liberacampus.dto`.
8. A configuração enviada do H2 contém `spring.datasource.usarname` em vez de `spring.datasource.username` e `spring.h2.console.enable` em vez de `spring.h2.console.enabled`.
9. O modelo de produto não possui restrições ou trilha de auditoria. O novo domínio inclui chaves únicas, índices, integridade e histórico.

## 2. Diagnóstico do Buffados League original

Os arquivos entregues (`index.html`, `usuario.html`, `requisicao.html`, `autorizacao.html`, `admin.html`, `style.css` e `script.js`) compõem um protótipo funcional no navegador, mas não uma aplicação web com servidor. As operações de criação, consulta e decisão trabalham sobre `localStorage`, sob a chave `liberacampus_buffados_v4`. A lista `DEMO` inicia o protótipo com dados fictícios.

O protótipo foi útil para validar navegação, os campos preliminares e a identidade visual. Porém, seu modelo de segurança não é suficiente para o novo propósito: um usuário com acesso ao navegador pode alterar os dados locais e a função JavaScript de decisão não representa uma autorização confiável.

A opção Visitante/terceiro foi retirada porque a finalidade atual é a liberação de turmas de cursos técnicos integrados. O usuário agora deve possuir conta de estudante vinculada a curso e turma.

## 3. Modelagem conceitual revisada

```text
USER_ACCOUNT (1) ─────────────── (N) PETITION
                                      │
                                      ├──── (N) PETITION_DEPARTMENT (N) ─── (1) DEPARTMENT [enum]
                                      │
                                      └──── (N) PETITION_EVENT
```

### `UserAccount`

Representa a conta que autentica no portal. `role` é `STUDENT` ou `ADMIN`. Estudantes possuem curso e turma. Administradores possuem uma coleção de departamentos atribuídos. A senha é armazenada somente como hash BCrypt.

### `Petition`

É o objeto principal do CRUD. Guarda o protocolo, solicitante, cópia do curso e turma, condição de representação do solicitante, data, primeiro/último horário, disciplinas, quantidade estimada, justificativa e status geral.

### `PetitionDepartment`

Resolve o caso de uma petição que depende de mais de um departamento. Cada associação guarda a situação local, a justificativa da manifestação, o nome de quem decidiu e a data/hora. A restrição única `(peticao_id, departamento)` impede que o mesmo departamento seja duplicado na mesma petição.

### `PetitionEvent`

Registra os eventos relevantes: protocolo, edição, início da análise, manifestação favorável/desfavorável e mudança do status consolidado. O nome do ator é armazenado como fotografia textual do momento, para que o histórico não dependa de alterações posteriores do cadastro.

## 4. Regras de encaminhamento

A entidade `Course` mantém a associação automática curso → departamento:

- INFORMATICA → DCOM
- EDIFICACOES e AGRIMENSURA → DINFRA
- EVENTOS e SECRETARIADO → DGH
- ELETRONICA e ELETROTECNICA → DEEA

O DABC é transversal: o estudante o acrescenta quando a liberação também alcança disciplinas de Base Comum. O curso principal sempre determina um departamento técnico responsável.

## 5. Regra de consolidação do status

A manifestação departamental não equivale automaticamente ao resultado geral:

- Qualquer `REJECTED` gera status geral `REJECTED` e encerra as demais etapas; uma manifestação posterior não pode contradizer a decisão final.
- Todos os departamentos em `APPROVED` geram `APPROVED`.
- Uma ou mais manifestações em `IN_ANALYSIS` ou `APPROVED`, sem indeferimento e sem conclusão total, geram `IN_ANALYSIS`.
- Todas as manifestações ainda em `PENDING` mantêm `PENDING`.

Essa regra está centralizada em `PetitionStatus.fromDepartmentStatuses`, é chamada pelo Service após cada manifestação e possui testes unitários para os cenários de pendência, análise, aprovação parcial, aprovação total e indeferimento. O estudante não recebe qualquer rota de decisão.

## 6. Autenticação e integração institucional

O portal oferece contas locais de demonstração, mas não finge integrar-se ao SUAP. Matrícula é um identificador informado pelo estudante; sem endpoint institucional autorizado, isso não prova o vínculo acadêmico. A senha local é independente e nunca deve ser reutilizada da conta SUAP.

A implantação oficial depende de aprovação institucional e de integração documentada. O projeto deixa o ponto de autenticação isolado no Spring Security, permitindo substituir o provedor local por OIDC/SAML no futuro.

## 7. Melhorias de qualidade de código

- Injeção por construtor.
- Entidades separadas de DTOs de formulário.
- Enumerações para papel, curso, departamento e status.
- Bean Validation no servidor e validações cruzadas no Service.
- Regras de autorização verificadas no backend.
- Transações nos casos de uso de negócio.
- Exclusão lógica por cancelamento, preservando histórico.
- Controle otimista de concorrência na Petição (`@Version`).
- Filtros de busca e estado renderizados no servidor.
- JavaScript reservado a melhorias de interação, não à segurança nem à persistência.


## 8. Evolução de plataforma e interface

A base foi atualizada para Java 21 e Spring Boot 4.1.2, usando o starter MVC modular `spring-boot-starter-webmvc`, Spring Security, Spring Data JPA, Bean Validation e Thymeleaf. O CSS foi reconstruído em um sistema de componentes responsivo, com hierarquia visual, estados, tabelas, formulários, navegação lateral, mensagens e adaptação para telas menores.

jQuery 3.7.1 e jQuery UI 1.14.2 são empacotados como WebJars e servidos pela própria aplicação. A interface utiliza Datepicker em português, Autocomplete para disciplinas, Dialog para confirmações e tooltips. As bibliotecas não são carregadas de CDN, reduzindo dependência de rede no momento de uso.

O JavaScript oferece somente aprimoramentos de usabilidade. Validações de segurança, autorização, integridade de dados e decisões permanecem no backend.

## 9. Configuração de execução

- `application.properties`: desenvolvimento local com H2 persistente, console habilitado e contas fictícias de demonstração.
- `application-prod.properties`: desativa console H2, cacheia templates, desativa dados demonstrativos e exige schema previamente migrado/validado (`ddl-auto=validate`).
- Variáveis de ambiente: permitem configurar MySQL, senha e administrador inicial sem gravar segredos no repositório.

## 10. Referência temporal

As datas de negócio usam `CampusTime`, com o fuso `America/Cuiaba`. Isso evita que a data de protocolo, validação do dia solicitado e registros de auditoria sejam deslocados quando o servidor estiver configurado em UTC.
