# Neo4j自定义函数与过程插件

> 此插件对应版本：NEO4J-3.4.X

> PLUGIN安装：MAVEN INSTALL之后在target目录下生成的JAR包安装到NEO4J安装目录下的PLUGIN目录，将dic文件夹移动到NEO4J安装根目录即可。

## 自定义中文全文检索

中文分词：需要新增的词表在user_defined.dic新增或者在cfg.xml文件中配置即可

```
# 版本信息：
 
LUCENE-5.5.0 
     
IKAnalyzer-5.0
```

## 自定义过程和函数

>过程：用 Call com.xxxx.xx （参数）来调用执行。

>函数：可以用在cypher中任何可以使用方法的地方如where子句，return子句中。如match (n) wehre com.xxx.xx(n) return n。

1、计算IDS中ID的个数
```sql
RETURN zdr.apoc.getEventIdsSize("123123,123123,2131,12321,23424,123123,2331") as value
match p=(n:LABEL1)<-[r:REL]-(m:LABEL2) where n.name='新闻_1432' and r.eventTargetIds IS NOT NULL return p ORDER BY zdr.apoc.getEventIdsSize(r.eventTargetIds) DESC limit 10
```

2、列表数字降序排列
```sql
RETURN zdr.apoc.sortDESC([4,3,5,1,6,8,7]) as descList
```

3、打印HELLO WORLD
```sql
RETURN zdr.apoc.hello("world") as greeting
```

4、创建测试节点
```sql
CALL zdr.apoc.createCustomer('Test') YIELD node RETURN node
```

5、离差标准化函数
```sql
zdr.apoc.scorePercentage
```

6、移动小数点
```sql
zdr.apoc.moveDecimalPoint
```

7、中文分词
```sql
RETURN zdr.index.iKAnalyzer('复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？',true) AS words
```

8、创建中文全文索引（不同标签使用相同的索引名即可支持跨标签类型检索）
```sql
CALL zdr.index.addChineseFulltextIndex('IKAnalyzer', ['description'], 'Loc') YIELD message RETURN message
CALL zdr.index.addChineseFulltextIndex('IKAnalyzer',['description','year'], 'Loc') YIELD message RETURN message
CALL zdr.index.addChineseFulltextIndex('IKAnalyzer', ['description','year'],'LocProvince') YIELD message RETURN message

```

9、中文全文索引查询（可跨标签类型检索）- *-1表示数据量不做限制返回全部 *-lucene查询示例 
```sql
CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node RETURN node
CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node,weight RETURN node,weight
CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', -1) YIELD node,weight RETURN node,weight
CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', '+(description:复联) AND -(_entity_name:美国)',10) YIELD node,weight RETURN node,weight
CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', '+(site_name:东方网) OR +(_entity_name:东方网)',10) YIELD node,weight RETURN node,weight
```

10、为节点添加索引
```sql
MATCH (n) WHERE n.name='A' WITH n CALL zdr.index.addNodeChineseFulltextIndex(n, ['description']) RETURN *
```

11、更多过程与函数请参考源码和测试...

## IKAnalyzer分词

分词步骤：词典加载、预处理、分词器分词、歧义处理、结尾处理（处理遗漏中文字符/处理数量词）

分词模式：SMART模式（歧义判断）与非SMART模式（最小力度的分词）
```
具体的实例：
     张三说的确实在理

smart模式的下分词结果为：  
     张三 | 说的 | 确实 | 在理

而非smart模式下的分词结果为：
     张三 | 三 | 说的 | 的确 | 的 | 确实 | 实在 | 在理
```
## 备注

为了避免不必要的BUG，函数和过程最好使用不同的类编写。

