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
	protected ArrayList<Message> msgs = new ArrayList<>() ;
	// buf contains the byte array that is currently written
	protected ByteBuffer buf = null;


	/**
	 * @param k
	 * @param sc
	 */
	public WriteCont(SelectionKey k,SocketChannel sc){
		super(sc);
		buf = ByteBuffer.allocate(100000000);
		key = k;
	}


	/**
	 * @return true if the msgs are not completly write.
	 */
	protected boolean isPendingMsg(){
		return !msgs.isEmpty();
	}


	/**
	 * Prepare the message. Put it in the waiting queue.
	 * @param data
	 * @throws IOException 
	 */
	protected void sendMsg(Message data) throws IOException{
		msgs.add(data);
		key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}


	/**
	 * @throws IOException
	 */
	protected void handleWrite() throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		switch (state) {
		case WRITING_DONE:
//			buf = intToBytes(msgs.get(0).marshall().length);
			buf.position(0);
			buf.limit(4);
			buf.putInt(msgs.get(0).marshall().length);
			buf.position(0);
			state = State.WRITING_LENGTH;
		case WRITING_LENGTH:
			socketChannel.write(buf);
			if (buf.remaining() == 0) {
				state = State.WRITING_DATA;
//				buf = ByteBuffer.wrap(msgs.get(0).marshall());
				buf.position(0);
				buf.limit(msgs.get(0).marshall().length);
				buf.put(msgs.get(0).marshall());
				buf.position(0);
				msgs.remove(0);
			}
			break;
		case WRITING_DATA:
			if (buf.remaining() > 0) {
				try {
					socketChannel.write(buf);
				} catch (IOException e) {
					// The channel has been closed
					key.cancel();
					socketChannel.close();
					return;
				}
			} else {
				if (!isPendingMsg()) {
					key.interestOps(SelectionKey.OP_READ);
				}
				state = State.WRITING_DONE;
			}
			break;
		}
	}
}
