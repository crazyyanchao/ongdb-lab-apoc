Neo4j自定义函数与过程实现高效的数据访问

1、计算IDS中ID的个数
RETURN zdr.apoc.getEventIdsSize("123123,123123,2131,12321,23424,123123,2331") as value

match p=(n:LABEL1)<-[r:REL]-(m:LABEL2) where n.name='新闻_1432' and r.eventTargetIds IS NOT NULL return p ORDER BY zdr.apoc.getEventIdsSize(r.eventTargetIds) DESC limit 10

2、列表数字降序排列
RETURN zdr.apoc.sortDESC([4,3,5,1,6,8,7]) as descList

3、打印HELLO WORLD
RETURN zdr.apoc.hello("world") as greeting

4、创建测试节点
CALL zdr.apoc.createCustomer('Test') YIELD node RETURN node

5、离差标准化函数
zdr.apoc.scorePercentage

6、移动小数点
zdr.apoc.moveDecimalPoint
