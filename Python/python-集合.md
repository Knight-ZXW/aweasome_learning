# PythonCookBook笔记-day1


---
# 使用 *表达式优雅地解决一些问题
>问题:我们希望从某个可迭代对象中分解出N个元素，但是这个可迭代对象的长度可能超过N，这时可以使用Python的 *表达式优雅地解决这个问题
```
    def drop_first_last(grades):
        first,*middle, last = grades
        return avg(middle)
```
第二个例子
```
    for tag,*args in records //我只是为了说明for in中也可以使用*表达式
```
```
    //想丢弃某些元素.在分解的时候，不能只是指定一个单独的*,但是可以使用几个常用来表示待丢弃值的变量名，比如_或者ign(ignored)。
    record = ('ACME',50,123.45,(12,18,2012))
    name, *_,(*_,year) = record
>>> name
'ACME'
>>> year
2012
```

# 使用collection.deque解决需要保存有限的历史记录的场景
```
#collections.deque的应用
from collections import deque
def search (lines, pattern, history = 5):
    previous_lines = deque(maxlen=history)
    for line in lines:
        if pattern in line:
            print('pattern in line:'+line)
            yield line,previous_lines
        print('pattern not in line:' + line)
        previous_lines.append(line)

if __name__ == '__main__':
    with open('teset.txt') as f:
        for line, prevlinews in search(f, 'python', 5):
            for pline in prevlinews:
                print(pline,end =' ')
            print(line,end ='')
            print('-'*20)
```
使用deque的好处是通过maxlen 创建了一个固定长度的队列。当有新的记录加入而队列已满时会自动移除最老的那条记录，而且当创建一个无线长的队列时，执行简单的添加和弹出操作它的复杂度是O(1)，而列表的复杂度为O(N)

# 使用heapq模块的 nlargest() 和 nsmallest（）找出某个集合中最大和最小的N个元素
```
import heapq
nums = [1,3,2,4,-1]
print(heapq.nlargest(2,nums))
print(heapq.nsmallest(2,nums))
//heaq的这2个函数还有一个带3个参数的版本，可以使用一个lamda函数，定义比较的key
//如: heaqp.nsamllest(3,protfolio,key=lambda s： s[price])
```
>例外，如果正在寻找最大或者最小的N个元素，且同集合中元素的总数目相比，N很小，那么先将集合转换成heap，然后用heapop()的形式弹出第一个元素（堆最重要的特性就是heap[0]总是最小的那个元素），这样做会有更好的性能，当N和集合差不多时，更好的方式是将集合先排序再做切片操作（如,使用sorted(items)[:N]或者sorted(items)[-N:]）


# 想要在字典上对数据执行各式各样的计算
```python
from collections import defaultdict


pieces = {
    'A':2.1,
    'B':1.0,
    'C':3.5
}
#通过zip将字典的键和值反过来，再通过min 求得最小
min_prices = min(zip(pieces.values(),pieces.keys()))
print(min_prices)
#通过sorted返回排序好的数据
sorted_pieces = sorted(zip(pieces.values(),pieces.keys()))
print(sorted_pieces)
```
**注意，通过zip()函数创建了一个迭代器，它的内容只能被消费一次**

# 字典的键有可以进行常见的集合操作，比如并集、交集、和差集
```
a.keys() & b.keys()
a.keys() - b.keys()
```

# 避免硬编码，对切片进行命名
```
record = '................100............513.25'
cost = int(record[10:20]) * float(record[2:30])
#-------用以下来替代----------
SHARES = slice(10:20)
PRICE = slice(2:30)
cost = int(record[SHARES]) * float(record[PRICE])
```

# 使用collection模块中的Count类，来实现数据制表或者计数，Counter统一可以轻松地实现各种数学运算操作 比如+,-

# 利用operator模块中的itemgetter函数对字典进行排序
```
from operator import itemgetter
...
rows = [
    {'fanme':'Brain','lname':'Jones','uid':1003}
]
sorted(rows,key = itemgetter('fname'))
# itemgetter函数还可以接受多个键
sorted(rows,key = itemgetter('lname','fanme'))
# 有时候会使用lm=ambda表达式来取代itemgetter()的功能 
 rows_by_fname = sorted(rows,key = lambda r: r['fname'])
 
#同样之前所展示的 min() 和max()这样的函数也可以使用
min(rows,key = itemgetter('uid'))
```

# 对不原生支持比较操作的对象排序
除了使用sorted函数外，另一种方式是使用operator.attrgettter()
```
    from operator import attrgetter
    sorted(users,key=attrgetter('user_id'))
    * attrgetter同样支持多字段 attrgetter('user_id','user_age')
```

# 将多个映射合并为单个映射
考虑我们有多个字典，我们想在逻辑上将它们合并为一个单独的映射结构，以此执行一些特定的曹组哦，比如查找值或者检查键是否村阻碍
Collections模块中的ChainMap可以很好的满足我们的需求.
它的一个特定是，Chainmap使用的其实是原始的字典，对原始字典的修改能很好的反应到合并后的字典上（毕竟实际上是同一个对象）
```
a = {'x':1,'z':3}
b = {'y':2,'z':4}
merged = chainMap(a,b)
merged(x) # get 1
a['x'] = 42
merged['x'] #get42

```




