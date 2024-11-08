import numpy as np
from numpy.random import normal

class Env:
    def __init__(self, name):
        self.Name = name
        self.N = 11  # 状态数量
        self.A = np.arange(4)  # 四个动作：0, 1, 2, 3
        self.X = np.arange(self.N)  # 状态空间
        self.X2RowCol = {i: (i // 4, i % 4) for i in range(self.N)}  # 状态到坐标的映射
        self.makeP()  # 定义转移概率矩阵
        self.makeR()  # 定义报酬向量
        self.Gamma = 1  # 折扣因子
        self.StartState = 0  # 起始状态
        self.EndStates = [6, 10]  # 终止状态

    def makeP(self):
        # 初始化转移概率矩阵
        self.P = np.zeros((self.N, len(self.A), self.N))
        for s in range(self.N):
            for a in self.A:
                self.P[s, a, :] = np.random.dirichlet(np.ones(self.N))

    def makeR(self):
        # 初始化报酬向量
        self.R = np.zeros(self.N)
        self.R[4] = 0.5
        self.R[6] = -1
        self.R[10] = 1

    def action(self, x, a):
        x_ = np.random.choice(self.N, p=self.P[x, a, :])
        return x_

class TD:
    def __init__(self, E):
        self.E = E
        self.Alpha = 0.5
        self.Pi = [3, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0]
        self.U = [0] * self.E.N

    def train(self, episodes=20):
        for episode in range(episodes):
            x = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])
            while x not in self.E.EndStates:
                a = self.Pi[x]
                _x = self.E.action(x, a)
                r = self.E.R[x]
                self.U[x] += self.Alpha * (r + self.E.Gamma * self.U[_x] - self.U[x])
                x = _x
            print(f"Episode {episode + 1} - 状态价值：{self.U}")

class Q_Learning:
    def __init__(self, E):
        self.E = E
        self.Alpha = 0.5
        self.Q = np.ones((self.E.N, len(self.E.A))) / 4
        self.Q[10, :] = 1
        self.Q[6, :] = -1

    def train(self, episodes=20):
        for episode in range(episodes):
            x = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])
            while x not in self.E.EndStates:
                P = np.exp(self.Q[x]) / np.sum(np.exp(self.Q[x]))
                a = np.random.choice(4, p=P)
                _x = self.E.action(x, a)
                r = self.E.R[x]
                self.Q[x, a] += self.Alpha * (r + self.E.Gamma * np.max(self.Q[_x]) - self.Q[x, a])
                x = _x
            print(f"Episode {episode + 1} - Q 值：\n{self.Q}")

class F_TD:
    def __init__(self, E):
        self.w = np.array([0.5, 0.5, 0.5])
        self.Pi = [3, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0]
        self.Alpha = 0.001
        self.E = E

    def U(self, x):
        if x == 10:
            return 1
        if x == 6:
            return -1
        (row, col) = self.E.X2RowCol[x]
        return np.dot(np.array([1, row, col]), self.w)

    def dU(self, x):
        (row, col) = self.E.X2RowCol[x]
        return np.array([1, row, col])

    # def train(self, episodes=10):
    #     for episode in range(episodes):
    #         x0 = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])
    #         a0 = self.Pi[x0]
    #
    #         Rsum = self.E.R[x0]
    #         x = x0
    #         a = a0
    #         gamma = self.E.Gamma
    #
    #         while x not in self.E.EndStates:
    #             x_ = self.E.action(x, a)
    #             Rsum += gamma * self.E.R[x]
    #             a = self.Pi[x]
    #             gamma *= self.E.Gamma
    #
    #         self.w += self.Alpha * (Rsum - self.U(x0)) * self.dU(x0)
    #         print(f"Episode {episode + 1} - 权重向量：{self.w}")
    def train(self, episodes=20, max_steps=100):
        for episode in range(episodes):
            x0 = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])
            a0 = self.Pi[x0]

            Rsum = self.E.R[x0]
            x = x0
            a = a0
            gamma = self.E.Gamma
            steps = 0  # 步数计数器

            while x not in self.E.EndStates and steps < max_steps:
                x_ = self.E.action(x, a)
                Rsum += gamma * self.E.R[x]
                a = self.Pi[x]
                gamma *= self.E.Gamma
                x = x_  # 更新状态
                steps += 1

            # 更新权重
            self.w += self.Alpha * (Rsum - self.U(x0)) * self.dU(x0)
            print(f"Episode {episode + 1} - 权重向量：{self.w}")
            print(f"  Rsum: {Rsum}, 初始状态: {x0}, 步数: {steps}")

# 实例化环境
env = Env("GridWorld")

# 实例化并训练TD算法
td_agent = TD(env)
print("TD算法训练中...")
td_agent.train()

# 实例化并训练Q学习算法
q_learning_agent = Q_Learning(env)
print("\nQ学习算法训练中...")
q_learning_agent.train()

# 实例化并训练F_TD算法
f_td_agent = F_TD(env)
print("\nF_TD算法训练中...")
f_td_agent.train()
