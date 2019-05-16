#!/usr/bin/python
from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer

PORT_NUMBER = 8080

#This class will handles any incoming request from
#the browser 
class myHandler(BaseHTTPRequestHandler):
    
    #Handler for the GET requests
    #def do_GET(self):
        #self.send_response(200)
        #self.send_header('Content-type','text/html')
        #self.end_headers()
        ## Send the html message
        #self.wfile.write("Hello World !")
        #return

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/html')
        self.end_headers()
        count = -40
        i = 1
        while i < 8000:
            print(i)
            i += 1
            if count < 40: 
                count = count+0.1
            else:
                count = -40
            self.wfile.write(count)
            self.wfile.write('<br>')
            self.wfile.
            print count
        return     
            
try:
    #Create a web server and define the handler to manage the
    #incoming request
    server = HTTPServer(('', PORT_NUMBER), myHandler)
    print 'Started httpserver on port ' , PORT_NUMBER
    #Wait forever for incoming htto requests
    server.serve_forever()

except KeyboardInterrupt:
    print '^C received, shutting down the web server'
    server.socket.close()

    
