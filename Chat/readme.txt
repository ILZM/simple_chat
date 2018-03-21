Authors: Nurmurat Smagulov & Akmaral Tokbergenova

Usage:
Firstly, run the server from the server/ChatServer class and then run multiple clients from the client/ChatClient class.

Runs on localhost, internet connection is not required.

Class information:
client/ChatClient 			- contains main method and initializes the chat client
client/ClientChatGUI 		- GUI class
client/GroupInfo			- nerfed ChatGroup class
client/ClientLogicProcessor	- class which handles with rules and logic of application usage

server/ChatServer			- contains main method and initializes the chat server
server/ClientManager		- keeps all the connected clients
server/GroupManager			- manages all the groups
server/ServerLogicProcessor	- class which handles with rules and logic of connection and groups

shared/ChatGroup			- chat group class, contains UDP listener class
shared/Client 				- client class, contains TCP listener class
shared/Encryption			- class handles with encryption methods
shared/LogicProcessor 		- abstract class to make shared classes sharable on client and server classes
shared/Shared 				- contains all the constants
shared/TCPListener			- class handless TCP connection
shared/UDPListener 			- class tracks MulticastSocket and UDP connections