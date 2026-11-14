import java.net.*;
import java.io.*;

public class Escravo {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Escravo <id> <operacao> <voltas>");
            return;
        }

        int id = Integer.parseInt(args[0]);
        int operacao = Integer.parseInt(args[1]);
        int voltas = Integer.parseInt(args[2]);

        int portaBase = 5000;
        int minhaPorta = portaBase + id;
        int proximoId = (id + 1) % 4;
        int proximaPorta = portaBase + proximoId;

        // Configuracao para execucao LOCAL (localhost)
        String proximoHost = "localhost";

        // Trecho para conexao entre computadores diferentes na rede (descomente quando for usar 4 PCs):
        /*
        if (args.length >= 4) {
            proximoHost = args[3];
        }
        */

        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(minhaPorta);
            InetAddress aHost = InetAddress.getByName(proximoHost);

            System.out.println("=== ESCRAVO " + id + " INICIALIZADO ===");
            System.out.println("Escutando na porta: " + minhaPorta);
            System.out.println("Sucessor no anel: No " + proximoId + " (" + proximoHost + ":" + proximaPorta + ")");
            System.out.println("Aguardando chegada do token...\n");

            for (int volta = 1; volta <= voltas; volta++) {
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String texto = new String(request.getData(), 0, request.getLength()).trim();
                int token = Integer.parseInt(texto);
                String origemIp = request.getAddress().getHostAddress();

                System.out.println(">>> [VOLTA " + volta + "/" + voltas + "] Token recebido: " + token + " (de " + origemIp + ":" + request.getPort() + ")");

                Thread.sleep(800);

                int tokenProcessado;
                if (operacao == 0) {
                    tokenProcessado = token + 1;
                    System.out.println("    [OPERACAO 0] " + token + " + 1 = " + tokenProcessado);
                } else {
                    tokenProcessado = token * 2;
                    System.out.println("    [OPERACAO 1] " + token + " * 2 = " + tokenProcessado);
                }

                Thread.sleep(400);

                byte[] msg = String.valueOf(tokenProcessado).getBytes();
                DatagramPacket envio = new DatagramPacket(msg, msg.length, aHost, proximaPorta);
                aSocket.send(envio);

                System.out.println("    [REPASSE] Token " + tokenProcessado + " enviado para No " + proximoId + " (" + proximoHost + ":" + proximaPorta + ")\n");
            }

            System.out.println("Escravo " + id + " completou as " + voltas + " volta(s). Encerrando.");

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
