import java.io.Serializable;
import java.security.*;
import java.util.*;

public class Transaction implements Serializable{

	private static final long serialVersionUID = -2449292470013667678L;//code de hachage de Transaction
	public String emetteur;
	public String destinataire;
	public int value; //hash of the sent file 
		
	public Transaction(String emetteur, String destinataire, int value) {
		this.emetteur = emetteur;
		this.destinataire = destinataire;
		this.value = value;
	}
}
	