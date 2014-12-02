<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="tasks">

    <script type="text/javascript">
        function executeFunction(url) {
            var userComment = prompt("Please enter comments for the run .. ?", "Test Run");
            if (userComment != null) {
                $("#results").empty();
                $.post(url, { comment: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
                });
            }
        }

        function editExpense(id) {
            $.get('/rest/server/editTask/' + id, function (data) {
                $('#span_task_edit').empty();
                $('#span_task_edit').html(data);
//                $('#span_expense_edit_'+id).empty();
//                $('#span_expense_edit_'+id).html(data);
            });
        }

        function addAgent(id) {
            $.get('/rest/server/addAgent/' + id, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }

        function removeAgent(agentId, taskId) {
            $.post('/rest/server/removeAgent/' + taskId + '/' + agentId, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }

        $(document).ready(function () {
            $(".content").hide();
//            $(".content").css("display", "none");
            $(".heading").click(function () {
                $(this).next(".content").slideToggle(200);
            });
        });
    </script>
    <#--<div class="container">-->
        <div class="heading btn-link">Add New Task</div>
        <div class="content">
            <form role="form" name="agent" action="/rest/server/addTask/0" method="POST">
                <div class="form-group">
                    <label for="exampleInputEmail1">Task Name</label>
                    <input type="text" class="form-control" id="exampleInputEmail1" name="name" placeholder="Enter Task Name">
                </div>
                <div class="form-group">
                    <label for="exampleInputPassword1">Task Description</label>
                    <input type="text" class="form-control" id="exampleInputPassword1" name="description" placeholder="Enter Task Description">
                </div>
                <div class="form-group">
                    <label for="exampleInputTags">Task Search Tags</label>
                    <input type="text" class="form-control" id="exampleInputTags" name="tags" placeholder="Enter Task Search tags">
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox"> Enable Me
                    </label>
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
                <button type="reset" class="btn btn-default">Reset</button>
            </form>
        </div>

        <h2 class="sub-header text-muted">Tasks</h2>

        <div class="table-responsive">
            <table class="table table-striped">
                <tr>
                    <th>#</th>
                    <th width="30%">Name</th>
                    <th width="30%">Description</th>
                    <th>Tags</th>
                    <th>Agents</th>
                    <th width="5%">Operation</th>
                </tr>
                <#list model["taskDatas"] as td>
                    <tr>
                        <td>${td_index+1} &nbsp;<a href="#" onclick="editExpense('${td.id}')"><span
                                class="glyphicon glyphicon-edit"/></a></td>
                        <td>${td.name?string}</td>
                        <td>${td.description?string}</td>
                        <td>${td.tags?string}</td>
                        <td>
                            <#list td.agentList as agent>
                                <form class="form-horizontal" name="agent"
                                      action="/rest/server/removeAgent/${td.id?string}/${agent.id}" method="post">
                                    <span class="label label-primary">${agent.name}</span>
                                    <button type="submit" class="btn btn-link">X</button>
                                </form>
                            </#list>
                        </td>
                        <td>
                            <div class="btn-group">
                                <button type="button" class="btn btn-info dropdown-toggle"
                                        data-toggle="dropdown"> Action <span class="caret"></span>
                                </button>
                                <ul class="dropdown-menu" role="menu">
                                    <li><a href="view/${td.id}">view</a></li>
                                    <li><a href="#" onclick="editExpense('${td.id}')">edit</a></li>
                                    <li><a href="#" onclick="addAgent('${td.id}')">add agent</a></li>
                                    <li><a href="#" onclick="executeFunction('run/${td.id}')">run</a></li>
                                    <li><a href="taskHistory/${td.id}">history</a></li>
                                    <li class="divider"></li>
                                    <li><a href="delete/${td.id}">Delete</a></li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </#list>
            </table>
            <span id="span_task_agent"></span>
            <span id="span_task_edit"></span>
        </div>
    <#--</div>-->
    </@com.page>
</#escape>