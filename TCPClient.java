import java.net.Socket;
import java.nio.Buffer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class TCPClient {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static int port = 8888;
    public Socket socket = null;

    public TCPClient(String name, String ip, Bloc bloc) throws Exception {
        // super(name);
        socket = new Socket(ip, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.sendMessage(name);
        MessageManagerTCP_x messenger = new MessageManagerTCP_x(in);
        message(bloc);
        Thread t = new Thread(messenger);
        t.start();
        // ne pas zapper de close
    }

    public void message(Bloc b) {
        this.sendBlock(b);
    }

    protected void sendMessage(String mesg) {
        try {
            this.out.writeObject(mesg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendBlock(Bloc b) {
        try {
            this.out.writeObject(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void getBlocs(Blockchain blockchain) {
        Bloc b = null;
        try {
            b = (Bloc) in.readObject();//récupérér les blocs
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        ArrayList<Bloc> bloclist = blockchain.getBlockchainArray();
        bloclist.add(b);//add the retrieved bloc to the array of bloc in the Blockchain class
    }*/


    class MessageManagerTCP_x implements Runnable
    {
        private ObjectInputStream in;
        private boolean state = true;
        private int errors = 0;
        public MessageManagerTCP_x(ObjectInputStream in) throws IOException
        {
            this.in= in;
        }

        public void close(){
            state = false;
        }

        public void run()
        {
            System.out.println("Message manager is up ...");

            Bloc b = null;
            while(state){
                try{
                    SimpleTextMessage m = (SimpleTextMessage)(this.in.readObject());
                    if(m.getMessage().startsWith("END")){
                        state = false;
                    }else{
                        System.out.println("Message recu");
                        System.out.println(m.getSenderName()+"] "+ m.getMessage()+"\n");
                        b = m.getBloc();
                        setBlockchain(b);
                        
                    }
                }catch(Exception e){
                    errors++;
                    System.out.println("Error: This is only for text messaging.");
                    e.printStackTrace();
                    if(errors >= 5){
                        state = false;
                    }
                }
            }
            System.out.println("message manager retired");
            System.exit(1);
        }
    }

}


