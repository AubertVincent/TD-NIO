package jus.aor.nio.v3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NIO client with continuation management
 * RICM4 TP
 * F. Boyer
 */
public class NioClient implements Runnable{
	// The channel used to communicate with the server
	private SocketChannel clientChannel;
	// Unblocking selector
	private Selector selector;
	// address server
	private InetAddress serverAddress;
	// The client only needs one read (resp. write) continuation 
	ReadCont readCont;
	WriteCont writeCont;
	// The message to send to the server
	Message msg;
	// The client Id
	private int clientId;
	// the logger used
	private Logger log;
	// the number of itérations to be done by the client
	private int nbIterations;
	/**
	 * NIO engine initialization for server side
	 * @param serverAddressName the server address name
	 * @param port the server port
	 * @param id the client id
	 * @param nbIterations 
	 * @throws IOException 
	 */
	public NioClient(String serverAddressName, int port, int id, int nbIterations) throws IOException{
		this.msg = new Message(id);
		this.clientId = id;
		this.nbIterations=nbIterations;

		serverAddress = InetAddress.getByName(serverAddressName);
		// create a new selector
		selector = SelectorProvider.provider().openSelector();
		// create a new non-blocking server socket channel
		clientChannel = SocketChannel.open();
		clientChannel.configureBlocking(false);
		// be notified when connection requests arrive
		clientChannel.register(selector, SelectionKey.OP_CONNECT);
		// les automates 
		readCont = new ReadCont(clientChannel);
		writeCont = new WriteCont(clientChannel.keyFor(this.selector), clientChannel);
		// connection to the server
		clientChannel.connect(new InetSocketAddress(serverAddress, port));
		log = Logger.getLogger("jus/aor/nio/v3/NioClient."+clientId);
	}
	/**
	 * NIO engine mainloop
	 * Wait for selected events on registered channels
	 * Selected events for a given channel may be ACCEPT, CONNECT, READ, WRITE
	 * Selected events for a given channel may change over time
	 */
	public void run(){
		log.log(Level.FINE,"NioClient running");
		// control the exexution of the client
		for(int iteration=0; iteration<nbIterations;iteration++){
			try{
				selector.select();
				Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();
				while(selectedKeys.hasNext()){
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					if			 (!key.isValid()){			continue;		
					}else if (key.isAcceptable()){	handleAccept(key);
					}else if (key.isReadable()){		handleRead(key);
					}else if (key.isWritable()){		handleWrite(key);
					}else if (key.isConnectable()){ handleConnect(key);
					}else 													System.err.println("  ---> unknown key=");
				}
			}catch(Exception e){
				e.printStackTrace(System.err);
			}
		}
	}
	/**
	 * Accept a connection and make it non-blocking
	 * @param key the key of the channel on which a connection is requested
	 */
	private void handleAccept(SelectionKey key){
		SocketChannel socketChannel = null;
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try{
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
		}catch(IOException e){
			// as if there was no accept done
			return;
		}
		// be notified when there is incoming data 
		try{
			socketChannel.register(this.selector, SelectionKey.OP_READ);
		}catch(ClosedChannelException e){
			handleClose(socketChannel);
		}
	}
	/**
	 * Finish to establish a connection
	 * @param key the key of the channel on which a connection is requested
	 * @throws IOException 
	 */
	private void handleConnect(SelectionKey key) throws IOException{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try{
			socketChannel.finishConnect();
		}catch(IOException e){
			// cancel the channel's registration with our selector
			System.err.println(e);
			key.cancel();
			return;
		}	
		// set up READ interest during all execution time
		key.interestOps(SelectionKey.OP_READ);	
		// when connected, send a message to the server 
		Message msg= new Message(clientId);
		send(msg);
	}
	/**
	 * Close a channel 
	 * @param socketChannel the channel to close
	 */
	private void handleClose(SocketChannel socketChannel){
		socketChannel.keyFor(selector).cancel();
		try{
			socketChannel.close();
		}catch(IOException e){
			//nothing to do, the channel is already closed
		}
	}
	/**
	 * Handle incoming data event
	 * @param key the key of the channel on which the incoming data waits to be received 
	 * @throws ClassNotFoundException 
	 */
	private void handleRead(SelectionKey key) throws ClassNotFoundException{
		try{
			Message msg = readCont.handleRead();
			if (msg != null){
				log.log(Level.FINE,String.format("Full Message Received : %s",msg));
				send(msg);
			}					
		}catch (IOException e){
			// The channel has been closed abruptly
			handleClose((SocketChannel) key.channel());
		}
	}
	/**
	 * Handle outgoing data event
	 * @param key the key of the channel on which data can be sent 
	 */
	private void handleWrite(SelectionKey key){
		try{
			writeCont.handleWrite();
		} catch (IOException e) {
			handleClose((SocketChannel) key.channel());
		}
	}
	/**
	 * Send message
	 * @param msg the message that should be sent
	 * @throws IOException 
	 */
	public void send(Message msg) throws IOException{
		// enqueue the data we want to send
		writeCont.sendMsg(msg);
	}
}
