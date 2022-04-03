package bgu.spl.net.impl.BGRSServer.Messages;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ACKMessage extends Message {
    private short messageOpCode;
    private List<String> optional;

    public ACKMessage(short msgCode) {
        super((short) 12);
        messageOpCode = msgCode;
        optional = new ArrayList<>();
    }

    @Override
    public List<String> getData() {
        return null;
    }

    public void setOptional(List<String> optional) {
        this.optional = optional;
    }

    @Override
    public byte[] selfEncode() {
        byte[] encodedMessage;
        List<byte[]> optionalByteList = new ArrayList<>();
        byte[] option = null;
        int size = 0;
        if (!optional.isEmpty()) { //check is there anything to encode from the optional list in ACK (e.g Student User list)
            for (int i = 0; i < optional.size() - 1; i++) {
                byte[] b = optional.get(i).getBytes(StandardCharsets.UTF_8);
                byte[] space = " ".getBytes(StandardCharsets.UTF_8);
                size += b.length + 1; //count the size to init the option array later + '0' char for knowing in the decoding client
                optionalByteList.add(b); //string
                optionalByteList.add(space); //zero
            }
            byte[] b = optional.get(optional.size() - 1).getBytes(StandardCharsets.UTF_8);
            optionalByteList.add(b);
            byte[] zero = {'\0'};
            optionalByteList.add(zero);
            size += b.length + 1;
        }
        if (size > 0) { //that means the list of optional was not empty, therefore we will append all arrays of bytes to 'option' array
            option = new byte[size]; // size including the zeros between each name string
            byte[] tmp = optionalByteList.get(0);
            int lengthCounter = tmp.length;
            System.arraycopy(tmp, 0, option, 0, lengthCounter); //copy the first into option
            for (int i = 1; i < optionalByteList.size(); i++) {
                byte[] src = optionalByteList.get(i);
                System.arraycopy(src, 0, option, lengthCounter, src.length);
                lengthCounter += src.length; //for the next append index
            }
        }
        //append all arrays to encodedMessage array
        byte[] opCodeBytes = shortToBytes(opCode); //opcode of the message
        byte[] messageOpCodeBytes = shortToBytes(messageOpCode); //the message opcode the ACK was sent for
        if (option != null)
            encodedMessage = new byte[opCodeBytes.length + messageOpCodeBytes.length + option.length];
        else
            encodedMessage = new byte[opCodeBytes.length + messageOpCodeBytes.length];
        System.arraycopy(opCodeBytes, 0, encodedMessage, 0, opCodeBytes.length); //append arrays
        System.arraycopy(messageOpCodeBytes, 0, encodedMessage, opCodeBytes.length, messageOpCodeBytes.length);
        if (option != null)
            System.arraycopy(option, 0, encodedMessage, (opCodeBytes.length + messageOpCodeBytes.length), option.length); //changes for each message (e.g student user list)
        return encodedMessage;
    }

    @Override
    public String toString() {
        return "ACKMessage{" +
                "messageOpCode=" + messageOpCode +
                ", optional=" + optional +
                '}';
    }
}
