package org.dbdoclet.music;

import java.io.Serializable;

public interface MusicElement extends Serializable, Cloneable {

	/**
	 * @deprecated Use {@linkplain #toElement()} instead
	 * 
	 * @return String
	 */
	public String element();

	/**
	 * Curabitur suscipit, augue sit amet vulputate commodo, dolor sapien
	 * pretium neque, ac "commodo" {@link MusicElement#element()} turpis purus a
	 * dolor.
	 * 
	 * @return String
	 */
	public String toElement();

	@Deprecated
	public void sound();

	public void play();
}
