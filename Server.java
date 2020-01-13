import java.io.*;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<Socket> Socketclients = new ArrayList<Socket>();//store all the connected clients so that we can 
    //send blocs/transactions to each connected clients

    public static void main(String[] args) throws Exception {
        ServerSocket server = null;

        try {
            server = new ServerSocket(3200);
            server.setReuseAddress(true);
            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                Socketclients.add(client);//add all connected clients' ip to the arrayList

                ClientFileHandler clientFile = new ClientFileHandler(client);
                new Thread(clientFile).start();//chaque nouvelle connexion(client) sera pris en charge par un thread

                ClientTransactionHandler clientTransaction = new ClientTransactionHandler(client);
                new Thread(clientTransaction).start();//en arrière plan: server get transactions and send them to connected clients

                ClientBlocHandler clientBlocHandler = new ClientBlocHandler(client);
                new Thread(clientBlocHandler).start();//en arrière plan: server get blocs and send them to connected clients
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    /**
     * receive transaction and broadcast it to the connected client (thread)
    */
    private static class ClientTransactionHandler implements Runnable{

        private final Socket s;
        public static Transaction transaction; 

        public ClientTransactionHandler(Socket socket){
            this.s = socket;
        }

        @Override
        public void run() { //get and send the transaction to all connected clients
            InputStream is = null;
            ObjectInputStream ois = null;
            try{
                is = s.getInputStream();
                ois = new ObjectInputStream(is);
                transaction = (Transaction)ois.readObject();//get the transaction
                SendTransaction(Socketclients, transaction);//send the transaction to all connected clients

            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        
    }

    /** 
     * Send the transaction to connected clients 
     * @param clients
     * @param t
    */
    public static void SendTransaction(ArrayList<Socket> clients, Transaction t){
        OutputStream os = null;
        ObjectOutputStream ob = null;
        for ( Socket c : clients) {
            try{
                os = c.getOutputStream();
                ob = new ObjectOutputStream(os);
                ob.writeObject(t);//send the transaction to all the connected clients
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private static class ClientBlocHandler implements Runnable{

        private final Socket cliSocket;
        public static Bloc bloc; 

        public ClientBlocHandler(Socket socket) {
            this.cliSocket = socket;
        }

        @Override
        public void run() {
            InputStream is = null;
            ObjectInputStream ois = null;
            try{
                is = cliSocket.getInputStream();
                ois = new ObjectInputStream(is);
                bloc = (Bloc)ois.readObject();//get the bloc
                SendBloc(Socketclients, bloc);//send the bloc to all connected clients
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        
    }

    /**
     * Send the bloc to connected clients
     * @param clients
     * @param b
     */
    public static void SendBloc(ArrayList<Socket> clients, Bloc b){
        OutputStream os = null;
        ObjectOutputStream ob = null;
        for ( Socket c : clients) {
            try{
                os = c.getOutputStream();
                ob = new ObjectOutputStream(os);
                ob.writeObject(b);//send the transaction to all the connected clients
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    
    /**
     * receive file and send it to the destination
     *
     */
    private static class ClientFileHandler implements Runnable { 

        private final Socket clientSocket;

        public ClientFileHandler(Socket socket) { 
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.printf("Sent from the client: %s\n", line);
                    out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null)
                        in.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}