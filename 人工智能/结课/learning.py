import numpy as np

class Env:
    def __init__(self, name="Robot Navigation"):
        self.name = name
        self.num_states = 11  # 状态数
        self.num_actions = 4  # 动作数
        self.gamma = 0.9  # 折扣因子
        self.start_state = 0  # 起始状态
        self.end_states = [6, 10]  # 终止状态
        self.init_transition_rewards()  # 初始化转移概率和奖励

    def init_transition_rewards(self):
        # 初始化转移概率矩阵和奖励
        self.P = np.zeros((self.num_states, self.num_actions, self.num_states))
        self.R = np.zeros(self.num_states)
        for s in range(self.num_states):
            for a in range(self.num_actions):
                if s in self.end_states:
                    self.P[s, a, s] = 1  # 终止状态保持不变
                else:
                    # 示例：定义一些简单的上下左右动作规则
                    if a == 0:  # 向上
                        self.P[s, a, max(s - 1, 0)] = 1
                    elif a == 1:  # 向下
                        self.P[s, a, min(s + 1, self.num_states - 1)] = 1
                    elif a == 2:  # 向左
                        self.P[s, a, max(s - 1, 0)] = 1
                    elif a == 3:  # 向右
                        self.P[s, a, min(s + 1, self.num_states - 1)] = 1
        self.R[6] = -1  # 给终止状态 6 一个负奖励
        self.R[10] = 1  # 给终止状态 10 一个正奖励

    def step(self, state, action):
        # 执行动作，返回下一个状态和奖励
        next_state = np.random.choice(self.num_states, p=self.P[state, action])
        reward = self.R[state]
        return next_state, reward


class TD:
    def __init__(self, env):
        self.env = env
        self.alpha = 0.5  # 学习率
        self.policy = [3, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0]  # 固定策略
        self.values = np.zeros(env.num_states)  # 初始化价值函数

    def train(self, episodes=100):
        for _ in range(episodes):
            state = np.random.choice([s for s in range(self.env.num_states) if s not in self.env.end_states])
            while state not in self.env.end_states:
                action = self.policy[state]
                next_state, reward = self.env.step(state, action)
                # 更新价值函数
                self.values[state] += self.alpha * (reward + self.env.gamma * self.values[next_state] - self.values[state])
                state = next_state


class QLearning:
    def __init__(self, env):
        self.env = env
        self.alpha = 0.5  # 学习率
        self.q_values = np.zeros((env.num_states, env.num_actions))  # 初始化 Q 值表

    def train(self, episodes=100):
        for _ in range(episodes):
            state = np.random.choice([s for s in range(self.env.num_states) if s not in self.env.end_states])
            while state not in self.env.end_states:
                action = np.argmax(self.q_values[state]) if np.random.rand() > 0.1 else np.random.choice(self.env.num_actions)
                next_state, reward = self.env.step(state, action)
                # 更新 Q 值
                best_next_action = np.argmax(self.q_values[next_state])
                td_target = reward + self.env.gamma * self.q_values[next_state, best_next_action]
                self.q_values[state, action] += self.alpha * (td_target - self.q_values[state, action])
                state = next_state


class LinearApproximationTD:
    def __init__(self, env):
        self.env = env
        self.alpha = 0.001  # 学习率
        self.policy = [3, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0]  # 固定策略
        self.weights = np.random.rand(3)  # 权重向量初始化

    def feature_vector(self, state):
        # 返回状态的特征向量
        row, col = divmod(state, int(np.sqrt(self.env.num_states)))
        return np.array([1, row, col])

    def value_function(self, state):
        # 通过线性函数逼近价值
        return np.dot(self.feature_vector(state), self.weights)

    def train(self, episodes=100):
        for _ in range(episodes):
            state = np.random.choice([s for s in range(self.env.num_states) if s not in self.env.end_states])
            while state not in self.env.end_states:
                action = self.policy[state]
                next_state, reward = self.env.step(state, action)
                td_target = reward + self.env.gamma * self.value_function(next_state)
                # 更新权重
                self.weights += self.alpha * (td_target - self.value_function(state)) * self.feature_vector(state)
                state = next_state


# 实例化并运行训练过程
env = Env()

# 策略价值学习
td_agent = TD(env)
td_agent.train()
print("策略价值函数:", td_agent.values)

# 最优行动价值学习
q_learning_agent = QLearning(env)
q_learning_agent.train()
print("Q-Learning Q值表:", q_learning_agent.q_values)

# 线性逼近价值学习
linear_td_agent = LinearApproximationTD(env)
linear_td_agent.train()
print("线性逼近权重:", linear_td_agent.weights)
