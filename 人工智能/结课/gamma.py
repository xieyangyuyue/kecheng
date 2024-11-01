import numpy as np

class Env:
    def __init__(self, name):
        self.Name = name
        self.N = 11  # 状态数
        self.A = np.arange(4)  # 动作数
        self.X = np.arange(self.N)  # 状态空间
        self.Gamma = 0.9  # 折扣因子
        self.StartState = 0  # 起始状态
        self.EndStates = [6, 10]  # 终止状态
        self.makeP()  # 定义转移概率矩阵
        self.makeR()  # 定义报酬向量

    def action(self, x, a):
        x_ = np.random.choice(self.N, p=self.P[x, a, :])  # 根据转移概率选择新状态
        return x_

    def makeP(self):
        # 设置一个示例转移概率矩阵，根据具体需求调整
        self.P = np.zeros((self.N, len(self.A), self.N))
        # 假设：动作 0-3 分别代表上下左右移动，边界状态将概率分配给自身
        for s in range(self.N):
            for a in range(len(self.A)):
                if s in self.EndStates:
                    self.P[s, a, s] = 1
                else:
                    if a == 0:  # 向上移动
                        self.P[s, a, max(s - 1, 0)] = 1  # 上移
                    elif a == 1:  # 向下移动
                        self.P[s, a, min(s + 1, self.N - 1)] = 1  # 下移
                    elif a == 2:  # 向左移动
                        self.P[s, a, max(s - 1, 0)] = 1  # 假设在一维中左移
                    elif a == 3:  # 向右移动
                        self.P[s, a, min(s + 1, self.N - 1)] = 1  # 假设在一维中右移

    def makeR(self):
        # 定义报酬向量的示例：终止状态奖励/惩罚，其余为零
        self.R = np.zeros(self.N)
        self.R[6] = -1  # 终止状态 6 的报酬
        self.R[10] = 1  # 终止状态 10 的报酬


class TD:
    def __init__(self, E):
        self.E = E
        self.Alpha = 0.5  # 学习率
        self.Pi = [3, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0]  # 策略
        self.U = np.zeros(self.E.N)  # 初始化价值函数

    def train(self):
        x = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])  # 随机选择起始状态
        while x not in self.E.EndStates:
            a = self.Pi[x]  # 按策略选择动作
            _x = self.E.action(x, a)  # 执行动作，获得新状态
            r = self.E.R[x]  # 获得报酬
            # 更新状态价值
            self.U[x] = self.U[x] + self.Alpha * (r + self.E.Gamma * self.U[_x] - self.U[x])
            x = _x


class Q_Learning:
    def __init__(self, E):
        self.E = E
        self.Alpha = 0.5  # 学习率
        self.Q = np.ones((self.E.N, len(self.E.A))) / 4  # 初始化Q值

    def train(self):
        x = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])  # 随机选择起始状态
        while x not in self.E.EndStates:
            a = np.argmax(self.Q[x]) if np.random.rand() > 0.1 else np.random.choice(self.E.A)
            _x = self.E.action(x, a)
            r = self.E.R[x]
            # Q值更新
            self.Q[x, a] = self.Q[x, a] + self.Alpha * (r + self.E.Gamma * np.max(self.Q[_x]) - self.Q[x, a])
            x = _x


class FTD:
    def __init__(self, E):
        self.w = np.array([0.5, 0.5, 0.5])  # 权重向量初始化
        self.E = E
        self.Alpha = 0.001  # 学习率
        self.Pi = [3, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0]  # 策略

    def U(self, x):
        if x in self.E.EndStates:
            return self.E.R[x]
        (row, col) = divmod(x, int(np.sqrt(self.E.N)))
        return np.dot(np.array([1, row, col]), self.w)  # 线性逼近的状态价值

    def dU(self, x):
        (row, col) = divmod(x, int(np.sqrt(self.E.N)))
        return np.array([1, row, col])

    def train(self):
        x0 = np.random.choice([0, 1, 2, 3, 4, 5, 7, 8, 9])
        Rsum = self.E.R[x0]
        x = x0
        gamma = self.E.Gamma
        while x not in self.E.EndStates:
            a = self.Pi[x]
            x = self.E.action(x, a)
            Rsum += gamma * self.E.R[x]
            gamma *= self.E.Gamma
        # 更新权重
        self.w = self.w + self.Alpha * (Rsum - self.U(x0)) * self.dU(x0)


# 创建环境并运行训练
env = Env("Robot Navigation")
td_agent = TD(env)
td_agent.train()

q_learning_agent = Q_Learning(env)
q_learning_agent.train()

ftd_agent = FTD(env)
ftd_agent.train()

# 输出最终的价值函数或 Q 值，用于验证
print("TD 价值函数:", td_agent.U)
print("Q-Learning Q值:", q_learning_agent.Q)
print("FTD 权重:", ftd_agent.w)
