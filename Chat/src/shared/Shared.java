package shared;

public class Shared
{
	public static final String  TCP_IP          = "localhost";
	public static final String  UDP_IP          = "227.3.7.3";
	public static final int     TCP_PORT        = 21337;
	public static final int     UDP_BUFFER_SIZE = 1024;

	public static final String  TITLE               = "Chat Client";
	public static final int     FRAME_WIDTH         = ( int )( 640 * 1.0 );
	public static final int     FRAME_HEIGHT        = ( int )( 480 * 1.0 );
	public static final String  DIALOG_NAME         = "Nickname";
	public static final String  DIALOG_NAME_INFO    = "Enter your nickname";
	public static final String  DIALOG_GROUP        = "Group name";
	public static final String  DIALOG_GROUP_INFO   = "Enter unique group name";

	public static final int BORDER_WIDTH        = 10;
	public static final int BORDER_HEIGHT       = 10;
	public static final int AREA_WIDTH          = 2 * FRAME_WIDTH / 3;
	public static final int AREA_HEIGHT         = 86;
	public static final int CHAT_WIDTH          = AREA_WIDTH;
	public static final int CHAT_HEIGHT         = FRAME_HEIGHT - AREA_HEIGHT - 7*BORDER_HEIGHT;
	public static final int SEND_BUTTON_WIDTH   = FRAME_WIDTH - AREA_WIDTH - 4*BORDER_WIDTH;
	public static final int SEND_BUTTON_HEIGHT  = AREA_HEIGHT / 3;
	public static final int LIST_WIDTH          = FRAME_WIDTH - AREA_WIDTH - 4*BORDER_WIDTH;
	public static final int LIST_HEIGHT         = 2 * CHAT_HEIGHT / 3;
	public static final int CHANGE_NAME_WIDTH   = LIST_WIDTH;
	public static final int CHANGE_NAME_HEIGHT  = AREA_HEIGHT / 3;
	public static final int GROUP_BUTTON_WIDTH  = LIST_WIDTH;
	public static final int GROUP_BUTTON_HEIGHT = CHANGE_NAME_HEIGHT;

	public static final String  DEFAULT_NAME            = "Client";
	public static final String  DEFAULT_GROUP           = "Default";
	public static final int     MAX_NAME_LENGTH         = 16;
	public static final String  DEPRECATED_NAME_SYMBOLS = "/ \n\r";

	public static final String  DELIMITER_MAIN  = "/";

	public static final int minXORKeyLen    = 8;
	public static final int maxXORKeyLen    = 16;
	public static final int RSAResistance   = 1024;

	private Shared ( ) { }
}