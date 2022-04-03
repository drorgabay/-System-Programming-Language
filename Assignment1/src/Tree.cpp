//
// Created by spl211 on 03/11/2020.
//
using namespace std;

#include "../include/Tree.h"
#include "../include/Session.h"

//Rule of Five
//normal constructor
Tree::Tree(int rootLabel) : node(rootLabel), children({}), degree(0), depth(0), allTreeNodes({}) {}


//Destructor
Tree::~Tree() {
    clearChildren();
}

//Copy Constructor
Tree::Tree(const Tree &other) : node(other.node), children({}), degree(other.degree), depth(other.depth),allTreeNodes({}) {
    for (auto i : other.children) {
        children.push_back(i->clone());
    }
    for (auto i : other.allTreeNodes) {
        allTreeNodes.push_back(i->clone());
    }
}

//Move Constructor
Tree::Tree(Tree &&other) : node(other.node), children(std::move(other.children)), degree(other.degree), depth(other.depth),
                            allTreeNodes(std::move(other.allTreeNodes)) {}


//Copy Assignment
Tree &Tree::operator=(const Tree &other) {
    if (&other != this) {
        node = other.node;
        clearChildren();
        children.clear(); //vector clear
        for (auto &i : other.children) {
            children.push_back(i->clone());
        }
    }
    return *this;
}

//Move Assignment
Tree &Tree::operator=(Tree &&other) {
    if (&other != this) {
        node = other.node;
        clearChildren();
        children.clear(); //vector clear

        children = std::move(other.children);
        allTreeNodes = std::move(other.allTreeNodes); //add to dror
    }
    return (*this);
}

//------------------------------------------------------------------------------
void Tree::addChild(const Tree &child) {
    Tree *copy = child.clone();
    copy->setDepth(getDepth() + 1);
    children.push_back(copy);
    setDegree(getDegree() + 1);
    for (auto &i : copy->children) {
        i->setDepth(copy->getDepth() + 1);
    }
}

void Tree::addChild(Tree *child) {
    child->setDepth(getDepth() + 1);
    children.push_back(child);
    setDegree(getDegree() + 1);
}


Tree *Tree::createTree(const Session &session, int rootLabel) {
    Tree *newTree;
    if (session.getTreeType() == MaxRank) {
        newTree = new MaxRankTree(rootLabel);
    }
    if (session.getTreeType() == Root) {
        newTree = new RootTree(rootLabel);
    }
    if (session.getTreeType() == Cycle) {
        newTree = new CycleTree(rootLabel, session.getCurrCycle());
    }
    return newTree;
}

void Tree::BFS(Session &session) {
    int NumVertices(session.getGraph().getEdges().size());
    std::vector<bool> visited(NumVertices, false);
    std::vector<int> queue;
    queue.push_back(node);
    visited[node] = true;
    int vis;
    int inNodes = 0;
    Tree *currentNode = this;
    while (!queue.empty()) {
        vis = queue[0];
        queue.erase(queue.begin());
        for (int i = 0; i < NumVertices; i++) {
            if (session.getGraph().getEdges()[vis][i] == 1 && (!visited[i])) {
                Tree *child = Tree::createTree(session, i);
                currentNode->addChild(child);
                allTreeNodes.push_back(child);
                queue.push_back(i);
                visited[i] = true;
            }
        }
        int allTreeNodeSize = allTreeNodes.size();
        if (inNodes < allTreeNodeSize) {
            currentNode = allTreeNodes[inNodes];
            inNodes++;
        }
    }
}

Tree *Tree::getChild(int i) const {
    return children[i];
}

//---------------------------------------
const int &Tree::getDegree() const {
    return degree;
}

const int &Tree::getDepth() const {
    return depth;
}

void Tree::setDepth(int dep) {
    depth = dep;
}

void Tree::setDegree(int deg) {
    degree = deg;
}

const int &Tree::getData() const {
    return node;
}

void Tree::clearChildren() {
    for (auto &i : children) {
        delete i;
    }
    allTreeNodes.clear(); //add to dror
}


//ONLY FOR TEST
const std::vector<Tree *> &Tree::getChildren() const {
    return children;
}


//CycleTree
CycleTree::CycleTree(int rootLabel, int currCycle) : Tree(rootLabel), currCycle(currCycle) {}


//--rule of five--
int CycleTree::traceTree() {
    int tmp = 0;
    Tree *tree = this;
    while (tmp < getCurrCycle()) {
        if (tree->getDegree() == 0) {
            return tree->getData();
        } else {
            tmp++;
            tree = tree->getChild(0);
        }
    }
    return tree->getData();
}

Tree *CycleTree::clone() const { return new CycleTree(*this); }

const int &CycleTree::getCurrCycle() const {
    return currCycle;
}
//-------------------------------------------------------------------------------

//MaxRankTree Class imp
MaxRankTree::MaxRankTree(int
                         rootLabel) : Tree(rootLabel) {}

int MaxRankTree::traceTree() {
    vector < Tree * > sameDegree;
    if (allTreeNodes.empty()) //in case it didnt come from bfs call
        allTreeNodes = children;
    Tree *chosenNode = this;
    for (auto &allTreeNode : allTreeNodes) {
        if (allTreeNode->getDegree() > chosenNode->getDegree()) {
            chosenNode = allTreeNode;
            sameDegree.clear(); //ADD
        }
        if (allTreeNode->getDegree() == chosenNode->getDegree()) {
            sameDegree.push_back(allTreeNode);
        }
    }
    if (!sameDegree.empty()) {
        for (auto &i : sameDegree) {
            if (i->getDepth() < chosenNode->getDepth())
                chosenNode = i;

        }
    }
    return chosenNode->getData();
}

Tree *MaxRankTree::clone() const { return new MaxRankTree(*this); }

//--------------------------------------------------------------------------------
//RootTree Class imp
RootTree::RootTree(int
                   rootLabel) : Tree(rootLabel) {}

int RootTree::traceTree() { return node; }

Tree *RootTree::clone() const { return new RootTree(*this); }









