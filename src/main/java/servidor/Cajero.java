package servidor;

import log.Log;
import log.tipo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cajero {


    public static void main(String[] args) throws IOException {
        SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-YYYY-HH_mm_ss");
        String formatoFecha = fecha.format(new Date());
        Log myLog = new Log("./"+formatoFecha+".log");
        int cantidad = 100;
        ServerSocket servidor;
        Socket cliente;
        int puerto=5000;
        servidor = new ServerSocket(puerto);
        boolean parar = false;



        while(!parar){ //cuando el cliente lo indique paramos el servidor

            System.out.println("Esperando clientes...");
            cliente = servidor.accept();
            myLog.addLine(tipo.INFO,"Se ha conectado el cliente"+ cliente.getInetAddress());
            System.out.println("Peticion de cliente -> " + cliente.getInetAddress() + " --- " + cliente.getPort());

            GestionClientes gestion = new GestionClientes(cliente);
            gestion.start();

        }

        System.out.println("Servidor finalizado...");

        servidor.close();

    }
    }

