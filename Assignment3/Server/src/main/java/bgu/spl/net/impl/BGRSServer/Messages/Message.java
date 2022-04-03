package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.List;

abstract public class Message {
    protected final short opCode;

    public Message(short opCode) {
        this.opCode = opCode;
    }

    public short getOpCode() {
        return opCode;
    }
    public abstract List<String> getData();
    public abstract byte[] selfEncode();

    protected byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

}
