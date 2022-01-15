package cliente;

import repository.OperacionesRepository;
import servidor.GestionClientes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Scanner;

public class Cliente {


    DataInputStream entrada;
    DataOutputStream salida;
    Socket servidor;
    int puerto = 5000;
    InetAddress direccion;
    boolean validado = false;
    Scanner sc = new Scanner(System.in);

    private final int SACAR =1;
    private final int CONSULTAR_SALDO =2;
    private final int CONSULTAR_MOVIMIENTO =3;
    private final int INGRESAR =4;
    private final int SALIR =5;



    public Cliente() throws IOException {

        direccion = InetAddress.getLocalHost();
        servidor = new Socket(direccion, puerto);

        entrada = new DataInputStream(servidor.getInputStream());
        salida = new DataOutputStream(servidor.getOutputStream());

    }

    public void inicio() throws IOException {

        while (validado == false) { //Mientras que no me validen las credenciales me las seguirá pidiendo

            //recibo peticion de email
            String peticion = entrada.readUTF();
            System.out.println(peticion);
            String email = sc.nextLine();
            //envio email
            salida.writeUTF(email);
            //recibo peticion contraseña
            peticion = entrada.readUTF();
            System.out.println(peticion);
            //envio contraseña
            String contraseña = getSHA512(sc.nextLine());
            salida.writeUTF(contraseña);

            //recibo validacion
            validado = entrada.readBoolean();
            if (validado == false) {

                System.out.println("Credenciales incorrectas, vuelva a escribirlas");
            } else {
                System.out.println("Credenciales correctas");
                validado = true;
            }
        }

        //recibo menu de opciones
        String opcionesRecibidas = entrada.readUTF();
        System.out.println(opcionesRecibidas);
        OperacionesRepository opciones = new OperacionesRepository();
        //elijo opcion deseada hasta que ya no quiera más operaciones
       int opcionElegida=0;
        while (opcionElegida != 5) {//paso por aqui mientras no se pulse en salir
            opcionElegida= sc.nextInt();
            GestionClientes operaciones = new GestionClientes();
            //Mando opcion elegida
            salida.writeInt(opcionElegida);
            if(opcionElegida==SACAR){
                System.out.println(entrada.readUTF());
                double cantidad = sc.nextDouble();
                salida.writeDouble(cantidad);
                System.out.println(entrada.readUTF());

            }
            if(opcionElegida==CONSULTAR_SALDO){
                System.out.println(entrada.readUTF());

            }
            if(opcionElegida==CONSULTAR_MOVIMIENTO){
                System.out.println(entrada.readUTF());

            }
            if(opcionElegida==INGRESAR){
                System.out.println(entrada.readUTF());
                double cantidad = sc.nextDouble();
                salida.writeDouble(cantidad);
                System.out.println(entrada.readUTF());

            }

        }
        System.out.println(entrada.readUTF());
        System.exit(0);
    }


    public static String getSHA512(String input) {

        String toReturn = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }

}
