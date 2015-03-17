<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="tasks">
    <style>
        .taskStepEdit {
            color: red;
            margin: 5px;
            cursor: pointer;
        }

        .taskStepEdit:hover {
            background: yellow;
        }
    </style>

    <script type="text/javascript">
        $(document).ready(function () {
            $(".taskStepEdit").click(function () {
                $('.taskstep_row').removeClass('alert-warning');
                $('#taskstep_row_' + this.id).addClass('alert-warning');
                $.get('../edit/' + this.id, function (data) {
                    $('#span_edit').empty();
                    $('#span_edit').html(data);
                    $('#span_edit').focus();
//                    var target = "#" + this.getAttribute('data-target');
                    $('html, body').animate({
                        scrollTop: $("#span_edit").offset().top
                    }, 2000);
                });
            });

            $(".cancel").click(function () {
                alert('button clicked external');
                $(this).slideUp();
            });

            $(".activeCheckBox").change(function () {
                $.ajax({
                    url: '../updateTaskStep',
                    type: 'POST',
                    data: { id: $(this).attr("id"), active: $(this).is(":checked") },
                    success: function(data) {
//                        location.reload();
                    }
                });
            });

            $(".ignoreFailureCheckBox").change(function () {
                $.ajax({
                    url: '../updateTaskStep2',
                    type: 'POST',
                    data: { id: $(this).attr("id"), ignoreFailure: $(this).is(":checked") },
                    success: function (data) {
//                        location.reload();
                    }
                });
            });

            $('#select_all').change(function() {
                alert("select all checked");
                var checkboxes = $(this).closest('form').find(':checkbox');
                if($(this).is(':checked')) {
                    checkboxes.prop('checked', true);
                } else {
                    checkboxes.prop('checked', false);
                }
            });
        });

        function cloneTask(url) {
            var userOption = confirm("Are you sure to Clone this Task ?");
            if (userOption) {
                $("#results").empty();
                $.post(url, {}, function (data) {
                    $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
                });
            }
        }

        function runTask(url) {
            var userComment = $('#userComments').val();
            if (userComment != null) {
                $("#results").empty();
                $.post(url, { comment: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
                });
                $('#myModal').modal('hide');
            }
        }

        function addAgent(taskStepId) {
            $.get('${rc.contextPath}/server/team/${model.task.team.id}/taskStep/addAgent/' + taskStepId, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
                $('#span_task_agent').focus();
                $('html, body').animate({
                    scrollTop: $("#span_task_agent").offset().top
                }, 2000);
            });
        }

        function removeAgent(agentId, taskId) {
            $.post('${rc.contextPath}/server/taskStep/removeAgent/' + taskId + '/' + agentId, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }
    </script>

    <br/>
    <ol class="breadcrumb">
        <li><a href="../team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li class="active"><a href="../task/${model.task.id}">${model.task.name}</a></li>
        <#--<li class="active">Data</li>-->
    </ol>

    <legend>Task Details</legend>
    <p>Task Name : ${model.task.name}</p>
    <p>Task Description : ${model.task.description}</p>

    <a href="../addTaskStep/${model.task.id?string}" class="btn btn-mini btn-primary">Add Step</a>
    <#--<a href="#" onclick="execute('../run/${model.task.id?string}')" class="btn btn-mini  btn btn-danger">Run</a>-->
    <a href="../taskHistory/${model.task.id}" class="btn btn-mini  btn btn-primary">history</a>
    <!-- Button trigger modal -->
    <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#myModal">Execute</button>
    <a href="#" onclick="cloneTask('../cloneTask/${model.task.id?string}')" class="btn btn-mini  btn btn-danger">Clone</a>
    <table class="table table-striped">
        <tr>
            <th>#</th>
            <th>Task Step</th>
            <th>Description</th>
            <th>Active</th>
            <th>Optional</th>
            <th>Agents</th>
            <th>Operation</th>
        </tr>
        <#list model.task.stepDataList as taskStep>
            <tr id="taskstep_row_${taskStep.id}" class="taskstep_row">
                <td width="5%">${taskStep.sequence?string}</td>
                <td width="12%">${taskStep.taskClass?string}</td>
                <td width="30%">${taskStep.description!?string}</td>
                <td width="5%"><input class="activeCheckBox" type="checkbox" id="${taskStep.id}"
                                      <#if taskStep.active?? && taskStep.active?string=="true">checked="true"</#if>></td>
                <td width="5%"><input class="ignoreFailureCheckBox" type="checkbox" id="${taskStep.id}"
                                      <#if taskStep.ignoreFailure?? && taskStep.ignoreFailure?string=="true">checked="true"</#if>>
                </td>
                <td width="13%">
                    <#list taskStep.agentList as agent>
                        <form class="form-horizontal" name="agent"
                              action="${rc.contextPath}/server/taskStep/removeAgent/${taskStep.id?string}/${agent.id}"
                              method="post">
                            <span class="label label-primary">${agent.name}</span>
                            <button type="submit" class="btn btn-link" id="save">X</button>
                        </form>
                    </#list>
                </td>
                <td width="12%"><a class="taskStepEdit" id="${taskStep.id?string}">edit</a>
                    <a href="../deleteStep/${taskStep.id}">delete</a>
                    <a href="#" onclick="addAgent('${taskStep.id}')">+agent</a>
                </td>
            </tr>
        </#list>
    </table>
    <span id="span_task_agent"></span>
    <span id="span_edit"></span>
    <br/>
    <span id="results"></span>
    <!-- Modal -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">Run Task</h4>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="recipient-name" class="control-label">User Comments</label>
                            <input type="text" class="form-control" id="userComments">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="runTask('../run/${model.task.id?string}')">Run Task</button>
                </div>
            </div>
        </div>
    </div>
    </@com.page>
</#escape>