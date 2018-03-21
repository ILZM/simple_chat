package shared;

import client.ClientLogicProcessor;
import server.ServerLogicProcessor;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPListener implements Runnable
{
	private LogicProcessor  lp;
	private Socket          sock;
	private String          cip, sip;
	private int             cport, sport;
	private volatile Thread th;
	private boolean         isServer;

	public TCPListener ( LogicProcessor lp, boolean isServer )
	{
		this.lp         = lp;
		this.th         = new Thread ( this, "TCP socket listener" );
		this.isServer   = isServer;
		this.cip        = null;
		this.sip        = null;
		this.cport      = -1;
		this.sport      = -1;
	}

	public void createSocket ( int port )
	{
		try
		{
			this.sock   = new Socket ( Shared.TCP_IP, port );
			this.sport  = port;

			Scanner sc = new Scanner ( this.sock.getInetAddress ( ).toString ( ) );

			sc.useDelimiter ( Shared.DELIMITER_MAIN );
			sc.next ( );

			this.sip = sc.next ( );

			this.log ( "Opened connection for server " + this.sip + ":" + this.sport );
		}
		catch ( Exception e )
		{
			this.connectionError ( );
		}
	}

	public void setSocket ( Socket sock )
	{
		this.sock   = sock;
		this.cport  = this.sock.getPort ( );
		this.cip    = this.sock.getInetAddress ( ).toString ( ).replace ( "/", "" );

		this.log ( "Opened connection for client " + this.cip + ":" + this.cport );
	}

	public void listen ( )
	{
		this.th.start ( );

		if ( this.isServer )
		{
			this.log ( "Started to listen TCP Socket of client " + this.cip + ":" + this.cport );
		}
		else
		{
			this.log ( "Started to listen TCP Socket of server " + this.sip + ":" + this.sport );
		}
	}

	public void closeConnection ( )
	{
		try
		{
			this.th.interrupt ( );
			this.sock.close ( );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	}

	@Override
	public void run ( )
	{
		DataInputStream dis     = null;

		try { dis = new DataInputStream ( this.sock.getInputStream ( ) ); }
		catch ( IOException e ) { e.printStackTrace ( ); }

		while ( !this.th.isInterrupted ( ) )
		{
			int msgLen = -1;

			try { msgLen = dis.readInt ( ); }
			catch ( IOException e )
			{
				this.connectionError ( );

				break;
			}

			byte [ ] msg = new byte [ msgLen ];

			try { dis.readFully ( msg, 0, msg.length ); }
			catch ( IOException e )
			{
				this.connectionError ( );

				break;
			}

			if ( this.isServer )
			{
				ServerLogicProcessor slp = ( ServerLogicProcessor )this.lp;

				slp.onClientMessage ( msg, this.sock );
			}
			else
			{
				ClientLogicProcessor clp = ( ClientLogicProcessor )this.lp;

				clp.onServerMessage ( msg );
			}
		}
	}

	public void sendMessage ( byte [ ] msg )
	{
		try
		{
			DataOutputStream dos = new DataOutputStream ( this.sock.getOutputStream ( ) );

			dos.writeInt ( msg.length );
			dos.write ( msg );
			dos.flush ( );
		}
		catch ( Exception e )
		{
			this.connectionError ( );
		}
	}

	public String getClientIP ( )
	{
		return this.cip;
	}

	public int getClientPort ( )
	{
		return this.cport;
	}

	public void setClientIP ( )
	{
		this.cip = this.sock.getInetAddress ( ).getHostAddress ( );
	}

	public void setClientPort (  )
	{
		this.cport = this.sock.getLocalPort ( );
	}

	public void setClientIP ( String ip )
	{
		this.cip = ip;
	}

	public void setClientPort ( int port )
	{
		this.cport = port;
	}

	public String getServerIP ( )
	{
		return this.sip;
	}

	public int getServerPort ( )
	{
		return this.sport;
	}

	public Socket getSocket ( )
	{
		return this.sock;
	}

	private void connectionError ( )
	{
		if ( this.isServer )
		{
			ServerLogicProcessor slp = ( ServerLogicProcessor )this.lp;

			slp.onClientLeave ( this.sock );
		}
		else
		{
			ClientLogicProcessor clp = ( ClientLogicProcessor )this.lp;

			clp.onServerShutdown ( );
		}
	}

	private void log ( String log )
	{
		System.out.println ( "[TCPListener] " + log );
	}
}