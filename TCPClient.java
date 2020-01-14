import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TCPClient {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static int port = 8888;
    public ArrayList<Bloc> stockBlocs = new ArrayList<Bloc>();// store in the client the block sent by the server

    public TCPClient(String name, String ip, Bloc bloc) throws Exception {
        // super(name);
        Socket socket = new Socket(ip, port);
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

    protected void getBloc() {
        Bloc b = null;
        try {
            b = (Bloc) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        stockBlocs.add(b);
    }


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
            while(state){
                try{
                    SimpleTextMessage m = (SimpleTextMessage)(this.in.readObject());
                    if(m.getMessage().startsWith("END")){
                        state = false;
                    }else{
                        System.out.println("Message recu");
                        System.out.println(m.getSenderName()+"] "+ m.getMessage()+"\n");
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


