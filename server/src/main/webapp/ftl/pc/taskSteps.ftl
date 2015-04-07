<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="tasks">
    <style> </style>

    <script type="text/javascript">
        useAjaxBusyWait();
        $(document).ready(function () {
            $(".cancel").click(function () {
                alert('button clicked external');
                $(this).slideUp();
            });

            $(".activeCheckBox").change(function () {
                $.ajax({
                    url: '../updateTaskStep',
                    type: 'POST',
                    data: {id: $(this).attr("id"), active: $(this).is(":checked")},
                    success: function (data) {
//                        location.reload();
                    }
                });
            });

            $(".ignoreFailureCheckBox").change(function () {
                $.ajax({
                    url: '../updateTaskStep2',
                    type: 'POST',
                    data: {id: $(this).attr("id"), ignoreFailure: $(this).is(":checked")},
                    success: function (data) {
//                        location.reload();
                    }
                });
            });

            $('#select_all').change(function () {
                alert("select all checked");
                var checkboxes = $(this).closest('form').find(':checkbox');
                if ($(this).is(':checked')) {
                    checkboxes.prop('checked', true);
                } else {
                    checkboxes.prop('checked', false);
                }
            });

            $("#taskClassSelect").change(function () {
                var selectedClass = $('#taskClassSelect').val();
                if (selectedClass != null) {
                    $("#myModalLabel").text("TaskStep - " + selectedClass);
                    $.get('${rc.contextPath}/server/addTaskStep/${model.task.id}?taskClass=' + selectedClass, function (data) {
                        $('#span_task_step').empty();
                        $('#span_task_step').html(data);
                    });
                }
            });
        });

        function cloneTask(taskId, taskName) {
            var userComment = prompt("Please provide the new task name - ", taskName + " - clone");
            if (userComment != null) {
                $("#results").empty();
                $.post("${rc.contextPath}/server/cloneTask/" + taskId, {taskName: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success small">Task Cloned Successfully - ' + userComment + '</div>');
                });
            }
        }

        function runTask(url) {
            var userComment = $('#userComments').val();
            var customProperties = $('#customPropertiesTextArea').val();
            var taskRunLoggingLevel = $('#taskRunLoggingLevel').val();
            if (userComment != null) {
                $("#results").empty();
                $.post(url, {comment: userComment, properties: customProperties, loggingLevel:taskRunLoggingLevel}, function (data) {
                    var result = "";
                    $.each(data, function (index, value) {
                        result += '<div class="alert alert-success small">Task Submitted Successfully - <a href="../taskRun/view/' + value.id + '" target="_blank">' + value.id + ' - Logs</a></div>';
                    });
                    $("#results").html(result);
                });
                $('#myModal').modal('hide');
            }
        }

        function executeStep(taskStepId) {
            var userComment = prompt("Please enter comments for the step run .. ?", "Test Step Run");
            if (userComment != null) {
                $("#results").empty();
                $.post("${rc.contextPath}/server/runSingleStep/${model.task.id}/" + taskStepId, {comment: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success small">Task Submitted Successfully - <a href="../taskRun/view/' + data.id + '" target="_blank">Logs</a></div>');
                });
            }
        }

        function addTaskStep(){
            var selectedClass = $('#taskClassSelect').val();
            if (selectedClass != null) {
                $("#myModalLabel").text("TaskStep - " + selectedClass);
                $.get('${rc.contextPath}/server/addTaskStep/${model.task.id}?taskClass=' + selectedClass, function (data) {
                    $('#span_task_step').empty();
                    $('#span_task_step').html(data);
                    $('#span_task_step').focus();
                    $('#myModalToListTaskClasses').modal({
                        keyboard: true
                    });
                });
            }
        }

        function editTaskStep(taskStepId, description) {
            $("#myModalLabelForEditTaskStep").text("Edit TaskStep - " + description);
            $('.taskstep_row').removeClass('alert-warning');
            $('#taskstep_row' + taskStepId).addClass('alert-warning');
            $.get('../edit/' + taskStepId, function (data) {
                $('#span_edit_task_step').empty();
                $('#span_edit_task_step').html(data);
                $('#span_edit_task_step').focus();
                $('#myModalForEditTaskStep').modal({
                    keyboard: true
                });
            });
        }

        function addAgent(taskStepId, description) {
            $("#myModalLabelForAgent").text("Step - " + description);
            $.get('${rc.contextPath}/server/team/${model.task.team.id}/taskStep/addAgent/' + taskStepId, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
                $('#span_task_agent').focus();
                $('#myModalForAgent').modal({
                    keyboard: true
                });
            });
        }

        function removeAgent(agentId, taskId) {
            var userOption = confirm("Are you sure to remove this Agent - " + agentId + " ?");
            if (userOption) {
                $.post('${rc.contextPath}/server/taskStep/removeAgent/' + taskId + '/' + agentId, function (data) {
                    $('#results').empty();
                    $('#results').html(data);
                });
            }
        }

        function removeStep(taskStepId, description) {
            var userOption = confirm("Are you sure to remove this Step - " + description + " ?");
            if (userOption) {
                $.post('${rc.contextPath}/server/deleteStep/' + taskStepId, function (data) {
                    location.reload();
                });
            }
        }
    </script>

    <br/>
    <ol class="breadcrumb">
        <li><a href="../team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li class="active"><a href="../task/${model.task.id}">${model.task.name} <span class="glyphicon glyphicon-refresh"/></a></li>
        <#--<li class="active">Data</li>-->
    </ol>

    <ul class="nav nav-tabs">
        <li role="presentation" class="active"><a href="../task/${model.task.id}">Task Details</a></li>
        <li role="presentation"><a href="../taskHistory/${model.task.id}">Activity</a></li>
        <li role="presentation"><a href="#">Configuration</a></li>
    </ul>

    <#--<legend>Task Details</legend>-->
    <p><strong>Task Name </strong>: ${model.task.name!}</p>
    <p><strong>Task Description </strong>: ${model.task.description!}</p>
    <p><strong>Task Schedule </strong>: ${model.task.schedule!}</p>
    <p><strong>Created By </strong>: ${(model.task.author.name)!}</p>
    <p><#if model.task.dateUpdated??><strong>Updated On</strong>: ${model.task.dateUpdated?datetime?string("dd MMM, yyyy hh.mm aa")}</#if></p>

    <button type="button" class="btn btn-mini btn-primary" onclick="addTaskStep()">Add Task Step</button>
    <#--<a href="#" onclick="execute('../run/${model.task.id?string}')" class="btn btn-mini  btn btn-danger">Run</a>-->
    <#--<a href="../taskHistory/${model.task.id}" class="btn btn-mini  btn btn-primary">Run History</a>-->
    <!-- Button trigger modal -->
    <a href="#" onclick="cloneTask('${model.task.id?string}', '${model.task.name!?string}')" class="btn btn-mini  btn btn-warning">Clone Task</a>
    <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#myModal">Execute</button>
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
                <td width="5%">${taskStep.sequence?string}&nbsp;<a href="javascript:void(0);" onclick="editTaskStep('${taskStep.id}', '${taskStep.description!?string}')"><span
                        class="glyphicon glyphicon-edit"/></a></td>
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
                              method="post" onsubmit="return confirm('Do you really want to remove this agent - ${agent.name} ?');">
                            <span class="label label-primary">${agent.name}</span>
                            <button type="submit" class="btn btn-link" id="save">X</button>
                        </form>
                    </#list>
                </td>
                <td width="12%">
                    <div class="btn-group">
                        <button type="button" class="btn btn-sm btn-default dropdown-toggle"
                                data-toggle="dropdown" aria-expanded="false"> Action <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="javascript:void(0);" onclick="editTaskStep('${taskStep.id}', '${taskStep.description!?string}')">Edit Step</a></li>
                            <li><a href="javascript:void(0);" onclick="addAgent('${taskStep.id}', '${taskStep.description!?string}')">Add Agent</a></li>
                            <li><a href="javascript:void(0);" onclick="executeStep('${taskStep.id}')">Execute Step</a></li>
                            <li class="divider"></li>
                            <li><a class="" href="#" onclick="removeStep('${taskStep.id}','${taskStep.description!?string}')">Delete Step</a></li>
                        </ul>
                    </div>
                </td>
            </tr>
        </#list>
    </table>
    <span id="span_edit"></span>
    <br/>
    <span id="results"></span>
    <!-- Modal for task run -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">Run Task - ${model.task.name!?string}</h4>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="recipient-name" class="control-label">Comments</label>
                            <input type="text" class="form-control" id="userComments" placeholder="TaskRun Comments" value="test run">
                        </div>

                        <div class="form-group">
                            <label for="customPropertiesTextArea">Custom Properties</label>
                                <textarea id="customPropertiesTextArea" type="text" class="form-control input-sm" placeholder="Agent properties to override" name="properties" rows="3"></textarea>
                        </div>
                        <div class="form-group">
                            <label>Logging Level</label>
                            <input type="range" size="2" id ="taskRunLoggingLevel" name="loggingLevel" min="1" max="4" value="3">
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

    <!-- Modal for selecting new Task Step Class -->
    <div class="modal fade" id="myModalToListTaskClasses" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="alert alert-warning">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="myModalLabel">Add Task Step</h4>

                        <div class="form-group">
                            <select id="taskClassSelect" class="form-control" name="taskClass">
                                <#list model["taskClasses"] as taskClass>
                                    <option value="${taskClass}">${taskClass}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                <span id="span_task_step">
                </span>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal for add agent -->
    <div class="modal fade" id="myModalForAgent" tabindex="-1" role="dialog" aria-labelledby="myModalLabelForAgent" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h5 class="modal-title text-muted" id="myModalLabelForAgent">Add Agent</h5>
                </div>
                <span id="span_task_agent">
                </span>
            </div>
        </div>
    </div>

    <!-- Modal for edit TaskStep -->
    <div class="modal fade" id="myModalForEditTaskStep" tabindex="-1" role="dialog" aria-labelledby="myModalLabelForEditTaskStep" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="alert alert-warning">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title text-muted" id="myModalLabelForEditTaskStep">Edit Task Step</h4>
                    </div>
                    <span id="span_edit_task_step" />
                </div>
            </div>
        </div>
    </div>
    </@com.page>
</#escape>