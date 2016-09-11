from socket import *
HOST = 'localhost'
PORT = 21567
BUFSIZ = 1024
ADDR = (HOST,PORT)

while True:
    tcpClicSock = socket(AF_INET, SOCK_STREAM)
    tcpClicSock.connect(ADDR)
    data = raw_input('> ')
    if not data:
        break
    tcpClicSock.send('%s\r\n' % data)
    data = tcpClicSock.recv(BUFSIZ)
    if not data:
        break
    print data.strip()
    tcpClicSock.close()