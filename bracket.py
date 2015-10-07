import random
import traceback
from copy import copy
random.seed()
class bracket:
    def __init__(self, left, right, victor):
        self.left=left
        self.right=right
        self.victor=victor
        self.mapped=False
        self.mappedTo=None
        self.num=0
        self.status=0
        self.generation=0
    def mapTo(self, target, used=set(), gen=0):
        number=0
        self.generation=gen
        target.generation=gen
        while(number in used):
            number=random.randint(1,10000)
        used=used|set([number])
        self.num=number
        target.num=number
        self.mapped=True
        self.mappedTo=target
        target.mapped=True
        target.mappedTo=self
        if(type(self.left)==bracket):
            used=used|self.left.mapTo(target.left, used, gen+1)
        if(type(self.right)==bracket):
            used=used|self.right.mapTo(target.right, used, gen+1)
        return used
    def nodes(self, indecies=set()):
        if(not self.mapped):
            return
        indecies=indecies|set([self.num])
        if(type(self.left)==bracket):
            indecies=indecies|self.left.nodes(indecies)
        if(type(self.right)==bracket):
            indecies=indecies|self.right.nodes(indecies)
        return indecies
    def dump(self):
        clone=copy(self)
        indecies=list(self.mapTo(clone))
        for item in indecies:
            target=clone.fetch(item)
            target.leftnum=target.left if(type(target.left)==str) else target.left.num
            target.rightnum=target.right if(type(target.right)==str) else target.right.num
            target.vicnum=target.victor if(type(target.victor)==str) else target.victor.num
        output=""
        for item in indecies:
            target=clone.fetch(item)
            output+=str(target.leftnum)+"::"+str(target.rightnum)+"::"+str(target.vicnum)+"::"+str(item)+"\n"
        self.unmap()
        return output.rstrip()
        
        
    def unmap(self):
        if(not self.mapped):
            return
        self.mapped=False
        if(type(self.left)==bracket):
            self.left.unmap()
        if(type(self.right)==bracket):
            self.right.unmap()
        self.mappedTo.mapped=False
        self.mappedTo.mappedTo=None
        self.mappedTo=None
    def correctness(self, key):
        indecies=list(self.mapTo(key))
        for item in indecies:
            target=self.fetch(item)
##            target.status=1 if(target.findVictor()==key.fetch(item).findVictor()) else (0 if(key.fetch(item).findVictor()=="FUTURE") else -1)
            target.status = 0 if(key.fetch(item).findVictor()=="FUTURE") else (1 if(target.findVictor()==key.fetch(item).findVictor()) else -1)
        for item in indecies:
            target=self.fetch(item)
            if(target.status==0):
                target.status=target.recursiveUpdate()
        self.unmap()
        clone=copy(self)
        indecies=list(self.mapTo(clone))
        for item in indecies:
            target=clone.fetch(item)
            target.leftnum=target.left if(type(target.left)==str) else target.left.num
            target.rightnum=target.right if(type(target.right)==str) else target.right.num
            target.vicnum=target.victor if(type(target.victor)==str) else target.victor.num
        output=""
        for item in indecies:
            target=clone.fetch(item)
            output+=str(target.leftnum)+"::"+str(target.rightnum)+"::"+str(target.vicnum)+"::"+str(target.status)+"::"+str(item)+"\n"
        self.unmap()
##        print(output)
        return output.rstrip()
    def recursiveUpdate(self):
        if(type(self.victor)==str):
            return 0
        elif(self.victor.status==0):
           return self.victor.recursiveUpdate()
        elif(self.victor.status==-1):
            return -1
        else:
            return 0

    def ppr(self, key, points):
        indecies=list(self.mapTo(key))
        for item in indecies:
            target=self.fetch(item)
##            target.status=1 if(target.findVictor()==key.fetch(item).findVictor()) else (0 if(key.fetch(item).findVictor()=="FUTURE") else -1)
            target.status = 0 if(key.fetch(item).findVictor()=="FUTURE") else (1 if(target.findVictor()==key.fetch(item).findVictor()) else -1)
        for item in indecies:
            target=self.fetch(item)
            if(target.status==0):
                target.status=target.recursiveUpdate()
        pointsRemaining=0
        for item in indecies:
            target=self.fetch(item)
            if(target.status==0):
                pointsRemaining+=points[target.generation]
        return pointsRemaining
    def numCorrect(self, key):
        indecies=list(self.mapTo(key))
        for item in indecies:
            target=self.fetch(item)
##            target.status=1 if(target.findVictor()==key.fetch(item).findVictor()) else (0 if(key.fetch(item).findVictor()=="FUTURE") else -1)
            target.status = 0 if(key.fetch(item).findVictor()=="FUTURE") else (1 if(target.findVictor()==key.fetch(item).findVictor()) else -1)
##        for item in indecies:
##            target=self.fetch(item)
##            if(target.status==0):
##                target.status=target.recursiveUpdate()
        total=0
        for item in indecies:
            target=self.fetch(item)
            if(target.status==1):
                total+=1
        return total

    def score(self, key, points):
        indecies=list(self.mapTo(key))
        total=0
        for item in indecies:
            if(self.fetch(item).findVictor()==key.fetch(item).findVictor()):
                total+=points[self.fetch(item).generation]
        self.unmap()
        return total
    def fetch(self, target):
        if(not self.mapped):
            return None
        if(self.num==target):
            return self
        if(type(self.left)==bracket):
            resLeft=self.left.fetch(target)
        else:
            resLeft=None
        if(type(self.right)==bracket):
            resRight=self.right.fetch(target)
        else:
            resRight=None
        if(resLeft!=None):
            return resLeft
        if(resRight!=None):
            return resRight
        return None
    def __str__(self):
        return self.makeString()
    def findVictor(self):
        if(type(self.victor)==bracket):
            return self.victor.findVictor()
        return self.victor
    def makeString(self, tablevel=0):
        output=""
        tabs=""
        for i in range(tablevel):
            tabs+="\t"
        if(type(self.right)==bracket):
           output+=self.right.makeString(tablevel+1)
        else:
           output+=tabs+"\t"+self.right
        output+="\n"+tabs+"("
        output+=""+self.findVictor()+")"
        output+="\n"
        if(type(self.left)==bracket):
            output+=self.left.makeString(tablevel+1)
        else:
            output+=tabs+"\t"+self.left
        return output
    def seekNext(self, indecies):
        gens=[]
        highest=0
        for item in indecies:
            gen=self.fetch(item).generation
            if((gen>highest) and self.fetch(item).victor=="FUTURE"):
                highest=gen
        for item in indecies:
            if(self.fetch(item).generation == highest):
                gens.append(item)
        return gens
            
    def setNext(self, target):
        indecies = list(self.mapTo(target))
        nums = self.seekNext(indecies)
        for num in nums:
            if(target.fetch(num).victor == target.fetch(num).left):
                self.fetch(num).victor = self.fetch(num).left
            else:
                self.fetch(num).victor = self.fetch(num).right
        self.unmap()
def assignBracket(nodes, target):
    item=nodes[target]
    victor=0
    if(item.victor.isnumeric()):
        if(item.victor==item.left):
            victor=-1
        elif(item.victor==item.right):
            victor=1
    if(item.left.isnumeric()):
        item.left=assignBracket(nodes, item.left)
    if(item.right.isnumeric()):
        item.right=assignBracket(nodes, item.right)
    if(victor==-1):
        item.victor=item.left
    elif(victor==1):
        item.victor=item.right
    return item
def bracketBuilder(source):
    proto=source.rstrip().rsplit("\n")
    nodes={}
    for line in proto:
##        print(line)
        if(line==""):
            print("Skipping blank line")
            continue
        data=line.rsplit("::")
        try:
            nodes[data[3]]=bracket(data[0], data[1], data[2])
        except IndexError as e:
            print(data)
            traceback.print_exc()
            raise SyntaxError("STOP")
            
    return assignBracket(nodes, "0")
