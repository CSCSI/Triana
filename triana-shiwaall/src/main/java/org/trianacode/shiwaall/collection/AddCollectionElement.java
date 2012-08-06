package org.trianacode.shiwaall.collection;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class AddCollectionElement extends CollectionElement<Number> {

    public AddCollectionElement(Number content) {
        super(content);
    }

    public AddCollectionElement() {

    }

    public void setContent(Number content) {
        if(this.content == null) {
            this.content = content;
        } else {
            this.content = new Double(this.content.doubleValue() + content.doubleValue());
        }
    }


}
