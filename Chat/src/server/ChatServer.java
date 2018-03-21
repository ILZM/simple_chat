package server;

import shared.Shared;

import java.io.*;

public class ChatServer
{
	public static void main(String[] args)
	{
		ChatServer cs = new ChatServer(Shared.UDP_IP, Shared.TCP_PORT);
	}

	private ServerLogicProcessor slp;
	private ClientManager cm;
	private GroupManager gm;

	public ChatServer(String uIP,int tPort)
	{
		this.slp = new ServerLogicProcessor(this);
		this.cm = new ClientManager(this.slp, tPort);
		this.gm = new GroupManager(this.slp, uIP);

		this.slp.init();
		this.slp.listen();
	}

	public GroupManager getGroupManager()
	{
		return this.gm;
	}

	public ClientManager getClientManager()
	{
		return this.cm;
	}
}