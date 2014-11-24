<#macro greet person color>
<font size="+2" color="${color}">Hello ${person}!</font>
</#macro>

<#macro monthlySummary summary total title="" hide=false >
    <#escape x as x?html>
    <div class="heading alert alert-info"><strong><span class="badge">${summary!?size}</span> ${title} [ Total Expense = ${total} ]</strong>
    </div>
    <div class="content" style="display: none;">
        <table class="table table-striped table-condensed">
            <tr>
                <th>#</th>
                <th>Amount</th>
                <th>Transact Type</th>
                <th>Description</th>
                <th>Account</th>
            </tr>
            <#list summary as expense>
                <tr>
                    <td>${expense_index+1}</td>
                    <td>${expense.amount?string.currency}</td>
                    <td>${expense.transactionType!?string}</td>
                    <td>${expense.description!?string}</td>
                    <td>${expense.account.name!?string}</td>
                </tr>
            </#list>
        </table>
    </div>
    </#escape>
</#macro>

<#macro documents items title="" hide=false size=0>
    <#escape x as x?html>
    <!--script type="text/javascript">
        $(document).ready(function () {
            $(".heading").click(function () {
                $(this).next(".content").slideToggle(200);
            });
        });
    </script-->
    <div class="heading alert alert-info">
        <strong><span class="badge"><#if size == 0>${items?size} <#assign size = items?size><#else>${size}</#if></span> ${title}</strong>
    </div>
    <div class="content" <#if items?size == 0 || hide ==true>style="display: none;" </#if>>
        <table class="table table-striped table-condensed">
            <thead>
            <tr>
                <th>#</th>
                <th width="50%">Title</th>
                <th>Tags</th>
                <th>Project</th>
                <th>Last Updated</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
                <#list items as document>
                <tr>
                    <td>${model.currentPage*10 + document_index + 1}</td>
                    <td>
                        <a class="title btn-link">${document.title!?string}</a>

                        <div class="description">
                            <#if document.description??>
                                <div class="markdown">${document.description?string?chop_linebreak}</div>
                                <#--<div class="markdown"><#noescape>${document.description?string?chop_linebreak}</#noescape></div>-->
                            </#if>
                            <#if document.attachments??>
                                <strong>Attachments</strong>
                                <#list document.attachments as attachment>
                                <li><a href="/rest/attachment/download/${attachment.id}">${attachment.title}</a></li>
                            </#list>
                            </#if>
                            <br/>
                            <a class="hiddenLink" href="#"><strong>Attach Files</strong></a>
                            <span class="hid">
                                <fieldset>
                                    <form method="post" class="form-horizontal" action="/rest/documents/saveFiles/${document.id}"
                                          name="uploadForm"
                                          enctype="multipart/form-data" modelAttribute="uploadForm">
                                        <button type="button" class="btn btn-link addFile">Add More Files</button>
                                        <ol class="fileTable" name="files">
                                            <li><input name="files[0]" type="file"/></li>
                                        </ol>
                                        <input type="submit" class="btn btn-xs btn-primary" value="Upload"/>
                                    </form>
                                </fieldset>
                            </span>
                        </div>

                    </td>
                    <td> <#if document.tags??>${document.tags}</#if></td>
                    <td> <#if document.project??>${document.project.id}</#if></td>
                    <td> <#if document.dateUpdated??>${document.dateUpdated!?date?string("dd MMM, yyyy")}</#if> </td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-primary dropdown-toggle btn-xs" data-toggle="dropdown">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="add/${document.id}" onclick="editExpense('${document.id}')">Edit</a></li>
                                <li><a href="#" onclick="markComplete('markComplete/${document.id}')">Settle</a></li>
                                <li class="divider"></li>
                                <li><a href="#" onclick="markDelete('markDelete/${document.id}')">Delete</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
        <#if (size >10)>
            <ul class="pagination">
                <li><a href="?page=${model['prevPage']}">&laquo;</a></li>
                <#list 1..model['totalPages'] as i>
                    <#if model['currentPage'] = (i-1)>
                        <li class="active"><a href="?page=${i-1}">${i}</a></li>
                    <#else>
                        <li><a href="?page=${i-1}">${i}</a></li>
                    </#if>
                </#list>
                <li><a href="?page=${model['nextPage']}">»</a></li>
            </ul>
        </#if>
    </div>
    <span id="results"></span>
    </#escape>
</#macro>

<#macro tasks items title="" hide=false size=0>
    <#escape x as x?html>
    <!--script type="text/javascript">
        $(document).ready(function () {
            $(".heading").click(function () {
                $(this).next(".content").slideToggle(200);
            });
        });
    </script-->
    <div class="heading alert alert-info">
        <strong><span class="badge"><#if size == 0>${items?size} <#assign size = items?size><#else>${size}</#if></span> ${title}</strong>
    </div>
    <div class="content" <#if items?size == 0 || hide ==true>style="display: none;" </#if>>
        <table class="table table-striped table-condensed1">
            <thead>
            <tr>
                <th>#</th>
                <th>Title</th>
                <th>Schedule</th>
                <th>Project</th>
                <th>Creation Date</th>
                <th>Active</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
                <#list items as task>
                <tr>
                    <td>${taskData_index + 1}</td>
                    <td>
                        <a class="title btn-link">${task.name!?string}</a>

                        <div class="description">
                            <#if task.description??>
                                <div class="markdown"><#noescape>${task.description?string?chop_linebreak}</#noescape></div>
                            </#if>
                        </div>
                    </td>
                    <td> <#if task.schedule??>${task.schedule}</#if></td>
                    <td> <#if task.project??>${task.project.id}</#if></td>
                    <td>
                        <#if task.dateUpdated??>${task.dateUpdated!?date?string("dd MMM, yyyy")}</#if>
                    </td>
                    <td>
                        <#if  (task.active?c == 'true')><span class="glyphicon glyphicon-ok"></span><#else><span class="glyphicon glyphicon-off"></#if>
                    </td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-primary dropdown-toggle btn-xs" data-toggle="dropdown">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="view/${task.id}">view</a></li>
                                <li><a href="add/${task.id}">edit</a></li>
                                <li><a href="#" onclick="myFunction('run/${task.id}')">run</a></li>
                                <li><a href="taskHistory/${task.id}">history</a></li>
                                <li class="divider"></li>
                                <li><a href="delete/${task.id}">Delete</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
        <#if (size >10)>
            <ul class="pagination">
                <li><a href="?page=${model['prevPage']}">&laquo;</a></li>
                <#list 1..model['totalPages'] as i>
                    <#if model['currentPage'] = (i-1)>
                        <li class="active"><a href="?page=${i-1}">${i}</a></li>
                    <#else>
                        <li><a href="?page=${i-1}">${i}</a></li>
                    </#if>
                </#list>
                <li><a href="?page=${model['nextPage']}">»</a></li>
            </ul>
        </#if>
    </div>
    <span id="results"></span>
    </#escape>
</#macro>