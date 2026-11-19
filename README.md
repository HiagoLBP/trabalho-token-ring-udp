# Trabalho Topologia Token Ring em Java (UDP)

**Disciplina:** Sistemas Distribuídos  
**Professor:** George Gabriel Mendes Dourado  

## Integrantes
- **Gustavo Nogueira** ([@gusta-101](https://github.com/gusta-101))
- **Hiago Souza** ([@HiagoLBP](https://github.com/HiagoLBP))
- **José Carvalho Nogueira Neto** ([@josecarvalhonogueiraneto](https://github.com/josecarvalhonogueiraneto))
- **Murilo Alves Botelho** ([@MuriloAlvesBotelho](https://github.com/MuriloAlvesBotelho))
- **Vinicius Benevides Reis** ([@Vini-Be](https://github.com/Vini-Be))

---

## 1. Estrutura do Projeto
- `Mestre.java`: Código do nó Mestre (`ID 0`, porta `5000`), responsável por gerar o token inicial (`valor = 2`), dispará-lo no anel lógico, contabilizar as voltas e exibir o resultado final.
- `Escravo.java`: Código modular dos nós Escravos (`IDs 1, 2 e 3`, portas `5001, 5002 e 5003`), responsáveis por receber o token, aplicar a operação configurada (`0` para `+1` ou `1` para `*2`) e repassar ao próximo nó no sentido horário (`0 -> 1 -> 2 -> 3 -> 0`).

---

## 2. Parâmetros de Linha de Comando
Tanto o `Mestre` quanto o `Escravo` recebem obrigatoriamente **3 argumentos** (e opcionalmente um **4º argumento** com o IP do próximo nó para execução em rede):

```bash
java Escravo <id> <operacao> <voltas> [ip_proximo_no]
java Mestre <id> <operacao> <voltas> [ip_proximo_no]
```

| Parâmetro | Valores Aceitos | Descrição |
| :--- | :---: | :--- |
| `<id>` | `0` (Mestre) ou `1, 2, 3` (Escravos) | Identificador do processo no anel lógico |
| `<operacao>` | `0` ou `1` | `0`: soma 1 ao token (`token + 1`) \| `1`: multiplica por 2 (`token * 2`) |
| `<voltas>` | `>= 1` | Quantidade de voltas completas que o token dará no anel |
| `[ip_proximo_no]` *(opcional)* | `localhost` (padrão) ou IPv4 | IP do computador onde está rodando o próximo nó do anel |

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

---

## 5. Execução em Computadores Diferentes (Rede)

Não edite o IP fixo dentro do código quando houver mais de um processo no mesmo PC — passe o IP de destino diretamente como **4º argumento** em cada terminal.

### Passo 1 — Descobrir o IP correto de cada PC
No Windows, execute `ipconfig` e utilize o **Endereço IPv4** do adaptador **Wi-Fi** ou **Ethernet** ativo (evite pegar IPs de adaptadores virtuais como VirtualBox `192.168.56.x` ou WSL/Docker `172.x.x.x`).

### Passo 2 — Rodar em 2 PCs (2 processos em cada computador)
Exemplo dividindo **Mestre + Escravo 1** no **PC A** e **Escravo 2 + Escravo 3** no **PC B** (sempre iniciando os Escravos antes do Mestre):

```bash
# No PC B (iniciar primeiro)
java Escravo 2 0 1 localhost
java Escravo 3 0 1 <IP_DO_PC_A>

# No PC A (iniciar em seguida)
java Escravo 1 0 1 <IP_DO_PC_B>
java Mestre 0 0 1 localhost
```

### Passo 3 — Rodar em 4 PCs (1 processo em cada computador)
Cada computador aponta apenas para o IP do seu sucessor no sentido horário (`0 -> 1 -> 2 -> 3 -> 0`):

```bash
# PC do Escravo 1 -> envia para PC do Escravo 2 (porta 5002)
java Escravo 1 0 1 <IP_ESCRAVO_2>

# PC do Escravo 2 -> envia para PC do Escravo 3 (porta 5003)
java Escravo 2 0 1 <IP_ESCRAVO_3>

# PC do Escravo 3 -> devolve para PC do Mestre (porta 5000)
java Escravo 3 0 1 <IP_MESTRE>

# PC do Mestre -> dispara para PC do Escravo 1 (porta 5001)
java Mestre 0 0 1 <IP_ESCRAVO_1>
```

---

## 6. Solução de Problemas na Rede (Caso 1 PC não receba do outro)

No protocolo **UDP**, `send()` não exibe erro quando o destino bloqueia o pacote — quem envia imprime `[DISPARO]`/`[REPASSE]` normalmente, mas o outro PC permanece travado em `Aguardando chegada do token...`. Verifique os 3 pontos abaixo:

1. **Firewall do Windows / Rede Pública:**
   Se um dos PCs estiver em perfil **Rede Pública** (ou o pop-up do Java no Firewall foi fechado/negado), o Windows bloqueia pacotes UDP recebidos nas portas `5000`–`5003`. Execute no **PowerShell como Administrador** em todos os PCs:
   ```powershell
   New-NetFirewallRule -DisplayName "Java Token Ring UDP" -Direction Inbound -Protocol UDP -LocalPort 5000-5003 -Action Allow
   ```
   *(Lembre também de permitir o `java.exe` em redes privadas e públicas caso o Firewall exiba o alerta na primeira execução).*

2. **Wi-Fi com *AP Isolation* (Redes de Universidade / Públicas):**
   Muitas redes acadêmicas bloqueiam a comunicação direta entre dois notebooks conectados no mesmo Wi-Fi. Teste com `ping <IP_DO_OUTRO_PC>`; se falhar, conectem os computadores no **Hotspot Wi-Fi de um celular**.

3. **PCs em Redes Distintas (Casas diferentes / CGNAT):**
   Como roteadores residenciais bloqueiam pacotes UDP externos, utilizem uma VPN Mesh gratuita como **Radmin VPN** ou **Tailscale** para colocar todos os PCs na mesma rede virtual e informem o IP virtual da VPN como 4º argumento.
