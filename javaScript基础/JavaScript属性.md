@(Android 开发之路)
# JavaScript  属性

## 属性读写
- 使用 **.**访问 和 **[]** 访问对象的属性，个人感觉.更方面，[]更灵活，应为你可以通过代码来构造出要访问的属性名称
- 属性的读写 有点区别，读 属性 会进行 原型链的查找，而写操作不会向上查找原型链
- delete 语法用于删除 对象属性,delete 的返回结果是 **true** 或者 **false**,但是它只能表示现在该对象还有该属性，而不能表示是否真的删除了原来存在的一个属性

## 属性 getter/setter 方法
```javascript
var man = {
	weibo:'@Bosb',
	$age:null,
	get age(){
		if (this.$age == undefined){
			return new Date().getFullYear() - 1988;
		} else {
			return this.$age;
		}
	},
	set age(val){
		val = +val;
		if (!isNaN(val) && val>0 && val <150){
			this.$age = +val;
		} else {
			throw new Error('Incorrect val ='+val);
		}
	} 
}

```
## 判断对象的 class 标签
```javascript
var toString = Object.prototype.toString;
function getTYpe(o){return toString.call(o).slice(8,-1);};

// test
toString.call(null); // "[object Null]"
getType(null); //"Null"
```

## extensible 标签
　　表示这个对象是否可拓展
```javascript
var obj = {x:1,y:2};
Object.isExtensible(obj) //true
Object.preventExtensions(obj);
Object.isExtensible(obj) //fase
obj.z = 1;
obj.z; //undefined
Object.getOwnPropertyDescriptor(obj,'x')
//Object {value:1,writable:true,enumerable:true,configurable:true}
// 注意此时，所有的属性依然是 configurable,如果想configurable为False, 则 可以用更强大的 Object.seal(obj),如果想writeable也为False则 用 Object.freeze(obj)
```