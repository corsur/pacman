# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for 
# educational purposes provided that (1) you do not distribute or publish 
# solutions, (2) you retain this notice, and (3) you provide clear 
# attribution to UC Berkeley, including a link to 
# http://inst.eecs.berkeley.edu/~cs188/pacman/pacman.html
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero 
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and 
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import mdp, util

from learningAgents import ValueEstimationAgent

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0

        # Write value iteration code here
        notStates = self.mdp.getStates()[:]
        yesStates = util.Counter()
        counter = 0
        for state in notStates:
            if self.mdp.isTerminal(state):
                notStates.remove(state)
                yesStates[state] = counter
        
        totalStates = {}
        for state in notStates:
            for action in self.mdp.getPossibleActions(state):
                if state in totalStates.keys():
                    totalStates[state] = totalStates[state] | set([st[0] for st in self.mdp.getTransitionStatesAndProbs(state, action)])
                else:
                    totalStates[state] = set([st[0] for st in self.mdp.getTransitionStatesAndProbs(state, action)])
            
        while notStates:
            counter += 1
            for state in notStates[:]:
                if len(totalStates[state] & set(yesStates.keys())):
                    yesStates[state] = counter
                    notStates.remove(state)
        
        
        sortedStates = sorted(yesStates, key=lambda st:st[1])
        sortedStates.reverse()
        
        times = 1
        while self.iterations >= times:
            oldValues = self.values.copy()
            for state in sortedStates:
                if not self.mdp.isTerminal(state) and yesStates[state] <= times:
                    oldValues[state] = max([self.computeQValueFromValues(state,action) for action in self.mdp.getPossibleActions(state)])
            self.values = oldValues
            times += 1
            
    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        transitions = self.mdp.getTransitionStatesAndProbs(state, action)
        sum = 0
        for transition in transitions:
            sum += transition[1]*(self.mdp.getReward(state, action, transition[0])+self.discount*self.values[transition[0]])
        return sum
    
    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        rightAction = None
        for action in self.mdp.getPossibleActions(state):
            if not rightAction:
                rightAction = action
            elif self.computeQValueFromValues(state, action) > self.computeQValueFromValues(state, rightAction):
                rightAction = action
        return rightAction

    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)
