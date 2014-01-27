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
     * @exception WrongNoteException - Ein falscher Ton wurde hinzugefügt!
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
     * Liefert eine Liste, der in diesem Motiv verwendeteten Noten.
     * 
     * @return Note[] Liste aller Noten
     */
    public Note[] getNotes() {
        return notes;
    }
}
