package org.dbdoclet.music;

import java.util.ArrayList;

/**
 * Ein Motiv.
 * 
 * @author mfuchs
 * 
 */
public class Motiv {

	// private static final int STANDARD_HEIGHT = 300;

	private Note[] notes;

	/**
	 * Fügt dem Motiv eine Note {@link Note} hinzu.
	 *
	 * @exception WrongNoteException
	 *                - Ein falscher Ton wurde hinzugefügt!
	 */
	public void addNote(Note note) throws WrongNoteException {

	}

	/**
	 * Fügt mehrere Noten hinzu.
	 * 
	 * @param notes
	 * @throws WrongNoteException
	 */
	public void addNotes(Note[] notes) throws WrongNoteException {

	}

	/**
	 * Fügt mehrere Noten hinzu.
	 * 
	 * @param notes
	 */
	public void addNotes(ArrayList<Note> notes) {

	}

	/**
	 * Liefert eine Liste aller Noten.
	 * 
	 * <p>
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec sagittis
	 * nisl augue, eget feugiat purus aliquam ac. Aliquam erat volutpat.
	 * Pellentesque eu nulla diam. Aliquam cursus, leo suscipit faucibus
	 * sollicitudin, leo nisl placerat ante, cursus sollicitudin purus velit
	 * eget urna.
	 * </p>
	 * 
	 * <h3>Morbi quis pulvinar nisi</h3>
	 *  
	 * <p>Nullam laoreet, augue eu consectetur
	 * lobortis, ante enim aliquam sem, eu tristique ligula arcu ut enim. Donec
	 * quis erat porta, interdum erat in, congue risus. Nunc a turpis a lacus
	 * consequat molestie ut vel orci. Praesent et viverra odio.
	 * </p>
	 * 
	 * @return Note[] Liste aller Noten
	 */
	public Note[] getNotes() {
		return notes;
	}
}
