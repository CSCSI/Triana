package org.trianacode.shiwaall.collection;

// TODO: Auto-generated Javadoc
/**
 * The Class AddCollectionElement.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class AddCollectionElement extends CollectionElement<Number> {

    /**
     * Instantiates a new adds the collection element.
     *
     * @param content the content
     */
    public AddCollectionElement(Number content) {
        super(content);
    }

    /**
     * Instantiates a new adds the collection element.
     */
    public AddCollectionElement() {

    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.collection.CollectionElement#setContent(java.lang.Object)
     */
    public void setContent(Number content) {
        if(this.content == null) {
            this.content = content;
        } else {
            this.content = new Double(this.content.doubleValue() + content.doubleValue());
        }
    }


}
