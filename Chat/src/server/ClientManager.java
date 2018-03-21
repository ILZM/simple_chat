package server;

import shared.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable
{
    private ServerLogicProcessor slp;
    private ArrayList<Client>  clients;
    private ServerSocket ss;
    private Thread th;
    private int port;

    public ClientManager(ServerLogicProcessor slp,int port)
    {
		this.slp = slp;
		this.clients = new ArrayList<>();
		this.port = port;
		this.th = new Thread(this,"Client manager thread");
    }

    public void openConnection()
    {
	    try
	    {
	        this.ss = new ServerSocket(this.port);
	        this.log("Created TCP server socket listener on " + this.port);
	    }
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}
    }

    public void listen()
    {
        this.th.start();
    }

    public void closeConnection()
    {
	    try
	    {
			this.th.interrupt();
			this.ss.close();
	    }
	    catch ( Exception e )
	    {
		    e.printStackTrace ( );
	    }
    }

    public void addClient(Client cnt)
    {
	this.clients.add(cnt);
    }

    public Client getClient(Socket sock)
    {
		for(Client cnt : this.clients)
		{
			if(cnt.getSocket() == sock)
			{
				return cnt;
			}
		}

		return null;
	}

    public Client getClient(String ip, int port)
    {
		for (Client cnt : this.clients)
		{
			if(cnt.getSocketIP().equals(ip) && cnt.getSocketPort() == port)
			{
				return cnt;
			}
		}

		return null;
	}

	public ArrayList<Client> getClients()
	{
		return this.clients;
	}

    public void removeClient(Client cnt)
    {
	this.clients.remove(cnt);
    }

    @Override
    public void run()
    {
		while(!this.th.isInterrupted())
		{
			try{this.slp.onClientArrive(this.ss.accept());}
			catch(IOException e){break;}
		}
	}

    private void log(String log)
    {
        System.out.println("[ClientListener] " + log);
    }
}