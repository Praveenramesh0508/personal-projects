# Project Title: SummitDrive: Mountain Car Mastery with Q-Learning and Linear Function Approximation

![Image](https://github.com/Praveenramesh0508/CMUProjects/blob/main/ReinforcementLearning%20/img.png)


## Project Description

In this project, the primary objective was to implement Q-learning with linear function approximation to solve a Gridworld navigation problem. The goal was to design an intelligent agent that could find the shortest path to a goal while avoiding obstacles in a grid environment. The agent learned by interacting with the environment through a series of steps and updating its Q-values using linear function approximation.

## Key Components

1. **Gridworld Environment**: A customizable grid environment representing the navigational space for the agent was created. The grid included obstacles, a starting point, and a goal point. The agent was able to take actions such as moving up, down, left, or right.
2. **Q-Learning Algorithm**: The Q-learning algorithm with linear function approximation was implemented, allowing the agent to update its Q-values and learn the optimal policy for navigating the grid environment.
3. **Feature Engineering**: Appropriate feature representations for the states of the grid environment were developed, ensuring that the agent could effectively learn from its interactions.
4. **Epsilon-Greedy Action Selection**: An epsilon-greedy action selection strategy was implemented, where the agent selected the optimal action with probability 1-ε and selected a random action with probability ε. This enabled the agent to balance exploration and exploitation during the learning process.
5. **Training and Evaluation**: The agent was trained on multiple grid environments with varying levels of complexity and obstacles. The performance of the agent was evaluated by measuring the average number of steps taken to reach the goal and the overall success rate in reaching the goal.

By the end of the project, the agent was able to efficiently navigate complex grid environments by learning the optimal policy through Q-learning with linear function approximation. This project provided valuable insights into the application of reinforcement learning algorithms for navigation and pathfinding problems.
