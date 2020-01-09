import java.security.*;
import java.util.*;

public class Wallet {
	
	public String privateKey;
	public String publicKey;
	private float solde=100.0f;
	private ArrayList<Bloc> blockchain = new ArrayList<Bloc>();
	
	public Wallet(ArrayList<Bloc> blockchain) {
		generateKeyPair();
		this.blockchain = blockchain;
	}
		
	public void generateKeyPair() {
		try {
			KeyPair keyPair;
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
	        privateKey = keyPair.getPrivate().toString();
	        publicKey = keyPair.getPublic().toString();
	        
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public float getSolde() {
		float total = solde;
        for (int i=0; i<blockchain.size();i++){
        	Bloc currentBlock = blockchain.get(i);
			for (int j=0; j<currentBlock.transactions.size();j++){
			    Transaction tr = currentBlock.transactions.get(j);
				if (tr.destinataire.equals(publicKey)){
				    total += tr.value;
				}
				if (tr.emetteur.equals(publicKey)){
				    total -= tr.value;
				}
			}
        }  
		return total;
	}
	
	public Transaction send(String destinataire,int value ) {
		if(getSolde() < value) {
			System.out.println("Fonds insuffisants. Transaction Refuse.");
			return null;
		}
		
		Transaction newTransaction = new Transaction(publicKey, destinataire , value);
		return newTransaction;
	}
	
}