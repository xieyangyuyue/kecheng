import pymysql


# 添加课程信息，输入课程信息格式为：Cno,Cname,Tno
def addCourse(cs):
    courseInfo = input()
    # **********begin********** #
    
	
	
    # **********end********** #


# 修改课程信息（通过课程编号修改课程名称），输入新课程信息格式为：Cno,Cname
def updateCourse(cs):
    courseInfo = input()
    # **********begin********** #
   
   
   
    # **********end********** #


# 查询课程信息（通过课程编号查询课程信息），输入课程编号 Cno
# 将课程信息打印到控制台
def findCourseByCno(cs):
    courseId = input()
    # **********begin********** #
   
   
   
    # **********end********** #


# 删除课程信息（通过课程编号删除课程信息），输入课程编号 Cno
def deleteCourse(cs):
    courseId = input()
    # **********begin********** #
    
	
	
    # **********end********** #


# 通过教师名称查询课程名称并将其打印到控制台,输入教师名称 Tname
# 打印格式为：课程名  （一个课程名一行，不含其它字符）
def findCourseByTeacherName(cs):
    tname = input()
	# **********begin********** #
    
	
	
	# **********end********** #


# 通过课程名称查询教师名称并将其打印到控制台，输出课程名称 Cname
# 打印格式为：教师名  （一个教师名一行，不含其它字符）
def selectTeacherByCname(cs):
    cname = input()
	# **********begin********** #
   
   
   
	# **********end********** #

def Test(cs):
    sql = "select * from Course"
    cs.execute(sql)
    courseInfo = cs.fetchall()
    print(courseInfo)


if __name__ == '__main__':
    conn = pymysql.connect(host='localhost', port=3306,
                           user='root', passwd='123123', db="nudt", charset='utf8')
    command = input()
    cs = conn.cursor()
    if command == '01':
        addCourse(cs)
    elif command == '02':
        updateCourse(cs)
    elif command == '03':
        findCourseByCno(cs)
    elif command == '04':
        deleteCourse(cs)
    elif command == '05':
        findCourseByTeacherName(cs)
    elif command == '06':
        selectTeacherByCname(cs)
    conn.commit()
    Test(cs)
    cs.close()
    conn.close()
