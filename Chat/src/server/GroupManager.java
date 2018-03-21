package server;

import shared.ChatGroup;
import shared.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupManager
{
	private ServerLogicProcessor slp;
	private ArrayList<ChatGroup> groups;
	private String ip;

	public GroupManager(ServerLogicProcessor slp,String ip)
	{
		this.groups = new ArrayList<>();
		this.slp = slp;
		this.ip = ip;
	}
	public void openDefaultGroups()
	{
		this.openGroup("Default");
		this.openGroup("CSCI 333 Class");
	}

	public void listen()
	{
		for(ChatGroup cg : this.groups)
		{
			cg.listen();
		}
	}

	public void openGroup(String name)
	{
		ChatGroup cg = new ChatGroup(this.slp, true);

		this.groups.add(cg);
		cg.createGroupConnection(this.groups.size()-1, name, this.ip);

		cg.generateXORKey();
	}

	public ChatGroup getGroup(int id)
	{
		return this.groups.get(id);
	}

	public ChatGroup getGroup(String name)
	{
		for(ChatGroup cg : this.groups)
		{
			if(cg.getName().equals(name))
			{
				return cg;
			}
		}

		return null;
	}

	public void removeGroup(ChatGroup cg)
	{
		this.groups.remove(cg);
	}

	public ArrayList<ChatGroup> getClientGroups(Client cnt)
	{
		ArrayList<ChatGroup> cgs = new ArrayList<>();

		for(ChatGroup cg : this.groups)
		{
			if(cg.getOwner()==cnt)
			{
				cgs.add(cg);
			}
		}

		return cgs;
	}

	public String getGroupList(Client cnt)
	{
		String res = "";
		
		for(int i=0; i<this.groups.size();i++)
		{
			ChatGroup cg = this.groups.get(i);

			res += i + "/" + cg.getName() + "/" + cg.getClientNum() + "/";

			if (cnt == cg.getOwner())
			{
				res += "1" + "/";
			}
			else
			{
				res += "0" + "/";
			}
		}

		return new StringBuilder(res).deleteCharAt(res.length()-1).toString();
	}
}