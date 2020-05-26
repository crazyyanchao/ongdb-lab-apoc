# ONgDB自定义函数与过程插件
>apoc包过程函数命名域：olab.*

> 此插件对应版本：ONgDB-3.5.X

> PLUGIN安装：MAVEN INSTALL之后在target目录下生成的JAR包安装到ONgDB安装目录下的PLUGIN目录，将dic文件夹移动到ONgDB安装根目录即可。

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

### 1、计算IDS中ID的个数
```cql
RETURN olab.getEventIdsSize("123123,123123,2131,12321,23424,123123,2331") as value
match p=(n:LABEL1)<-[r:REL]-(m:LABEL2) where n.name='新闻_1432' and r.eventTargetIds IS NOT NULL return p ORDER BY olab.getEventIdsSize(r.eventTargetIds) DESC limit 10
```

### 2、列表数字降序排列
```cql
RETURN olab.sortDESC([4,3,5,1,6,8,7]) as descList
```

### 3、打印HELLO WORLD
```cql
RETURN olab.hello("world") as greeting
```

### 4、创建测试节点
```cql
CALL olab.createCustomer('Test') YIELD node RETURN node
```

### 5、离差标准化函数
```cql
olab.scorePercentage
```

### 6、移动小数点
```cql
olab.moveDecimalPoint
```

### 7、中文分词 *-true 智能分词，false 细粒度分词
```cql
RETURN olab.index.iKAnalyzer('复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？',true) AS words
```
- 组合切词结果后进行查询
```cql
CALL olab.iKAnalyzer('北京基金赵总',true) YIELD words WITH words
CALL olab.ik.combination.couple(words) YIELD wordF,wordT WITH wordF,wordT
UNWIND wordF AS row1
UNWIND wordT AS row2
MATCH p=(n)-[*..2]-(m) WHERE n.name CONTAINS row1 AND m.name CONTAINS row2 RETURN p LIMIT 100
```
```cql
CALL olab.iKAnalyzer('东城基金赵总',true) YIELD words WITH words
CALL olab.ik.combination.triple(words) YIELD wordF,wordT,wordR WITH wordF,wordT,wordR
UNWIND wordF AS row1
UNWIND wordT AS row2
UNWIND wordR AS row3
MATCH p=(n)-[*..2]-(f)-[*..2]-(m) WHERE n.name CONTAINS row1 AND m.name CONTAINS row2 AND f.name CONTAINS row3 RETURN p LIMIT 100
```

### 8、创建中文全文索引（不同标签使用相同的索引名即可支持跨标签类型检索）
```cql
CALL olab.index.addChineseFulltextIndex('IKAnalyzer', ['description'], 'Loc') YIELD message RETURN message
CALL olab.index.addChineseFulltextIndex('IKAnalyzer',['description','year'], 'Loc') YIELD message RETURN message
CALL olab.index.addChineseFulltextIndex('IKAnalyzer', ['description','year'],'LocProvince') YIELD message RETURN message

```

### 9、中文全文索引查询（可跨标签类型检索）- *-1表示数据量不做限制返回全部 *-lucene查询示例 
```cql
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node RETURN node
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node,weight RETURN node,weight
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', -1) YIELD node,weight RETURN node,weight
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '+(description:复联) AND -(_entity_name:美国)',10) YIELD node,weight RETURN node,weight
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '+(site_name:东方网) OR +(_entity_name:东方网)',10) YIELD node,weight RETURN node,weight
-- 包含小和合 不包含婷、诗和Jason Lim
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '_entity_name:(+小 +合 -"婷" -诗 -"Jason Lim")',10) YIELD node,weight RETURN node,weight
-- 范围查询
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '+(name:东方网) AND +(testTime:[1582279892461 TO 1582279892461])',10) YIELD node,weight RETURN node,weight

```

### 10、为节点添加索引
```cql
MATCH (n) WHERE n.name='A' WITH n CALL olab.index.addNodeChineseFulltextIndex(n, ['description']) RETURN *
```

### 11、生成JSON-从CYPHER直接生成JSON【支持节点转换/属性转换/路径转换】
```cql
match (n) return olab.convert.json(n) limit 10
match p=(n)-[]-() return olab.convert.json(p) limit 1
match (n) return olab.convert.json(properties(n)) limit 10
```

### 12、更多过程与函数请参考源码和测试...

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

### 13、生成文本指纹
```cql
RETURN olab.simhash('公司系经长春经济体制改革委员会长体改(1993)165号文批准') AS simHash
╒══════════════════════════════════════════════════════════════════╕
│"simHash"                                                         │
╞══════════════════════════════════════════════════════════════════╡
│"1010111110110100111001000100110010000110100110101110101110000110"│
└──────────────────────────────────────────────────────────────────┘
```

### 14、计算两个节点simhash相似度
- 创建三个两两之间简介相似的组织机构节点
```cql
MERGE (n:组织机构:中文名称 {name:'阿里'}) SET n.brief='阿里巴巴(中国)网络技术有限公司成立于1999年09月09日，注册地位于浙江省杭州市滨江区网商路699号，法定代表人为戴珊。经营范围包括开发、销售计算机网络应用软件；设计、制作、加工计算机网络产品并提供相关技术服务和咨询服务；',n.simhash=olab.simhash(n.brief)
MERGE (m:组织机构:中文名称 {name:'阿里巴巴'}) SET m.brief='阿里巴巴(中国)网络技术有限公司成立于1999年09月09日，注册地位于浙江省杭州市滨江区网商路699号，法定代表人为戴珊。成年人的非证书劳动职业技能培训（涉及许可证的除外）。（依法须经批准的项目，经相关部门批准后方可开展经营活动）阿里巴巴(中国)网络技术有限公司对外投资86家公司，具有37处分支机构。',m.simhash=olab.simhash(n.brief)
MERGE (f:组织机构:中文名称 {name:'Alibaba'}) SET f.brief='经营范围包括开发、销售计算机网络应用软件；设计、制作、加工计算机网络产品并提供相关技术服务和咨询服务；服务：自有物业租赁，翻译，成年人的非证书劳动职业技能培训（涉及许可证的除外）。（依法须经批准的项目，经相关部门批准后方可开展经营活动）阿里巴巴(中国)网络技术有限公司对外投资86家公司，具有37处分支机构。',f.simhash=olab.simhash(n.brief)
RETURN n,m,f
```
- 创建三个两两之间简介不相似的组织机构节点【短文本的阈值设置hammingDistance】
```
MERGE (n:组织机构:中文名称 {name:'天猫'}) SET n.brief='37处分支机构。',n.simhash=olab.simhash(n.brief)
MERGE (m:组织机构:中文名称 {name:'阿猫'}) SET m.brief='1999年09月09日',m.simhash=olab.simhash(m.brief)
MERGE (f:组织机构:中文名称 {name:'猫猫'}) SET f.brief='自有物业租赁',f.simhash=olab.simhash(f.brief)
RETURN n,m,f
```
- 生成组织机构之间的‘相似简介‘的关系
```cql
MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) 
WHERE n<>m AND NOT ((n)-[:相似简介]-(m))
CALL olab.simhash.build.rel(n,m,'simhash','simhash','相似简介',3) YIELD pathJ RETURN pathJ
```

