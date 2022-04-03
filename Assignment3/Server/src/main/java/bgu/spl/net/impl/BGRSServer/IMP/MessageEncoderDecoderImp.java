package bgu.spl.net.impl.BGRSServer.IMP;
import bgu.spl.net.api.*;
import bgu.spl.net.impl.BGRSServer.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageEncoderDecoderImp implements MessageEncoderDecoder<Message> {
    private byte[] bytes;
    private int len;
    private boolean isFirst;
    private int msgLength;
    private LinkedBlockingDeque<String> components;
    private short opCodeOperation;
    private HashMap<Short, Decode> decodeHashMap;
    private HashMap<Short, Integer> zeros;
    private int numOfZerosToExpect;

    public MessageEncoderDecoderImp() {
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
        isFirst = true; //indicates if the first bit is the first of the message
        components = new LinkedBlockingDeque<>();
        opCodeOperation = 0;
        numOfZerosToExpect = -1;
        msgLength = -1;
        init(); //init maps
    }


    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        Message msg = null;
        if (isFirst && len == 1) {//the first 2-bytes indicates opcode therefore we first take the opcode and continue fresh
            pushByte(nextByte); //push the second byte to get the opCode
            isFirst = false; //flag
            byte[] opCode = {bytes[0], bytes[1]}; //we take the first 2 bytes to opcode operation
            opCodeOperation = bytesToShort(opCode); //decode the opCode
            int tmp = zeros.get(opCodeOperation); // hash map that contains all commands amount of zeros
            if (tmp == 0) { //if this command has no zeros then the length is 2 or 4 bytes, therefore without the opcode is minus 2
                if (opCodeOperation == 11 || opCodeOperation == 4) msgLength = 0; // LogoutMessage or MYCourseMessage
                else msgLength = 2; //the rest
            } else numOfZerosToExpect = tmp; //if its all kind of register or student stat messages
            clear(); //clear to start from a fresh bytes array for the message
        } else if (nextByte == '\0' && len > 0) { // if the 1-byte is zero then we add to the list of strings
            components.add(popString());
            numOfZerosToExpect--; //decrease the expected zeros ahead
            clear();//clear to start from a fresh bytes array for the message
        } else {
            pushByte(nextByte);
            msgLength--;
        }
        if (msgLength == 0 || (numOfZerosToExpect == 0)) { //means we got the whole message and now we need to operate
            msg = decodeHashMap.get(opCodeOperation).act(); //when we get here if the message contains zeros then the components are in the list already by order
        }
        if (msg != null) //that means that the decode/encode has been done and we can clean resources
            cleanAfterFinishDecodeEncode();
        return msg;
    }


    protected byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    @Override
    public byte[] encode(Message message) { //Server-to-Client
       return message.selfEncode();
    } //the only messages to encode here are the ACKMessage and ErrorMessage


    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }


    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    private void clear() {
        bytes = new byte[1 << 10];
        len = 0;
    }

    private void cleanAfterFinishDecodeEncode() {
        components.clear();
        clear();
        isFirst = true; //indicates if the first bit is the first of the message
        opCodeOperation = 0;
        msgLength = -1;
        numOfZerosToExpect = -1;
    }

    private void init() {
        decodeHashMap = new HashMap<>();
        //Client-to-Server Messages

        //------------Admin Register---------------
        decodeHashMap.put((short) 1, () -> {
            String username = components.poll();
            String pass = components.poll();
            return new AdminRegMessage(username, pass);
        });

        //------------Student Register------------
        decodeHashMap.put((short) 2, () -> {
            String username = components.poll();
            String pass = components.poll();
            return new StudentRegMessage(username, pass);
        });

        //------------Login---------------------
        decodeHashMap.put((short) 3, () -> {
            String username = components.poll();
            String pass = components.poll();
            return new LoginMessage(username, pass);
        });

        //-----------Logout--------------------
        decodeHashMap.put((short) 4, LogoutMessage::new);

        //--------Course Register---------------
        decodeHashMap.put((short) 5, () -> {
            short num = bytesToShort(bytes);
            return new CourseRegMessage(num);
        });

        //-------KDAM Check---------------------
        decodeHashMap.put((short) 6, () -> {
            short courseNumber = bytesToShort(bytes);
            return new CheckKDAMMessage(courseNumber);
        });

        //---------------Course Status---------(Admin Message)---
        decodeHashMap.put((short) 7, () -> {
            short courseNumber = bytesToShort(bytes);
            return new CourseStatusMessage(courseNumber);
        });

        //------------Student Status--------(Admin Message)------
        decodeHashMap.put((short) 8, () -> {
            String studentUserName = components.poll();
            return new StudentStatusMessage(studentUserName);
        });

        //--------------Is Registered-------------------------
        decodeHashMap.put((short) 9, () -> {
            short courseNumber = bytesToShort(bytes);
            return new IsRegisteredMessage(courseNumber);
        });

        //----------------Unregister--------------------
        decodeHashMap.put((short) 10, () -> {
            short courseNumber = bytesToShort(bytes);
            return new UnregisterMessage(courseNumber);
        });

        //----------------My Courses ---------------------
        decodeHashMap.put((short) 11, MyCoursesMessage::new);


        zeros = new HashMap<>();
        zeros.put((short)1, 2);
        zeros.put((short)2, 2);
        zeros.put((short)3, 2);
        zeros.put((short)8, 1);
        zeros.put((short)4, 0);
        zeros.put((short)5, 0);
        zeros.put((short)6, 0);
        zeros.put((short)7, 0);
        zeros.put((short)9, 0);
        zeros.put((short)10, 0);
        zeros.put((short)11, 0);

    }


}
