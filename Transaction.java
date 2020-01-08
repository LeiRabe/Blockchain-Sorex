import java.security.*;
import java.util.*;

public class Transaction {
	public String emetteur;
	public String destinataire;
	public float value; 
		
	public Transaction(String emetteur, String destinataire, float value) {
		this.emetteur = emetteur;
		this.destinataire = destinataire;
		this.value = value;
	}
}
	