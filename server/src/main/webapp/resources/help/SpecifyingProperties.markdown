
##What are Step Properties ? 
Properties are variables that we can define at various levels and then use them inside task steps. 
For example, if i want to parametrize a taskstep to use build version dynamically, then we can specify the below input for the Task
  
    tdm-dist-#{build.version.number}.zip

Here build.version.number is a property defined in AIDS at one of the level - TaskRun, Task, Agent, User, Team.

Here is the screenshot for Task Level Properties -
![alt text](../help/images/taskProperties.PNG "Task Properties")

**Properties Configuration at different Levels (Increasing Order of their priority)**

1. Team Level
2. User Level
3. Task Level
4. Agent Level
5. Task Run Level

A single property defined at multiple levels will be picked up according to its priority. TaskRun has maximum proprity and Team Level has least.


Basically AIDS execution engine looks for a given property at TaskRun level first, if not found then lookup is made at Agent Level and so on upto team Level, if it is found at any level then 
property values are returned otherwise a runtime exception will be thrown. **(which exception ??)**

> Please not that properties support does not work for FileUploadStep

**Example Commands**

1. To find the task running on port 8080 type the below command    
    ```tasklist | find /i "8080"```

2. To Start a windows Service, use the below __command__    
    ```net start "<service name>"```
    
3. To Stop a windows Service, use the below __command__    
    ```net stop "<service name>"```
    
4. To run a asynchronous long running task (agent will not wait for the termination of program)    
    ```start c:/server.bat```
   
   Also set ```wait for termination``` field to ```false```


    private String name = "Munish";
    net start "<service name>"
    
    net start "<service name>"
    
<table>
    <tr>
        <td>Foo</td>
        <td>Bar</td>
    </tr>
</table>

> we can also do it like this

> we can also do it like this

Some of these words *are emphasized*.
Some of these words _are emphasized also_.

Use two asterisks for **strong emphasis**.
Or, if you prefer, __use two underscores instead__.

+   Candy.
+   Gum.
+   Booze.

*   A list item.

    With multiple paragraphs.

*   Another item in the list.

This is an [example link](http://example.com/)


[1]: http://google.com/        "Google"
[2]: http://search.yahoo.com/  "Yahoo Search"
[3]: http://search.msn.com/    "MSN Search"com/ "With a Title"

![alt text](../images/ajax-loader.gif "Title")

I strongly recommend against using any `<blink>` tags.

I wish SmartyPants used named entities like `&mdash;`
instead of decimal-encoded entites like `&#8212;`

---

[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/21hSUASaQkc/0.jpg)](https://www.youtube.com/watch?v=21hSUASaQkc)

Colons can be used to align columns.

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

The outer pipes (|) are optional, and you don't need to make the raw Markdown line up prettily. You can also use inline Markdown.

Markdown | Less | Pretty
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3

First Header  | Second Header
------------- | -------------
Content Cell  | Content Cell
Content Cell  | Content Cell

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |


| Left-Aligned  | Center Aligned  | Right Aligned |
| :------------ |:---------------:| -----:|
| col 3 is      | some wordy text | $1600 |
| col 2 is      | centered        |   $12 |
| zebra stripes | are neat        |    $1 |

<a href="http://www.youtube.com/watch?feature=player_embedded&v=21hSUASaQkc
" target="_blank"><img src="http://img.youtube.com/vi/21hSUASaQkc/0.jpg" 
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>

~Mistaken text.~

I get 10 times more traffic from [Google] [1] than from
[Yahoo] [2] or [MSN] [3].

  [1]: http://google.com/        "Google"
  [2]: http://search.yahoo.com/  "Yahoo Search"
  [3]: http://search.msn.com/    "MSN Search"
  
    TransactionAttribute(TestDataService testDataService) {
          this.testDataService = testDataService
      }
  
      @Override
      String getAttributeCode() {
          Attribute.Transaction.code
      }
      
```js 
console.log("hello");
```

```java
System.out.println("hello"); 
```