from sklearn import datasets
import matplotlib.pyplot as plt
import numpy as np

# 获取数据集并进行探索
iris = datasets.load_iris()
irisFeatures = iris['data']
irisFeaturesName = iris['feature_names']
irisLabels = iris['target']

def norm2(x):
    # 求2范数的平方值
    return np.sum(x * x)

class KMeans(object):
    def __init__(self,k=int,n=int):
        # k:聚类数目，n:数据集维度
        self.K = k
        self.N = n
        self.u = np.zeros((k,n))
        self.C = [[] for i in range(k)]
        self.labels = np.zeros(len(irisFeatures),dtype=int)
        # u[i]:第i个类的中心点，C[i]:第i类的数据点

    def fit(self,data: np.ndarray):
        # data:每行是一个数据点
        self.select_u0(data)
        # 聚类中心初始化
        J = 0
        oldJ = 100
        while abs(J - oldJ) > 0.001:
            oldJ = J
            J = 0
            self.C = [[] for i in range(self.K)]
            # for x in data:
            #     nor = [norm2(self.u[i] - x) for i in range(self.K)]
            #     J += np.min(nor)
            #     self.C[np.argmin(nor)].append(x)
            # self.u = [np.mean(np.array(self.C[i]),axis=0) for i in range(self.K)]
            for i,x in enumerate(data):
                nor = [norm2(self.u[j] - x) for j in range(self.K)]
                cluster_idx = np.argmin(nor)
                J += np.min(nor)
                self.C[cluster_idx].append(x)
                self.labels[i] = cluster_idx
            self.u = [np.mean(np.array(self.C[i]),axis=0) for i in range(self.K)]

    def select_u0(self,data: np.ndarray):
        for j in range(self.N):
            # 得到该列数据的最大最小值
            minJ = np.min(data[:,j])
            maxJ = np.max(data[:,j])
            rangeJ = float(maxJ - minJ)
            # 聚类中心的第j维坐标随机初始化
            self.u[:,j] = minJ + rangeJ * np.random.rand(self.K)

model = KMeans(4,4)
# k=3,n=4
model.fit(irisFeatures)

# 绘制原始数据的分布图
plt.figure(figsize=(12,6))
plt.rcParams['font.sans-serif'] = ['KaiTi']

# 原始数据分布图
plt.subplot(1,2,1)
plt.scatter(irisFeatures[:,0],irisFeatures[:,1],c=irisLabels,cmap='viridis',marker='o',label='原始数据')
plt.xlabel('petal length')
plt.ylabel('petal width')
plt.title('原始数据分布/origin data')
plt.legend(loc=2)

# 聚类结果分布图
plt.subplot(1,2,2)
# x = np.array(model.C[0])
# plt.scatter(x[:,0],x[:,1],c='red',marker='o',label='cluster1')
# x = np.array(model.C[1])
# plt.scatter(x[:,0],x[:,1],c='green',marker='*',label='cluster2')
# x = np.array(model.C[2])
# plt.scatter(x[:,0],x[:,1],c='blue',marker='+',label='cluster3')
# u = np.array(model.u)
# plt.scatter(u[:,0],u[:,1],c='black',marker='X',label='center')

plt.scatter(irisFeatures[:,0],irisFeatures[:,1],c=model.labels,cmap='viridis',marker='o',label='聚类结果')
u = np.array(model.u)
plt.scatter(u[:,0],u[:,1],c='red',marker='X',label='中心点')
plt.xlabel('petal length')
plt.ylabel('petal width')
plt.title('聚类结果分布/clustered data')
plt.legend(loc=2)

plt.tight_layout()
plt.show()

