package triana.types;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */
class OutOfRangeType extends TrianaType {

    public OutOfRangeType() {
    }

    public TrianaType copyMe() {
        return new DefaultType();
    }

    protected void copyData(TrianaType source) {
    }
}
