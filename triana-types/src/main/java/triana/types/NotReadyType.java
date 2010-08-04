package triana.types;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */
class NotReadyType extends TrianaType {

    public NotReadyType() {
    }

    public TrianaType copyMe() {
        return new DefaultType();
    }

    protected void copyData(TrianaType source) {
    }
}
