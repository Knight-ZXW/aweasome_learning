##WSGI简介
*WSGI（Python Web Server GateWay Interface）
*定义了Web服务器和Web应用程序之间通信的接口规范

好处：应用程序可以部署和切换在符合WSGI协议的服务器上，
	方便的进行中间件的开发

## WSGI应用
  是一个接受两个人参数的可调用对象
###两个参数
1.environ 参数是个字典对象，包含CGI风格的环境变量
2.start_response参数是一个接受两个固定参数和一个可选参数的可调用者。

## WSGI服务器
  为每一个HTTP请求，调用WSGI应用
  1.GUnicorn
  2.Gevent
  3等等
 ## Web服务器
  1.Nginx(可以做反向代理，处理静态文件更高效，可以做负载均衡)


## Virtualenv
  用于创建独立的Python运行环境
  