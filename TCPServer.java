import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TCPServer
{
	public static final int port = 8888;
	private static boolean state = true;
	private static ConcurrentLinkedQueue<SimpleTextMessage> messageQueue = new ConcurrentLinkedQueue<SimpleTextMessage>();
	private static 	Hashtable<String, UserChannelInfo> users = new Hashtable<String, UserChannelInfo>();
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{	 
		ServerSocket server = new ServerSocket(port);
		ServerMessageManager smm = new ServerMessageManager(messageQueue, users);
		Thread ts = new Thread(smm);
		ts.start();
		while(state){
			try{
				System.out.println("server: listening");
				Socket socket = server.accept();
				System.out.println("Connexion Utilisateur");
				UserChannelInfo channel = new UserChannelInfo(socket, messageQueue);
				System.out.println("Utilisateur: " + channel.getName());
				users.put(channel.getName(), channel);
				Thread t = new Thread(channel);
				t.start();
			}catch(java.net.SocketTimeoutException te){
				System.out.println("Timeout");
				state = false;
			}
		}
		server.close();
		System.exit(0);
	}

	public static synchronized UserChannelInfo removeUserChannel(String name)
	{
		 return users.remove(name);
	}
}


class UserChannelInfo implements Runnable
{
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket socket;
	private String name = null;
	private ConcurrentLinkedQueue<SimpleTextMessage> messageQueue;
	private boolean state = true;
	private int errors = 0;
	public UserChannelInfo(Socket socket, ConcurrentLinkedQueue<SimpleTextMessage> messageQueue) throws IOException, ClassNotFoundException
	{
		
		this.socket = socket;
		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		this.name = (String) (this.input.readObject());
		this.messageQueue = messageQueue;
		this.messageQueue.add(new SimpleTextMessage("System", this.name+" joined.",null));	}
	
	public void run()
	{
		System.out.println("Tunnel " + this.name +" up");
		while(state){
			try{
				Thread.sleep(500);
			}catch(Exception e2){
				errors++;
			}
			try{
				String mesg = (String)input.readObject();
				Bloc bloc = (Bloc)input.readObject();//recevoir le bloc venant du client
				//this.output.writeObject(bloc);//envoyer aux clients connectés
				//Blockchain.setBlockchain(bloc);//ajouter les blocs dans la blockchain

				if(mesg.startsWith("END")){
					//message de fin pour fermer le tunnel
					state = false;
				}else{
					//Ajoue du message a la file
					SimpleTextMessage sm = new SimpleTextMessage(this.getName(), mesg,bloc);
					this.messageQueue.add(sm);
				}
			}catch(Exception e){
				errors++;
				if(errors>=5){
					//ferme le tunnel
					System.out.println("Fermeture de connexion " + this.name
							+" erreur: "+ e.getMessage());
					state = false;
					sendMessage(new SimpleTextMessage("Server", "END",null));
				}
			}
		}
		//supprime de la table de hash
		UserChannelInfo U = TCPServer.removeUserChannel(this.getName());
		if(U != null){
			//Noctification que l'utilisateur est deco a tlm (en inserant dans la file)
			SimpleTextMessage sm = new SimpleTextMessage("System", U.getName() + " left.",null);
			this.messageQueue.add(sm);
		}
	}
	
	public void sendMessage(SimpleTextMessage message)
	{
		try{
			this.output.writeObject(message);
		}catch(Exception e){
			errors++;
			if(errors >= 5){
				throw new RuntimeException(e);
			}
		}
	}
	
	public String getName(){
		return this.name;
	}
}


class ServerMessageManager implements Runnable
{
	private ConcurrentLinkedQueue<SimpleTextMessage> messageQueue = null;
	private Hashtable<String, UserChannelInfo> users = null;
	private boolean state = true;
	public ServerMessageManager(ConcurrentLinkedQueue<SimpleTextMessage> messageQueue,
												Hashtable<String, UserChannelInfo> users)
	{
		this.messageQueue = messageQueue;
		this.users = users;
	}
	
	public void run()
	{
		System.out.println("ServerMessageManager up, attente de message entrants");
		while(state){
			try{
				Thread.sleep(100);
			}catch(Exception e){
				
			}
			while(!messageQueue.isEmpty()){
				SimpleTextMessage sm = messageQueue.poll();
				Enumeration<UserChannelInfo> all = users.elements();
				while(all.hasMoreElements()){
					UserChannelInfo user = all.nextElement();
					user.sendMessage(sm);
				}
			}
		}
	}
	
}
