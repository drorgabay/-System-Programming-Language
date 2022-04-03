//
// Created by spl211 on 03/01/2021.
//

#include "../include/SocketAnswer.h"
#include <boost/algorithm/string.hpp>


using namespace std;

SocketAnswer::SocketAnswer(ConnectionHandler &connectionHandler) : myConnectionHandler(connectionHandler), print(""),
                                                                   optional("") {
}

bool SocketAnswer::getAnswer(std::string &ans) {

    bool success;
    //------------get opcode-----------
    char opCode[2];
    success = myConnectionHandler.getBytes(opCode, 2);
    short opcodeNumber = bytesToShort(opCode); //12\13
    //----------------------------------
    //----------------if op is 12 -> ACK msg----------------
    if (opcodeNumber == (short) 12) {
        print += "ACK ";
        //-------get the msgOpCode---------
        char msgOpcode[2];
        success = myConnectionHandler.getBytes(msgOpcode, 2);
        short msgOpcodeNumber = bytesToShort(msgOpcode);
        print += to_string(msgOpcodeNumber);
        //--------------------------------

        //check all msgOpCodes and translate the optional list
        if (msgOpcodeNumber == (short) 4) { //if its 'LOGOUT' ACK
            myConnectionHandler.setTerminate(-1); //set terminate indicator to -1 to tell keyboard to shutdown
        }
        if (msgOpcodeNumber == (short) 6) {
            std::string answer;
            success = myConnectionHandler.getLine(answer);
            if (answer != "-") {
                vector<string> data;
                boost::split(data, answer, boost::is_any_of(" "));
                if (data.size() > 1) {
                    string ans = "[";
                    for (unsigned int i = 0; i < data.size() - 1; ++i) {
                        ans += data[i] + ",";
                    }
                    ans += data.at(data.size() - 1) + "]";
                    optional += ans;
                } else {
                    string ans = "[";
                    ans += data[0] + "]";
                    optional += ans;
                }
            } else optional += "[]";
        }
        if (msgOpcodeNumber == (short) 7) { //if its 'COURSESTAT' ACK
            std::string answer;
            success = myConnectionHandler.getLine(answer);
            vector<string> data;
            boost::split(data, answer, boost::is_any_of(" "));
            string tmp = "Course: (" + data[0] + ") " + data[1] + "\n" + "Seats Available: " + data[2] + "/"
                         + data[3] + "\n";
            string tmp2 = "Students Registered: [";
            if (data.size() - 1 > 4) {
                for (unsigned int i = 4; i < data.size() - 1; ++i) {
                    tmp2 += data[i] + ",";
                }
                tmp2 += data[data.size() - 1] + "]";
            } else if (data.size() - 1 == 4) {
                tmp2 += data[4] + "]";
            } else
                tmp2 += "]";
            string ans = tmp + tmp2;
            optional += ans;
        }
        if (msgOpcodeNumber == (short) 8) { //if its 'STUDENTSTAT' ACK
            std::string answer;
            success = myConnectionHandler.getLine(answer);
            vector<string> data;
            boost::split(data, answer, boost::is_any_of(" "));
            if (data.size() > 1) {
                string ans = "Student: " + data[0] + "\n" + "Courses: [";
                for (unsigned int i = 1; i < data.size() - 1; ++i) {
                    ans += data[i] + ",";
                }
                ans += data.at(data.size() - 1) + "]";
                optional += ans;
            } else {
                optional += "Student: " + data[0] + "\n" + "Courses: []";
            }
        }
        if (msgOpcodeNumber == (short) 9) { //if its 'ISREGISTERED' ACK
            std::string answer;
            success = myConnectionHandler.getLine(answer);
            optional += answer;
        }
        if (msgOpcodeNumber == (short) 11) { //if its 'MYCOURSES' ACK
            std::string answer;
            success = myConnectionHandler.getLine(answer);
            if (answer != "-") {
                vector<string> data;
                boost::split(data, answer, boost::is_any_of(" "));
                std::string ans = "[";
                if (data.size() > 1) {
                    for (unsigned int i = 0; i < data.size() - 1; ++i) {
                        ans += data[i] + ",";
                    }
                    ans += data.at(data.size() - 1);
                    optional += ans + "]";
                } else if (data.size() == 1) {
                    ans += data.at(0) + "]";
					optional+=ans; //FORGOT TO ADD THIS IN SOCKETANSWER FOR MYCOURSES COMMAND
                }
            } else {
                optional += "[]";
            }
        }
        if (msgOpcodeNumber == (short) 3) { //logedIn
            myConnectionHandler.setLogedIn(true); //only for knowing to shutdown the keyboard thread
        }
    }
    if (opcodeNumber == (short) 13) { //Error msg
        print += "ERROR ";
        char msgOpcode[2];
        success = myConnectionHandler.getBytes(msgOpcode, 2);
        short msgOpcodeNumber = bytesToShort(msgOpcode);
        print += to_string(msgOpcodeNumber);
        if (msgOpcodeNumber == ((short) 4)) //if its Error 4 message then dont stop the keyboard thread
            myConnectionHandler.setTerminate(0);
    }
    ans = print;
    if (!optional.empty()) {
        ans += "\n";
        ans += optional;
    }
    print = "";
    optional = "";
    return success;
}


short SocketAnswer::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}
