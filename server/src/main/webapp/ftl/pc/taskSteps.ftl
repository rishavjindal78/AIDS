<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="tasks">
    <style>
        /*.taskStepEdit {
            color: red;
            margin: 5px;
            cursor: pointer;
        }

        .taskStepEdit:hover {
            background: yellow;
        }*/

        #ajaxBusy {
            display: none;
            margin: 0px 0px 0px -50px; /* left margin is half width of the div, to centre it */
            padding: 30px 10px 10px 10px;
            position: absolute;
            left: 30%;
            top: 325px;
            width: 500px;
            height: 150px;
            text-align: center;
            background: #f7f7f7 url(${rc.contextPath}/images/ajax-loader.gif) no-repeat center center;
            border: 1px solid #8597d1;
            z-index: 999;
        }


    </style>

    <script type="text/javascript">

   /*     $(document).bind("ajaxSend", function(){
            alert('start');
            $(".busyindicatorClass").addClass('busyindicatorClass');
        }).bind("ajaxComplete", function(){
            alert('complete');
            $(".busyindicatorClass").removeClass('busyindicatorClass');
        });*/

        $(document).ready(function () {
            $('body').append('<div id="ajaxBusy"><p id="ajaxBusyMsg">Please wait...</p></div>');

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
                    }, 1000);
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

        function cloneTask(taskId, taskName) {
            var userComment = prompt("Please provide the new task name - ", taskName + " - clone");
//            var userOption = confirm("Are you sure to Clone this Task ?");
            if (userComment != null) {
                $("#results").empty();
                $.post("${rc.contextPath}/server/cloneTask/"+taskId, {taskName: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success">Task Cloned Successfully - ' + userComment + '</div>');
                });
            }
        }

        function runTask(url) {
            var userComment = $('#userComments').val();
            if (userComment != null) {
                $("#results").empty();
                $.post(url, { comment: userComment}, function (data) {
                    var result = "";
                    $.each(data, function(index, value){
                       result += '<div class="alert alert-success">Task Submitted Successfully - <a href="../taskRun/view/'+value.id+'" target="_blank">' + value.id + ' - Logs</a></div>';
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
                $.post("${rc.contextPath}/server/runSingleStep/${model.task.id}/"+taskStepId, { comment: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success">Task Submitted Successfully - <a href="../taskRun/view/'+data.id+'" target="_blank">Logs</a></div>');
                });
            }
        }

        function addAgent(taskStepId, description) {
            $("#myModalLabelForAgent").text("Step - " + description);
            $.get('${rc.contextPath}/server/team/${model.task.team.id}/taskStep/addAgent/' + taskStepId, function (data) {
                $('#span_task_agent1').empty();
                $('#span_task_agent1').html(data);
                $('#span_task_agent1').focus();
                $('#myModalForAgent').modal({
                    keyboard: true
                });
            });
        }

        function removeAgent(agentId, taskId) {
            var userOption = confirm("Are you sure to remove this Agent - "+agentId+" ?");
            if (userOption) {
                $.post('${rc.contextPath}/server/taskStep/removeAgent/' + taskId + '/' + agentId, function (data) {
                    $('#span_task_agent').empty();
                    $('#span_task_agent').html(data);
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

        // AJAX activity indicator bound to ajax start/stop document events
        $(document).ajaxStart(function () {
            $('#ajaxBusy').show();
        }).ajaxStop(function () {
            $('#ajaxBusy').hide();
        });
    </script>

    <br/>
    <ol class="breadcrumb">
        <li><a href="../team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li class="active"><a href="../task/${model.task.id}">${model.task.name} <span class="glyphicon glyphicon-refresh"/></a></li>
        <#--<li class="active">Data</li>-->
    </ol>

    <legend>Task Details</legend>
    <p>Task Name : ${model.task.name}</p>
    <p>Task Description : ${model.task.description}</p>

    <a href="../addTaskStep/${model.task.id?string}" class="btn btn-mini btn-primary">Add Step</a>
    <#--<a href="#" onclick="execute('../run/${model.task.id?string}')" class="btn btn-mini  btn btn-danger">Run</a>-->
    <a href="../taskHistory/${model.task.id}" class="btn btn-mini  btn btn-primary">Run History</a>
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
                              method="post" onsubmit="return confirm('Do you really want to remove this agent - ${agent.name} ?');">
                            <span class="label label-primary">${agent.name}</span>
                            <button type="submit" class="btn btn-link" id="save">X</button>
                        </form>
                    </#list>
                </td>
                <td width="12%">
                    <div class="btn-group">
                        <button type="button" class="btn btn-sm btn-primary dropdown-toggle"
                                data-toggle="dropdown" aria-expanded="false"> Action <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a class="taskStepEdit" id="${taskStep.id?string}">Edit</a></li>
                            <li><a href="#" onclick="addAgent('${taskStep.id}', '${taskStep.description!?string}')">Add Agent</a></li>
                            <li><a href="#" onclick="executeStep('${taskStep.id}')">Execute Step</a></li>
                            <li class="divider"></li>
                            <li><a class="" href="#" onclick="removeStep('${taskStep.id}','${taskStep.description!?string}')">Delete Step</a></li>
                        </ul>
                    </div>
                </td>
            </tr>
        </#list>
    </table>
    <span id="span_task_agent"></span>
    <span id="span_edit"></span>
    <br/>
    <span id="results"></span>
    <!-- Modal for task run -->
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
                            <input type="text" class="form-control" id="userComments" placeholder="TaskRun Comments" value="test run">
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

    <!-- Modal for add agent -->
    <div class="modal fade" id="myModalForAgent" tabindex="-1" role="dialog" aria-labelledby="myModalLabelForAgent" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h5 class="modal-title text-muted" id="myModalLabelForAgent">Add Agent</h5>
                </div>
                <span id="span_task_agent1">
                </span>
            </div>
        </div>
    </div>
    </@com.page>
</#escape>