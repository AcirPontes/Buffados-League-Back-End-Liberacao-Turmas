# Modelo de dados e fluxo institucional

## 1. Visão entidade-relacionamento

```mermaid
erDiagram
    USER_ACCOUNT ||--o{ PETITION : "protocola"
    PETITION ||--|{ PETITION_DEPARTMENT : "tramita em"
    PETITION ||--o{ PETITION_EVENT : "registra"
    USER_ACCOUNT o|--o{ PETITION_DEPARTMENT : "decide"
    USER_ACCOUNT o|--o{ PETITION_EVENT : "executa"
    USER_ACCOUNT ||--o{ ADMIN_DEPARTMENT : "possui escopo"
    DEPARTMENT ||--o{ PETITION_DEPARTMENT : "analisa"
    DEPARTMENT ||--o{ ADMIN_DEPARTMENT : "autoriza"

    USER_ACCOUNT {
        bigint id PK
        varchar matricula UK
        varchar full_name
        varchar email
        varchar password_hash
        varchar role
        varchar course
        varchar class_name
    }

    PETITION {
        bigint id PK
        varchar protocolo UK
        bigint solicitante_id FK
        varchar course
        varchar class_name
        varchar representative_role
        date requested_date
        int first_period
        int last_period
        varchar affected_subjects
        int expected_students
        varchar reason
        varchar status
        datetime created_at
        datetime updated_at
        bigint version
    }

    PETITION_DEPARTMENT {
        bigint id PK
        bigint petition_id FK
        varchar department
        varchar status
        varchar decision_note
        bigint decidido_por_id FK
        varchar decided_by_name
        datetime decided_at
    }

    PETITION_EVENT {
        bigint id PK
        bigint petition_id FK
        varchar title
        varchar description
        bigint ator_id FK
        varchar actor_name
        datetime occurred_at
    }

    ADMIN_DEPARTMENT {
        bigint user_id FK
        varchar department
    }
```

**Observação:** `Course`, `Department`, `Role`, `PetitionStatus`, `DepartmentStatus` e `RepresentativeRole` são enums persistidos como texto, não entidades com tabelas próprias. `ADMIN_DEPARTMENT` é a tabela de coleção gerada pelo `@ElementCollection` de `UserAccount`.

## 2. Justificativa das cardinalidades

- Um estudante pode protocolar várias petições; cada petição possui exatamente um solicitante.
- Uma petição pode exigir manifestações de um ou mais departamentos. A associação `PETITION_DEPARTMENT` contém os dados específicos da etapa departamental e a referência ao administrador responsável.
- Uma petição pode acumular eventos de auditoria, preservando quem realizou cada ação e quando.
- Um administrador pode responder por um ou vários departamentos. A coleção de escopo não concede autoridade para áreas não associadas à conta.
- O protocolo pertence à petição, não à manifestação departamental. Assim, uma solicitação multiárea permanece um único processo rastreável.

## 3. Encaminhamento

| Curso técnico informado na conta | Departamento principal |
|---|---|
| Técnico em Informática | DCOM |
| Técnico em Edificações | DINFRA |
| Técnico em Agrimensura | DINFRA |
| Técnico em Eventos | DGH |
| Técnico em Secretariado | DGH |
| Técnico em Eletrônica | DEEA |
| Técnico em Eletrotécnica | DEEA |

DABC é uma área transversal. O formulário permite incluí-lo quando a solicitação também afeta aulas da Base Comum. A decisão não depende de o aluno escrever livremente o código do departamento: o principal é calculado pelo curso e o DABC é selecionado em um campo próprio.

## 4. Ciclo de vida

```mermaid
flowchart TD
    A[Estudante autenticado] --> B[Preenche a petição]
    B --> C[Servidor valida os dados]
    C --> D[Cria protocolo e histórico]
    D --> E[Encaminha ao departamento do curso]
    E --> F{Incluiu Base Comum?}
    F -- Sim --> G[Inclui DABC]
    F -- Não --> H[Fila dos departamentos envolvidos]
    G --> H
    H --> I[Administrador autorizado inicia a análise]
    I --> J{Manifestação departamental}
    J -- Favorável --> K[Registra aprovação parcial]
    J -- Desfavorável --> L[Registra indeferimento e justificativa]
    J -- Em análise --> M[Atualiza etapa]
    K --> N{Todos aprovaram?}
    N -- Sim --> O[Petição aprovada]
    N -- Não --> P[Permanece em análise]
    L --> Q[Petição indeferida]
    M --> P
    P --> H
```

O estado geral é consolidado no backend. Um indeferimento encerra o processo e bloqueia novas manifestações, preservando as etapas ainda pendentes apenas como histórico de tramitação. A interface não é fonte de autoridade. Um estudante não possui endpoint, botão ou permissão para aprovar/indeferir. O `SecurityFilterChain` restringe `/admin/**` a `ROLE_ADMIN`, e o `PetitionService` confirma novamente se o administrador tem o departamento atribuído.

## 5. Matriz de permissões

| Ação | Estudante | Administrador |
|---|---:|---:|
| Criar conta de estudante | Sim | Não pelo cadastro público |
| Protocolar petição | Sim | Não |
| Consultar petição própria | Sim | Não, salvo seu escopo departamental |
| Editar/cancelar petição pendente própria | Sim | Não |
| Iniciar análise departamental | Não | Sim, dentro do escopo |
| Aprovar/indeferir etapa departamental | Não | Sim, dentro do escopo |
| Consultar petições de outra área | Não | Não, exceto se também tiver essa atribuição |

## 6. Limites intencionais do protótipo

- O cadastro local recebe matrícula digitada pelo usuário; não valida o vínculo no SUAP.
- Curso e turma são declarados no cadastro e dependem de conferência institucional na implantação.
- A grade do EduPage é aberta para consulta externa; não há sincronização automática ou API presumida.
- O registro da decisão fica no sistema acadêmico do projeto. Ele não substitui portaria, autorização formal ou procedimento oficial do IFMT.
