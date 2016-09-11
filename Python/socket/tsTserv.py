from socket import *
from time import ctime

HOST = ''
PORT = 21567
BUFSIZE = 1024
ADDR = (HOST,PORT)

tcpSerSock = socket(AF_INET,SOCK_STREAM)
tcpSerSock.bind(ADDR)
tcpSerSock.listen(5)

while True:
    print 'waiting for connection'
    tcpClickSock, addr = tcpSerSock.accept()
    print '... connected form:', addr

    while True:
        data = tcpClickSock.recv(BUFSIZE)
        print 'he say'
        if not data:
            break
        tcpClickSock.send('[%s] %s' % (
            ctime(),data
        ))

    tcpClickSock.close()
tcpSerSock.close()