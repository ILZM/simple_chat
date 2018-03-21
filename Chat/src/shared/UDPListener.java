package shared;

import client.ClientLogicProcessor;
import server.ServerLogicProcessor;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPListener implements Runnable
{
	private LogicProcessor  lp;
	private MulticastSocket mSock;
	private DatagramSocket  dSock;
	private int             mPort, dPort;
	private String          gip, sip;
	private volatile Thread th;
	private boolean         isServer;
	private ChatGroup       cg;

	public UDPListener ( LogicProcessor lp, boolean isServer, ChatGroup cg )
	{
		try
		{
			this.lp     = lp;
			this.mSock  = null;
			this.dSock  = new DatagramSocket ( 0 );
			this.mPort  = -1;
			this.dPort  = this.dSock.getLocalPort ( );
			this.gip    = null;
			this.cg     = cg;

			Scanner sc = new Scanner ( InetAddress.getLocalHost ( ).toString ( ) );

			sc.useDelimiter ( Shared.DELIMITER_MAIN );
			sc.next ( );

			this.sip        = sc.next ( );
			this.th         = new Thread ( this, "UDP socket listener" );
			this.isServer   = isServer;

			this.log ( "Created UDP socket sender on " + this.sip + " " + this.dPort + " port" );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	}

	public void createMulticastConnection ( String gip )
	{
		try
		{
			this.gip    = gip;
			this.mSock  = new MulticastSocket ( 0 );
			this.mPort  = this.mSock.getLocalPort ( );

			this.mSock.joinGroup ( InetAddress.getByName ( this.gip ) );

			this.log ( "Created group " + this.gip + ":" + this.mPort );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	}

	public void joinMulticastConnection ( String gip, int port )
	{
		try
		{
			this.gip    = gip;
			this.mSock  = new MulticastSocket ( port );
			this.mPort  = this.mSock.getLocalPort ( );

			this.mSock.joinGroup ( InetAddress.getByName ( this.gip ) );

			this.log ( "Joined group " + this.gip + ":" + port );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	}

	public void listen ( )
	{
		this.th.start ( );

		this.log ( "Started to listen group on " + this.gip + ":" + this.mPort );
	}

	public void closeConnection ( )
	{
		try
		{
			this.th.interrupt ( );
			this.mSock.leaveGroup ( InetAddress.getByName ( this.gip ) );
			this.mSock.close ( );
			this.dSock.close ( );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	}

	@Override
	public void run ( )
	{
		DatagramPacket pck = new DatagramPacket ( new byte [ Shared.UDP_BUFFER_SIZE ], Shared.UDP_BUFFER_SIZE );

		while ( !this.th.isInterrupted ( ) )
		{
			try { this.mSock.receive ( pck ); }
			catch ( IOException e ) { break; }

			byte data [ ] = new byte [ pck.getLength ( ) ];

			for ( int i = 0; i < pck.getLength ( ); i++ )
			{
				data [ i ] = pck.getData ( ) [ i ];
			}

			if ( this.isServer )
			{
				ServerLogicProcessor slp = ( ServerLogicProcessor )this.lp;

				slp.onGroupMessage ( data, pck.getAddress ( ).toString ( ).replace ( "/", "" ),
					pck.getPort ( ), this.cg );
			}
			else
			{
				ClientLogicProcessor clp = ( ClientLogicProcessor )this.lp;

				clp.onGroupMessage ( data, pck.getAddress ( ).toString ( ).replace ( "/", "" ),
					pck.getPort ( ) );
			}
		}
	}

	public void sendMessage ( byte [ ] msgB )
	{
		try
		{
			this.dSock.send ( new DatagramPacket (  msgB,  msgB.length,
				InetAddress.getByName ( this.gip ), this.mPort ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	}

	public String getGroupIP ( )
	{
		return this.gip;
	}

	public int getGroupPort ( )
	{
		return this.mPort;
	}

	private void log ( String log )
	{
		System.out.println ( "[UDPListener] " + log );
	}
}