package client;

import shared.ChatGroup;
import shared.Client;
import shared.Shared;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class ClientChatGUI
{
	private JFrame                  mainFrame;
	private JPanel                  panelC, panelG;
	private JTextArea               area;
	private JTextPane               chat;
	private ClientLogicProcessor    clp;
	private DefaultListModel        mmodel;
	private DefaultListModel        gmodel;
	private JList                   glist;
	private JButton                 createBut, delBut;
	private boolean                 isInit;

	public ClientChatGUI ( ClientLogicProcessor clp )
	{
		this.clp        = clp;
		this.mainFrame  = null;
		this.panelC     = null;
		this.panelG     = null;
		this.area       = null;
		this.chat       = null;
		this.createBut  = null;
		this.delBut     = null;
		this.mmodel     = new DefaultListModel ( );
		this.gmodel     = new DefaultListModel ( );
		this.isInit     = false;

		this.initMainFrame ( );
	}

	private void initMainFrame ( )
	{
		this.mainFrame = new JFrame ( );

		this.mainFrame.setSize ( Shared.FRAME_WIDTH, Shared.FRAME_HEIGHT );
		this.mainFrame.setLayout ( null );
		this.mainFrame.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
		//this.mainFrame.pack ( );
		//this.mainFrame.getContentPane ( ).setLayout ( null );
		this.mainFrame.setResizable ( false );
		this.mainFrame.setLocationRelativeTo ( null );
		this.mainFrame.setVisible ( false );

		this.initGroupPanel ( );
		this.initChatPanel ( );

		this.panelC.setVisible ( false );
		this.panelG.setVisible ( true );

		this.mainFrame.add ( this.panelG );
		this.mainFrame.add ( this.panelC );
		this.mainFrame.revalidate ( );
		this.mainFrame.getContentPane ( ).validate();
		this.mainFrame.repaint ( );

		this.showNameDialog ( true );

		this.setTitle ( "" );
	}

	private void initChatPanel ( )
	{
		// CHAT PANEL
		this.panelC = new JPanel ( );

		this.panelC.setBounds ( 0, 0, Shared.FRAME_WIDTH, Shared.FRAME_HEIGHT );
		this.panelC.setLayout ( null );

		// TEXT AREA
		this.area = new JTextArea ( );

		this.area.setEditable ( true );
		this.area.setLineWrap ( true );
		this.area.setBounds
		(
			Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT - Shared.AREA_HEIGHT - 4*Shared.BORDER_HEIGHT,
			Shared.AREA_WIDTH,
			Shared.AREA_HEIGHT
		);

		JScrollPane areaScroll = new JScrollPane ( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		areaScroll.setViewportView ( this.area );
		areaScroll.setBorder ( BorderFactory.createLineBorder ( Color.BLACK, 2 ) );
		areaScroll.setBounds
		(
			Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT - Shared.AREA_HEIGHT - 4*Shared.BORDER_HEIGHT,
			Shared.AREA_WIDTH,
			Shared.AREA_HEIGHT
		);

		// SEND BUTTON
		JButton sendB = new JButton ( "SEND MESSAGE" );

		sendB.setBounds
		(
			Shared.FRAME_WIDTH - Shared.SEND_BUTTON_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT + Shared.SEND_BUTTON_HEIGHT - 3*Shared.BORDER_HEIGHT - Shared.AREA_HEIGHT,
			Shared.SEND_BUTTON_WIDTH,
			Shared.SEND_BUTTON_HEIGHT
		);

		sendB.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					prepareMessage ( area.getText ( ) );
				}
			}
		);

		// CHANGE NAME BUTTON
		JButton nameB = new JButton ( "CHANGE NAME" );

		nameB.setBounds
		(
			Shared.FRAME_WIDTH - Shared.CHANGE_NAME_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT + Shared.CHANGE_NAME_HEIGHT - 4*Shared.BORDER_HEIGHT - Shared.LIST_HEIGHT,
			Shared.CHANGE_NAME_WIDTH,
			Shared.CHANGE_NAME_HEIGHT
		);

		nameB.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					onNameChange ( );
				}
			}
		);

		// GROUP BUTTON
		JButton leaveB = new JButton ( "CHANGE GROUP" );

		leaveB.setBounds
		 (
			 Shared.FRAME_WIDTH - Shared.GROUP_BUTTON_WIDTH - 2*Shared.BORDER_WIDTH,
			 Shared.FRAME_HEIGHT + Shared.CHANGE_NAME_HEIGHT - 4*Shared.BORDER_HEIGHT -
				 Shared.LIST_HEIGHT + Shared.GROUP_BUTTON_HEIGHT + Shared.BORDER_HEIGHT/2,
			 Shared.GROUP_BUTTON_WIDTH,
			 Shared.GROUP_BUTTON_HEIGHT
		 );

		leaveB.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					onGroupChange ( );
				}
			}
		);

		// CHAT AREA
		this.chat = new JTextPane ( );

		this.chat.setEditable ( false );
		this.chat.setBounds
		(
			Shared.BORDER_WIDTH,
			Shared.BORDER_HEIGHT,
			Shared.CHAT_WIDTH,
			Shared.CHAT_HEIGHT
		);

		JScrollPane chatScroll = new JScrollPane ( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		chatScroll.setViewportView ( this.chat );
		chatScroll.setBorder ( BorderFactory.createLineBorder ( Color.darkGray, 1 ) );
		chatScroll.setBounds
		(
			Shared.BORDER_WIDTH,
			Shared.BORDER_HEIGHT,
			Shared.CHAT_WIDTH,
			Shared.CHAT_HEIGHT
		);

		// MEMBER LIST
		JList mlist = new JList ( this.mmodel );

		mlist.setSelectionMode ( ListSelectionModel.SINGLE_SELECTION );
		mlist.setLayoutOrientation ( JList.VERTICAL );

		JScrollPane mlistScroll = new JScrollPane ( );

		mlistScroll.setViewportView ( mlist );
		mlistScroll.setBounds
		(
			Shared.FRAME_WIDTH - Shared.LIST_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.BORDER_HEIGHT,
			Shared.LIST_WIDTH,
			Shared.LIST_HEIGHT
		);

		//
		this.panelC.add ( leaveB );
		this.panelC.add ( nameB );
		this.panelC.add ( mlistScroll );
		this.panelC.add ( areaScroll );
		this.panelC.add ( chatScroll );
		this.panelC.add ( sendB );
	}

	private void initGroupPanel ( )
	{
		// GROUP PANEL
		this.panelG = new JPanel ( );

		this.panelG.setBounds ( 0, 0, Shared.FRAME_WIDTH, Shared.FRAME_HEIGHT );
		this.panelG.setLayout ( null );

		// GROUP LIST
		this.glist = new JList ( this.gmodel );

		this.glist.setSelectionMode ( ListSelectionModel.SINGLE_SELECTION );
		this.glist.setLayoutOrientation ( JList.VERTICAL );

		this.glist.addListSelectionListener
		(
			new ListSelectionListener ( )
			{
				public void valueChanged ( ListSelectionEvent ev )
				{
					changeButton ( glist.getSelectedIndex ( ) );

					//System.out.print ( glist.getSelectedIndex ( ) );
				}
			}
		);

		JScrollPane glistScroll = new JScrollPane ( );

		glistScroll.setViewportView ( this.glist );
		glistScroll.setBounds
		(
			Shared.FRAME_WIDTH - Shared.LIST_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.BORDER_HEIGHT,
			Shared.LIST_WIDTH,
			Shared.LIST_HEIGHT
		);

		this.glist.setSelectedIndex ( 0 );

		// GROUP BUTTON
		JButton joinB = new JButton ( "JOIN GROUP" );

		joinB.setBounds
		(
			Shared.FRAME_WIDTH - Shared.GROUP_BUTTON_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT + Shared.CHANGE_NAME_HEIGHT - 4*Shared.BORDER_HEIGHT -
				Shared.LIST_HEIGHT + Shared.GROUP_BUTTON_HEIGHT + Shared.BORDER_HEIGHT/2,
			Shared.GROUP_BUTTON_WIDTH,
			Shared.GROUP_BUTTON_HEIGHT
		);

		joinB.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					onJoinGroup ( glist.getSelectedIndex ( ) );
				}
			}
		);

		// CHANGE NAME BUTTON
		JButton nameB = new JButton ( "CHANGE NAME" );

		nameB.setBounds
		(
			Shared.FRAME_WIDTH - Shared.CHANGE_NAME_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT + Shared.CHANGE_NAME_HEIGHT - 4*Shared.BORDER_HEIGHT - Shared.LIST_HEIGHT,
			Shared.CHANGE_NAME_WIDTH,
			Shared.CHANGE_NAME_HEIGHT
		);

		nameB.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					onNameChange ( );
				}
			}
		);

		// CREATE GROUP BUTTON
		this.createBut = new JButton ( "CREATE GROUP" );

		this.createBut.setBounds
		(
			Shared.FRAME_WIDTH - Shared.SEND_BUTTON_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT + Shared.SEND_BUTTON_HEIGHT - 3*Shared.BORDER_HEIGHT - Shared.AREA_HEIGHT,
			Shared.SEND_BUTTON_WIDTH,
			Shared.SEND_BUTTON_HEIGHT
		);

		this.createBut.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					showGroupDialog ( );
				}
			}
		);

		// CREATE GROUP BUTTON
		this.delBut = new JButton ( "DELETE GROUP" );

		this.delBut.setBounds
		(
			Shared.FRAME_WIDTH - Shared.SEND_BUTTON_WIDTH - 2*Shared.BORDER_WIDTH,
			Shared.FRAME_HEIGHT + Shared.SEND_BUTTON_HEIGHT - 3*Shared.BORDER_HEIGHT - Shared.AREA_HEIGHT,
			Shared.SEND_BUTTON_WIDTH,
			Shared.SEND_BUTTON_HEIGHT
		);

		this.delBut.addActionListener
		(
			new ActionListener ( )
			{
				public void actionPerformed ( ActionEvent e )
				{
					onGroupDestroy ( glist.getSelectedIndex ( ) );
				}
			}
		);

		this.delBut.setVisible ( false );

		//
		this.panelG.add ( this.createBut );
		this.panelG.add ( this.delBut );
		this.panelG.add ( joinB );
		this.panelG.add ( nameB );
		this.panelG.add ( glistScroll );
	}

	private void onGroupDestroy ( int id )
	{
		if ( id == -1 )
		{
			return;
		}

		this.clp.onGroupDestroy ( id );
	}

	private void changeButton ( int id )
	{
		if ( id == -1 )
		{
			this.createBut.setVisible ( true );
			this.delBut.setVisible ( false );

			return;
		}

		GroupInfo gi = this.clp.getGroups ( ).get ( id );

		if ( gi.isOwner ( ) )
		{
			this.createBut.setVisible ( false );
			this.delBut.setVisible ( true );
		}
		else
		{
			this.createBut.setVisible ( true );
			this.delBut.setVisible ( false );
		}
	}

	private void showGroupDialog ( )
	{
		JLabel ttl = new JLabel ( );

		ttl.setText ( Shared.DIALOG_GROUP_INFO );
		ttl.setFont ( new Font ( "TimesRoman", Font.PLAIN, 14 ) );

		JTextField usr = new JTextField ( );

		//( ( AbstractDocument) usr.getDocument ( ) ).setDocumentFilter ( new LimitDocumentFilter ( Shared.NAME_LENGTH ) );

		usr.setText ( Shared.DEFAULT_GROUP );

		final JComponent [ ] inpComp = new JComponent [ ]
			{
				ttl,
				usr,
			};

		ImageIcon   im  = new ImageIcon ( new BufferedImage ( 1, 1, BufferedImage.TYPE_INT_ARGB ) );
		int         rst = JOptionPane.showConfirmDialog ( null, inpComp,
			Shared.DIALOG_GROUP, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, im );

		if ( rst == JOptionPane.OK_OPTION )
		{
			String checkUsr = removeDeprecatedSymbols ( usr.getText ( ) );

			if ( checkUsr.length ( ) == 0 || this.clp.hasGroup ( checkUsr ) )
			{
				this.showGroupDialog ( );
			}
			else
			{
				this.clp.onGroupCreation ( checkUsr );
			}
		}
	}

	private void showNameDialog ( boolean firstTime )
	{
		JLabel ttl = new JLabel ( );

		ttl.setText ( Shared.DIALOG_NAME_INFO );
		ttl.setFont ( new Font ( "TimesRoman", Font.PLAIN, 14 ) );

		JTextField usr = new JTextField ( );

		//( ( AbstractDocument) usr.getDocument ( ) ).setDocumentFilter ( new LimitDocumentFilter ( Shared.NAME_LENGTH ) );

		usr.setText ( this.clp.getClient ( ).getName ( ) );

		final JComponent [ ] inpComp = new JComponent [ ]
			{
				ttl,
				usr,
			};

		ImageIcon   im  = new ImageIcon ( new BufferedImage ( 1, 1, BufferedImage.TYPE_INT_ARGB ) );
		int         rst = JOptionPane.showConfirmDialog ( null, inpComp,
			Shared.DIALOG_NAME, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, im );

		if ( rst == JOptionPane.OK_OPTION )
		{
			String checkUsr = removeDeprecatedSymbols ( usr.getText ( ) );

			if ( firstTime )
			{
				if ( checkUsr.length ( ) == 0 ) { checkUsr = Shared.DEFAULT_NAME; }

				this.clp.setClientName ( checkUsr );
			}
			else
			{
				if ( checkUsr.length ( ) > 0 )
				{
					this.clp.onNameChange ( checkUsr );
				}
			}
		}
	}

	public void showErrorDialog ( )
	{
		JOptionPane.showMessageDialog ( this.mainFrame, "Server is offline" );

		System.exit ( 0 );
	}

	private void onGroupChange ( )
	{
		if ( this.glist.getSelectedIndex ( ) == -1 )
		{
			this.glist.setSelectedIndex ( 0 );
		}

		this.clp.onGroupLeave ( );

		this.showGroupPanel ( );
	}

	public void showGroupPanel ( )
	{
		this.chat.setText ( "" );
		this.area.setText ( "" );

		this.panelC.setVisible ( false );
		this.panelG.setVisible ( true );

		this.mainFrame.revalidate ( );
		this.mainFrame.getContentPane ( ).validate();
		this.mainFrame.repaint ( );
	}

	private void onJoinGroup ( int id )
	{
		if ( id == -1 )
		{
			return;
		}

		ArrayList < GroupInfo > l = this.clp.getGroups ( );

		this.clp.joinGroup ( id );
	}

	public void joinedGroup ( )
	{
		this.panelC.setVisible ( true );
		this.panelG.setVisible ( false );

		this.mainFrame.revalidate ( );
		this.mainFrame.getContentPane ( ).validate ( );
		this.mainFrame.repaint ( );
	}

	private void onNameChange ( )
	{
		this.showNameDialog ( false );
	}

	public void setInit ( boolean flag )
	{
		this.isInit = flag;
	}

	public boolean getInit ( )
	{
		return this.isInit;
	}

	public void setVisible ( boolean flag )
	{
		this.mainFrame.setVisible ( flag );
	}

	public void clearTexter ( )
	{
		this.area.setText ( "" );
	}

	public void appendTextChat ( String str )
	{
		StyledDocument  doc     = this.chat.getStyledDocument ( );
		Style           style   = this.chat.addStyle ( "I'm a Style", null );

		StyleConstants.setForeground ( style, Color.lightGray );

		try { doc.insertString ( doc.getLength ( ), "< " + str + " >\n", style ); }
		catch ( BadLocationException e ) { e.printStackTrace ( ); }
	}

	public void setTitle ( String name )
	{
		String title = Shared.TITLE;

		if ( !name.isEmpty ( ) )
		{
			title += " (Group name " + name + ")";
		}

		title += " [" + this.clp.getClient ( ).getName ( ) + "]";

		this.mainFrame.setTitle ( title );
	}

	public void refreshListMember ( )
	{
		ArrayList < Client > l = this.clp.getClient ( ).getChatGroup ( ).getClients ( );

		this.mmodel.clear ( );

		for ( int i = 0; i < l.size ( ); i++ )
		{
			Client cnt = l.get ( i );

			this.mmodel.add ( i, cnt.getName ( ) );
		}
	}

	public void refreshGroupList ( )
	{
		ArrayList < GroupInfo > l = this.clp.getGroups ( );

		this.gmodel.clear ( );

		for ( int i = 0; i < l.size ( ); i++ )
		{
			GroupInfo gi = l.get ( i );

			if ( gi.isOwner ( ) )
			{
				this.gmodel.add ( i, "(YOUR) " + gi.getName ( ) + "(" + gi.getClientNum ( ) + ")" );
			}
			else
			{
				this.gmodel.add ( i, gi.getName ( ) + "(" + gi.getClientNum ( ) + ")" );
			}
		}
	}

	public void appendTextChat ( String name, String ip, int port, String str )
	{
		StyledDocument  doc     = this.chat.getStyledDocument ( );
		Style           style   = this.chat.addStyle ( "I'm a Style", null );

		StyleConstants.setForeground ( style, Color.black );
		StyleConstants.setBold ( style, true );

		String bold = name + "@" + ip + "/" + port + ": ";

		try
		{
			doc.insertString ( doc.getLength ( ), bold, style );
			doc.insertString ( doc.getLength ( ), str + "\n", new SimpleAttributeSet ( ) );
		}
		catch ( BadLocationException e )
		{
			e.printStackTrace ( );
		}
	}

	private void prepareMessage ( String msg )
	{
		while
		(
			msg.length ( ) > 0 &&
			(
				msg.charAt ( msg.length ( ) - 1 ) == '\n' ||
				msg.charAt ( msg.length ( ) - 1 ) == ' '
			)
		)
		{
			msg = new StringBuilder ( msg ).deleteCharAt ( msg.length ( ) - 1 ).toString ( );
		}

		if ( msg.length ( ) > 0 )
		{
			this.clp.onGroupSend ( msg );
			this.clearTexter ( );
		}
	}

	private String removeDeprecatedSymbols ( String str )
	{
		for ( int i = 0; i < Shared.DEPRECATED_NAME_SYMBOLS.length ( ); i++ )
		{
			str = str.replaceAll ( Shared.DEPRECATED_NAME_SYMBOLS.charAt ( i ) + "", "" );
		}

		return str;
	}

	/*private class LimitDocumentFilter extends DocumentFilter
	{
		private int lm;

		public LimitDocumentFilter ( int lm ) { this.lm = lm; 	}

		@Override
		public void replace ( FilterBypass fb, int off, int len, String str, AttributeSet att )
			throws BadLocationException
		{
			if ( str == null ) { return; }

			Document    doc = fb.getDocument ( );
			String      txt = new StringBuilder ( doc.getText ( 0,
				doc.getLength ( ) ) ).replace ( off, off + len, str ).toString ( );

			if ( off == 0 )
			{
				while ( txt.length ( ) > 0 && Shared.DEPRECATED_NAME_SYMBOLS.indexOf ( txt.charAt ( 0 ) ) >= 0 )
				{
					txt = new StringBuilder ( txt ).deleteCharAt ( 0 ).toString ( );
				}
			}

			super.remove ( fb, 0, fb.getDocument ( ).getLength ( ) );
			super.insertString ( fb, 0, txt, att );
			super.insertString ( fb, 2, "", att );
		}

		@Override
		public void remove ( FilterBypass fb, int off, int len ) throws BadLocationException
		{
			Document    doc = fb.getDocument ( );
			String      txt = new StringBuilder ( doc.getText ( 0,
				doc.getLength ( ) ) ).delete ( off, off + len ).toString ( );

			if ( off == 0 )
			{
				while ( txt.length ( ) > 0 && Shared.DEPRECATED_NAME_SYMBOLS.indexOf ( txt.charAt ( 0 ) ) >= 0 )
				{
					txt = new StringBuilder ( txt ).deleteCharAt ( 0 ).toString ( );
				}
			}

			super.remove ( fb, 0, fb.getDocument ( ).getLength ( ) );
			super.insertString ( fb, 0, txt, null );
			super.insertString ( fb, 2, "", null );
		}
	}*/
}