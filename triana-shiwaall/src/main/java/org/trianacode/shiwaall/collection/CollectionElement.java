package org.trianacode.shiwaall.collection;

// TODO: Auto-generated Javadoc
/**
 * The Class CollectionElement.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CollectionElement<C> {

    /** The content. */
    protected C content;

    /**
     * Instantiates a new collection element.
     *
     * @param content the content
     */
    public CollectionElement(C content) {
        this.content = content;
    }

    /**
     * Instantiates a new collection element.
     */
    public CollectionElement() {
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public C getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the new content
     */
    public void setContent(C content) {
        this.content = content;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Collection Element content:" + content;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionElement)) {
            return false;
        }

        CollectionElement that = (CollectionElement) o;

        if (content != null ? !content.equals(that.content) : that.content != null) {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
