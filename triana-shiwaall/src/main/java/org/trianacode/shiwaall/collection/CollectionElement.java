package org.trianacode.shiwaall.collection;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CollectionElement<C> {

    protected C content;

    public CollectionElement(C content) {
        this.content = content;
    }

    public CollectionElement() {
    }

    public C getContent() {
        return content;
    }

    public void setContent(C content) {
        this.content = content;
    }

    public String toString() {
        return "Collection Element content:" + content;
    }

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

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
