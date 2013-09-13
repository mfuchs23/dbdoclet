package org.dbdoclet.music;

import java.io.Serializable;

/**
 * Abstrakte Vaterklasse für Musikelemente.
 * 
 * @author Michael Fuchs
 *
 * @param <E>
 * @param <F>
 */
public abstract class AbstractElement<E extends Comparable<? super E> & Serializable, F extends Serializable> implements Serializable,
	Comparable<AbstractElement<E, F>>, MusicElement {

    private static final long serialVersionUID = 1L;

    public E first;
    public E second;
    public Comparable<? super Number> third;

    public AbstractElement() {
	// 
    }
    
    public <U extends E, V extends E, W extends Comparable<? super Number>> AbstractElement(U arg1, V arg2, W arg3) {

	first = arg1;
	second = arg2;
	third = arg3;
    }

    public int compareTo(AbstractElement<E, F> o) {
	return 0;
    }
    
    public String element() {
        return toString();
    }
}
