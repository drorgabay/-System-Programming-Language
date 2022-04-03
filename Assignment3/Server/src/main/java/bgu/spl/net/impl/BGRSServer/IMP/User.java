package bgu.spl.net.impl.BGRSServer.IMP;

import java.util.concurrent.LinkedBlockingQueue;
public class User {
    private final String username;
    private final String password;
    private final UserType type;
    private LinkedBlockingQueue<Integer> listOfCourses;
    private boolean logedin;

    public User(String user, String pass, UserType t){
        username = user;
        password = pass;
        type = t;
        listOfCourses = new LinkedBlockingQueue<>();
        logedin = false;
    }

    public LinkedBlockingQueue<Integer> getListOfCourses() {
        return listOfCourses;
    }

    public boolean takingCourse(int i){
        return listOfCourses.contains(i);
    }
    public void registerToCourse(int i){
        listOfCourses.add(i);
    }


    public String getPassword() {
        return password;
    }

    public UserType getType() {
        return type;
    }

    public boolean isLogedin() {
        return logedin;
    }

    public void setLogedin(boolean logedin) {
        this.logedin = logedin;
    }
}
