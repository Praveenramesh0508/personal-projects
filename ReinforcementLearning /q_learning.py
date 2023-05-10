import argparse
import numpy as np

from environment import MountainCar, GridWorld

"""

class Environment: # either MountainCar or GridWorld
"""

def parse_args() -> tuple:
    """
    Parses all args and returns them. Returns:

        (1) env_type : A string, either "mc" or "gw" indicating the type of 
                    environment you should use
        (2) mode : A string, either "raw" or "tile"
        (3) weight_out : The output path of the file containing your weights
        (4) returns_out : The output path of the file containing your returns
        (5) episodes : An integer indicating the number of episodes to train for
        (6) max_iterations : An integer representing the max number of iterations 
                    your agent should run in each episode
        (7) epsilon : A float representing the epsilon parameter for 
                    epsilon-greedy action selection
        (8) gamma : A float representing the discount factor gamma
        (9) lr : A float representing the learning rate
    
    Usage:
        env_type, mode, weight_out, returns_out, episodes, max_iterations, epsilon, gamma, lr = parse_args()
    """
    parser = argparse.ArgumentParser()
    parser.add_argument("env", type=str, choices=["mc", "gw"])
    parser.add_argument("mode", type=str, choices=["raw", "tile"])
    parser.add_argument("weight_out", type=str)
    parser.add_argument("returns_out", type=str)
    parser.add_argument("episodes", type=int)
    parser.add_argument("max_iterations", type=int)
    parser.add_argument("epsilon", type=float)
    parser.add_argument("gamma", type=float)
    parser.add_argument("learning_rate", type=float)

    args = parser.parse_args()

    return args.env, args.mode, args.weight_out, args.returns_out, args.episodes, args.max_iterations, args.epsilon, args.gamma, args.learning_rate


if __name__ == "__main__":

    env_type, mode, weight_out, returns_out, episodes, max_iterations, epsilon, gamma, lr = parse_args()

    if env_type == "mc":
        env = MountainCar(mode=mode,debug=False) # Replace me!
    elif env_type == "gw":
        env = GridWorld(mode=mode,debug=False) # Replace me!
    else: raise Exception(f"Invalid environment type {env_type}")



    w=np.zeros((env.action_space,env.state_space+1),dtype=float)
    returns=[]



    for episode in range(episodes):

        s_0 = env.reset()
        s_0 = np.insert(s_0, 0, 1)
        rewards=0


        for iteration in range(max_iterations):

            q=np.matmul(w, s_0)

            if np.random.uniform(low=0,high=1)>=epsilon:
                    a = np.argmax(q)
            else:
                    a=np.random.choice(env.action_space)

            s, r, done = env.step(a)
            rewards+=r
            s = np.insert(s, 0, 1)
            q_s_a_2 = r + (gamma * (np.max(np.matmul(w, s))))
            w[a] = w[a] - (lr * (q[a] - q_s_a_2) * s_0)
            s_0=s
            if (done==True):
                break

        returns.append(rewards)





    np.savetxt(weight_out,w, fmt="%.18e", delimiter=" ")
    np.savetxt(returns_out,np.array(returns), fmt="%.18e", delimiter=" ")
