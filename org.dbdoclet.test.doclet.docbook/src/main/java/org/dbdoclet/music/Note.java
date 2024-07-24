package org.dbdoclet.music;

import org.dbdoclet.music.annotation.Transpose;
import org.dbdoclet.music.annotation.Volume;

/**
 * Eine Musiknote.
 * 
 * <p>
 * Beispiel:
 * </p>
 * <img src="doc-files/note.png">
 * 
 * @author Michael Fuchs
 */
@Volume
public class Note extends AbstractElement<String, Integer> {

	private static final long serialVersionUID = 1L;

	/**
	 * Die Note C.
	 */
	public static final int PITCH_C =  0;
	
	/**
	 * Die Tonhöhe als ganzahliger Wert. Der Wert 0 (Null) entspricht dabei dem
	 * Ton c.
	 * 
	 */
	private int pitch;

	public Note(int pitch) {
		this.pitch = pitch;
	}

	/** Liefert die <code>Tonhöhe</code> zurück. */
	@Transpose
	public int getPitch() {
		return pitch;
	}

	/**
	 * Test
	 * 
	 * @param pitch
	 */
	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	/**
	 * Methode toElement() mit XML-Kommenatar <!-- Kommentar --> Ein Kommentar
	 * 
	 */
	public String toElement() {
		return "Element";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Abspielen einer Note
	 */
	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}
}