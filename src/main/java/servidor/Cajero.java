package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Cajero {


    public static void main(String[] args) throws IOException {
        int cantidad = 100;
        ServerSocket servidor;
        Socket cliente;
        int puerto=5000;
        servidor = new ServerSocket(puerto);
        boolean parar = false;



        while(!parar){ //cuando el cliente lo indique paramos el servidor

            System.out.println("Esperando clientes...");
            cliente = servidor.accept();
            System.out.println("Peticion de cliente -> " + cliente.getInetAddress() + " --- " + cliente.getPort());

            GestionClientes gestion = new GestionClientes(cliente);
            gestion.start();

        }

        System.out.println("Servidor finalizado...");
        servidor.close();

    }
    }

