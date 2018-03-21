package shared;

import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Client
{
	private String          name;
	private LogicProcessor  lp;
	private ChatGroup       cg;
	private TCPListener     tl;
	private boolean         isServer;
	private byte [ ]        XORKey;
	private PublicKey       publicRSAOwnKey, publicRSAForeignKey;
	private PrivateKey      privateRSAOwnKey;

	public Client ( LogicProcessor lp, boolean isServer )
	{
		this.name               = Shared.DEFAULT_NAME;
		this.lp                 = lp;
		this.isServer           = isServer;
		this.tl                 = new TCPListener ( this.lp, this.isServer );
		this.cg                 = null;

		this.XORKey                 = null;
		this.publicRSAOwnKey        = null;
		this.privateRSAOwnKey       = null;
		this.publicRSAForeignKey    = null;
	}

	public void createSocketConnection ( String ip, int port )
	{
		this.tl.createSocket ( port );
	}

	public void setSocketConnection ( Socket sock )
	{
		this.tl.setSocket ( sock );
	}

	public void listenSocket ( )
	{
		this.tl.listen ( );
	}

	public void closeSocketConnection ( )
	{
		this.tl.closeConnection ( );

		this.tl = null;
	}

	public boolean isClient ( String ip, int port )
	{
		if ( this.tl.getClientIP ( ).equals ( ip ) && this.tl.getClientPort ( ) == port )
		{
			return true;
		}

		return false;
	}

	public byte [ ] getXORKey ( )
	{
		return this.XORKey;
	}

	public void setXORKey ( byte [ ] key )
	{
		this.XORKey = key;
	}

	public boolean hasXORKey ( )
	{
		if ( this.XORKey == null )
		{
			return false;
		}

		return true;
	}

	public void generateXORKey ( )
	{
		this.XORKey = Encryption.generateXORKey ( );
	}

	public void generateRSAKeyPair ( )
	{
		Encryption.generateRSAKeyPair ( );

		this.publicRSAOwnKey    = Encryption.getPublicRSAKey ( );
		this.privateRSAOwnKey   = Encryption.getPrivateRSAKey ( );
	}

	public PublicKey getPublicRSAOwnKey ( )
	{
		return this.publicRSAOwnKey;
	}

	public PrivateKey getPrivateRSAOwnKey ( )
	{
		return this.privateRSAOwnKey;
	}

	public void setPublicRSAForeignKey ( byte [ ] keyB )
	{
		this.publicRSAForeignKey = Encryption.restorePublicKey ( keyB );
	}

	public boolean hasPublicRSAForeignKey ( )
	{
		if ( this.publicRSAForeignKey == null )
		{
			return false;
		}

		return true;
	}

	public PublicKey getPublicRSAForeignKey ( )
	{
		return this.publicRSAForeignKey;
	}

	public void setChatGroup ( ChatGroup cg )
	{
		this.cg = cg;
	}

	public ChatGroup getChatGroup ( )
	{
		return this.cg;
	}

	public void setName ( String name )
	{
		this.name = name;
	}

	public String getName ( )
	{
		return this.name;
	}

	public void setSocketIP ( )
	{
		this.tl.setClientIP ( );
	}

	public void setSocketIP ( String ip )
	{
		this.tl.setClientIP ( ip );
	}

	public String getSocketIP ( )
	{
		return this.tl.getClientIP ( );
	}

	public void setSocketPort ( )
	{
		this.tl.setClientPort ( );
	}

	public void setSocketPort ( int port )
	{
		this.tl.setClientPort ( port );
	}

	public int getSocketPort ( )
	{
		return this.tl.getClientPort ( );
	}

	public Socket getSocket ( )
	{
		return this.tl.getSocket ( );
	}

	public void sendSocketMessage ( byte [ ] msg )
	{
		this.tl.sendMessage ( msg );
	}
}