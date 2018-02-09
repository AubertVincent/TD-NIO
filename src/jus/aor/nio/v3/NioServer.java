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
 * NIO server with continuation management
 * RICM4 TP
 * F. Boyer
 */

public class NioServer implements Runnable{
	// The channel used to accept connections from server-side
	private ServerSocketChannel serverChannel;
	// Unblocking selector
	private Selector selector;
	// Ip address of the server
	private InetAddress hostAddress;
	// Continuations for reading messages
	private Map<SocketChannel,ReadCont> readConts = new HashMap<SocketChannel, ReadCont>();
	// Continuations for writing messages
	private Map<SocketChannel,WriteCont> writeConts = new HashMap<SocketChannel, WriteCont>();
	private Logger log;
	/**
	 * NIO engine initialization for server side
	 * @param port the host address and port of the server
	 * @throws IOException 
	 */
	public NioServer(int port) throws IOException{
		// create a new selector
		selector = SelectorProvider.provider().openSelector();
		// create a new non-blocking server socket channel
		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		// bind the server socket to the given address and port
		hostAddress = InetAddress.getByName("localhost");
		InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
		serverChannel.socket().bind(isa);
		// be notified when connection requests arrive
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		log= Logger.getLogger("jus/aor/nio/v3/NioServer."+port);
	}
	
	
	/**
	 * NIO engine mainloop
	 * Wait for selected events on registered channels
	 * Selected events for a given channel may be ACCEPT, CONNECT, READ, WRITE
	 * Selected events for a given channel may change over time
	 */
	public void run(){
		log.log(Level.FINE,"NioServer running");
		while(true){
			try{
				selector.select();
				Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();
				while(selectedKeys.hasNext()){
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					if			(!key.isValid()){ 			continue;
					}else if(key.isAcceptable()){		handleAccept(key);
					}else if(key.isReadable()){ 		handleRead(key);
					}else if(key.isWritable()){ 		handleWrite(key);
					}else if(key.isConnectable()){	handleConnect(key);
					}else 													System.err.println("  ---> unknow key=");
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
		// create continuations for the socketChannel
		SelectionKey k = socketChannel.keyFor(this.selector);
		readConts.put(socketChannel, new ReadCont(socketChannel));
		writeConts.put(socketChannel, new WriteCont(k, socketChannel));
	}
	/**
	 * Finish to establish a connection
	 * @param key the key of the channel on which a connection is requested
	 */
	private void handleConnect(SelectionKey key){
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try{
			socketChannel.finishConnect();
		}catch(IOException e){
			// cancel the channel's registration with our selector
			System.err.println(e);
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_READ);	
	}
	/**
	 * Close a channel 
	 * @param socketChannel the channel to close
	 */
	private void handleClose(SocketChannel socketChannel){
		try{
			socketChannel.close();
		}catch(IOException e){
			// nothing to do, the channel is already closed
		}
		socketChannel.keyFor(selector).cancel();
	}
	/**
	 * Handle incoming data event
	 * @param key the key of the channel on which the incoming data waits to be received 
	 * @throws ClassNotFoundException 
	 */
	private void handleRead(SelectionKey key) throws ClassNotFoundException{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try{
			Message msg = readConts.get(socketChannel).handleRead();
			if (msg != null){
				log.log(Level.FINE,"Full Message Received:" + msg);
				send(socketChannel, msg); 
			}					
		}catch(IOException e){
			// The channel has been closed abruptly
			handleClose(socketChannel);
		}
	}
	/**
	 * Handle outgoing data event
	 * @param key the key of the channel on which data can be sent 
	 */
	private void handleWrite(SelectionKey key){
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try{
			writeConts.get(socketChannel).handleWrite();
		}catch(IOException e){
			// The channel has been closed abruptly
			handleClose(socketChannel);
		}
	}
	/**
	 * Send data
	 * @param socketChannel the channel on which data that should be sent
	 * @param data the data that should be sent
	 * @throws IOException 
	 */
	public void send(SocketChannel socketChannel, Message data) throws IOException{
		// enqueue the data we want to send
		WriteCont writeCont = writeConts.get(socketChannel);
		writeCont.sendMsg(data);
	}
}
