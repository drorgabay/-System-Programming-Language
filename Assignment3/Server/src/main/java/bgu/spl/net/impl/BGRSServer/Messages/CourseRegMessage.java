package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.ArrayList;
import java.util.List;

public class CourseRegMessage extends Message {
    private int courseNumber;
    public CourseRegMessage(int courseNum){
        super((short) 5);
        courseNumber = courseNum;
    }

    public int getCourseNumber() {
        return courseNumber;
    }


    @Override
    public List<String> getData() {
        List<String> l = new ArrayList<>();
        l.add(String.valueOf(courseNumber));
        return l;
    }

    @Override
    public byte[] selfEncode() {
        return new byte[0];
    }
}
