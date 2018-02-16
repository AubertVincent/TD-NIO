package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author morat, chanet
 */
public class ReadCont extends Continuation {

	private ByteBuffer readBuf;
	private State state;
	private int nbSteps;
	private int length;
	
	private enum State{COMPLETE, READING_LENGTH, READING_DATA;}
	
	/**
	 * @param sc
	 */
	public ReadCont(SocketChannel sc) {
		super(sc);
		readBuf = ByteBuffer.allocate(100000000);
		state = State.COMPLETE;
		nbSteps = 0;
		length = 0;
	}

	/**
	 * @return the message
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected Message handleRead() throws IOException, ClassNotFoundException {
		switch(state) {
		case COMPLETE:
			readBuf.position(0);
			readBuf.limit(4);
			state = State.READING_LENGTH;
			nbSteps = 0;
			length = 0;
		case READING_LENGTH:
			socketChannel.read(readBuf);
			if(readBuf.remaining() <= 0) {
				length = bytesToInt(readBuf);
				readBuf.position(0);
				readBuf.limit(length);
				state = State.READING_DATA;
			} else {
				nbSteps++;
				break;
			}
		case READING_DATA:
			socketChannel.read(readBuf);
			nbSteps++;
			if(readBuf.remaining() <= 0) {
				state = State.COMPLETE;
				byte[] dst = new byte[length];
				readBuf.position(0);
				readBuf.get(dst);
				return new Message(dst, nbSteps);
			}
			break;
		default:
		}
		return null;
	}
	
	/**
	 * @return the number of steps done
	 */
	protected int getNbSteps() {
		return nbSteps;
	}
}
