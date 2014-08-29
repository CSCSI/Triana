Triana
======

Triana is an open source problem solving environment developed at Cardiff University that combines an 
intuitive visual interface with powerful data analysis tools. Already used by scientists for a range of 
tasks, such as signal, text and image processing, Triana includes a large library of pre-written 
analysis tools and the ability for users to easily integrate their own tools.

Triana comes with a wide variety of built-in tools. There is an extensive signal-analysis toolkit, 
an image-manipulation toolkit, a desk-top publishing toolkit, and many more. Most Triana tools will 
show you a parameter window, so you can adjust the way they work. Parameters can typically be changed 
dynamically, without interrupting the flow of data.

Triana will display your data, either in a text-editor window or in a versatile graph-display window. 
The grapher will display several curves at once and will allow you to zoom in on interesting features 
and mark them.

Triana is particularly good at automating repetitive tasks, such as performing a find-and-replace 
on all the text files in a particular directory, or continuously monitoring the spectrum of data 
that comes from an experiment that runs for days or even years. If you run an experiment, Triana 
can help you save the cost of buying extra expensive oscilloscopes or spectrum analyzers: just 
let your laptop or lab PC do the job. If you maintain a web site you will find you can automate 
many of your tedious updating tasks. If you are a teacher, you can simplify the maintenance of 
student records and the grading of papers. If you regularly create reports, Triana will allow 
you to feed updated data directly into the finished document, no matter how it is formatted.


Docker
======

There is a self contained docker container with Triana installed inside available.

Pull the image down with
```
docker pull keyz182/triana
```

then run with
```
docker run -i -t -p 6080:6080 keyz182/triana
```

Finally, you can access Triana via a browser at the following URL, using the password ```ubuntu```:


http://127.0.0.1:6080/vnc.html

