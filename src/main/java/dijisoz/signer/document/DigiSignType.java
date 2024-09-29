package dijisoz.signer.document;

public enum DigiSignType {
    Digital(1), e(2), Mobile(3);

    int value;
    DigiSignType(int value) {
        this.value = value;
    } 

    public byte getValue(){
        return (byte)value;
    }
}
