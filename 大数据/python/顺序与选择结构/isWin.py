jimscore = int(input())
jerryscore = int(input())

# 判断若jim的得分jimscore更高，则赢家为jim
if jimscore > jerryscore:
    winner = "jim"

# 若jerry的得分jerryscore更高，则赢家为jerry
else:
    winner = "jerry"

# 输出赢家的名字
print(winner)
