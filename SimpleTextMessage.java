
public class SimpleTextMessage implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private String message;
	private Bloc b;
	
	public SimpleTextMessage(String senderName, String message, Bloc b)
	{
		this.name = senderName;
		this.message = message;
		this.b = b;
	}
	
	public String getSenderName(){
		return this.name;
	}

	public String getMessage(){
		return this.message;
	}

	public Bloc getBloc(){
		return this.b;
	}

}
