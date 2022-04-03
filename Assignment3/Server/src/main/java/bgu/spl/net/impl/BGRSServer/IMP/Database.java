package bgu.spl.net.impl.BGRSServer.IMP;


import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
enum UserType {
    ADMIN,
    USER
}

public class Database {


    private static class DatabaseSingleton {
        private static Database instance = new Database();
    }
    //to prevent user from creating new Database

    private ConcurrentHashMap<Integer, Course> courses;
    private ConcurrentHashMap<String, User> users;
    private final Object registerLock;


    private Database() {
        users = new ConcurrentHashMap<>();
        courses = new ConcurrentHashMap<>();
        registerLock = new Object();
        initialize("Courses.txt");
    }

    public List<String> studentStatus(String name, String username) {
        if (!users.containsKey(username) || users.get(username).getType() != UserType.ADMIN)
            return null;
        if (users.containsKey(name)) {
            synchronized (users.get(name)) {
                List<String> l = new ArrayList<>();
                l.add(name);
                l.addAll(users.get(name).getListOfCourses().stream().map(String::valueOf).collect(Collectors.toList()));
                users.get(name).notifyAll();
                return l;
            }
        }
        return null;
    }


    public void Logout(String username) {
        users.get(username).setLogedin(false);
    }

    public boolean CourseRegister(Integer i, String user) {
        boolean ans = true;
        if (courses.containsKey(i)) {
            synchronized (courses.get(i)) { //sync the course object if exists to prevent two thread clients to register at the same time
                if (!users.containsKey(user)) ans = false;
                else if (!courses.get(i).isAvailable() || users.get(user).getType() == UserType.ADMIN || users.get(user).getListOfCourses().contains(i))
                    ans = false;
                else {
                    User user1 = users.get(user);
                    List<Integer> listOfKdamCourse = courses.get(i).getKdamCourseList();
                    for (int kdamCourse : listOfKdamCourse) {
                        if (!user1.takingCourse(kdamCourse))
                            ans = false;
                    }
                    if (ans) {
                        user1.registerToCourse(i); // add the course to the list of the client
                        courses.get(i).decrease(); // increment the current num of students in this course
                        courses.get(i).addStudent(user);
                    }
                }
               courses.get(i).notifyAll();
            }
        } else ans = false;
        return ans;
    }

    public List<String> myCourses(String username) {
        if (users.containsKey(username) && users.get(username).getType() != UserType.USER) {
            return null;
        }
        List<String> listOfCurses = new ArrayList<>();
        if (!users.get(username).getListOfCourses().isEmpty()) {
            synchronized (users.get(username)) {
                LinkedBlockingQueue<Integer> list = users.get(username).getListOfCourses();
                List<Integer> l = new ArrayList<>(list);
                l.sort(Comparator.comparingInt(o->courses.get(o).getCount()));
                listOfCurses = l.stream().map(String::valueOf).collect(Collectors.toList());
                users.get(username).notifyAll();
                return listOfCurses;
            }
        } else {
            listOfCurses.add("-");
        }
        return listOfCurses;
    }

    public boolean Register(String username, String pass, UserType t) { // check about split- AdminRegister
        boolean ans = true;
        synchronized (registerLock) { //scenario when 2 different users try to register (inorder not to add twice)
            if (users.containsKey(username))
                ans = false;
            else {
                users.put(username, new User(username, pass, t));
            }
            registerLock.notifyAll();
        }
        return ans;
    }

    public boolean Login(String username, String pass) {
        boolean ans = false;
        if (users.containsKey(username)) {
            synchronized (users.get(username)) {
                if (!users.get(username).isLogedin() && checkPass(username, pass)) {
                    users.get(username).setLogedin(true);
                    ans = true;
                }
                users.get(username).notifyAll();
            }
        }
        return ans;
    }

    public boolean Unregister(int courseNUmber, String username) {
        if (courses.containsKey(courseNUmber) && users.containsKey(username) && users.get(username).takingCourse(courseNUmber)) {
            synchronized (users.get(username)) {
                synchronized (courses.get(courseNUmber)) {
                    courses.get(courseNUmber).getNamesOfStudents().remove(username);
                    courses.get(courseNUmber).increment();
                    users.get(username).getListOfCourses().remove(courseNUmber);
                    courses.get(courseNUmber).notifyAll();
                }
                users.get(username).notifyAll();
                return true;
            }
        }
        return false;
    }


    public boolean isRegisterToSpecifiedCourse(int courseNUmber, String username) {
        boolean result;
        if (!users.containsKey(username) || !courses.containsKey(courseNUmber) || users.get(username).getType() == UserType.ADMIN)
            result = false;
        else {
            synchronized (users.get(username)) {
                result = users.get(username).getListOfCourses().contains(courseNUmber);
                users.get(username).notifyAll();
            }
        }
        return result;
    }

    private boolean checkPass(String username, String pass) {
        return users.get(username).getPassword().equals(pass);
    }

    public ConcurrentHashMap<Integer, Course> getCourses() {
        return courses;
    }

    public List<String> courseStatus(int courseNUmber, String user) {
        List<String> out = new ArrayList<>();
        if (courses.containsKey(courseNUmber)) {
            synchronized (courses.get(courseNUmber)) {
                if (!users.containsKey(user) || users.get(user).getType() != UserType.ADMIN)
                    out = null;
                if (out != null) {
                    out.add(String.valueOf(courses.get(courseNUmber).getCourseNum()));
                    out.add(courses.get(courseNUmber).getCourseName());
                    out.add(String.valueOf(courses.get(courseNUmber).getCurrentNumOfStudents()));
                    out.add(String.valueOf(courses.get(courseNUmber).getMaxStudents()));
                    String[] names = courses.get(courseNUmber).getNamesOfStudents().toArray(new String[0]);
                    Arrays.sort(names);
                    out.addAll(Arrays.asList(names));
                }
                courses.get(courseNUmber).notifyAll();
            }
        } else out = null;
        return out;
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseSingleton.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    public boolean initialize(String coursesFilePath) {
        // TODO: implement
        try {
            Scanner scan = new Scanner(new File(coursesFilePath));
            int counter = 0;
            List<Integer> idCourses = new ArrayList<>();
            while (scan.hasNextLine()) {
                String[] data = scan.nextLine().split("\\|");
                List<Integer> kdamCourses = new LinkedList<>();
                if (data[2].length() > 2) {
                    String sub = data[2].substring(1, data[2].length() - 1);
                    String[] splitOfSub = sub.split(",");
                    for (String s : splitOfSub) {
                        kdamCourses.add(Integer.parseInt(s));
                    }
                }
                courses.put(Integer.parseInt(data[0]), new Course(Integer.parseInt(data[0]), data[1], kdamCourses, Integer.parseInt(data[3]), counter++));
                idCourses.add(Integer.parseInt(data[0]));
            }
            for (Integer idCourse : idCourses) {
                courses.get(idCourse).getKdamCourseList().sort(Comparator.comparingInt(o -> courses.get(o).getCount()));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
