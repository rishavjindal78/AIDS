<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="tasks">
    <script type="text/javascript">
        /* $(prettyPrint);*/
        $(document).ready(function () {

        });
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
    <ol class="breadcrumb">
        <li><a href="../team/${Session['SELECTED_TEAM'].id}/tasks">Tasks</a></li>
        <li><a href="../task/${model.task.id}">${model.task.name}</a></li>
        <li class="active"><a href="../taskHistory/${model.task.id}">History</a></li>
    </ol>

    <ul class="nav nav-tabs">
        <li role="presentation"><a href="../task/${model.task.id}">Task Details</a></li>
        <li role="presentation" class="active"><a href="../taskHistory/${model.task.id}">Activity</a></li>
        <li role="presentation"><a href="#">Configuration</a></li>
    </ul>
        <table class="table table-striped table-condensed">
            <tr>
                <th>#</th>
                <th>Task Title</th>
                <th>Comments</th>
                <th>Task Start Time</th>
                <th>Duration (hh:mm:ss)</th>
                <th>Run By</th>
                <th>Status</th>
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
                <td><a href="${rc.getContextPath()}/server/taskRun/view/${taskHistory.id}">${taskHistory.name!?string}</a></td>
                <td>${taskHistory.comments!?string}</td>
                <td><#if taskHistory.startTime??>${taskHistory.startTime?datetime?string("dd MMM, yyyy hh.mm aa")}</#if></td>
                <td><#if taskHistory.finishTime??>${taskHistory.timeConsumed()}</#if></td>
                <td>${(taskHistory.runBy.name)!?string}</td>
                <#if taskHistory.runStatus?string == 'FAILURE'>
                    <td>
                        <button type="button" class="btn btn-sm btn-danger">${taskHistory.runStatus!?string}</button>
                    </td>
                <#elseif taskHistory.runStatus?string == 'SUCCESS'>
                    <td>
                        <button type="button" class="btn btn-sm btn-success">${taskHistory.runStatus!?string}</button>
                    </td>
                <#else >
                    <td>
                        <button type="button" class="btn btn-sm btn-warning">${taskHistory.runStatus!?string}</button>
                    </td>
                </#if>

                <td>
                    <a href="../deleteTaskHistory/${taskHistory.id}" class="btn btn-sm btn-primary">delete</a>
                </td>
            </tr>
            </#list>
        </table>
    <script type="text/javascript">
    </script>
    </@com.page>
</#escape>