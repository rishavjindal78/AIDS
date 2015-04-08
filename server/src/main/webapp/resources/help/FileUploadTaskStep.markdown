##FileUpload Task Step
This Task Step unzips multiple zip file (recursively optionally) into the specified location. 
This Task Step utilizes Java 8's parallel stream and tries to extract multiple zip files in parallel (depending on the number of cores available on agent machine)

###Input Parameters

**1. Target Agent Address** - Placeholder to add comma separated list of target agents where we want to upload the files.
    convention here is to use #{<agent name>} for a particular agent, so if we want to upload file to agent1 and agent2 then
    
    #{agent1}, #{agent2}
Otherwise we can use comma separated fully qualified agent address ```http://hostname1:9290/agent/, http://hostname2:9290/agent/```
   
**2. Local File Path** - The absolute file path for the source file that we want to transfer to agents.
    
    E:\aids_home\modules\aids-agent-8.7.0.0-SNAPSHOT.zip

OR parameterized file path

    #{download.dir}\aids-agent-8.7.0.0-SNAPSHOT.zip   

**3. Target File Name** - Final File name at the target location

    aids-agent-8.7.0.0-SNAPSHOT.zip  
OR parametrized form
    #{aids_module}
    
**4. Target Folder** - Target Directory on agent where wa want to place the source file.
   
    E:\AIDS_HOME\conf\formats
   
###Sample Task Step Configuration Screenshot
Here is the screenshot for sample FileUploadStep Configuration -

![alt text](../help/images/fileUploadStep-1.PNG "File Upload Step Definition")    