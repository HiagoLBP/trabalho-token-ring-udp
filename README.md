# Trabalho Topologia Token Ring em Java (UDP)

**Disciplina:** Sistemas Distribuídos  
**Professor:** George Gabriel Mendes Dourado  

## Integrantes
- **Hiago Souza** ([@HiagoLBP](https://github.com/HiagoLBP))
- **José Carvalho Nogueira Neto** ([@josecarvalhonogueiraneto](https://github.com/josecarvalhonogueiraneto))
- **Murilo Alves Botelho** ([@MuriloAlvesBotelho](https://github.com/MuriloAlvesBotelho))
- **Vinicius Benevides Reis** ([@Vini-Be](https://github.com/Vini-Be))
- **Gustavo Nogueira Lima**  (.[@gusta-101](https://github.com/gusta-101))

---

## 1. Estrutura do Projeto
- `Mestre.java`: Código do nó Mestre (`ID 0`, porta `5000`), responsável por gerar o token inicial (`valor = 2`), dispará-lo no anel lógico, contabilizar as voltas e exibir o resultado final.
- `Escravo.java`: Código modular dos nós Escravos (`IDs 1, 2 e 3`, portas `5001, 5002 e 5003`), responsáveis por receber o token, aplicar a operação configurada (`0` para `+1` ou `1` para `*2`) e repassar ao próximo nó no sentido horário (`0 -> 1 -> 2 -> 3 -> 0`).

---

## 2. Parâmetros de Linha de Comando
Tanto o `Mestre` quanto o `Escravo` recebem obrigatoriamente **3 argumentos** via terminal:

```bash
java Escravo <id> <operacao> <voltas>
java Mestre <id> <operacao> <voltas>
```

| Parâmetro | Valores Aceitos | Descrição |
| :--- | :---: | :--- |
| `<id>` | `0` (Mestre) ou `1, 2, 3` (Escravos) | Identificador do processo no anel lógico |
| `<operacao>` | `0` ou `1` | `0`: soma 1 ao token (`token + 1`) \| `1`: multiplica por 2 (`token * 2`) |
| `<voltas>` | `>= 1` | Quantidade de voltas completas que o token dará no anel |

---

## 3. Compilação
Na pasta do projeto, compile as duas classes Java:

```bash
javac Escravo.java Mestre.java
```

---

## 4. Execução (Cenários de Teste da Especificação)
Abra **4 terminais** na pasta do projeto. Sempre inicie primeiro os **3 Escravos** (para que fiquem aguardando pacotes UDP nas portas `5001`, `5002` e `5003`) e, por último, inicie o **Mestre** (porta `5000`).

### Cenário 1 — Operação `0` (+1 em todos os nós) e `1` volta (Resultado esperado: `6`)
```bash
# Terminal 1
java Escravo 1 0 1

# Terminal 2
java Escravo 2 0 1

# Terminal 3
java Escravo 3 0 1

# Terminal 4 (dispara o token inicial = 2)
java Mestre 0 0 1
```

### Cenário 2 — Operação `1` (*2 em todos os nós) e `1` volta (Resultado esperado: `32`)
```bash
# Terminal 1
java Escravo 1 1 1

# Terminal 2
java Escravo 2 1 1

# Terminal 3
java Escravo 3 1 1

# Terminal 4 (dispara o token inicial = 2)
java Mestre 0 1 1
```

### Cenário 3 — Operação `0` (+1 em todos os nós) e `2` voltas (Resultado esperado: `10`)
```bash
# Terminal 1
java Escravo 1 0 2

# Terminal 2
java Escravo 2 0 2

# Terminal 3
java Escravo 3 0 2

# Terminal 4 (dispara o token inicial = 2)
java Mestre 0 0 2
```
