package client;

import shared.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientLogicProcessor extends LogicProcessor
{
	private ChatClient              cc;
	private Client                  thisCnt;
	private ArrayList < GroupInfo > groups;

	public ClientLogicProcessor ( ChatClient cc )
	{
		this.thisCnt        = new Client ( this, false );
		this.cc             = cc;
		this.groups         = new ArrayList < > ( );
	}

	public void init (String ip, int port )
	{
		this.thisCnt.createSocketConnection ( ip, port );

		this.onServerConnect ( );
	}

	public void listen ( )
	{
		this.thisCnt.listenSocket ( );
		/*
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		while ( true )
		{
			try
			{
				String in = inFromUser.readLine ( );

				if ( in.equals ( "LIVAY" ) )
				{
					//this.onGroupLeave ( );
				}
				else
				{
					String check = new Scanner ( in ).next ( );

					if ( check.toLowerCase ( ).equals ( "t" ) )
					{
						this.thisCnt.sendSocketMessage ( in.replace ( check + " ", "" ) );
					}
					else
					{
						this.thisCnt.sendGroupMessage ( in );
					}
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace ( );
			}
		}*/

		// this line might be a program end
	}

	public ArrayList < GroupInfo > getGroups ( )
	{
		return this.groups;
	}

	public void setClientName ( String name )
	{
		this.thisCnt.setName ( name );
	}

	public void onNameChange ( String name )
	{
		this.thisCnt.setName ( name );
		this.sendName ( name );
		this.cc.getGUI ( ).setTitle ( "" );
	}

	public void onServerConnect ( )
	{
		this.log ( "Connected to server" );

		this.thisCnt.setSocketIP ( );
		this.thisCnt.setSocketPort ( );
		this.thisCnt.setChatGroup ( null );
		this.thisCnt.generateRSAKeyPair ( );

		this.sendPublicRSAKey ( );
	}

	public void onServerShutdown ( )
	{
		this.log ( "Server has shutdown" );

		this.cc.getGUI ( ).showErrorDialog ( );
	}

	public void onGroupMessage ( byte [ ] msgB, String ip, int port )
	{
		msgB = Encryption.processXOR ( msgB, this.thisCnt.getChatGroup ( ).getXORKey ( ) );

		String msg = new String ( msgB );

		this.log ( "Group member " + ip + ":" + port + " messages " + msg );

		Scanner sc = new Scanner ( msg );
		String mtd = sc.next ( );

		sc.skip ( " " );
		sc.useDelimiter ( Shared.DELIMITER_MAIN );

		switch ( mtd.toLowerCase ( ) )
		{
			case "leave":
			{
				this.onClientGroupLeave ( sc.next ( ), sc.nextInt ( ) );

				break;
			}
			case "join":
			{
				this.onClientGroupConnect ( sc.next ( ), sc.next ( ), sc.nextInt ( ) );

				break;
			}
			case "msg":
			{
				String  cip     = sc.next ( );
				int     cport   = sc.nextInt ( );
				String  rep     = mtd + " " + cip + "/" + cport + "/";

				this.onGroupMemberMessage ( msg.replaceFirst ( rep, "" ), cip, cport );

				break;
			}
			case "name":
			{
				String  name    = sc.next ( );
				String  cip     = sc.next ( );
				int     cport   = sc.nextInt ( );

				this.onGroupMemberNameChange ( name, cip, cport );

				break;
			}
		}
	}

	public boolean hasGroup ( String name )
	{
		for ( GroupInfo gi : this.groups )
		{
			if ( gi.getName ( ).equals ( name ) )
			{
				return true;
			}
		}

		return false;
	}

	public void onGroupDestroy ( int id )
	{
		GroupInfo gi = this.groups.get ( id );

		this.sendGroupDestroy ( id );
	}

	public void onGroupCreation ( String name )
	{
		this.log ( "Created group " + name );

		this.sendGroupCreation ( name );
	}

	public void onGroupLeave ( )
	{
		this.leaveGroup ( );
		this.sendLeave ( );
	}

	private void leaveGroup ( )
	{
		ChatGroup cg = this.thisCnt.getChatGroup ( );

		this.log ( "Leaving " + cg.getName ( ) );

		cg.closeConnection ( );

		this.thisCnt.setChatGroup ( null );
		this.cc.getGUI ( ).setTitle ( "" );
	}

	public void joinGroup ( int id )
	{
		this.log ( "Requesting group info of " + this.groups.get ( id ).getName ( ) );

		this.askJoinGroup ( id );
	}

	public void onServerMessage ( byte [ ] msgB )
	{
		if ( !this.thisCnt.hasXORKey ( ) )
		{
			this.log ( "Got XOR key encrypted by RSA from the server" );

			msgB = Encryption.decryptRSA ( msgB, this.thisCnt.getPrivateRSAOwnKey ( ) );

			this.log ( "Decrypted XOR key of the server " + new String ( msgB ) );

			this.thisCnt.setXORKey ( msgB );
			this.sendName ( this.thisCnt.getName ( ) );
		}
		else
		{
			msgB = Encryption.processXOR ( msgB, this.thisCnt.getXORKey ( ) );

			String msg = new String ( msgB );

			this.log ( "Server messages " + msg );

			Scanner sc  = new Scanner ( msg );
			String  mtd = sc.next ( );

			sc.skip ( " " );

			switch ( mtd.toLowerCase ( ) )
			{
				case "group":
				{
					sc.useDelimiter ( Shared.DELIMITER_MAIN );

					int     id      = sc.nextInt ( );
					String  name    = sc.next ( );
					String  ip      = sc.next ( );
					int     port    = sc.nextInt ( );
					int     cntNum  = sc.nextInt ( );
					String  list    = "";

					if ( cntNum == 0 )
					{
						list = sc.next ( ) + "/";
					}
					else
					{
						for ( int i = 0; i < cntNum; i++ )
						{
							list += sc.next ( ) + "/" + sc.next ( ) + "/" + sc.next ( ) + "/";
						}
					}

					String  rep = mtd + " " + id + "/" + name + "/" + ip + "/" + port + "/" + cntNum + "/" + list;

					this.onGroupConnect ( id, name, ip, port, msg.replace ( rep, "" ) );
					this.onClientListReceive ( cntNum, list );

					break;
				}
				case "groups":
				{
					this.onGroupListReceive ( msg.replaceFirst ( mtd + " ", "" ) );

					break;
				}
				case "leave":
				{
					this.leaveGroup ( );
					this.cc.getGUI ( ).showGroupPanel ( );

					break;
				}
			}
		}
	}

	public Client getClient ( )
	{
		return this.thisCnt;
	}

	//
	private void onGroupMemberNameChange ( String name, String ip, int port )
	{
		this.log ( "Group member " + ip + ":" + port + "has changed name to " + name );

		ChatGroup       cg = this.thisCnt.getChatGroup ( );
		Client          cnt = cg.getClient ( ip, port );
		ClientChatGUI   gui = this.cc.getGUI ( );

		cnt.setName ( name );
		gui.refreshListMember ( );
		gui.setTitle ( cg.getName ( ) );
	}

	public void onGroupSend ( String msg )
	{
		this.log ( "Sent group message " + msg );

		String msgF = "MSG " + this.thisCnt.getSocketIP ( ) + "/" + this.thisCnt.getSocketPort ( ) + "/" + msg;

		this.sendGroupMessage ( msgF );
	}

	private void onGroupMemberMessage ( String msg, String ip, int port )
	{
		this.log ( "Group member " + ip + ":" + port + " says " + msg );

		Client cnt = this.thisCnt.getChatGroup ( ).getClient ( ip, port );

		this.cc.getGUI ( ).appendTextChat ( cnt.getName ( ), ip, port, msg );
	}

	private void onGroupListReceive ( String list )
	{
		this.log ( "Received group list" );

		Scanner sc = new Scanner ( list );

		sc.useDelimiter ( Shared.DELIMITER_MAIN);

		this.groups.clear ( );

		while ( sc.hasNextInt ( ) )
		{
			this.groups.add ( new GroupInfo ( sc.nextInt ( ), sc.next ( ), sc.nextInt ( ), sc.nextInt ( ) ) );
		}

		ClientChatGUI gui = this.cc.getGUI ( );

		gui.refreshGroupList ( );

		if ( !gui.getInit ( ) )
		{
			gui.setVisible ( true );
			gui.setInit ( true );
		}
	}

	private void onClientListReceive ( int num, String list )
	{
		this.log ( "Received client list " + list );

		if ( list.equals ( "null" ) ) { return; }

		Scanner sc = new Scanner ( list );

		sc.useDelimiter ( Shared.DELIMITER_MAIN );

		for ( int i = 0; i < num; i++ )
		{
			String  name    = sc.next ( );
			String  ip      = sc.next ( );
			int     port    = sc.nextInt ( );
			Client  cnt     = new Client ( this, false );

			cnt.setName ( name );
			cnt.setSocketIP ( ip );
			cnt.setSocketPort ( port );
			this.thisCnt.getChatGroup ( ).addClient ( cnt );
			this.cc.getGUI ( ).refreshListMember ( );
		}
	}

	private void onGroupConnect ( int id, String name, String ip, int port, String XORKey )
	{
		this.log ( "Connected to group with id " + id + " and name " + name + " on " + ip + ":" + port +
			" with XOR key " + XORKey );

		ChatGroup cg = new ChatGroup ( this, false );

		cg.joinGroupConnection ( id, name, ip, port );
		cg.listen ( );

		cg.setXORKey ( XORKey.getBytes ( StandardCharsets.UTF_8 ) );

		ClientChatGUI gui = this.cc.getGUI ( );

		this.thisCnt.setChatGroup ( cg );

		cg.addClient ( this.thisCnt );

		gui.refreshListMember ( );
		gui.setTitle ( name );
		gui.joinedGroup ( );
	}

	private void onClientGroupConnect ( String name, String ip, int port )
	{
		if ( this.thisCnt.isClient ( ip, port ) )
		{
			return;
		}

		this.log ( "Client " + name + " has joined the group chat" );

		ChatGroup   cg  = thisCnt.getChatGroup ( );
		Client      cnt = new Client ( this, false );

		cnt.setName ( name );
		cnt.setSocketIP ( ip );
		cnt.setSocketPort ( port );
		cg.addClient ( cnt );

		this.cc.getGUI ( ).refreshListMember ( );
		this.cc.getGUI ( ).appendTextChat ( cnt.getName ( ) + " entered the group" );
	}

	private void onClientGroupLeave ( String ip, int port )
	{
		if ( this.thisCnt.isClient ( ip, port ) )
		{
			return;
		}

		ChatGroup   cg  = this.thisCnt.getChatGroup ( );
		Client      cnt = cg.getClient ( ip, port );

		this.log ( "Client " + cnt.getName ( ) + " has left the group chat" );

		ClientChatGUI gui = this.cc.getGUI ( );

		gui.appendTextChat ( cnt.getName ( ) + " has left the group" );
		cg.removeClient ( cnt );
		gui.refreshListMember ( );
	}

	private void sendName ( String name )
	{
		this.log ( "Sent this client's name " + name + " to the server" );

		String msg = "NAME " + name;

		this.sendServerMessage ( msg );
	}

	private void sendGroupDestroy ( int id )
	{
		this.log ( "Sent group destroy with id " + id );

		String msg = "DESTROY " + id;

		this.sendServerMessage ( msg );
	}

	private void sendGroupCreation ( String name )
	{
		this.log ( "Sent group creation with name " + name );

		String msg = "CREATE " + name;

		this.sendServerMessage ( msg );
	}

	private void askJoinGroup ( int id )
	{
		this.log ( "Sent join request of " + id + " group chat to the server" );

		String msg = "JOIN " + id;

		this.sendServerMessage ( msg );
	}

	private void sendLeave ( )
	{
		this.log ( "Sent leave message to the server" );

		String msg = "LEAVE ";

		this.sendServerMessage ( msg );
	}

	private void sendPublicRSAKey ( )
	{
		this.log ( "Sent public RSA key to the server" );

		this.sendFreeServerMessage ( this.thisCnt.getPublicRSAOwnKey ( ).getEncoded ( ) );
	}

	private void sendGroupMessage ( String msg )
	{
		ChatGroup cg = thisCnt.getChatGroup ( );

		byte [ ] msgB   = msg.getBytes ( StandardCharsets.UTF_8 );
		byte [ ] msgXOR = Encryption.processXOR ( msgB, cg.getXORKey ( ) );

		cg.sendMessage ( msgXOR );
	}

	private void sendServerMessage ( String msg )
	{
		byte [ ]    msgB    = msg.getBytes ( StandardCharsets.UTF_8 );
		byte [ ]    msgXOR  = Encryption.processXOR ( msgB, this.thisCnt.getXORKey ( ) );

		this.thisCnt.sendSocketMessage ( msgXOR );
	}

	private void sendFreeServerMessage ( byte [ ] msgB )
	{
		this.thisCnt.sendSocketMessage ( msgB );
	}

	private void log ( String log )
	{
		System.out.println ( "[LogicProcessor] " + log );
	}
}