import pymysql
import matplotlib
 
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from pylab import mpl
 
mpl.rcParams['font.sans-serif'] = ['SimHei']
 
if __name__ == '__main__':
    # **********begin***********#
 
    # 获取连接对象
    conn = pymysql.connect(host = 'localhost',port = 3306,
                        user = 'root',passwd = '123123',
                        db='nudt',charset='utf8')
 
    # 获取光标
    cs = conn.cursor()
 
    # 执行SQL，统计教师的课程数量并按照教师名称倒序
    sql = "select Tname,count(Cno) \
            from Teacher left outer join Course \
            on Teacher.Tno = Course.Tno \
            group by Tname \
            order by Tname desc"
 
    cs.execute(sql)
 
    # 获取结果集，将其赋予给变量 results
    results = cs.fetchall()
 
    # 遍历结果集，按照格式 --> 教师:课程数量 ，输出到控制台
    for result in results:
        print("%s : %d" % (result[0],result[1]))
        
    # 绘制柱状图
    names, courseNum = zip(*results)
    plt.xticks(fontsize=16)
    plt.yticks([])  # 不显示y轴刻度
    for i in range(len(courseNum)):
        plt.text(i, courseNum[i] * 0.5, courseNum[i], ha='center', fontsize=20)
    plt.bar(range(len(courseNum)), courseNum, color='y', tick_label=names)
    plt.savefig("step4_img/1.png")
 
    # 关闭资源
    cs.close()
    conn.close()
 
    # **********end**********#