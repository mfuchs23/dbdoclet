/**
 * The class <code>Thought</code> represents any thoughts a human being can have
 * during a day.
 *
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class Thought {

    /**
     * The method <code>isPrivate</code>.
     *
     * @author Michael Fuchs
     * @version 1.0
     * @since   0.8
     * @see java.lang.Boolean
     * @see ThoughtException
     * @see <a href="http://www.dbdoclet.org">dbdoclet Home</a>
     * @see "DocBook Doclet Homepage"
     * @deprecated See method bigBrother
     * @param flag a <code>boolean</code> value
     * @return void
     */
    private void isPrivate(boolean flag) {

    }

    /**
     * The method <code>isPrivate</code> is true, if this thought is a private
     * thoughts.
     *
     * <div id="warning_is_private">
     * <p>Please do not read any further!</p>
     * </div>
     *
     * @return a <code>boolean</code> value
     */
    private boolean isPrivate() {
    	return true;
    }

    /**
     * The method <code>isReallyImportant</code> returns true, if this thought
     * is a <b>really</b> important thought.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isReallyImportant() {
    	return true;
    }

    /**
     * The method <code>isImportant</code> returns true, if this thought is
     * important.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isImportant() {
    	return true;
    }

    /**
     * The method <code>think</code> is executed, if the thought is thought.
     *
     * Use this method as often as you can, even if the return type id void.
     *
     * @exception ThoughtException Something interrupted. Hopefully something nice!
     */
    public void think() throws ThoughtException {

    }
}
