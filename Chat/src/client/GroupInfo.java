package client;

public class GroupInfo
{
	private String  name;
	private int     id, cn;
	private boolean isOwner;

	public GroupInfo ( int id, String name, int cn, int iOwner )
	{
		this.id         = id;
		this.name       = name;
		this.cn         = cn;

		if ( iOwner == 0 ) { this.isOwner = false; }
		else { this.isOwner = true; }

		this.log ( "Got group info with id " + this.id + " name " + this.name +
			" client number " + this.cn + " owner " + iOwner );
	}

	public int getId ( ) { return this.id; }

	public String getName ( ) { return this.name; }

	public int getClientNum ( ) { return this.cn; }

	public boolean isOwner ( )
	{
		return this.isOwner;
	}

	private void log ( String log )
	{
		System.out.println ( "[GroupInfo] " + log );
	}
}