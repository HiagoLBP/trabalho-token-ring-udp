import java.net.*;
import java.io.*;

public class Mestre {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Mestre <id> <operacao> <voltas> [ip_proximo_no]");
            return;
        }

        int id = Integer.parseInt(args[0]);
        int operacao = Integer.parseInt(args[1]);
        int voltas = Integer.parseInt(args[2]);

        int portaBase = 5000;
        int minhaPorta = portaBase + id;
        int proximoId = 1;
        int proximaPorta = portaBase + proximoId;

        // Configuracao para execucao LOCAL (localhost) ou via rede (4o argumento)
        String proximoHost = "localhost";
        if (args.length >= 4) {
            proximoHost = args[3];
        }

        int token = 2;

        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(minhaPorta);
            InetAddress aHost = InetAddress.getByName(proximoHost);

            System.out.println("=== MESTRE " + id + " INICIALIZADO ===");
            System.out.println("Escutando na porta: " + minhaPorta);
            System.out.println("Enviando para: Escravo 1 (" + proximoHost + ":" + proximaPorta + ")");
            System.out.println("Token inicial: " + token + "\n");

            Thread.sleep(1000);

            for (int volta = 1; volta <= voltas; volta++) {
                System.out.println("--------------------------------------------------");
                System.out.println(">>> INICIANDO VOLTA " + volta + " DE " + voltas + " COM TOKEN = " + token);
                System.out.println("--------------------------------------------------");

                byte[] msg = String.valueOf(token).getBytes();
                DatagramPacket envio = new DatagramPacket(msg, msg.length, aHost, proximaPorta);
                aSocket.send(envio);

                System.out.println("[DISPARO] Mestre enviou token " + token + " para Escravo 1");
                System.out.println("Aguardando retorno do anel...\n");

                byte[] buffer = new byte[1000];
                DatagramPacket retorno = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(retorno);

                String texto = new String(retorno.getData(), 0, retorno.getLength()).trim();
                token = Integer.parseInt(texto);
                String origemIp = retorno.getAddress().getHostAddress();

                System.out.println("[RETORNO] Mestre recebeu token do Escravo 3: " + token + " (de " + origemIp + ":" + retorno.getPort() + ")");

                Thread.sleep(800);

                int tokenAntigo = token;
                if (operacao == 0) {
                    token = token + 1;
                    System.out.println("[OPERACAO 0 MESTRE] " + tokenAntigo + " + 1 = " + token);
                } else {
                    token = token * 2;
                    System.out.println("[OPERACAO 1 MESTRE] " + tokenAntigo + " * 2 = " + token);
                }

                Thread.sleep(500);
            }

            System.out.println("\n==================================================");
            System.out.println("   RESULTADO FINAL DO TOKEN = " + token);
            System.out.println("==================================================");

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Interrupcao: " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }
}
