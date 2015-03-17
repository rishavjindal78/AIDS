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
        $(document).ready(function () {
            $('.content').hide();
            $('.heading').click(function () {
                var targetSpan = '#span_' + this.id;
                $.get('../viewLogs/' + this.id, function (data) {
                    $(targetSpan).html(data);
                });
                $('#content_' + this.id).slideToggle(300);
//                $(this).next('.content').slideToggle(300);
            })
        });

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
                            $('#progressBarId').attr( 'aria-valuenow',''+progress+'');
                            $('#progressBarId').attr( 'style','width :'+progress+'%');
                            $('#progressBarId').text(progress+'%');
                            var content = '<tr><th>Seq</th>';
                            content += '<th width="30%">Title</th>';
                            content += '<th>Machine</th>';
                            content += '<th>Start Time</th>';
                            content += '<th>Duration</th>';
                            content += '<th>Status</th>';
                            content += '<th width="15%">Operation</th></tr>';
                            $.each(data.taskStepRuns, function (index, taskStepRun) {
                                var taskStepRunContent = '<td>'+taskStepRun.sequence+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.taskStep.description+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.agent.name+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.startTime+'</td>';
                                taskStepRunContent += '<td>'+taskStepRun.duration+'</td>';
                                /*if(taskStepRun.runStatus=='RUNNING'){
                                    taskStepRunContent += '<td><div class="progress">' +
                                    '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">' +
                                    '<span class="sr-only">45%</span>' +
                                    '</div></div></td>';
                                }else {*/
                                    taskStepRunContent += '<td><button type="button" class="' + getRowClassForButton(taskStepRun.runStatus) + '">' + taskStepRun.runStatus + '</button></td>';
//                                }
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
        $(document).ready(lpStart);

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
        }

    </script>
    <br>
    <ol class="breadcrumb">
        <li><a href="../team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li><a href="../team/${model.taskRun.team.id}/taskHistory">Task History</a></li>
        <li class="active"><a href="../../taskRun/view/${model.taskRun.id}">${model.taskRun.name!}</a></li>
    </ol>

    <h4 class="text-muted">${model.taskRun.id}. ${model.taskRun.name!}</h4>
    <div class="progress">
        <div id="progressBarId" class="progress-bar" role="progressbar" aria-valuenow="${model.taskRun.progress!}" aria-valuemin="0" aria-valuemax="100" style="width: ${model.taskRun.progress!}%;">
        ${model.taskRun.progress!}%
        </div>
    </div>
        <table class="table table-striped table-condensed" id="resultDisplayTable">
            <tbody id="taskRunTable">
                <th>Seq</th>
                <th>Title</th>
                <th>Machine</th>
                <th>Updated</th>
                <th>Duration</th>
                <th>Status</th>
                <th>Operation</th>
            </tbody>
            <#--<#list model.taskHistory.taskStepRuns as taskStepRun>
                <#if taskStepRun.runStatus?string == 'FAILURE'>
                <tr class="text-danger">
                <#elseif taskStepRun.runStatus?string == 'NOT_RUN'>
                <tr class="text-warning">
                <#else >
                <tr class="text-success"></#if>
                <td>${taskStepRun.id?string}</td>
                <td>${taskStepRun.sequence?string}</td>
                <td>${taskStepRun.taskStep.description!?string}</td>
                <td>${taskStepRun.agent.name!?string}</td>
                <td><#if taskStepRun.finishTime??>${taskStepRun.finishTime?datetime?string("dd MMM, yyyy hh.mm aa")}</#if></td>
                <td>${taskStepRun.timeConsumed()?string}</td>
                <td>
                    <#if taskStepRun.runStatus?string == 'FAILURE'>
                        <button type="button" class="btn btn-danger btn-sm">${taskStepRun.runStatus!?string}</button>
                    <#elseif taskStepRun.runStatus?string == 'SUCCESS' >
                        <button type="button" class="btn btn-success btn-sm">${taskStepRun.runStatus!?string}</button>
                    <#else >
                        <button type="button" class="btn btn-warning btn-sm">${taskStepRun.runStatus!?string}</button>
                    </#if>
                </td>
                <td>
                    <#if taskStepRun.runStatus!?string == 'RUNNING'>
                        <a id="${taskStepRun.id}" href="${rc.contextPath}/server/getMemoryLogs/view/${taskStepRun.id}"
                           target="_blank">tail logs</a>
                    <#else>
                        <a class="btn btn-sm btn-primary viewTaskLogs heading" id="${taskStepRun.id}"
                           href="#">logs</a>
                        <a class="btn btn-sm btn-warning" href="#" onclick="deleteTaskStepRun('${taskStepRun.id}')">delete</a>
                    </#if>
                </td>
            </tr>
                <tr class="content" id='content_${taskStepRun.id}'>
                    <td colspan="7">
                        <span><pre id='span_${taskStepRun.id}'>loading..</pre></span>
                    </td>
                </tr>
            </#list>-->
        </table>

    </@com.page>
</#escape>