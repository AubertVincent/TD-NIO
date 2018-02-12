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
	
	private enum State{COMPLETE, READING_LENGTH,READING_DATA;}
	
	/**
	 * @param sc
	 */
	public ReadCont(SocketChannel sc) {
		super(sc);
		readBuf = ByteBuffer.allocate(4);
		state = State.COMPLETE;
		nbSteps = 0;
	}

	/**
	 * @return the message
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected Message handleRead() throws IOException, ClassNotFoundException {
		switch(state) {
		case COMPLETE:
			readBuf.clear();
			readBuf.allocate(4);
			state = State.READING_LENGTH;
			nbSteps = 0;
		case READING_LENGTH:
			socketChannel.write(readBuf);
			nbSteps++;
			if(readBuf.remaining() <= 0) {
				readBuf = ByteBuffer.allocate(bytesToInt(readBuf));
				state = State.READING_DATA;
			}
			break;
		case READING_DATA:
			socketChannel.write(readBuf);
			nbSteps++;
			if(readBuf.remaining() <= 0) {
				state = State.COMPLETE;
				return new Message(readBuf.array(), nbSteps);
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
