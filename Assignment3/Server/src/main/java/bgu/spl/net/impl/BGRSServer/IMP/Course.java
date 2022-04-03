package bgu.spl.net.impl.BGRSServer.IMP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Course {
    private final int courseNum;
    private final String courseName;
    private final List<Integer> kdamCourseList;
    private LinkedBlockingQueue<String> namesOfStudents;
    private final int maxStudents;
    private int currentNumOfStudents;
    private int count;

    public Course(int courseNumber, String name, List<Integer> kdam, int max, int counter) {
        courseNum = courseNumber;
        courseName = name;
        kdamCourseList = kdam;
        maxStudents = max;
        currentNumOfStudents = max;
        namesOfStudents = new LinkedBlockingQueue<>();
        count = counter;
    }

    public int getCurrentNumOfStudents() {
        return currentNumOfStudents;
    }

    public LinkedBlockingQueue<String> getNamesOfStudents() {
        return namesOfStudents;
    }

    public void addStudent(String name) {
        namesOfStudents.add(name);
    }

    public boolean isAvailable() {
        return currentNumOfStudents > 0 && currentNumOfStudents <= maxStudents;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getKdamCourseList() {
        return kdamCourseList;
    }

    public String getCourseName() {
        return courseName;
    }
    public void decrease(){
        currentNumOfStudents--;
    }

    public void increment() {
        currentNumOfStudents++;
    }

}
