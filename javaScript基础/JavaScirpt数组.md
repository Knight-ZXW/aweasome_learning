#JavaScript 数组
@(Android 开发之路)
  跟其他语言的不同点在于，**JS中的数组是弱类型的，数组中可以含有不同类型的元素。数组元素可以是对象或者其他数组**，数组也是**对象**，按照索引访问数组常常比访问一般对象属性会快，数组对象继承 Array.prototype 上的大量数组操作方法。
　　字符串是类数组的，可以当做一个数组来访问和修改，但是没有数组的很多操作函数。事实上，字符串是 immutable 不可变的

##delete操作
```
var arr = Array()
arr[0] = 1 
0 in arr; //True
arr[0] = undefined // 设置值为undefined
0 in arr; //true 判断认为arr中依然存在index 为0的元素
delete arr[0] //使用delete操作删除元素
0 in arr;// false, 此时认为不存在
arr[0] // undefined ,但是值依然还是 undefined
```

## 数组方法
> [] => Array.prototype
>  - join 
>  - reverse (原数组被修改了)
>  -sort (原数组也被修改了，而且sort 默认会把数字转成字符排序),不过可以通过 arr.sort(function ...)，，传入比较函数
>  - concat (原数组未被修改)， 想要得到concat的结果，只能通过得到返回值的方式，数组元素会被拉平一次（即元素内容也是数组）
>  - slice (原数组未被修改) 左闭右开截取
>  -splice  (会修改原数组), arr.splice(1,1,'a','b') 删除元素的同时，添加一些元素

**以下ECMScirpt5的新特性**
>  - forEach  迭代
>  - map 为每个元素调用函数，原数组未被修改
>  - filter 过滤函数,原数组未被修改
>  - every 判断函数，判断每个元素是否都符合函数的判定，返回值是Bool
>  - some  是否存在某一个 （个人感觉 any 更符合这个语义吧）
>  - reduce  把数组每两个元素应用于函数，每次返回的结果，作为下次调用函数的第一个参数，
>  - reduceRight 从右到左
>  - indexOf  根据索引查找到元素。可选的第二个参数，负数表示从右想做第几个开始查找

### 判断是不是一个数组
```javascript
[] instanceof Array 
({}).toString.apply([]) === ['object Array']
[].constructor ==== Array //构造器是可以修改的，所以这个方法可能会失效
```