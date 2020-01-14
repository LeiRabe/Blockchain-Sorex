import java.io.Serializable;
import java.security.*;
import java.util.*;

public class Bloc implements java.io.Serializable{
    public int numero;
    public long timestamp;
    public String currentHash;
    public String precHash;
	public String data;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public int nonce;
	
    public Bloc(int numero, String precHash, ArrayList<Transaction> transactions) {
        this.numero = numero;
        this.timestamp = System.currentTimeMillis();
        this.precHash = precHash;
        this.transactions = transactions;
        nonce = 0;
        currentHash = calculateHash();
    }
    public String calculateHash(){
		try {
            data="";
		    for (int j=0; j<transactions.size();j++){
			    Transaction tr = transactions.get(j);
				data = data + tr.emetteur+tr.destinataire+tr.value;
			}
		    String input = numero + timestamp + precHash + data + nonce;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
	        
			StringBuffer hexString = new StringBuffer(); 
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void mineBlock(int difficulte) {
        nonce = 0;
	    String target = new String(new char[difficulte]).replace('\0', '0');
	    while (!currentHash.substring(0,  difficulte).equals(target)) {
            nonce++;
            currentHash = calculateHash();
        }
    }
	public String toString() {
        String s = "Bloc #      : " + numero + "\r\n";
		s = s +    "PrecHash : " + precHash + "\r\n";
		s = s +    "Timestamp    : " + timestamp + "\r\n";
		s = s +    "Transactions  : " + data + "\r\n"; 
		s = s +    "Nonce        : " + nonce + "\r\n"; 
        s = s +    "Hash  : " +currentHash + "\r\n";
    return s;
	}		
	
	//public static Bloc 
}