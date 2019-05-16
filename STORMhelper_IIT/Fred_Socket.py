import socket

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

    #conn.send("Message")
    #print("Message sent")
    #conn.send(bytes("Message"+"\r\n",'UTF-8'))
    #data = conn.recv(1024)
    #msg = data.decode(encoding='UTF-8')
    #print(msg)
    #if ( msg == "Hello" ):
    #    print("Hey everyone")
    #else:
    #    print("Go away")
