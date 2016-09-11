
from SocketServer import (TCPServer as TCP,StreamRequestHandler as SRH)
from time import ctime

HOST = ''
PORT = 21567
ADDR = (HOST,PORT)

class MyRequestHandler(SRH):
    def handle(self):
        print '...connect from:',self.client_address
        self.wfile.write('[%s] %s' % (ctime(),self.rfile.readline()))

tcpServ = TCP(ADDR,MyRequestHandler)
print ' waiting for client connection'
tcpServ.serve_forever()