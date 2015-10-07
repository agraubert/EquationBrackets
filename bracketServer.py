import socket
import os
import time
import threading
import bracket
import weakref
#from switches import switch, case
import random
from datetime import date
lock = (date.today()>date(2014,4,8))
Shutdown=False
clients={}
sessions={}
submitted=set()
version=1.02
pointSystem=[16,8,4,2,1]
master=None
key=None
source=None
current=None
ticking=False
passwordKey="PeNiS"
dataLock=threading.Lock()

def decrypt(msg, key):
    chrs=[]
    for i in range(len(msg)):
        enc_c=ord(msg[i])
        key_c=ord(key[i % len(key)])
        num = enc_c - key_c
        if(num<0):
            num+=256
        msg_c=chr(num)
        chrs.append(msg_c)
    return "".join(chrs)

def encrypt(msg, key):
    chrs=[]
    for i in range(len(msg)):
        msg_c=ord(msg[i])
        key_c=ord(key[i % len(key)])
        enc_c=chr((msg_c + key_c) % 256)
        chrs.append(enc_c)
    return "".join(chrs)

def newEncrypt(msg, key):
    chrs=[]
    for i in range(len(msg)):
        msg_c=ord(msg[i])
        key_c=ord(key[i%len(key)])
        chrs.append(str(msg_c+key_c))
    return " ".join(chrs)

def passwordTrans(msg, key):
    return newEncrypt(decrypt(msg, key), key)

def newDecrypt(msg, key):
    intake=msg.rsplit()
    if(not intake[1].isnumeric()):
        intake=passwordTrans(msg, key).rsplit()
    chrs=[]
    for i in range(len(intake)):
        enc_c=int(intake[i])
        key_c=ord(key[i%len(key)])
        msg_c=chr(enc_c - key_c)
        chrs.append(msg_c)
    return "".join(chrs)


def updateClientList(user):
    reader=open("clients\\@clientlist.brak")
    intake=reader.readlines()
    reader.close()
    writer=open("clients\\@clientlist.brak", mode='w')
    intake.append("\n"+user)
    for line in intake:
        writer.write(line)
    writer.close()
class equation:
    def __init__(self, name, equation):
        self.name=name
        self.eq=equation
class newInterface:
    def __init__(self, preset=None):
        if(preset==None):
            self.sock=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        else:
            self.sock=preset
##        self.signaler=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    def sockListen(self, port=4242):
        self.sock.bind(('', port))
        self.sock.listen(5)
    def sockAccept(self):
        return newInterface(preset=self.sock.accept()[0])
    def sockConnect(self, address, port=4242):
        self.sock.connect((address, port))
    def sockSend(self, msg):        
        msg+="\\\r\n"
        totalsent=0
        while(totalsent<len(msg)):
            sent = self.sock.send(msg[totalsent:].encode())
            if(sent==0):
                raise RuntimeError("Socket Connection Broken!")
            totalsent+=sent
    def sockRec(self):
        msg=""
        self.sock.settimeout(30)
        while(len(msg.rsplit('\\'))==1):
            intake=self.sock.recv(4096)
##            print(intake.decode())
            if(intake==b""):
                raise RuntimeError("Socket Connection Broken!")
            msg+=intake.decode()
        self.sock.setblocking(True)
        return msg.rsplit('\\')[0]
    def sockDisconnect(self):
        self.sock.shutdown(socket.SHUT_RDWR)
        self.sock.close()
        self.sock=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    def sockIP(self):
        return self.sock.getpeername()[0]
    
class Logger:
    def __init__(self):
        self.out=None
        self.started=False
        self.writeLock=threading.Lock()
        self.stamp=0
    def getReference(self):
        return weakref.ref(self)
##        return self
##    def __call__(self):
##        return self
    def start(self):
        if(self.started):
            return
        print("--LOG STARTED--")
        self.stamp=time.time()
        self.out=open("serverLog.txt", mode='w')
        self.out.write("Bracket Server Log:\n")
        self.out.write(time.strftime("%A %B %d <%H:%M>")+"\n")
        self.out.flush()
        self.started=True
    def write(self, string):
        self.writeLock.acquire()
        if(not self.started):
            self.start()
        if((time.time()-self.stamp)>=300):
            self.out.write("---<TIMESTAMP>---\n")
            self.out.write(time.strftime("%A %B %d <%H:%M>")+"\n")
            self.stamp=time.time()
        self.out.write(string+"\n")
        self.out.flush()
        self.writeLock.release()
        print(string)
    def timestamp(self):
        self.writeLock.acquire()
        if(not self.started):
            self.start()
            return
        self.out.write("---<TIMESTAMP>---\n")
        self.out.write(time.strftime("%A %B %d <%H:%M>")+"\n")
        self.stamp=time.time()
        self.out.flush()
        self.writeLock.release()
    def close(self):
        self.writeLock.acquire()
        self.out.close()
        self.started=False
        print("--LOG CLOSED--")
        self.writeLock.release()
        
class Client:
    def __init__(self, name, password, email=""):
        if(name.rsplit("::")[0]=="RELOAD" and password=="1948535"):
            reader=open("clients\\"+name.rsplit("::")[1]+".brak")
            intake=reader.readlines()
            reader.close()
            self.username=intake[0].rstrip()
            self.password=intake[1].rstrip()
            self.lastKnownScore=int(intake[2])
            self.email=intake[3].rstrip()
        else:
            self.username=name
            self.password=password
            self.lastKnownScore=0
            self.email=email
            if(name!="@admin"):
                self.save()
    def save(self):
        writer = open("clients\\"+self.username+".brak", mode='w')
        writer.write(self.username+"\n")
        writer.write(self.password+"\n")
        writer.write(str(self.lastKnownScore)+"\n")
        writer.write(self.email+"\n")
        writer.close()
    def getCorrect(self):
        global submitted
        global current
        if(self.username in submitted):
            reader=open("brackets\\"+self.username+".brak")
            brak=bracket.bracketBuilder(reader.read())
            reader.close()
            return brak.numCorrect(current)
        return 0
class echo(threading.Thread):
    def __init__(self, interface, logref):
        threading.Thread.__init__(self)
        self.sock=interface
        self.stop=False
        self.logref=logref
    def run(self):
        self.tag="["+str(self.ident)+"]:"
        msg=[""]
        global Shutdown
        global sessions
        global ticking
        self.logref().write(self.tag+"thread active\n")
        while((not self.stop) and msg[0]!="9Z9"):
            try:
                msg=self.sock.sockRec().rsplit("::")
            except socket.timeout:
                self.logref().write(self.tag+"client timed out")
                self.stop=True
                self.sock.sockDisconnect()
                continue
            except ConnectionResetError:
                self.logref().write(self.tag+"Client disconnected")
                self.stop=True
                continue
            self.logref().write(self.tag+"recieved command")
            if(msg[0]=="9Z9"):
                continue
            elif(msg[0]=="LIST"):
                self.listBrackets(int(msg[1]))
            elif(msg[0]=="OUT"):
                self.signout(msg[1])
            elif(msg[0]=="SIGNIN"):
                self.signin(msg[1], msg[2])
            elif(msg[0]=="VIEW"):
                self.viewBracket(msg[1])
            elif(msg[0]=="BRACKET"):
                self.createBracket(int(msg[1]))
            elif(msg[0]=="LOCKED"):
                self.checkLock()
            elif(msg[0]=="KEY"):
                self.key()
            elif(msg[0]=="SRC"):
                self.source()
            elif(msg[0]=="LEAD"):
                self.leaderboard()
            elif(msg[0]=="REG"):
                self.register()
            elif(msg[0]=="MARCO"):
                self.logref().write(self.tag+"POLO!")
                self.sock.sockSend("POLO")
            elif(msg[0]=="SHUTDOWN" and sessions[int(msg[1])].username=="@admin"):
                Shutdown=True
                self.stop=True
                self.logref().write(self.tag+"Preparing to shut down")
            elif(msg[0]=="TICK" and sessions[int(msg[1])].username=="@admin"):
                self.tick()
##            elif(msg[0]=="UPDATE" and sessions[int(msg[1])].username=="@admin"):
##                self.updateServer()
##            elif(msg[0]=="PULSE"):
##                if(ticking):
##                    self.sock.sock
            else:
               self.logref().write(self.tag+"Unknown command: "+str(msg))
        self.logref().write(self.tag+"Disconnected")
    def signin(self, username, password):
        global clients
        global sessions
        global passwordKey
        global dataLock
        self.logref().write(self.tag+"Signing in...")
##        self.sock.sockSend("RDY")
##        authkey=self.sock.sockRec().rsplit("::")
##        username=authkey.pop(0).lower()
##        password=authkey.pop(0)
        if((username in clients) and (clients[username].password==
                                      newEncrypt(password, passwordKey))):
            num = random.randint(0,10000)
            self.logref().write(self.tag+"Welcome, "+username)
            while(num in sessions):
                num = random.randint(0, 10000)
            dataLock.acquire()
            sessions[num]=clients[username]
            dataLock.release()
            self.logref().write(self.tag+"Assigned session "+str(num))
        else:
            num= -1
            username="BAD"
        self.sock.sockSend(str(num)+"::"+username)
    def register(self):
        global clients
        global passwordKey
        global dataLock
        self.logref().write(self.tag+"Registering...")
        self.sock.sockSend("RDY")
        username=self.sock.sockRec()
        self.logref().write(self.tag+"recieved username "+username)
        if(username in clients):
            self.sock.sockSend("BAD")
            self.logref().write(self.tag+"Username was taken")
            return
        self.sock.sockSend("GOOD")
        password=newEncrypt(self.sock.sockRec(), passwordKey)
        email=self.sock.sockRec()
        dataLock.acquire()
        clients[username]=Client(username, password, email)
        updateClientList(username)
        self.logref().write(self.tag+"Client "+username+" was registered")
        dataLock.release()
    def key(self):
        global key
        self.logref().write(self.tag+"sending key")
##        print(key)
        self.sock.sockSend(key)
    def source(self):
        global source
        self.logref().write(self.tag+"Sending source")
        self.sock.sockSend(source)
    def createBracket(self, session):
        global sessions
        global submitted
        global lock
        global current
        global dataLock
        global pointSystem
        self.logref().write(self.tag+"Creating new bracket")
        if((session not in sessions) or lock):
            self.logref().write(self.tag+"DUN")
            self.stop=True
            self.sock.sockDisconnect()
            return
        user=sessions[session]
        self.logref().write(self.tag+"REC")
        self.sock.sockSend("GO")
        intake = self.sock.sockRec()
        self.logref().write(self.tag+"Aquiring Lock")
        dataLock.acquire()
        writer = open("brackets\\"+user.username+".brak", mode='w')
        writer.write(intake)
        writer.close()
        submitted= submitted|set([user.username])
        writer=open("clients\\@submitted.brak",mode='w')
        for thing in list(submitted):
            writer.write(thing.rstrip()+'\n')
        writer.close()
        self.logref().write(self.tag+"Bracket made")
        reader=open("brackets\\"+user.username+".brak")
        temp = bracket.bracketBuilder(reader.read())
        reader.close()
        writer = open("brackets\\"+user.username+".brak", mode='w')
        writer.write(temp.dump())
        writer.close()
        sessions[session].lastKnownScore=temp.score(current, pointSystem)
        dataLock.release()
        self.logref().write(self.tag+"Bracket reformatted")
        self.sock.sockSend("GO")
    def listBrackets(self, session):
        global submitted
        global lock
        global sessions
        self.logref().write(self.tag+"sending user list")
        if(session not in sessions):
            self.logref().write(self.tag+"Bad session")
##            print(session, type(session))
##            print(sessions)
            self.stop=True
            self.sock.sockDisconnect()
            return
        if((not lock) and sessions[session].username!="@admin"):
            if(sessions[session].username in submitted):
                self.sock.sockSend(sessions[session].username+"\n")
            else:
                self.sock.sockSend("@NO AVALIABLE USERS\n")
            return
        names=""
        for user in list(submitted):
            names+=user+"\n"
        names+="@MasterBracket\n"
        self.sock.sockSend(names)
        
    def checkLock(self):
        global lock
        global version
        self.logref().write(self.tag+"Checking lock status")
        self.sock.sockSend("LOCKED" if(lock) else("OPEN"))
        self.sock.sockSend(str(version))

    def leaderboard(self):
        global clients
        global lock
        global submitted
        self.logref().write(self.tag+"sending leaderboard")
        if(not lock):
            self.sock.sockDisconnect()
            return
        unsorted=[clients[name] for name in list(submitted)]
        unsorted.sort(key= lambda x:(x.lastKnownScore)+(x.getCorrect()/100), reverse=True)
##        unsorted.reverse()
        unsorted=unsorted[0:10]
        out="\n".join([user.username+"::"+str(user.lastKnownScore) for user in unsorted])
        self.logref().write(self.tag+"Leaders:")
        self.logref().write(self.tag+str(out))
        self.sock.sockSend(out)
    def viewBracket(self, user):
        global submitted
        global current
        global dataLock
        global pointSystem
        global clients
        self.logref().write(self.tag+"viewing bracket")
        if(user.rstrip()=="@MasterBracket"):
            self.logref().write(self.tag+"Sending current master bracket")
            self.sock.sockSend("0")
            self.sock.sockSend("0")
            self.sock.sockSend(current.correctness(current))
            return
        elif(user not in submitted):
            self.logref().write(self.tag+"BAD")
            self.stop=True
            self.sock.sockDisconnect()
            return
        dataLock.acquire()
        reader = open("brackets\\"+user+".brak")
        intake=reader.read()
        reader.close()
        dataLock.release()
        outBracket= bracket.bracketBuilder(intake)
        self.logref().write(self.tag+"Updating score of requested bracket")
        outScore=outBracket.score(current, pointSystem)
        self.sock.sockSend(str(outScore))
        self.sock.sockSend(str(outBracket.ppr(current, pointSystem)))
        output=outBracket.correctness(current)
        dataLock.acquire()
        clients[user].lastKnownScore=outScore
        clients[user].save()
        dataLock.release()
        self.sock.sockSend(output)
    def signout(self, session):
        global sessions
        global dataLock
        self.logref().write(self.tag+"signing out")
        if(session not in sessions):
            self.stop=True
            self.sock.sockDisconnect()
            return
        dataLock.acquire()
        sessions= sessions-set([session])
        dataLock.release()
##        self.sock.sockSend("GOOD")
    def tick(self):
        global master
        global current
        global dataLock
        global submitted
        global ticking
        global clients
        global pointSystem
        global lock
        self.logref().write(self.tag+"Tick active.  Acquiring lock!")
        dataLock.acquire()
        ticking=True
        self.logref().write(self.tag+"Updating @current.brak")
        current.setNext(master)
        writer = open("brackets\\@current.brak", mode='w')
        writer.write(current.dump())
        writer.close()
        self.logref().write(self.tag+"@current.brak updated.  Rescoring users")
        for user in list(submitted):
            reader = open("brackets\\"+user+".brak")
            brak = bracket.bracketBuilder(reader.read())
            reader.close()
            clients[user].lastKnownScore=brak.score(current, pointSystem)
            clients[user].save()
            self.logref().write(self.tag+"finished scoring "+str(user))
##        self.logref().write(self.tag+"Updating time lock")
##        lock = (date.today()>=date(2014,4,8))
##        self.logref().write(self.tag+"Time Lock status: "+ str(lock))
        dataLock.release()
        ticking=False
        self.sock.sockSend("DONE")
        self.logref().write(self.tag+"Tick complete.  lock released")
        
    
        
        
            
        
        
        
        
serverLog = Logger()
reader=open("clients\\@clientlist.brak")
clientNames=reader.readlines()
reader.close()
for name in clientNames:
    clients[name.rstrip()]=Client("RELOAD::"+name.rstrip(), "1948535")
reader=open("clients\\@submitted.brak")
submittedNames=reader.readlines()
reader.close()
submitted=set([name.rstrip() for name in submittedNames])
reader=open("brackets\\@master.brak")
masterSource=reader.read()
reader.close()
master=bracket.bracketBuilder(masterSource)
reader=open("brackets\\@current.brak")
currentSource=reader.read()
reader.close()
current=bracket.bracketBuilder(currentSource)
reader=open("brackets\\@key.brak")
key="".join(reader.readlines())
reader.close()
reader=open("brackets\\@source.brak")
source="".join(reader.readlines())
reader.close()

clients["@admin"]=Client("@admin", newEncrypt("BRACKETS_ADMIN!", passwordKey), "none@none.com")

    
primary=newInterface()
primary.sockListen(4242)
socks=[]
sockSize=0
serverLog.write("Startup complete.  Server Active")
while(not Shutdown):
    socks.append(echo(primary.sockAccept(), serverLog.getReference()))
    socks[-1].start()
    sockSize+=1
    serverLog.write("New client connected")
    if(sockSize>=10):
        serverLog.write("Checking for dead threads")
        removedSocks=0
        for i in range(len(socks)):
            if(socks[i]==None):
                continue
            if(not socks[i].is_alive()):
                socks[i].join()
                socks[i]=None
                removedSocks+=1
        sockSize-=removedSocks
        serverLog.write("\nRemoved "+str(removedSocks)+" dead threads")
        
                
serverLog.write("Joining threads")
for thing in socks:
    if(thing!=None):
        thing.join()
primary.sock.close()
serverLog.write("FINISHED")
serverLog.timestamp()
serverLog.close()
