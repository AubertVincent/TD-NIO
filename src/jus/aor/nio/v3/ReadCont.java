package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author morat
 */
public class ReadCont extends Continuation {

	private ByteBuffer readBuf;
	private ByteBuffer readInt;
	private int state;
	
	private static final int READINT = 0;
	private static final int READMSG = 1;
	
	/**
	 * @param sc
	 */
	public ReadCont(SocketChannel sc) {
		super(sc);
		readInt = ByteBuffer.allocate(4);
		state = READINT;
	}

	/**
	 * @return the message
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected Message handleRead() throws IOException, ClassNotFoundException {
		switch(state) {
		case(READINT):
			socketChannel.write(readInt);
			if(readInt.remaining() <= 0) {
				readBuf = ByteBuffer.allocate(bytesToInt(readInt));
				state = READMSG;
			}
			break;
		case(READMSG):
			socketChannel.write(readBuf);
			if(readBuf.remaining() <= 0) {
				readInt.clear();
				state = READINT;
				return new Message(readBuf.array());
			}
			break;
		default:
		}
		return null;
	}
}
