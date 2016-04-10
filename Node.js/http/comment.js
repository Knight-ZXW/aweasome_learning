var http = require('http')
var querystring = require('requirestring')

var postData = querystring.stringify({
	'content':'一起期待下一期的课程',
	'cid':348
})

var options = {
	hostname = 'http://www.baidu.com',
	port : 80,

}
var req = http.request(options,function(res){
	console.log('Status: '+res.statusCode)
	console.log('headers: '+JSON.stringfy(res.headers))
	res.on('data',function(chunk){
		console.log(Buffer.isBUffer(chunk))
		console.log(typeof chunk)
	}

	)
})
req.write()
req.end()