<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="history">
    <style type="text/css">
        body {
            font-size: 12px;
            /*background-color: #f5f5f5;*/
        }
    </style>
    <script type="text/javascript">
        /* $(prettyPrint);*/
        $(document).ready(function () {

        });

        function deleteTaskRun(taskRun) {
            var option = confirm("Are you sure to delete the Step Run ?");
            if (option == true) {
                var url = '${rc.contextPath}/server/deleteTaskRun/' + taskRun;
                $("#results").empty();
                $.post(url, {}, function (data) {
                    location.reload();
//                    $("table#tasksTable tr#table_row_"+stepRunIdId).remove();
//                    $("#results").html('<div class="alert alert-success">Row Deleted Successfully - ' + url + '</div>');
                });
            }
        }

        function cancelTaskRun(taskRun) {
            var option = confirm("Are you sure to delete the Step Run ?");
            if (option == true) {
                var url = '${rc.contextPath}/server/cancel/' + taskRun;
                $("#results").empty();
                $.post(url, {}, function (data) {
                    $("#results").html('<div class="alert alert-success">Request submitted to cancel the TaskRun - ' + taskRun+ '</div>');
                });
            }
        }
    </script>

    <#--<div class="well">
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
    </div>-->
    <br/>
    <#--<hr/>-->
    <#--<div class="well">-->
    <h3 class="text-muted">Task Run History</h3>
    <table class="table table-striped table-condensed">
        <tr>
            <th>#</th>
            <th>Task Title</th>
            <th>Comments</th>
            <th>Updated</th>
            <th>Time Consumed</th>
            <th>Status</th>
            <th>RunBy</th>
            <th>Operation</th>
        </tr>
        <#list model["taskHistoryList"] as taskHistory>
            <#if taskHistory.runStatus?string == 'FAILURE'>
            <tr class="text-danger">
            <#elseif taskHistory.runStatus?string == 'NOT_RUN'>
            <tr class="text-warning">
            <#else >
            <tr class="text-success"></#if>
            <td>${taskHistory.id?string}</td>
            <td>
                <a href="${rc.getContextPath()}/server/taskStepHistory/${taskHistory.id}">${taskHistory.name!?string}</a>
            </td>
            <td>${taskHistory.comments!?string}</td>
            <td><#if taskHistory.startTime??>${taskHistory.startTime?datetime?string("dd MMM, yyyy hh.mm aa")}</#if></td>
            <td><#if taskHistory.finishTime??>${taskHistory.timeConsumed()}</#if></td>
            <#if taskHistory.runStatus?string == 'FAILURE'>
                <td>
                    <button type="button" class="btn btn-danger btn-sm">${taskHistory.runStatus!?string}</button>
                </td>
            <#elseif taskHistory.runStatus?string == 'SUCCESS'>
                <td>
                    <button type="button" class="btn btn-success btn-sm">${taskHistory.runStatus!?string}</button>
                </td>
            <#else >
                <td>
                    <button type="button" class="btn btn-warning btn-sm">${taskHistory.runStatus!?string}</button>
                </td>
            </#if>
            <td>
                <#if taskHistory.runBy?exists>${taskHistory.runBy.username!?string}</#if>
            </td>
            <td>
                <a class="btn btn-small btn-warning" href="#" onclick="deleteTaskRun('${taskHistory.id}')">delete</a>
                <a class="btn btn-small btn-warning" href="#" onclick="cancelTaskRun('${taskHistory.id}')">Cancel</a>
            </td>
        </tr>
        </#list>
    </table>
    <span id="results"></span>
    <#--</div>-->
    <script type="text/javascript">
    </script>
    </@com.page>
</#escape>