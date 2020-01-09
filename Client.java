import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Client{
    String idClient;
    static int nbTrans;
    static String id,filePath,destID;
    static char resp;
    public static int port = 8080;
    static boolean sent = false;
    static ArrayList<Transaction> transactions = new ArrayList<Transaction>();// chaque client aura sa liste de transaction
    
    /** Constructeur du client
     * Des sa creation, un client ouvre une connexion tcp et ecoute 
     */
    public Client(){
        idClient  = this.idClient + "umpc" ;//id unique client
        
    }
    public static void main(String[] args) {
        

        /*********Connexion & transaction********/
        try{
            InetAddress ipServer = InetAddress.getByName("localhost");
            Socket s = new Socket(ipServer,port);
            InputStream fromServ = s.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(fromServ);
            

            while(true){ //keep retrieving transactions
                Transaction t = (Transaction)ois.readObject();// retrieve the Transaction object by the server
                transactions.add(t); //add the new transaction to the list of the client transaction
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        /*********Interaction***********/
        Client c = new Client();
        System.out.println("Input id : ");
        Scanner input = new Scanner(System.in);
        id = input.nextLine();//get the client id
        c.idClient = id;
        System.out.println("Client ID: "+c.idClient);
        System.out.println("Do you want to tranfert file [Y/N]");
        resp = input.next().charAt(0);//get the decision of the client

        if (resp == 'Y'){
            System.out.println("How many transaction do you want to do: ");
            nbTrans = input.nextInt();
            for (int i = 0; i < nbTrans; i++){
                System.out.println("Input the file location path : ");
                filePath = input.next();
                System.out.println("Input the destination ID: ");
                destID = input.next();
                Transaction t = new Transaction(c.idClient, destID, filePath.hashCode());//create the transaction
                transactions.add(t);//add the transaction to the client Transaction list
                sendTransactionServer(t);// send it to the server
            } 
        }
        if(resp == 'N'){
            System.out.println("connection to the server...");
        }
        else{
            System.out.println("Do you want to tranfert file [Y/N]");
        }
    
    }
    
    /**
     * send transaction to the server
     * send Transaction through socket
     * @param t
     */
    public static void sendTransactionServer(Transaction t){
        try {
            InetAddress serverIP = InetAddress.getByName("localhost");
            Socket s = new Socket(serverIP,port);
            OutputStream os = s.getOutputStream();
            ObjectOutputStream ob = new ObjectOutputStream(os);
            ob.writeObject(t);
            os.close();
            ob.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}