//
// Created by spl211 on 05/01/2021.
//

#include "../include/connectionHandler.h"
#include <thread>
#include <Keyboard.h>
#include <SocketAnswer.h>

using namespace std;

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);


    ConnectionHandler connectionHandler(host, port);
    Keyboard keyboard(connectionHandler); //keyboard thread
    SocketAnswer socketAnswer(connectionHandler); //main thread

    //connect to server
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    //start the keyboard thread
    std::thread th2(&Keyboard::run, &keyboard);

    //get the answer from server and print
    while (1) {
        //reads from user keyboard and returns a line to send to server
        std::string answer;
        if (!socketAnswer.getAnswer(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        std::cout << answer << std::endl;
        if (answer == "ACK 4") { //means there was a logout
            break;
        }
    }
    th2.join();
    return 0;
}
