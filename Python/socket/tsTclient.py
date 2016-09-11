from socket import *

HOST = 'localhost'
PORT = 21567
BUFSIZE = 1024
ADDR = (HOST,PORT)

tcpClickSock = socket(AF_INET, SOCK_STREAM)
tcpClickSock.connect(ADDR)

while True:
    data = raw_input('> ')
    if not data:
        break
    tcpClickSock.send(data)
    data = tcpClickSock.recv(BUFSIZE)
    if not data:
        break
    print data
tcpClickSock.close()