package shared;

import java.io.IOException;
import java.net.MulticastSocket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class ChatGroup
{
	private ArrayList < Client >    clients;
	private LogicProcessor          lp;
	private UDPListener             ul;
	private String                  name;
	private int                     id;
	private Client                  owner;
	private boolean                 isServer;
	private byte [ ]                XORKey;

	public ChatGroup ( LogicProcessor lp, boolean isServer )
	{
		this.clients    = new ArrayList < > ( );
		this.lp         = lp;
		this.id         = -1;
		this.owner      = null;
		this.name       = null;
		this.isServer   = isServer;
		this.ul         = new UDPListener ( this.lp, this.isServer, this );

		this.XORKey     = null;
	}

	public void createGroupConnection ( int id, String name, String ip )
	{
		this.name   = name;
		this.id     = id;

		this.ul.createMulticastConnection ( ip );
	}

	public void joinGroupConnection ( int id, String name, String ip, int port )
	{
		this.name   = name;
		this.id     = id;

		this.ul.joinMulticastConnection ( ip, port );
	}

	public void listen ( )
	{
		this.ul.listen ( );
	}

	public void closeConnection ( )
	{
		this.log ( "Chat group " + this.name + " has been closed" );

		this.name   = null;
		this.id     = -1;

		this.ul.closeConnection ( );
	}

	public void generateXORKey ( )
	{
		this.XORKey = Encryption.generateXORKey ( );
	}

	public byte [ ] getXORKey ( )
	{
		return this.XORKey;
	}

	public void setXORKey ( byte [ ] key )
	{
		this.XORKey = key;
	}

	public void addClient ( Client cnt )
	{
		this.clients.add ( cnt );
	}

	public Client getClient ( String ip, int port )
	{
		for ( Client cnt : this.clients )
		{
			if ( cnt.getSocketIP ( ).equals ( ip ) && cnt.getSocketPort ( ) == port )
			{
				return cnt;
			}
		}

		return null;
	}

	public ArrayList < Client > getClients ( )
	{
		return this.clients;
	}

	public void removeClient ( Client cnt )
	{
		this.clients.remove ( cnt );
	}

	public void removeClients ( )
	{
		this.clients.clear ( );
	}

	public String getClientList ( )
	{
		String res = "";

		for ( Client cnt : this.clients )
		{
			res += cnt.getName ( ) + "/" + cnt.getSocketIP ( ) + "/" + cnt.getSocketPort ( ) + "/";
		}

		if ( res.isEmpty ( ) )
		{
			return null;
		}
		else
		{
			return new StringBuilder ( res ).deleteCharAt ( res.length ( ) - 1 ).toString ( );
		}
	}

	public void setOwner ( Client cnt )
	{
		this.owner = cnt;
	}

	public Client getOwner ( )
	{
		return this.owner;
	}

	public String getIP ( )
	{
		return this.ul.getGroupIP ( );
	}

	public int getPort ( )
	{
		return this.ul.getGroupPort ( );
	}

	public int getId ( )
	{
		return this.id;
	}

	public String getName ( )
	{
		return this.name;
	}

	public int getClientNum ( )
	{
		return this.clients.size ( );
	}

	public void sendMessage ( byte [ ] msgB )
	{
		this.ul.sendMessage ( msgB );
	}

	private void log ( String log )
	{
		System.out.println ( "[ChatGroup] " + log );
	}
}