package client;

import shared.ChatGroup;
import shared.Client;
import shared.Shared;

import java.io.*;

public class ChatClient
{
	public static void main ( String args [ ] )
	{
		ChatClient cl = new ChatClient ( Shared.TCP_IP, Shared.TCP_PORT );
	}

	private ClientLogicProcessor    clp;
	private ClientChatGUI           gui;

	public ChatClient ( String tIP, int tPort )
	{
		this.clp    = new ClientLogicProcessor ( this );
		this.gui    = new ClientChatGUI ( this.clp );

		this.clp.init ( tIP, tPort );
		this.clp.listen ( );
	}

	public ClientChatGUI getGUI ( )
	{
		return this.gui;
	}
}