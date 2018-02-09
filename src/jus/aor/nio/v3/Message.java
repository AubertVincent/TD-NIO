package jus.aor.nio.v3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Le message servant de data à transférer contenant
 * diverses informations permettant l'identification 
 * de celui-ci.
 * @author morat 
 */
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	/** une information asociée au message, contriendra ses états successifs */
	private String data;
	/** l'identification du client */
	private int id;
	/** la date de la dernière manipulation de ce message */
	private Calendar date;
	/** le nombre d'aller-retour de ce message dans la communication client/serveur */
	private int nexchanges;
	/** nombre de lectures élémentaires */
	private int nsteps;
	/**
	 * Constructeur initial
	 * @param id l'identificateur du client
	 */
	public Message(int id){
		this.data="";
		this.id = id;
		this.date = Calendar.getInstance();
		this.nexchanges = 0;
		this.data = toString() + data;
	}
	/**
	 * construit un message à partir de sa sérialisation
	 * @param marshalled la sérialisation à un
	 * marshalliser
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Message(byte[] marshalled) throws IOException, ClassNotFoundException{
		try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(marshalled));){
			Message m = (Message)ois.readObject();
			this.data=m.data;
			this.id=m.id;
			this.nexchanges = m.nexchanges;
			this.date = Calendar.getInstance();
			this.nsteps=m.nsteps;
		}catch(ClassNotFoundException|IOException e){
			throw e;
		}
	}
	/**
	 * construit un message à partir de sa sérialisation et du nombre de lectures
	 * ayant qui ont été nécessaires à son acquisition. La date est ajustée au moment 
	 * de cette création et la data associée est augmentée de de la représentation
	 * de ce message (progression arithmétique de 43).
	 * @param marshalled la sérialisation à unmarshalliser
	 * @param nbSteps le nombre de pas de lecture utilisés.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Message(byte[] marshalled, int nbSteps) throws ClassNotFoundException, IOException{
		this(marshalled);
		this.date = Calendar.getInstance();
		this.nsteps=nbSteps;
		this.data = toString()+" "+data;
	}
	/**
	 * Marshalisation du message;
	 * @return la sérialisation de l'objet
	 * @throws IOException 
	 */
	public byte[] marshall() throws IOException{
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos)){
			oos.writeObject(this);
			return bos.toByteArray();
		}catch(IOException e){
			throw e;
		}
	}
	/**
	 * incrémente le nombre d'échanges du message.
	 */
	public void incrementExchange() {this.nexchanges++;}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		int hours =date.get(Calendar.HOUR_OF_DAY);
		int minutes = date.get(Calendar.MINUTE);
		int seconds = date.get(Calendar.SECOND);
		int nanos = (int)(date.getTime().getTime()%1e3);
		return String.format("(%02d:%02d:%02d.%03d) %2d[%10d / %3d / %4d]",hours,minutes,seconds,nanos, id, data.length(), nsteps, nexchanges);
	}
}

