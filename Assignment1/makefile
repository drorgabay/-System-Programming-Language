# All Targets
all: bin/cTrace

# Tool invocations
# Executable "cTrace" depends on the files main.o Agent.o Graph.o Tree.o Session.o
bin/cTrace: bin/main.o bin/Agents.o bin/Graph.o bin/Tree.o bin/Session.o
	@echo 'Building target: cTrace'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/cTrace bin/main.o bin/Agents.o bin/Graph.o bin/Tree.o bin/Session.o
	@echo 'Finished building target: cTrace'
	@echo ' '

#main.o Depends on the source and header files src/main.cpp include include/Sessoion.h
bin/main.o: src/main.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/main.o src/main.cpp

#Session.o Depends on the source and header files src/Session.cpp include/Agent.h include/json.hpp 
bin/Session.o: src/Session.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Session.o src/Session.cpp

#Agent.o Depends on the source and header files src/Agent.cpp include/Agent.h incluce/Tree.h
bin/Agents.o: src/Agents.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Agents.o src/Agents.cpp

#Tree.o Depends on the source and header files src/Tree.cpp include/Session.h
bin/Tree.o: src/Tree.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Tree.o src/Tree.cpp

#Graph.o Depends on the source and header files src/Graph.cpp include/Graph.h
bin/Graph.o: src/Graph.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Graph.o src/Graph.cpp

#Clean the build directory
clean: 
	@echo 'Cleaning'
	rm -f bin/*
