CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_thread

# All Targets
all: bin/BGRSclient

bin/BGRSclient: bin/connectionHandler.o bin/client.o bin/Keyboard.o bin/SocketAnswer.o
	@echo 'Building target: BGRSclient'
	@echo 'Invoking: C++ Linker'
	g++ -Wall -Weffc++ -o bin/BGRSclient bin/connectionHandler.o bin/client.o bin/Keyboard.o bin/SocketAnswer.o $(LDFLAGS)
	@echo 'Finished building target: BGRSclient'
	@echo ' '

# the main class of the client
bin/client.o: src/client.cpp
	g++ $(CFLAGS) -o bin/client.o src/client.cpp
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp $(LDFLAGS)

bin/Keyboard.o: src/Keyboard.cpp
	g++ $(CFLAGS) -o bin/Keyboard.o src/Keyboard.cpp $(LDFLAGS)

bin/SocketAnswer.o: src/SocketAnswer.cpp
	g++ $(CFLAGS) -o bin/SocketAnswer.o src/SocketAnswer.cpp $(LDFLAGS)
	

#Clean the build directory	
.PHONY: clean
clean:
	@echo 'Cleaning'
	rm -f bin/*

