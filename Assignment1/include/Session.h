#ifndef PROJECT1_SESSION_H
#define PROJECT1_SESSION_H
#include <vector>
#include <string>
#include "Graph.h"

class Agent;

enum TreeType{
    Cycle,
    MaxRank,
    Root
};

class Session{
public:
    Session(const std::string& path);

    //rule of five--------------------
    virtual ~Session();//destructor
    Session(const Session &other); //copy constructor
    Session(Session&& other); //move constructor
    Session& operator=(const Session& other); //copy assignment
    Session& operator=(Session&& other); //move assignment


    void simulate(); //NEW UPDATE
    void addAgent(const Agent &agent);
    const int& getCurrCycle() const;
    void setGraph(const Graph& graph);
    Graph& getGraph();
    void enqueueInfected(int);
    int dequeueInfected();
    TreeType getTreeType() const;



private:
    int currCycle;
    std::vector<Agent*> agents;
    Graph g;
    TreeType treeType;



    void clearSession();
    void makeOutputJson(); //NEW

};

#endif //PROJECT1_SESSION_H
