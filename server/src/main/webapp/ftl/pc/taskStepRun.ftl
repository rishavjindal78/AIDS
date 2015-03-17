<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="history">
    <style>
        body {
            font-size: 12px;
            /*background-color: #f5f5f5;*/
        }
        .layer1 {
            width: 600px;
            padding: 0;
            margin: 0;
            cursor: pointer;
        }

        .heading {
            /*margin-bottom: 10px;*/
        }

        .content {
            padding: 5px 10px;
            background-color: #fafafa;
        }

        .taskStepEdit:hover {
            /*background: yellow;*/
        }
    </style>

    <script type="text/javascript">
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
    </script>

    <!--div class="well">
        <fieldset>
            <legend>Search Tasks</legend>
            <form id="user" class="form-inline" name="resource" modelAttribute="resource" action="search" method="post">
                <div class="row">
                    <div class="col-lg-4">
                        <input type="text" class="form-control" placeholder="Type something to search.." name="search"">
                    </div>
                    <button type="submit" class="btn btn-default" id="save">Search</button>
                </div>
            </form>
        </fieldset>
    </div-->
    <br>
    <ol class="breadcrumb">
        <li><a href="../team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li><a href="../team/${model.taskHistory.team.id}/taskHistory">Task History</a></li>
        <li class="active"><a href="../taskStepHistory/${model.taskHistory.id}">${model.taskHistory.name!}</a></li>
    <#--<li class="active">Data</li>-->
    </ol>
    <div class="well">
        <legend>${model.taskHistory.id}. ${model.taskHistory.name!}</legend>
        <table class="table table-striped table-condensed">
            <tr>
                <th>Id</th>
                <th>Seq</th>
                <th>Title</th>
                <th>Agent</th>
                <th>Updated</th>
                <th>Duration</th>
                <th>Status</th>
                <th>Operation</th>
            </tr>
            <#list model.taskHistory.taskStepRuns as taskStepRun>
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
                        <a class="btn btn-small btn-primary viewTaskLogs heading" id="${taskStepRun.id}"
                           href="#">logs</a>
                        <a class="btn btn-small btn-warning" href="#" onclick="deleteTaskStepRun('${taskStepRun.id}')">delete</a>
                    </#if>
                </td>
            </tr>
                <tr class="content" id='content_${taskStepRun.id}'>
                    <td colspan="7">
                        <span><pre id='span_${taskStepRun.id}'>loading..</pre></span>
                    </td>
                </tr>
            </#list>
        </table>
        <span id="results"></span>
    </div>
    </@com.page>
</#escape>