import java.io.Serializable;
import java.security.*;
import java.util.*;

public class Transaction implements java.io.Serializable {
	public String emetteur;
	public String destinataire;
	public int value; //hash of the sent file 
		
	public Transaction(String emetteur, String destinataire, int value) {
		this.emetteur = emetteur;
		this.destinataire = destinataire;
		this.value = value;
	}
}
	