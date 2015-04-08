##Multi Unzip Task Step
This Task Step unzips multiple zip file (recursively optionally) into the specified location. 
This Task Step utilizes Java 8's parallel stream and tries to extract multiple zip files in parallel (depending on the number of cores available on agent machine)

###Input Parameters

**1. inputOutputTuples** - represents the line separated pairs of zip file = output dir that we want to extract.

    c:/project-dist1.zip=c:/targetFolder
    c:/project-dist2.zip=c:/targetFolder

Or using parametrized input, as shown below

    #{dist.dir}/first.zip=#{dist.dir}
    #{dist.dir}/second.zip=#{dist.dir}
    
Where ```dist.dir=c:\dist``` defined at Task/Agent/TaskRun level.

![alt text](../help/images/multiUnzipTask-1.PNG "MultiUnzipTask Step Definition")  
   
**2. unzip recursively** - Whether we should recursively extract the zipped contents or leave nested zips as it is ?

    true - unzip all nested zip files
    false - leave nested zip files as it is.


###Sample Task Step Configuration Screenshot
Here is the screenshot for sample MultiUnziTaskStep Configuration -

![alt text](../help/images/multiUnzipTask.PNG "MultiUnzipTask Step Definition")    