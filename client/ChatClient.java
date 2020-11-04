// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  public static String login_id;
  
  
  
  public static void setInfo(String id) 
  {
	  login_id = id;
	  
  }
  
  public static String getInfo() 
  {
	  return (String) login_id.subSequence(6,login_id.length());
  }
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String id) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.login_id = id ;
    
    openConnection();
    handleMessageFromClientUI("#login<" +id+">");
  }

  public ChatClient(String host, int port, ChatIF clientUI) throws IOException // le constructeur au cas ou aucun ID n'est specifie
  {
	  super(host, port); //Call the superclass constructor
	    this.clientUI = clientUI;
	    openConnection();  
	    System.out.println("ERROR - No login ID specified.  Connection aborted ");
	    quit();
  }
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  clientUI.display(msg.toString());
   
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    { 
    	char [] tableau = new char[message.length()];
    	tableau = message.toCharArray();
    	String commande ; // nom de la commande
    	
    	if( tableau[0]=='#') // tester le message afin de savoir s'il s'agit d'une commande 
    	{
    		commande = message.substring(1);
    		//S'il s'agit d'une commande , determiner laquelle et l'executer 
    		switch(commande) 
    		{
    		case "gethost":
    			sendToServer(getHost());
    			break ;
    		case "quit":
    			closeConnection();
    			quit();
    			break ;
    		case "logoff":
    			closeConnection(); 
    			break ; 
    		case "login" : 
    			if(isConnected()) 
				{
					sendToServer("Client already  connected ");
				}
    			openConnection();
    			break ;
    		case "getport" : 
    			sendToServer("Port actuel " +Integer.toString(getPort()));
    			break ;
    		default :  // Au cas ou il s'agit d'une commande contenant des arguments comme setPort ou setHost
    			String subcommande = (String) commande.subSequence(0,7);
    			switch(subcommande) 
    			{
    			case "setport":
    				if(isConnected()) 
    				{
    					sendToServer(getInfo() + " " +"Client already  connected ");
    				}
    				else 
    				{
    					//recuperer la nouvelle du port 
        				String nouveau_port = (String) commande.subSequence(7,commande.length());
        				setPort(Integer.parseInt(nouveau_port));// l'attribuer 	
    				}
    				
    				break ;
    			case "sethost": 
    				if(isConnected()) 
    				{
    					sendToServer(getInfo() + " " +"Client already  connected ");
    				}
    				else 
    				{
    					String nouvel_hote = (String) commande.subSequence(7,commande.length());
        				setHost(nouvel_hote);
    				}
    				
    				break;
    			default : 
    				if (subcommande.equals("login<#") )
    				{
    					sendToServer(getInfo() + " " +message);
    				}
    				else 
    				{
    					sendToServer(getInfo() + " " +"Commande inconnue");	
    				}
    				
    				break ;
    			
    			}
    		}
    	}
    	else 
    	{
    		sendToServer(getInfo() + " " +message);
    	}
    	
     
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  protected void connectionClosed() {
	  System.out.println(" connection closed ");
	}
  
  protected void connectionException(Exception exception) {
	  System.out.println("connection to the server  lost ");
	  quit();
	}
  
  protected void connectionEstablished() {
	  try {
		sendToServer(getInfo() +"has logged in");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

  
 

  
  
}
//End of ChatClient class
