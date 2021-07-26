# RandomAccessFileMockJava
Mock (Fake) implementation of java.io.RandomAccessFile for test in RAM without Disk access

Fake implementation of the RandomAccessFile class for tests

Unfortunately, the RandomAccessFile class does not have a corresponding interface, making it difficult to test code that uses RandomAccessFile without accessing a disk.

As a remedy, the interfaces

* DataOutput
* DataInput
* Closeable

that implement the RandomAccessFile class should be used in the code if possible.

Where this is not possible, the classes and interfaces from this project can be used.

In the code to be tested, the Interface

* RandomAccessFileInterface

can be used.


The prerequisite for this is the use of the class

* RandomAccessFileRealImplementation

The prerequisite for this is the ability to call the constructors of class

* RandomAccessFileRealImplementation

instead of the constructors of class

* RandomAccessFile


If the constructors of class

* RandomAccessFile

are called in code that cannot be changed (unchangeable libraries, frameworks, application servers), the classes and interfaces from this project cannot be used.

------------------------------------------------

Fake-Implementierung der Klasse RandomAccessFile für Tests

Leider besitzt die Klasse RandomAccessFile kein entsprechendes Interface, wodurch Tests von Code, der RandomAccessFile verwendet, ohne Zugriff auf eine Disk schwierig sind.

Als Abhilfe sollten möglichst die Interfaces

* DataOutput
* DataInput
* Closeable

welche die Klasse RandomAccessFile implementiert, im Code verwendet werden.

Wo dies nicht möglich ist, können die Klassen und Interfaces aus diesem Projekt verwendet werden.

Im zu testenden Code kann das Interface

* RandomAccessFileInterface

verwendet werden.


Voraussetzung dafür ist die Verwendung der Klasse

* RandomAccessFileRealImplementation


Voraussetzung dafür ist die Möglichkeit, statt der Konstruktoren der Klasse

* RandomAccessFile

die Konstruktoren der Klasse

* RandomAccessFileRealImplementation

aufzurufen.


Falls die Konstruktoren der Klasse

* RandomAccessFile

in Code aufgerufen werden, der nicht verändert werden kann (nicht veränderbare Bibliotheken, Frameworks, Applikationsserver), können die Klassen und Interfaces aus diesem Projekt nicht verwendet werden.
