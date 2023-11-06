import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

    public static final String CLASS_NAME = ConnectionHandler.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);


    private UserManager users;
    private Socket clientSocket = null;

    private BufferedReader input;
    private PrintWriter output;


    public ConnectionHandler(UserManager u, Socket s) {
        users = u;
        clientSocket = s;

        try {
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String buffer = null ;
        while (true) {
            try {
                buffer = input.readLine();
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
            String command = buffer.trim();

            // CONNECT Juan
            if( command.startsWith("CONNECT") ) {
                String userName = command.substring(command.indexOf(' ')  ).trim();

               boolean isConnected =  users.connect(userName,clientSocket);
               if( isConnected ) {
                   output.println("OK");
                   System.out.println(userName);
               } else {
                   output.println("FAIL");
               }
            }

            // SEND #<mensaje>@<usuario>
            if( command.startsWith("SEND") ) {
                String message = command.substring(command.indexOf('#')+1,
                        command.indexOf('@') );
                System.out.println(message);
                String userName = command.substring(command.indexOf('@')+1 ).trim();
                System.out.println(userName);
                users.send(message);
                //output.println(message);
            }

            if (command.startsWith("DISCONNECT")) {
                String userName = command.substring(command.indexOf(' ') + 1).trim();
                System.out.println(userName);

                boolean isDisconnected = users.disconnect(userName);
                if (isDisconnected) {
                    output.println("OK");
                    // se desconecta al cliente del servidor
                    try {
                        clientSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    output.println("FAIL");
                }
            }

            if (command.startsWith("LIST")) {
                String userList = users.getUserList(); // se obtiene la lista de usuarios del UserManager
                output.println(userList); // se envía la lista de usuarios de regreso al cliente
            }
        }


    }
}
