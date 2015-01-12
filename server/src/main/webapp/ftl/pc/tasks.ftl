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

        function deleteTask(taskId) {
            var option = confirm("Are you sure to delete the Task ?");
            if (option == true) {
                var url = '${rc.getContextPath()}/server/delete/'+taskId;
                $("#results").empty();
                $.post(url, {}, function (data) {
                    $("table#tasksTable tr#table_row_"+taskId).remove();
                    $("#results").html('<div class="alert alert-success">Task Deleted Successfully - ' + url + '</div>');
                });
            }
        }

        function editTask(id) {
            $('.task_row').removeClass('alert-warning');
            $('#table_row_'+id).addClass('alert-warning');
            $.get('${rc.contextPath}/server/team/${model.team.id}/editTask/' + id, function (data) {
                $('#span_task_edit').empty();
                $('#span_task_edit').html(data);
            });
        }

        function addAgent(id) {
            $.get('${rc.getContextPath()}/server/team/${model.team.id}/addAgent/' + id, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }

        function removeAgent(agentId, taskId) {
            $.post('${rc.contextPath}/server/removeAgent/' + taskId + '/' + agentId, function (data) {
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
    <div class="content alert alert-warning">
        <form role="form" name="agent" action="${rc.contextPath}/server/team/${model.team.id}/addTask/0" method="POST">
            <div class="form-group">
                <label for="exampleInputEmail1">Task Name</label>
                <input type="text" class="form-control" id="exampleInputEmail1" name="name"
                       placeholder="Enter Task Name">
            </div>
            <div class="form-group">
                <label for="exampleInputPassword1">Task Description</label>
                <input type="text" class="form-control" id="exampleInputPassword1" name="description"
                       placeholder="Enter Task Description">
            </div>
            <div class="form-group">
                <label for="exampleInputTags">Task Search Tags</label>
                <input type="text" class="form-control" id="exampleInputTags" name="tags"
                       placeholder="Enter Task Search tags">
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

    <div class="heading btn-link">Import Task</div>
    <div class="content">
        <fieldset>
            <form class="form-horizontal" name="upload"
                  action="${rc.contextPath}/server/team/${Session['SELECTED_TEAM'].id}/taskUpload"
                  method="post"
                  enctype="multipart/form-data">

                <div class="form-group">
                    <div class="col-sm-6">
                        <input id="documentFileId" type="file" name="file" placeholder="FilePath" class="form-sm"/>
                    </div>
                    <input type="submit" class="btn btn-primary" value="Save"/>
                    <button class="btn cancel btn-default" id="cancel">Cancel</button>
                </div>
            </form>
        </fieldset>
    </div>

    <h2 class="sub-header text-muted">Tasks</h2>

    <div class="table-responsive">
        <table id="tasksTable" class="table table-striped">
            <tr>
                <th width="5%">#</th>
                <th width="25%">Name</th>
                <th width="30%">Description</th>
                <th width="20%">Tags</th>
                <th width="15%">Agents</th>
                <th width="5%">Operation</th>
            </tr>
            <#list model["tasks"] as td>
                <tr id="table_row_${td.id}" class="task_row">
                    <td>${td_index+1} &nbsp;<a href="#" onclick="editTask('${td.id}')"><span
                            class="glyphicon glyphicon-edit"/></a></td>
                    <td><a href="${rc.getContextPath()}/server/task/${td.id}">${td.name?string}</a></td>
                    <td>${td.description?string}</td>
                    <td>${td.tags?string}</td>
                    <td>
                        <#list td.agentList as agent>
                            <form class="form-horizontal" name="agent"
                                  action="${rc.contextPath}/server/removeAgent/${td.id?string}/${agent.id}"
                                  method="post">
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
                                <li><a href="${rc.getContextPath()}/server/task/${td.id}">view</a></li>
                                <li><a href="#" onclick="editTask('${td.id}')">edit</a></li>
                                <li><a href="#" onclick="addAgent('${td.id}')">add agent</a></li>
                                <li><a href="#" onclick="executeFunction('${rc.getContextPath()}/server/run/${td.id}')">run</a>
                                </li>
                                <li><a href="${rc.getContextPath()}/server/taskHistory/${td.id}">history</a></li>
                                <li><a href="${rc.getContextPath()}/server/export/task/${td.id}">Download</a></li>
                                <li class="divider"></li>
                                <li><a href="#" onclick="deleteTask('${td.id}')">Delete</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </#list>
        </table>
        <span id="span_task_agent"></span>
        <span id="span_task_edit"></span>
        <span id="results"></span>
    </div>
    <#--</div>-->
    </@com.page>
</#escape>