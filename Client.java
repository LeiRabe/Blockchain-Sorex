import java.io.*;
import java.net.Socket;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    
    static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    static ArrayList<Bloc> blockChain = new ArrayList<Bloc>();
    String idClient;
    static String host = "127.0.0.1";
    static int port = 3200;
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
        Bloc b;
        int cpt = 0; 

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

                    // todo : a finir, la partie server qui reçoit le fichier et le diffuse
                    /* begining send file*/
                    filePath = scanner.next();
                    File f = new File(filePath);
                    length = f.length();
                    byte[] buffer = new byte[(int) length];
                    fos = new FileOutputStream(f); 
                    bos = new BufferedOutputStream(fos);
                    bos.write(buffer); 
                    /*end of sending file*/

                    /*Create the transaction and the related bloc and send it to the server*/
                    Transaction t = new Transaction(idClient, destID, f.hashCode());
                    transactions.add(t);//add the new transaction into the array of transactions
                    if(blockChain.isEmpty()){
                        b = new Bloc(0, null, transactions);
                    }
                    else {

                        for (Bloc bloc : blockChain) {
                            cpt++;//count the number of bloc in the blockchain
                        }
                        b = new Bloc(cpt, blockChain.get(cpt-1).currentHash, transactions);
                    }
                    sendTransactionServer(t);//send transaction

                }
                  
            }
            scanner.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }


    public static void main(String[] args) throws Exception {

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
            ob.writeObject(t);//send transaction
            os.close();
            ob.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * send the related bloc to the server
     * @param b
     */

    public static void sendBlocServer(Bloc b){
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream ob = new ObjectOutputStream(os);
            ob.writeObject(b);//send transaction
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
            blockChain.add(bloc);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * get the related file from server
    */
    public static void getFile(){
        try {
            byte [] buff = new byte[65535];
            InputStream in = socket.getInputStream();
            FileOutputStream fo = new FileOutputStream("fichierTest.txt");
            in.read(buff,0,buff.length);
            fo.write(buff,0,buff.length);
            
        } catch (Exception e) {
            //TODO: handle exception
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