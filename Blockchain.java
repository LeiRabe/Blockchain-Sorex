import java.security.*;
import java.util.*;

public class Blockchain {
	
	public static ArrayList<Bloc> blockchain = new ArrayList<Bloc>();
	public static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public static int difficulte = 5;
	public static int cpt = 0;


	public static void setBlockchain(Bloc b){
		blockchain.add(b);
	}

	public static Bloc createBloc(ArrayList<Transaction> transactions){
		Bloc b = null;
		if(blockchain.isEmpty()){
			b = new Bloc(0, null, transactions);
		}
		else{
			b = new Bloc(blockchain.get(blockchain.size()-1).numero+1,blockchain.get(blockchain.size()-1).currentHash, transactions);
		}
		return b;
	}

	public static void main(String[] args) {
	    Wallet A = new Wallet(blockchain);
	    Wallet B = new Wallet(blockchain);
		System.out.println("Wallet A Solde: " + A.getSolde() + " euros");
		System.out.println("Wallet B Solde: " + B.getSolde() + " euros");

		System.out.println("Ajout de 2 transactions... ");
		Transaction t1 = A.send(B.publicKey, 10);
		if (t1!=null){
		    transactions.add(t1);
		}
		Transaction t2 = A.send(B.publicKey, 200);
		if (t2!=null){
		    transactions.add(t2);
		}
		
		Bloc b = new Bloc(0, null, transactions);
        b.mineBlock(difficulte);
		TCPClient client = null;
		Bloc c = createBloc(transactions);
		try{
			client= new TCPClient("alex", "localhost", b); 
		}catch(Exception e){
			System.exit(2);
		}
        blockchain.add(b);
		System.out.println("Wallet A Solde: " + A.getSolde() + " euros");
		System.out.println("Wallet B Solde: " + B.getSolde() + " euros");
		System.out.println("Blockchain Valide : " + ValideChaine(blockchain));

		for (Bloc blo : blockchain) {
			System.out.println(blo+"\n");
		}
		System.out.println("Nombre de bloc dans la liste: " +cpt);
		

	}
	public static boolean ValideChaine(ArrayList<Bloc> blockchain) {
	    if (!ValideBloc(blockchain.get(0), null)) {
          return false;
		}

		for (int i = 1; i < blockchain.size(); i++) {
		  Bloc currentBloc = blockchain.get(i);
		  Bloc precBloc = blockchain.get(i - 1);

		  if (!ValideBloc(currentBloc, precBloc)) {
			return false;
		  }
		}

		return true;
	}
	public static boolean ValideBloc(Bloc newBloc, Bloc precBloc) {
	    if (precBloc == null){  // Premier bloc
			if (newBloc.numero != 0) {
			  return false;
			}

			if (newBloc.precHash != null) {
			  return false;
			}

			if (newBloc.currentHash == null ||
				  !newBloc.calculateHash().equals(newBloc.currentHash)) {
			  return false;
			}

			return true;

		}
		else{                        //Blocs suivants
			if (newBloc != null ) {
			  if (precBloc.numero + 1 != newBloc.numero) {
				return false;
			  }

			  if (newBloc.precHash == null  ||
				!newBloc.precHash.equals(precBloc.currentHash)) {
				return false;
			  }

			  if (newBloc.currentHash == null  ||
				!newBloc.calculateHash().equals(newBloc.currentHash)) {
				return false;
			  }

			  return true;
			}
			return false;
		   
		}
    }
}
