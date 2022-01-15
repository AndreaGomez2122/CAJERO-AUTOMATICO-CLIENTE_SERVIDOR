package servidor;

import cliente.Cliente;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import database.MongoDBController;
import model.Movimiento;
import model.Usuario;
import model.tipo;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class GestionClientes extends Thread {

    private Socket cliente;
    private boolean valida = false;
    DataOutputStream salida;
    DataInputStream entrada;
    Usuario usuario = new Usuario();
    Movimiento movimiento;
    List<Movimiento> movimientosUsuario;
    MongoCollection<Usuario> usuariosCollection;
    MongoCollection<Movimiento> movimientosCollection;
    private double limiteDiarioRetirada = 0;
    private double limiteDiarioIngreso = 1000;


    public GestionClientes(Socket cliente) throws IOException {

        salida = new DataOutputStream(cliente.getOutputStream());
        entrada = new DataInputStream(cliente.getInputStream());
        this.cliente = cliente;

    }

    public GestionClientes() {


    }


    @Override
    public void run() {

        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();

        // Obtenemos las bases de datos
        Optional<List<Document>> databases = mongoController.getDataBases();
        System.out.println("Todos las bases de datos existentes");
        databases.ifPresent(documents -> documents.forEach(db -> System.out.println(db.toJson())));

        // Me conecto a la colección de departamentos
        usuariosCollection = mongoController
                .getCollection("test", "usuarios", Usuario.class);
        movimientosCollection = mongoController
                .getCollection("test", "movimientos", Movimiento.class);

        // Se borran tanto collecciones como base de datos para tenerlas limpias
        mongoController.removeCollection("test", "usuarios");
        mongoController.removeCollection("test", "movimientos");
        mongoController.removeDataBase("test");


        //PREPARACION BASE DE DATOS DE EJEMPLO
        System.out.println("Añadiendo usuarios...");
        Usuario usu1 = new Usuario(new ObjectId(), "lola@gmail.com", Cliente.getSHA512("1234"), 5000, 1000);
        Usuario usu2 = new Usuario(new ObjectId(), "jose@gmail.com", Cliente.getSHA512("1234"), 1000, 1000);
        List<Usuario> usuariosInsert = new ArrayList<>(Arrays.asList(usu1, usu2));
        System.out.println("Insertando usuarios en la bbdd");
        usuariosCollection.insertMany(usuariosInsert);

        System.out.println("Añadiendo movimientos...");
        Movimiento mov1 = new Movimiento(LocalDate.now(), tipo.CONSULTAR_MOVIMIENTO, "lola@gmail.com");
        Movimiento mov2 = new Movimiento(LocalDate.now(), tipo.CONSULTAR_SALDO, "jose@gmail.com");

        List<Movimiento> movimientosInsert = new ArrayList<>(Arrays.asList(mov1, mov2));
        System.out.println("Insertando movimientos en la bbdd");
        movimientosCollection.insertMany(movimientosInsert);

        System.out.println("Mostrando todos los usuarios");
        usuariosCollection.find().into(new ArrayList<>()).forEach(System.out::println);
        System.out.println("Mostrando todos los movimientos");
        movimientosCollection.find().into(new ArrayList<>()).forEach(System.out::println);


        try {
            while (!valida) {
                //cliente.setSoLinger(true, 10);//tiempo para que el puerto este abierto

                //Enviar peticion email
                String peticionCredenciales = "Por favor introduzca email";
                DataOutputStream sal = new DataOutputStream(cliente.getOutputStream());
                sal.writeUTF(peticionCredenciales);

                //recibo email
                String email = entrada.readUTF();

                //Enviar peticion contraseña
                peticionCredenciales = "Por favor introduzca la contraseña";
                salida.writeUTF(peticionCredenciales);
                //recibo contraseña
                String contraseña = entrada.readUTF();

                //Busqueda de usuario en la bbdd
                usuario = usuariosCollection.find(and(eq("email", email), eq("password", contraseña))).first();//pasa por aqui hasta que las credenciales sean correctas

                if (usuario != null) {
                    //Enviar validacion correcta
                    valida = true;
                    salida.writeBoolean(valida);

                } else {
                    //Enviar validacion incorrecta
                    valida = false;
                    salida.writeBoolean(valida);
                }
            }
            //Mandar menu de opciones
            salida.writeUTF(menuOpciones());
            //
            funcionalidadMenu();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String menuOpciones() {
        String opciones = "SELECCIONE LA OPCIÓN QUE DESEE: \n" +
                "1. SACAR EFECTIVO.\n" +
                "2. CONSULTAR SALDO.\n" +
                "3. CONSULTAR MOVIMIENTO\n" +
                "4. REALIZAR INGRESO\n" +
                "5. SALIR\n";

        return opciones;
    }

    public void funcionalidadMenu() throws Exception {

        int opcionElegida = 0;
        while (opcionElegida != 5) {
            opcionElegida = entrada.readInt();

            switch (opcionElegida) {
                case 1:
                    sacarEfectivo();
                    break;
                case 2:
                    consultarSaldo();
                    break;
                case 3:
                    consultarMovimiento();
                    break;
                case 4:
                    realizarIngreso();
                    break;
            }
        }
        salir();
    }

    public void sacarEfectivo() throws IOException {
        limiteDiarioRetirada = usuario.getLimite();

        String solicitarCantidad = "Introduzca la cantidad que quiere retirar";
        salida.writeUTF(solicitarCantidad);
        //DEBO METER FILTRO POR FECHA
        //filtro que comprueba si la cantidad es numero positivo, que no dejaría la cuenta en numeros negativos y que no supere el limite diario
        double retirada = entrada.readDouble();
        if (retirada > 0 && retirada < usuario.getSaldo() && retirada < limiteDiarioRetirada) {
            limiteDiarioRetirada = limiteDiarioRetirada - retirada;
            usuario.setSaldo(usuario.getSaldo() - retirada);
            usuario.setLimite(limiteDiarioRetirada);

            actualizar(usuario);
            movimiento = new Movimiento(LocalDate.now(), tipo.SACAR, usuario.getEmail(), retirada);
            movimientosCollection.insertOne(movimiento);

            salida.writeUTF("Se ha retirado el dinero correctamente ahora su saldo es\n" +
                    usuario.getSaldo() + "y su limite diario está en : " + limiteDiarioRetirada);

            //movimientosCollection.find(eq("user_email", usuario.getEmail())).into(new ArrayList<>()).forEach();

        } else {
            System.out.println("No ha ingresado bien la cantidad");

        }
    }

    public void consultarSaldo() throws IOException {
        String saldo = "Su saldo es de: " + usuario.getSaldo() + " €";
        salida.writeUTF(saldo);
        movimiento = new Movimiento(LocalDate.now(), tipo.CONSULTAR_SALDO, usuario.getEmail());
        movimientosCollection.insertOne(movimiento);

    }
    //No sé qué se pide
    //Sacar todos los movimientos de un usuario?
    public void consultarMovimiento() {
        System.out.println("Pagina consultar movimiento");

    }

    public void realizarIngreso() throws Exception {

        String solicitarCantidad = "Introduzca la cantidad que quiere ingresar";
        salida.writeUTF(solicitarCantidad);
        double ingreso = entrada.readDouble();

        if (ingreso > 0 && ingreso < limiteDiarioIngreso) {
            limiteDiarioIngreso = limiteDiarioIngreso - ingreso;
            usuario.setSaldo(usuario.getSaldo() + ingreso);
            actualizar(usuario);
            movimiento = new Movimiento(LocalDate.now(), tipo.INGRESAR, usuario.getEmail(), ingreso);
            movimientosCollection.insertOne(movimiento);

            salida.writeUTF("Se ha ingresado el dinero correctamente ahora su saldo es\n" +
                    usuario.getSaldo() + "y su limite diario de ingreso está en : " + limiteDiarioIngreso);

        }

    }

    public void salir() throws IOException {
        String correo[] = usuario.getEmail().split("@");
        String nombre = correo[0];
        String despedida = "¡¡ HASTA PRONTO " + nombre + " !!";

        salida.writeUTF(despedida);

    }

    private void actualizar(Usuario usuario) {
        Document filtered = new Document("_id", usuario.getId());
        FindOneAndReplaceOptions returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        Usuario actualizado = usuariosCollection.findOneAndReplace(filtered, usuario, returnDoc);
        System.out.println("Actualizado:\t" + actualizado);

    }
}





