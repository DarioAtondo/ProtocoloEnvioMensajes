import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

public class UserManager {

    public static final String CLASS_NAME = UserManager.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    private HashMap<String, Socket> connections;

    public UserManager() {
        super();
        connections = new  HashMap<String, Socket>();
    }

    public synchronized boolean isUsernameTaken(String username) { //manejo para validar que no se puedan repetir los usuarios
        return connections.containsKey(username);
    }

    public synchronized boolean connect(String user, Socket socket) { //manejo del connecct
        if (!isUsernameTaken(user)) {
            connections.put(user, socket);
            return true;
        }
        return false;
    }

    public synchronized boolean disconnect(String user) { //manejo del disconnect
        if (connections.containsKey(user)) {
            connections.remove(user);
            return true;
        }
        return false;
    }

    public Socket get(String user) {

        Socket s = connections.get(user);

        return s;
    }

    public void send(String message) {

        Collection<Socket> conexiones = connections.values();

        for( Socket s: conexiones) {
            try {
                PrintWriter output = new PrintWriter(s.getOutputStream(), true);
                output.println(message);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public String getUserList() { //manejo de list
        StringBuilder userList = new StringBuilder("LIST: ");
        for (String user : connections.keySet()) {
            userList.append(user).append(", ");
        }
        userList.setLength(userList.length() - 2);
        return userList.toString();
    }



}
