# LiberaCampus IFMT — Buffados League

Protótipo acadêmico de **Desenvolvimento Web Back-End** para o **3º ano C de Informática**.

## Equipe
- Acir Pontes
- Fabrício Paiva
- Fernanda
- Ikaro
- Maria Cecília
- Vitor

## Estrutura
- `index.html` — entrada do protótipo
- `admin.html` — central administrativa
- `usuario.html` — área do usuário
- `requisicao.html` — criação de Petição
- `autorizacao.html` — fila de decisão
- `style.css` — único CSS compartilhado
- `script.js` — lógica compartilhada e `localStorage`

## Regras incorporadas
1. A entidade **Petição** possui **Histórico** como atributo/conjunto de registros.
2. Pessoas sem código de matrícula podem solicitar acesso como **Visitante / terceiro**.
3. A Petição recebe identificador, status e histórico cronológico.
4. Autorizar ou indeferir altera o status e acrescenta uma nova entrada ao histórico.
5. O fluxo pode ser demonstrado sem servidor: o navegador usa `localStorage` como persistência simulada.

## Entidades e atores considerados
O protótipo contempla conceitualmente Secretaria, Coordenação de Curso, Portaria, Aluno, Monitoria, Departamento, Usuário, Turma, Mensagem e Petição, com foco no fluxo de Petição e seus responsáveis.

## Visual
A interface foi reconstruída com aparência de sistema institucional contemporâneo, usando uma paleta inspirada no contexto institucional, estados funcionais, tabelas, timeline, cards, responsividade e fotografias hospedadas no domínio oficial do Campus Cuiabá.

### Referências visuais externas
As imagens de destaque usadas no protótipo são carregadas do site oficial do IFMT Campus Cuiabá – Cel. Octayde Jorge da Silva:
- https://cba.ifmt.edu.br/wp-content/uploads/sites/4/2025/11/%F0%9F%92%BB-SITE-Noticia-2025-1.png

Contexto institucional consultado:
- https://cba.ifmt.edu.br/o-campus/
- https://cba.ifmt.edu.br/acesso-ao-campus/

## Observação
Este projeto é um **protótipo acadêmico estático**. Não representa um sistema oficial do IFMT nem realiza autenticação ou autorização reais.
