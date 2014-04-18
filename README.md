Functional implementations of mobile robotics algorythms

Particle filter
---------------

Extensible particle filter with abstract predictor and sensor are pluggable as a separate modules.


Run
---

Requirements:

* [OpenCV](http://docs.opencv.org/doc/tutorials/introduction/desktop_java/java_dev_intro.html)
* [Imshow for Java](https://github.com/master-atul/ImShow-Java-OpenCV)

Both are not inluded here

Running with sbt

*  put openCV and imshow jars in the `lib` dir (usual sbt location for external jars) 
* `sbt -Djava.library.path=<PATH TO OPENCV LIB>`
* `sbt> test` or run or whatever  


