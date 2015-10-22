package cliente;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteSocket extends JFrame implements ActionListener {
    private Socket conexao;
    Socket socket;
    JTextArea areaTexto;
    JTextField msg;
    JButton btn;
    JLabel rotulo;
    PrintStream saida;
    BufferedReader entrada;
    String enderecoServidor;
    Integer portaServidor;
    String meuNome;

    public ClienteSocket() {
        super("Chat");

        Container tela = getContentPane();

        BorderLayout layout = new BorderLayout();
        tela.setLayout(layout);

        rotulo = new JLabel("");
        btn = new JButton("Enviar");
        btn.addActionListener(this);
        msg = new JTextField(20);

        areaTexto = new JTextArea(15, 30);
        JScrollPane painelRolagem = new JScrollPane(areaTexto);
        painelRolagem.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        painelRolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel pSuperior = new JPanel();
        pSuperior.setLayout(new FlowLayout(FlowLayout.LEFT));
        pSuperior.add(rotulo);

        JPanel pCentro = new JPanel();
        pCentro.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pCentro.add(painelRolagem);

        JPanel pInferior = new JPanel();
        pInferior.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pInferior.add(msg);
        pInferior.add(btn);

        tela.add(BorderLayout.NORTH, pSuperior);
        tela.add(BorderLayout.CENTER, pCentro);
        tela.add(BorderLayout.SOUTH, pInferior);

        pack();
        setVisible(true);
    }

    void executa() {
        try {
            /* Conexao ao servidor */
            enderecoServidor = JOptionPane.showInputDialog("Digite o endereço do servidor");
            portaServidor = Integer.parseInt(JOptionPane.showInputDialog("Digite a porta do servidor "));

            socket = new Socket(enderecoServidor, portaServidor);
            saida = new PrintStream(socket.getOutputStream());

            /* Define username e envia ao servidor */
            meuNome = JOptionPane.showInputDialog("Digite seu nome ");
            rotulo.setText("Bem Vindo " + meuNome);
            saida.println(meuNome.toUpperCase());

            Recebedor r = new Recebedor(this, socket.getInputStream());
            new Thread(r).start();
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    public static void main(String[] args) {
        ClienteSocket cliente = new ClienteSocket();
        WindowListener ls = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        cliente.addWindowListener(ls);
        cliente.executa();
    }

    // execução da thread
    public void run() {
        try {
            /* Pool de mensagens recebidas */
            BufferedReader msgRecebida = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            String tmpMensagem;

            while (true) {
                tmpMensagem = msgRecebida.readLine();

                if (tmpMensagem.trim().length() == 0) {
                    System.out.println("Conexão encerrada!");
                    //System.exit(0);
                }
                areaTexto.append(tmpMensagem + "\n");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object fonte = event.getSource();

        if (fonte == btn) {
            String text = msg.getText();
            saida.println(text);
            msg.setText(new String(""));
        }
    }
}

