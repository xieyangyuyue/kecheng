import numpy as np

class Env:
    def __init__(self, name, n_states=11, n_actions=4, gamma=0.9, end_states=[6, 10]):
        self.name = name
        self.n_states = n_states  # 状态数
        self.n_actions = n_actions  # 动作数
        self.gamma = gamma  # 折扣因子
        self.start_state = 0  # 起始状态
        self.end_states = end_states  # 终止状态
        self.P = self.makeP()  # 定义转移概率矩阵
        self.R = self.makeR()  # 定义报酬向量

    def action(self, state, action):
        return np.random.choice(self.n_states, p=self.P[state, action])  # 根据转移概率选择新状态

    def makeP(self):
        P = np.zeros((self.n_states, self.n_actions, self.n_states))
        for s in range(self.n_states):
            for a in range(self.n_actions):
                if s in self.end_states:
                    P[s, a, s] = 1
                else:
                    next_state = self.get_next_state(s, a)
                    P[s, a, next_state] = 1
        return P

    def get_next_state(self, state, action):
        if action == 0:  # 向上移动
            return max(state - 1, 0)
        elif action == 1:  # 向下移动
            return min(state + 1, self.n_states - 1)
        elif action == 2:  # 向左移动
            return max(state - 1, 0)  # 假设在一维中左移
        elif action == 3:  # 向右移动
            return min(state + 1, self.n_states - 1)  # 假设在一维中右移

    def makeR(self):
        R = np.zeros(self.n_states)
        R[6] = -1  # 终止状态 6 的报酬
        R[10] = 1  # 终止状态 10 的报酬
        return R


class TD:
    def __init__(self, env, alpha=0.5, policy=None):
        self.env = env
        self.alpha = alpha  # 学习率
        self.policy = policy if policy is not None else [3] * self.env.n_states  # 默认策略
        self.U = np.zeros(self.env.n_states)  # 初始化价值函数

    def train(self, episodes=1000):
        for _ in range(episodes):
            state = np.random.choice([i for i in range(self.env.n_states) if i not in self.env.end_states])
            while state not in self.env.end_states:
                action = self.policy[state]  # 按策略选择动作
                next_state = self.env.action(state, action)  # 执行动作，获得新状态
                reward = self.env.R[state]  # 获得报酬
                # 更新状态价值
                self.U[state] += self.alpha * (reward + self.env.gamma * self.U[next_state] - self.U[state])
                state = next_state


class QLearning:
    def __init__(self, env, alpha=0.5, epsilon=0.1):
        self.env = env
        self.alpha = alpha  # 学习率
        self.epsilon = epsilon  # 探索率
        self.Q = np.ones((self.env.n_states, self.env.n_actions)) / self.env.n_actions  # 初始化Q值

    def train(self, episodes=1000):
        for _ in range(episodes):
            state = np.random.choice([i for i in range(self.env.n_states) if i not in self.env.end_states])
            while state not in self.env.end_states:
                if np.random.rand() < self.epsilon:  # 探索
                    action = np.random.choice(self.env.n_actions)
                else:  # 利用
                    action = np.argmax(self.Q[state])
                next_state = self.env.action(state, action)
                reward = self.env.R[state]
                # Q值更新
                self.Q[state, action] += self.alpha * (reward + self.env.gamma * np.max(self.Q[next_state]) - self.Q[state, action])
                state = next_state


class FTD:
    def __init__(self, env, alpha=0.001):
        self.w = np.array([0.5, 0.5, 0.5])  # 权重向量初始化
        self.env = env
        self.alpha = alpha  # 学习率
        self.policy = [3] * self.env.n_states  # 默认策略

    def U(self, state):
        if state in self.env.end_states:
            return self.env.R[state]
        row, col = divmod(state, int(np.sqrt(self.env.n_states)))
        return np.dot(np.array([1, row, col]), self.w)  # 线性逼近的状态价值

    def dU(self, state):
        row, col = divmod(state, int(np.sqrt(self.env.n_states)))
        return np.array([1, row, col])

    def train(self, episodes=1000):
        for _ in range(episodes):
            state = np.random.choice([i for i in range(self.env.n_states) if i not in self.env.end_states])
            Rsum = self.env.R[state]
            gamma = self.env.gamma
            while state not in self.env.end_states:
                action = self.policy[state]
                state = self.env.action(state, action)
                Rsum += gamma * self.env.R[state]
                gamma *= self.env.gamma
            # 更新权重
            self.w += self.alpha * (Rsum - self.U(state)) * self.dU(state)


# 进行多次训练并记录结果
def run_multiple_trainings(num_runs=10, episodes=1000):
    td_results = []
    q_learning_results = []
    ftd_results = []

    for run in range(num_runs):
        print(f"运行 {run + 1}/{num_runs}...")

        # 创建环境和代理
        env = Env("Robot Navigation")
        
        # TD训练
        td_agent = TD(env)
        td_agent.train(episodes)
        td_results.append(td_agent.U)  # 记录价值函数
        
        # Q-Learning训练
        q_learning_agent = QLearning(env)
        q_learning_agent.train(episodes)
        q_learning_results.append(q_learning_agent.Q)  # 记录Q值
        
        # FTD训练
        ftd_agent = FTD(env)
        ftd_agent.train(episodes)
        ftd_results.append(ftd_agent.w)  # 记录权重

    return td_results, q_learning_results, ftd_results


# 运行多次训练
num_runs = 10
episodes = 1000
td_results, q_learning_results, ftd_results = run_multiple_trainings(num_runs, episodes)

# 输出最终的价值函数或 Q 值，用于验证
for i in range(num_runs):
    print(f"运行 {i + 1}:")
    print("TD 价值函数:", td_results[i])
    print("Q-Learning Q值:", q_learning_results[i])
    print("FTD 权重:", ftd_results[i])
    print()
