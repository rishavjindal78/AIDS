<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="history">
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
    <#--<hr/>-->
    <#--<div class="well">-->
        <h3 class="text-muted">Task Run History</h3>
        <table class="table table-striped table-condensed">
            <tr>
                <th>Id</th>
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
                <td><a href="../server/taskStepHistory/${taskHistory.id}">${taskHistory.name!?string}</a></td>
                <td>${taskHistory.comments!?string}</td>
                <td><#if taskHistory.startTime??>${taskHistory.startTime?datetime?string("dd MMM, yyyy hh.mm aa")}</#if></td>
                <td>${taskHistory.finishTime!0?long - taskHistory.startTime!0?long}</td>
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
                    <a href="../deleteTaskHistory/${taskHistory.id}" class="btn btn-sm btn-primary">delete</a>
                </td>
            </tr>
            </#list>
        </table>
    <#--</div>-->
    <script type="text/javascript">
    </script>
    </@com.page>
</#escape>