package server;

import shared.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerLogicProcessor extends LogicProcessor
{
	private ChatServer cs;

	public ServerLogicProcessor(ChatServer cs)
	{
		this.cs = cs;
	}

	public void init()
	{
		this.cs.getClientManager().openConnection();
		this.cs.getGroupManager().openDefaultGroups();
	}

	public void listen()
	{
		this.cs.getClientManager().listen();
		this.cs.getGroupManager().listen();
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		/*while (true)
		{
			try
			{
				String in = inFromUser.readLine();
				this.sendGroupMessage(in, this.cs.getGroupManager().getGroup(0));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}*/
	}

	public void onClientArrive(Socket sock)
	{
		Client cnt = new Client(this, true);

		this.cs.getClientManager().addClient(cnt);
		cnt.setSocketConnection(sock);

		cnt.listenSocket();
		cnt.setChatGroup(null);
		cnt.generateXORKey();

		this.log("Client has arrived from " + cnt.getSocketIP() + ":" + cnt.getSocketPort() + "\n" +
			"Generated XOR key for the client " + new String ( cnt.getXORKey() ) + "\n" +
			"CLIENT NUMBER " + this.cs.getClientManager().getClients().size());
	}

	public void onClientLeave(Socket sock)
	{
		Client cnt = this.cs.getClientManager().getClient(sock);

		this.removeClientGroups ( cnt );

		if ( cnt.getChatGroup ( ) != null )
		{
			this.sendGroupMemberLeave(cnt);
		}

		this.cs.getClientManager().removeClient(cnt);
		this.sendGroupList();
		cnt.closeSocketConnection();

		this.log("Client " + cnt.getName() + " left" + "\n" +
			"CLIENT NUMBER " + this.cs.getClientManager().getClients().size() );
	}

	public void onGroupMessage(byte [] msgB,String ip,int port, ChatGroup cg)
	{
		msgB = Encryption.processXOR ( msgB, cg.getXORKey ( ) );

		String msg = new String ( msgB );

		this.log("Group member " + ip + ":" + port + " messages " + msg);
		Scanner sc = new Scanner(msg);
		String mtd = sc.next();
		sc.skip(" ");
		sc.useDelimiter(Shared.DELIMITER_MAIN);

		switch (mtd.toLowerCase())
		{
			case "join":{break;}
			case "name":{break;}
			case "leave":{break;}
		}
	}

	public void onClientMessage(byte [ ] msgB, Socket sock)
	{
		Client cnt = this.cs.getClientManager().getClient(sock);

		if(!cnt.hasPublicRSAForeignKey())
		{
			this.log ( "Got public RSA encrypt key from the client " + cnt.getName() );

			cnt.setPublicRSAForeignKey ( msgB );
			this.sendXORKey(cnt);
			this.sendGroupList();
		}
		else
		{
			msgB = Encryption.processXOR(msgB, cnt.getXORKey());

			String msg = new String ( msgB );

			this.log("Client " + cnt.getName() + " messages " + msg);

			Scanner sc = new Scanner(msg);
			String mtd = sc.next();
			sc.skip(" ");
			switch(mtd.toLowerCase())
			{
				case "join":{this.onClientGroupJoin(cnt, sc.nextInt());break;}
				case "name":{this.onClientNameChange(cnt, sc.next());break;}
				case "leave":{this.onClientGroupLeave(cnt); break;}
				case "create":{this.onClientGroupCreation(cnt, sc.next());break;}
				case "destroy":{this.onClientGroupDestroy(cnt, sc.nextInt());break;}
			}
		}
	}

	private void onClientGroupJoin(Client cnt,int id)
	{
		ChatGroup cg = this.cs.getGroupManager().getGroup(id);
		this.log("Client " + cnt.getName() + " has joined group " + cg.getName());
		cnt.setChatGroup(cg);

		this.sendGroupInfo(cnt);
		cg.addClient(cnt);
		this.sendGroupJoin(cnt, cg);
		this.sendGroupList();
	}

	private void onClientGroupCreation(Client cnt,String name)
	{
		GroupManager gm = this.cs.getGroupManager();

		gm.openGroup(name);

		ChatGroup cg = gm.getGroup(name);

		cg.setOwner(cnt);
		cnt.setChatGroup(cg);
		this.sendGroupInfo(cnt);
		cg.addClient(cnt);
		this.sendGroupList();
	}

	private void onClientGroupDestroy(Client cnt, int id)
	{
		ChatGroup cg = this.cs.getGroupManager().getGroup(id);

		this.log("Destroyed group " + cg.getName() + " of client " + cnt.getName());

		this.forceGroupToLeave ( cg, cnt );

		cg.setOwner(null);
		cnt.setChatGroup(null);
		cg.removeClient(cnt);

		cg.closeConnection();
		this.cs.getGroupManager().removeGroup(cg);
		this.sendGroupList();
	}

	private void removeClientGroups ( Client cnt )
	{
		ArrayList<ChatGroup> cgs = this.cs.getGroupManager().getClientGroups(cnt);

		for(ChatGroup cg:cgs)
		{
			this.forceGroupToLeave(cg, cnt);

			cg.closeConnection();
			this.cs.getGroupManager().removeGroup(cg);
			this.log("Destroyed group " + cg.getName() + " of client " + cnt.getName());
		}
	}

	private void forceGroupToLeave ( ChatGroup cg, Client skip )
	{
		for(Client cnt : cg.getClients())
		{
			if (cnt!=skip)
			{
				this.forceGroupMemberToLeave(cnt);
			}

			cnt.setChatGroup(null);
		}

		cg.removeClients();
	}

	private void onClientGroupLeave(Client cnt)
	{
		ChatGroup cg = cnt.getChatGroup();
		this.log("Client " + cnt.getName() + " has left group " + cg.getName());
		this.sendGroupMemberLeave(cnt);
		cg.removeClient(cnt);
		cnt.setChatGroup(null);
		this.sendGroupList();
	}

	private void onClientNameChange(Client cnt,String name)
	{
		this.log("Client " + cnt.getName() + " has changed name to " + name);
		cnt.setName(name);

		if(cnt.getChatGroup()!=null)
		{
			this.sendGroupMemberName(cnt);
		}
	}

	private void sendGroupJoin(Client cnt, ChatGroup cg)
	{
		this.log("Sent join of " + cnt.getName() + " client to the group " + cg.getName());

		String msg = "JOIN " + cnt.getName() + "/" + cnt.getSocketIP() + "/" + cnt.getSocketPort();

		this.sendGroupMessage(msg, cg);
	}

	private void sendGroupInfo(Client cnt)
	{
		ChatGroup cg = cnt.getChatGroup();

		this.log("Sent group " + cg.getName() + " to " + cnt.getName() + " with groupp XOR key");

		String msg = "GROUP " + cg.getId() + "/" + cg.getName() + "/" + cg.getIP() +
			"/" + cg.getPort() + "/" + cg.getClientNum() + "/" + cg.getClientList() +
			"/" + new String ( cg.getXORKey ( ) );

		this.sendClientMessage(msg,cnt);
	}

	private void sendGroupList()
	{
		this.log("Sent group info list");

		ArrayList<Client> cnts=this.cs.getClientManager().getClients();

		for(Client cnt:cnts)
		{
			if (cnt.getChatGroup()==null)
			{
				String msg = "GROUPS " + this.cs.getGroupManager().getGroupList(cnt);

				this.sendClientMessage( msg, cnt);
			}
		}
	}

	private void sendGroupMemberName(Client cnt)
	{
		ChatGroup cg = cnt.getChatGroup();
		this.log("Sent name of client " + cnt.getName() + " to the group " + cg.getName() );

		String msg = "NAME " + cnt.getName() + "/" + cnt.getSocketIP() + "/" + cnt.getSocketPort();
		this.sendGroupMessage(msg, cg);
	}

	private void forceGroupMemberToLeave(Client cnt)
	{
		ChatGroup cg = cnt.getChatGroup();
		this.log("Forced to leave client " + cnt.getName() + " from the group " + cg.getName());

		String msg = "LEAVE ";

		this.sendClientMessage(msg, cnt);
	}

	private void sendGroupMemberLeave(Client cnt)
	{
		ChatGroup cg = cnt.getChatGroup();

		this.log("Sent leave event of client " + cnt.getName() + " to the group " + cg.getName());

		String msg ="LEAVE " + cnt.getSocketIP() + "/" + cnt.getSocketPort();

		this.sendGroupMessage(msg, cg);
	}

	private void sendXORKey (Client cnt )
	{
		this.log ( "Sent encrypt XOR key to the client " + cnt.getName ( )  );

		byte [ ]    keyXOR = cnt.getXORKey ( );
		byte [ ]    keyRSA = Encryption.encryptRSA ( keyXOR, cnt.getPublicRSAForeignKey ( ) );

		this.sendFreeClientMessage ( keyRSA, cnt );
	}

	public void sendGroupMessage(String msg, ChatGroup cg)
	{
		byte [ ]    msgB    = msg.getBytes(StandardCharsets.UTF_8);
		byte [ ]    msgXOR  = Encryption.processXOR(msgB, cg.getXORKey() );

		cg.sendMessage(msgXOR);
	}

	public void sendClientMessage(String msg, Client cnt)
	{
		byte [ ]    msgB    = msg.getBytes(StandardCharsets.UTF_8);
		byte [ ]    msgXOR  = Encryption.processXOR(msgB, cnt.getXORKey() );

		cnt.sendSocketMessage(msgXOR);
	}

	public void sendFreeClientMessage(byte [ ] msg, Client cnt)
	{
		cnt.sendSocketMessage(msg);
	}

	private void log(String log)
	{
		System.out.println("[LogicProcessor] " + log);
	}
}