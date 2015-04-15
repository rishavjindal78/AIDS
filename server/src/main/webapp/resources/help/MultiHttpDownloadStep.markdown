##Multi Http Download Step
This Step downloads multiple files from http(s) urls into the specified location using multiple threads (optional). 
This Step utilizes Java 8's parallel stream and tries to utilize all available processor cores to download multiple urls in parallel (depending on the number of cores available on agent machine)

###Input Parameters

**1. inputOutputTuples** - represents the line separated pairs of zip file = output dir that we want to extract.

    http://www.google.com/first=c:/targetFolder/first.txt
    http://www.google.com/second=c:/targetFolder/second.dat    

Or using parametrized input, as shown below

    #{host.name}/first.zip=#{dist.dir}/first
    #{host.name}/second.zip=#{dist.dir}/second
    
Where ```dist.dir=c:\dist``` defined at Task/Agent/TaskRun level.

![alt text](../help/images/multiUnzipTask-1.PNG "MultiUnzipTask Step Definition")  
   
**2. Download Parallel ?** - Whether we should download file urls in parallel ? Default behavior is to download in parallel.

    true - download files in parallel
    false - sequential file download


###Sample Task Step Configuration Screenshot
Here is the screenshot for sample MultiHttpDownload Configuration -

![alt text](../help/images/multiHttpDownloadStep.PNG "MultiUnzipTask Step Definition")    