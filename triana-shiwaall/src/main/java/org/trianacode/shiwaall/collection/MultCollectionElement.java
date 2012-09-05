package org.trianacode.shiwaall.collection;

// TODO: Auto-generated Javadoc
/**
 * The Class MultCollectionElement.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class MultCollectionElement extends CollectionElement<Number> {

    /**
     * Instantiates a new mult collection element.
     *
     * @param content the content
     */
    public MultCollectionElement(Number content) {
        super(content);
    }

    /**
     * Instantiates a new mult collection element.
     */
    public MultCollectionElement() {
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.collection.CollectionElement#setContent(java.lang.Object)
     */
    public void setContent(Number content) {
        if (this.content == null) {
            this.content = content;
        } else {
            this.content = new Double(this.content.doubleValue() * content.doubleValue());
        }
    }


}