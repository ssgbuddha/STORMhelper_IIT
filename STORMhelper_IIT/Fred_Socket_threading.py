import socket
import time
from autofocus_class_cent import autofocus

af = autofocus()
HOST = "localhost"
PORT = 9999
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Socket created')
try:
    s.bind((HOST, PORT))
except socket.error as err:
    print('Bind failed. Error Code : ' .format(err))
s.listen(10)
conn, addr = s.accept()
print("Socket Listening", addr)

def getZposition(i):
    if (i < 41):
        i = i+1
    else:
        i = -40
    time.sleep(0.1)    
    return i
        
def socketListener():
    st = -40
    while(True):
        data = conn.recv(1024)
        #print("call received in Python")
        msg = data.decode(encoding='UTF-8')
        #print(msg)
        if ( msg == "call"+"\r\n"):
            st = af.main()
            #st = getZposition(st)
            #stt = 'num4er lkmdsglkdfslgnlkjfdsngsgn'+"\r\n"
            stt = str(st)+"\r\n"
            byt = stt.encode()
            conn.send(byt)
            #print("Message sent form Python: " + str(st))
            print(str(st))
        else:
            st = getZposition(st)
            #stt = 'num4er lkmdsglkdfslgnlkjfdsngsgn'+"\r\n"
            stt = str(st)+"\r\n"
            byt = stt.encode()
            conn.send(byt)
            print("Message sent and not identified: " + str(st))
            
socketListener()

if(False):
    while(True):
        #conn.send(byt)
        data = conn.recv(1024)
        print("call received in Python")
        msg = data.decode(encoding='UTF-8')
        print(msg)
        if ( msg == "call"+"\r\n"):
            st1 = 'MEassage'
            st2 = '\r\n'
            st = st1+st2
            byt = st.encode()
            conn.send(byt)
            print("Message sent form Python")
        else:
            print("Go away")
