/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.aor.nio.v3;

import java.nio.channels.SocketChannel;

import java.nio.ByteBuffer;

/**
 * Classe Générale à toutes les continuations du projet.
 * Elle définit essentiellement des fonctions de confort pour
 * la lecture et l'écriture de types primitifs (int) avec un
 * Bytebuffer qq.
 * @author morat 
 */
public abstract class Continuation{
	protected SocketChannel socketChannel;
	/**
	 * fixe le socketChannel utilisé par cette continuation
	 * @param sc le socketchannel
	 */
	protected Continuation(SocketChannel sc){ socketChannel=sc;} 
	/**
	 * Convert a byte array to an int
	 * @param buffer le byteBuffer 
	 * @return l'entier en big-endian
	 */
	protected static final int bytesToInt(ByteBuffer buffer){
		return buffer.getInt(0);
	}
	/**
	 * made un byteBuffer dfor the integer i
	 * @param i the integer to be bufferised
	 * @return the buffer of the in value i.
	 */
	protected static final ByteBuffer intToBytes(int i){
    ByteBuffer buffer = ByteBuffer.allocate(4); 
    buffer.putInt(i); buffer.position(0);
    return buffer;
	}
}
