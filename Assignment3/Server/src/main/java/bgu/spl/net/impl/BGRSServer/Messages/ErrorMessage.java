package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.List;

public class ErrorMessage extends Message {
    private short messageOpCode;
    public ErrorMessage(short msgOpCode){
        super((short)13);
        messageOpCode = msgOpCode;
    }

    @Override
    public List<String> getData() {
        return null;
    }

    @Override
    public byte[] selfEncode() {
        byte[] opCodeBytes = shortToBytes(opCode); //opcode of the message
        byte[] messageOpCodeBytes = shortToBytes(messageOpCode); //the message opcode the ACK was sent for
        byte[] encodedMessage = new byte[opCodeBytes.length + messageOpCodeBytes.length];
        System.arraycopy(opCodeBytes, 0, encodedMessage, 0, opCodeBytes.length); //just append arrays
        System.arraycopy(messageOpCodeBytes, 0, encodedMessage, opCodeBytes.length, messageOpCodeBytes.length);
        return encodedMessage;
    }
}
