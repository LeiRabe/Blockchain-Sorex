import java.io.*;
import java.net.Socket;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    
    static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    static ArrayList<Bloc> blocs = new ArrayList<Bloc>();
    String idClient;
    static String host = "127.0.0.1";
    static int port = 8080;
    static Socket socket = null;//client socket 
    static Transaction transaction;
    static Bloc bloc; 
    

    public Client(){
        String id,filePath,destID,decision,resp;
        PrintWriter out = null;
        BufferedReader in = null;
        Scanner scanner = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        long length = 0L; 

        idClient += "upmc";

        
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(System.in);
            String line = null;
            System.out.println("input your user id: ");
            idClient += scanner.nextLine();
            System.out.println("Your id: " + idClient);

            while (!"exit".equalsIgnoreCase(line)) {

                ServerHandler serverSock = new ServerHandler();
                new Thread(serverSock).start();//run en arrière plan pour chaque server qui envoie bloc/transaction au client
                
                System.out.println("Do you want to do a transaction? [Y/N]");
                resp = scanner.next();

                if("Y".equalsIgnoreCase(resp)){
                    System.out.println("Input the destination id: ");
                    destID = scanner.next();
                    System.out.println("Input the file path: ");

                    /* begining send file*/
                    filePath = scanner.next();
                    File f = new File(filePath);
                    length = f.length();
                    byte[] buffer = new byte[(int) length];
                    fos = new FileOutputStream(f); 
                    bos = new BufferedOutputStream(fos);
                    bos.write(buffer); 
                    /*end of sending file*/

                    

                    Transaction t = new Transaction(idClient, destID, filePath.hashCode());
                    sendTransactionServer(t);
                }
                  
            }
            scanner.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }


    public static void main(String[] args) {

        Client client = new Client();

    }

    /**
     * send transaction to the server
     * send Transaction through socket
     * @param t
     */
    public static void sendTransactionServer(Transaction t){
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream ob = new ObjectOutputStream(os);
            ob.writeObject(t);
            os.close();
            ob.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get transactions from the sender trough the server
     * 
     */
    public static void getTransaction(){
        try {
            InputStream is = socket.getInputStream();
            ObjectInputStream oiStream = new ObjectInputStream(is);
            transaction = (Transaction)oiStream.readObject();//get the Transaction sent from the server
            transactions.add(transaction);//add the retrieved transaction within the array of transactions
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get bloc from the server
     */
    public static void getBloc(){
        try{
            InputStream in = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            bloc = (Bloc)ois.readObject();
            blocs.add(bloc);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /** class that will be run in the back for each client */
    private static class  ServerHandler implements Runnable {

        @Override
        public void run() {
            getBloc();
            getTransaction();
        }
    } 

    
}