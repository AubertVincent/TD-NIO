package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * @author morat 
 */
public class WriteCont extends Continuation{
	private SelectionKey key;
	// state automata
	private enum State{WRITING_DONE, WRITING_LENGTH,WRITING_DATA;}
	// initial state
	protected State state = State.WRITING_DONE;
	// the list of bytes messages to write
	protected ArrayList<byte[]> msgs = new ArrayList<>() ;
	// buf contains the byte array that is currently written
	protected ByteBuffer buf = null;


	/**
	 * @param k
	 * @param sc
	 */
	public WriteCont(SelectionKey k,SocketChannel sc){
		super(sc);
		key = k;
	}


	/**
	 * @return true if the msgs are not completly write.
	 */
	protected boolean isPendingMsg(){
		// TODO
		System.out.println("NOT IMPLEMENTED YET");
		return false;
	}


	/**
	 * @param data
	 * @throws IOException 
	 */
	protected void sendMsg(Message data) throws IOException{
		// TODO
		System.out.println("NOT IMPLEMENTED YET");
	}


	/**
	 * @throws IOException
	 */
	protected void handleWrite()throws IOException{
		// TODO
		System.out.println("NOT IMPLEMENTED YET");
	}
}
