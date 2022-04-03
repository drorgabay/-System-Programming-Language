#ifndef PROJECT1_TREE_H
#define PROJECT1_TREE_H
#include <vector>
#include "Graph.h"

class Session;

class Tree{
public:
    Tree(int rootLabel); //normal constructor
    //rule of five--------------------
    virtual ~Tree();//destructor
    Tree(const Tree &other); //copy constructor
    Tree(Tree&& other); //move constructor
    Tree& operator=(const Tree& other); //copy assignment
    Tree& operator=(Tree&& other); //move assignment

    //--------------------------------

    void addChild(Tree* child);
    void addChild(const Tree& child);
    Tree * getChild(int i) const;

    static Tree * createTree(const Session& session, int rootLabel);
    void BFS(Session &session);

    virtual int traceTree()=0;
    virtual Tree * clone() const=0;

    //new------------
    const int& getDegree() const;
    const int& getDepth() const;
    void setDegree(int deg);
    void setDepth(int dep);
    const int& getData() const;


    //ONly FOR TESTER
    const std::vector<Tree*>& getChildren()const;


private:
    void clearChildren();
    //------------
protected:
    int node;
    std::vector<Tree*> children;
    //---------------
    int degree;
    int depth;
    std::vector<Tree*> allTreeNodes;
    //-----------
};

class CycleTree: public Tree{
public:
    CycleTree(int rootLabel, int currCycle);
    int traceTree();
    Tree* clone() const;
    const int& getCurrCycle() const;

private:
    int currCycle;
};

class MaxRankTree: public Tree{
public:
    MaxRankTree(int rootLabel);
    int traceTree();
    Tree* clone() const;
};

class RootTree: public Tree{
public:
    RootTree(int rootLabel);
    int traceTree();
    Tree* clone() const;
};

#endif //PROJECT1_TREE_H
