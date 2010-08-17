package org.trianacode.pegasus.collection;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class MultCollectionElement extends CollectionElement<Number> {

    public MultCollectionElement(Number content) {
        super(content);
    }

    public MultCollectionElement() {
    }

    public void setContent(Number content) {
        if (this.content == null) {
            this.content = content;
        } else {
            this.content = new Double(this.content.doubleValue() * content.doubleValue());
        }
    }


}