<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="history">
    <style>
        body {
            font-size: 12px;
            /*background-color: #f5f5f5;*/
        }
    </style>

    <script type="text/javascript">
        var cacheId;

        function deleteTaskStepRun(stepRunId) {
            var option = confirm("Are you sure to delete the Step Run ?");
            if (option == true) {
                var url = '${rc.contextPath}/server/deleteStepRun/' + stepRunId;
                $("#results").empty();
                $.post(url, {}, function (data) {
                    location.reload();
//                    $("table#tasksTable tr#table_row_"+stepRunIdId).remove();
//                    $("#results").html('<div class="alert alert-success">Row Deleted Successfully - ' + url + '</div>');
                });
            }
        }

        var lpStart = function () {
            var jqxhr = $.get("${rc.contextPath}/server/taskRunMonitor", { taskRunId: ${model.taskRun.id}, cacheId: cacheId},
                    function (data) {
                        if (data != undefined) {
                            cacheId = data.cacheId;
                            var progress = data.progress;
                            $('#taskRunStatusId').attr('class', ''+getRowClassForButton(data.runStatus)+'');
                            $('#taskRunStatusId').html('<strong>'+data.runStatus+'</strong>');
                            $('#progressBarId').attr( 'aria-valuenow',''+progress+'');
                            $('#progressBarId').attr( 'style','width :'+progress+'%');
                            $('#progressBarId').text(progress+'%');
                            var content = '<tr><th>#</th>';
                            content += '<th>Machine</th>';
                            content += '<th width="40%">Task Step Details</th>';
                            content += '<th>Start Time</th>';
                            content += '<th>Duration</th>';
                            content += '<th>Status</th>';
                            content += '<th width="15%">Operation</th></tr>';
                            $.each(data.taskStepRuns, function (index, taskStepRun) {
                                var taskStepRunContent = '<td>'+taskStepRun.sequence+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.agent.name+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.taskStep.taskClass+' - '+taskStepRun.taskStep.description+'</td>';
                                if(taskStepRun.startTime == undefined)
                                    taskStepRun.startTime='NA';
                                taskStepRunContent += '<td>'+taskStepRun.startTime+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.duration+'</td>';
                                taskStepRunContent += '<td><button type="button" class="' + getRowClassForButton(taskStepRun.runStatus) + '">' + taskStepRun.runStatus + '</button></td>';
                                taskStepRunContent += '<td><a class="btn btn-sm btn-info" href="../../getMemoryLogs/view/'+taskStepRun.id+'" target="_blank">tail logs</a> ';
                                if(taskStepRun.runState == 'COMPLETED'){
                                    taskStepRunContent += '<a class="btn btn-sm btn-warning" href="#" onclick="deleteTaskStepRun('+taskStepRun.id+')">delete</a>';
                                }
                                taskStepRunContent += '</td>';
                                content += '<tr class="'+getRowClass(taskStepRun.runStatus)+'">'+taskStepRunContent+'</tr>';
                            });
                            $("#taskRunTable").empty().append(content);
                        } else {
                            $("#taskRunTable").empty();
                        }
                        if(data.runState != 'COMPLETED'){
                            lpStart();
                        }
                    }, "json");
            jqxhr.error(function () {
            });
        };
//        $(document).ready(lpStart);

        var getRowClass = function (runStatus) {
            var classname;
            switch (runStatus) {
                case 'FAILURE':
                    classname = 'text-danger';
                    break;
                case 'NOT_RUN':
                    classname = 'text-warning';
                    break;
                default:
                    classname = 'text-success';
                    break;
            }
            return classname;
        };

        var getRowClassForButton = function (runStatus) {
            var classname;
            switch (runStatus) {
                case 'FAILURE':
                    classname = 'btn btn-danger btn-sm';
                    break;
                case 'NOT_RUN':
                case 'CANCELLED':
                    classname = 'btn btn-warning btn-sm';
                    break;
                case 'RUNNING':
                    classname = 'btn btn-info btn-sm';
                    break;
                default:
                    classname = 'btn btn-success btn-sm';
                    break;
            }
            return classname;
        };

        function cancelTaskRun(taskRun) {
            var option = confirm("Are you sure to Cancel the Step Run ?");
            if (option == true) {
                var url = '${rc.contextPath}/server/cancel/' + taskRun;
                $("#results").empty();
                $.post(url, {}, function (data) {
                    $("#results").html('<div class="alert alert-success">Request submitted to cancel the TaskRun - ' + taskRun+ '</div>');
                });
            }
        }

        $(document).ready(function () {
            $('#taskRunStatusId').attr('class', ''+getRowClassForButton('${model.taskRun.runStatus!?string}')+'');
            lpStart();
        });
    </script>
    <br>
    <ol class="breadcrumb">
        <li><a href="${rc.getContextPath()}/server/team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li><a href="${rc.getContextPath()}/server/team/${model.taskRun.team.id}/taskHistory">Task History</a></li>
        <li class="active"><a href="${rc.getContextPath()}/server/taskRun/view/${model.taskRun.id}">${model.taskRun.name!} <span class="glyphicon glyphicon-refresh"/></a></li>
    </ol>
    <h4 class="text-muted"><a href="${rc.getContextPath()}/server/task/${model.taskRun.task.id}">${model.taskRun.id}. ${model.taskRun.name!}</a>
        <button id="taskRunStatusId" type="button" class="btn btn-info btn-sm">${model.taskRun.runStatus!?string}</button>
        <a class="btn btn-sm btn-warning" href="#" onclick="cancelTaskRun('${model.taskRun.id}')">Cancel</a>
    </h4>
    <div class="progress">
        <div id="progressBarId" class="progress-bar" role="progressbar" aria-valuenow="${model.taskRun.progress!}" aria-valuemin="0" aria-valuemax="100" style="width: ${model.taskRun.progress!}%;">
        ${model.taskRun.progress!}%
        </div>
    </div>
        <table class="table table-striped table-condensed" id="resultDisplayTable">
            <tbody id="taskRunTable">
            </tbody>
        </table>
    </@com.page>
</#escape>