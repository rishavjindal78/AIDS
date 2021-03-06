<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="tasks">

    <style>

    </style>

    <script type="text/javascript">
        useAjaxBusyWait();

        function runTask(url) {
            var userComment = prompt("Please enter comments for the run .. ?", "Test Run");
            if (userComment != null) {
                $("#results").empty();
                $.post(url, { comment: userComment}, function (data) {
                    var result = "";
                    $.each(data, function(index, value){
                        result += '<div class="alert alert-success small">Task Submitted Successfully - <a href="../../taskRun/view/'+value.id+'" target="_blank">' + value.id + ' - Logs</a></div>';
                    });
                    $("#results").html(result);
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
                    $("#results").html('<div class="alert alert-success small">Task Deleted Successfully - ' + url + '</div>');
                });
            }
        }

        function editTask(id) {
            $('.task_row').removeClass('alert-warning');
            $('#table_row_'+id).addClass('alert-warning');
            $.get('${rc.contextPath}/server/team/${model.team.id}/editTask/' + id, function (data) {
                $('#span_task_edit').empty();
                $('#span_task_edit').html(data);
                scrollToElement('#span_task_edit', 1200);
                /*$('html, body').animate({
                    scrollTop: $("#span_edit").offset().top
                }, 1000);*/
            });
        }

        function addAgent(id, description) {
            $("#myModalLabelForAgent").text("Task - " + description);
            $.get('${rc.getContextPath()}/server/team/${model.team.id}/addAgent/' + id, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
                $('#span_task_agent').focus();
                $('#myModalForAgent').modal({
                    keyboard: true
                });
            });
        }

        function removeAgent(agentId, taskId) {
            $.post('${rc.contextPath}/server/removeAgent/' + taskId + '/' + agentId, function (data) {
                $('#results').empty();
                $('#results').html(data);
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
    <div class="heading alert alert-info" style="padding: 6px;margin-bottom: 2px;margin-top: 2px;">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span><strong> Add New Task</strong>
    </div>
    <div class="content alert alert-warning">
        <form role="form" name="agent" action="${rc.contextPath}/server/team/${model.team.id}/addTask/0" method="POST">
            <div class="form-group" style="width: 80%;">
                <label for="exampleInputEmail1">Task Name</label>
                <input type="text" class="form-control" id="exampleInputEmail1" name="name"
                       placeholder="Enter Task Name">
            </div>
            <div class="form-group" style="width: 80%;">
                <label for="exampleInputPassword1">Task Description</label>
                <input type="text" class="form-control" id="exampleInputPassword1" name="description"
                       placeholder="Enter Task Description">
            </div>
            <div class="form-group" style="width: 50%;">
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

    <div class="heading alert alert-info" style="padding: 6px;margin-bottom: 2px; margin-top: 2px;">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span><strong> Import Task</strong>
    </div>
    <div class="content">
        <fieldset>
            <form  name="upload"
                  action="${rc.contextPath}/server/team/${Session['SELECTED_TEAM'].id}/taskUpload"
                  method="post"
                  enctype="multipart/form-data">

                <div class="form-group">
                    <label for="documentFileId">Tasks Json Input</label>
                    <input type="file" id="documentFileId" name="file">
                    <p class="help-block">Upload tasks json file.</p>
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
            </form>
        </fieldset>
    </div>

    <h3 class="sub-header text-muted">Team Tasks</h3>
    <div class="table">
        <table id="tasksTable" class="table table-striped">
            <tr>
                <th width="5%"># <a href="../../export/tasks"><span class="glyphicon glyphicon-download-alt"/></a></th>
                <th width="25%">Name</th>
                <th width="30%">Description</th>
                <th width="15%">Schedule</th>
                <th width="15%">Agents</th>
                <th width="10%">Operation</th>
            </tr>
            <#list model["tasks"] as td>
                <tr id="table_row_${td.id}" class="task_row">
                    <td>${td_index+1} &nbsp;<a href="#" onclick="editTask('${td.id}')"><span
                            class="glyphicon glyphicon-edit"/></a></td>
                    <td><a href="${rc.getContextPath()}/server/task/${td.id}">${td.name?string}</a></td>
                    <td>${td.description?string}</td>
                    <td>${td.schedule!''?string}</td>
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
                            <button type="button" class="btn btn-sm btn-default dropdown-toggle"
                                    data-toggle="dropdown"> Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="${rc.getContextPath()}/server/task/${td.id}">view</a></li>
                                <li><a href="#" onclick="editTask('${td.id}')">edit</a></li>
                                <li><a href="#" onclick="addAgent('${td.id}','${td.name!}')">add agent</a></li>
                                <li><a href="#" onclick="runTask('${rc.getContextPath()}/server/run/${td.id}')">run</a>
                                </li>
                                <li><a href="${rc.getContextPath()}/server/taskHistory/${td.id}">history</a></li>
                                <li><a href="${rc.getContextPath()}/server/export/task/${td.id}">download</a></li>
                                <li><a href="${rc.getContextPath()}/server/view/task/${td.id}">jsonView</a></li>
                                <li class="divider"></li>
                                <li><a href="#" onclick="deleteTask('${td.id}')">Delete</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </#list>
        </table>
        <span id="span_task_edit"></span>
        <span id="results"></span>
    </div>
    <!-- Modal for add agent -->
    <div class="modal fade" id="myModalForAgent" tabindex="-1" role="dialog" aria-labelledby="myModalLabelForAgent" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title text-muted" id="myModalLabelForAgent">Add Agent</h4>
                </div>
                <span id="span_task_agent">
                </span>
            </div>
        </div>
    </div>
    </@com.page>
</#escape>