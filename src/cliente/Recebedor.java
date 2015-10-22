package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class Recebedor implements Runnable {

    private ClienteSocket clienteSocket;
    private InputStream servidor;

    public Recebedor(ClienteSocket clienteSocket, InputStream servidor) {
        this.clienteSocket = clienteSocket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            /* Recebe mensagens do Servidor */
            clienteSocket.entrada = new BufferedReader(new InputStreamReader(clienteSocket.socket.getInputStream()));
            String mensagemRecebida;

            while (true) {
                mensagemRecebida = clienteSocket.entrada.readLine();

                if (mensagemRecebida == null) {
                    System.out.println("Conexão encerrada!");
                    System.exit(0);
                }
                clienteSocket.areaTexto.append(mensagemRecebida + "\n");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);
        }
    }
}
