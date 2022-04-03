package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.ArrayList;
import java.util.List;

public class StudentStatusMessage extends Message {
    private String studentUserName;
    public StudentStatusMessage(String student){
       super((short)8);
        studentUserName = student;
    }

    public String getStudentUserName() {
        return studentUserName;
    }

    @Override
    public List<String> getData() {
        List<String> l = new ArrayList<>();
        l.add(studentUserName);
        return l;
    }

    @Override
    public byte[] selfEncode() {
        return new byte[0];
    }
}
