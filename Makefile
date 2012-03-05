JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        src/receiver/Receiver.java \
        src/receiver/Packet.java \
        src/receiver/MyLogger.java \
        src/sender/Sender.java \
        src/sender/Packet.java \
        src/sender/MyTimer.java \
        src/sender/MyLogger.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class
