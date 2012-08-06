package org.trianacode.shiwaall.collection;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class StringCollectionElement extends CollectionElement<CharSequence> {

    public StringCollectionElement(CharSequence content) {
        super(content);
    }

    public StringCollectionElement() {
    }

    public void append(CharSequence sequence) {
        if(content == null) {
            this.content = sequence;
        } else {
            this.content = this.content.toString() + sequence.toString();
        }
    }
}
