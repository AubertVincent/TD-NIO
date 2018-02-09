package jus.aor.nio.v3;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Un Handler permettant d'écrire sur le support IO.
 * @author Morat
 */
class IOHandler extends Handler {
	PrintStream out;
	/**
	 * @param out 
	 * 
	 */
	public IOHandler(PrintStream out){
		this.out=out;
	}
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() throws SecurityException{}
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush(){}
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record){
		out.println(record.getMessage());
		if(record.getLevel().intValue()<=Level.FINE.intValue() & record.getThrown()!=null) record.getThrown().printStackTrace();
	}
}