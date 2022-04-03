package bgu.spl.net.impl.BGRSServer.IMP;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.Process;
import bgu.spl.net.impl.BGRSServer.Messages.*;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageProtocolImp implements MessagingProtocol<Message> {
    private HashMap<Short, Process> process;
    private boolean shouldTerminate;
    private boolean logedIn;
    private String username;

    public MessageProtocolImp() {
        process = new HashMap<>();
        shouldTerminate = false;
        logedIn = false;
        username = "";
        init();
    }


    @Override
    public Message process(Message msg) {
        return process.get(msg.getOpCode()).act(msg);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    } //should terminate check

    private void init() {
        process.put((short) 1, (m) -> {
            if (!logedIn) {
                List<String> data = m.getData();
                String username = data.get(0);
                String pass = data.get(1);
                boolean success = Database.getInstance().Register(username, pass, UserType.ADMIN);
                if (success) {
                    //  this.username = username;
                    return new ACKMessage(m.getOpCode());
                }
                return new ErrorMessage(m.getOpCode());
            } else return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 2, (m) -> {
            if (!logedIn) {
                List<String> data = m.getData();
                String username = data.get(0);
                String pass = data.get(1);
                boolean success = Database.getInstance().Register(username, pass, UserType.USER);
                if (success) {
                    //  this.username = username;
                    return new ACKMessage(m.getOpCode());
                }
                return new ErrorMessage(m.getOpCode());
            } else return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 3, (m) -> {
            List<String> data = m.getData();
            String username = data.get(0);
            String pass = data.get(1);
            boolean success = Database.getInstance().Login(username, pass);
            if (success && !logedIn) { //check if ACK is needed
                logedIn = true;
                this.username = username;
                return new ACKMessage(m.getOpCode());
            }
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 4, (m) -> {
            if (!logedIn)
                return new ErrorMessage(m.getOpCode());
            logedIn = false;
            shouldTerminate = true; //check where it is processed
            Database.getInstance().Logout(username);
            return new ACKMessage(m.getOpCode());
        });
        process.put((short) 5, (m) -> {
           if (!logedIn) {
                return new ErrorMessage(m.getOpCode());
            }
            int courseNUmber = Integer.parseInt(m.getData().get(0));
            boolean success = Database.getInstance().CourseRegister(courseNUmber, username);
            if (success)
                return new ACKMessage(m.getOpCode());
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 6, (m) -> {
            if (logedIn) {
                int courseNUmber = Integer.parseInt(m.getData().get(0));
                ACKMessage out = new ACKMessage(m.getOpCode());
                List<Integer> kdam = Database.getInstance().getCourses().get(courseNUmber).getKdamCourseList();
                List<String> l = kdam.stream().map(String::valueOf).collect(Collectors.toList());
                if (l.isEmpty())
                    l.add("-");
                out.setOptional(l);
                return out;
            }
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 7, (m) -> {
            if (logedIn) {
                int courseNUmber = Integer.parseInt(m.getData().get(0));
                List<String> optional = Database.getInstance().courseStatus(courseNUmber, username);
                if (optional == null)
                    return new ErrorMessage(m.getOpCode());
                ACKMessage out = new ACKMessage(m.getOpCode());
                out.setOptional(optional);
                return out;
            }
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 8, (m) -> {
            if (logedIn) {
                String nameOfRequiredStudent = m.getData().get(0);
                List<String> optional = Database.getInstance().studentStatus(nameOfRequiredStudent, username);
                if (optional == null)
                    return new ErrorMessage(m.getOpCode());
                ACKMessage out = new ACKMessage(m.getOpCode());
                out.setOptional(optional);
                return out;
            }
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 9, (m) -> {
            if (logedIn) {
                int courseNUmber = Integer.parseInt(m.getData().get(0));
                boolean success = Database.getInstance().isRegisterToSpecifiedCourse(courseNUmber, username);
                List<String> optional = new ArrayList<>();
                if (success)
                    optional.add("REGISTERED");
                else
                    optional.add("NOT REGISTERED");
                ACKMessage output = new ACKMessage(m.getOpCode());
                output.setOptional(optional);
                return output;
            }
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 10, (m) -> {
            if (logedIn) {
                int courseNUmber = Integer.parseInt(m.getData().get(0));
                boolean success = Database.getInstance().Unregister(courseNUmber, username);
                if (success)
                    return new ACKMessage(m.getOpCode());
                return new ErrorMessage(m.getOpCode());
            }
            return new ErrorMessage(m.getOpCode());
        });
        process.put((short) 11, (m) -> {
            if (logedIn) {
                List<String> optional = Database.getInstance().myCourses(username);
                if (optional == null) return new ErrorMessage(m.getOpCode());
                ACKMessage output = new ACKMessage(m.getOpCode());
                output.setOptional(optional);
                return output;
            }
            return new ErrorMessage(m.getOpCode());
        });
    }


}
