package org.trianacode.shiwaall.collection;

// TODO: Auto-generated Javadoc
/**
 * The Class StringCollectionElement.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class StringCollectionElement extends CollectionElement<CharSequence> {

    /**
     * Instantiates a new string collection element.
     *
     * @param content the content
     */
    public StringCollectionElement(CharSequence content) {
        super(content);
    }

    /**
     * Instantiates a new string collection element.
     */
    public StringCollectionElement() {
    }

    /**
     * Append.
     *
     * @param sequence the sequence
     */
    public void append(CharSequence sequence) {
        if(content == null) {
            this.content = sequence;
        } else {
            this.content = this.content.toString() + sequence.toString();
        }
    }
}
