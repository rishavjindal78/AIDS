<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="tasks">

    <style>
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
            /*    $( ".taskStepEdit" ).click(function() {
                    $.get('../viewLogs/'+this.id, function(data) {
                        $(this).next(".content").html(data);
                    });
                });*/

            $('.content').hide();

            $('.heading').click(function () {
                $('#content_' + this.id).slideToggle(300);
//                $(this).next('.content').slideToggle(300);
            })

            prettyPrint();

        });
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
    <hr/>
    <a href="../taskHistory/${model.taskHistory.taskData.id}"> << Back To Task History</a>
    <div class="well">
        <table class="table table-striped">
            <tr>
                <th>Id</th>
                <th>Seq</th>
                <th>Title</th>
                <th>Updated</th>
                <th>Status</th>
                <th>Operation</th>
            </tr>
            <#list model.taskHistory.taskStepRuns as taskStepHistory>
                <#if taskStepHistory.runStatus?string == 'FAILURE'>
                <tr class="text-danger">
                <#elseif taskStepHistory.runStatus?string == 'NOT_RUN'>
                <tr class="text-warning">
                <#else >
                <tr class="text-success"></#if>
                <td>${taskStepHistory.id?string}</td>
                <td>${taskStepHistory.sequence?string}</td>
                <td>${taskStepHistory.taskStepData.description!?string}</td>
                <td><#if taskStepHistory.startTime??>${taskStepHistory.startTime?datetime?string("dd MMM, yyyy hh.mm aa")}</#if></td>
                <#if taskStepHistory.runStatus?string == 'FAILURE'>
                <td class="text-danger"><#else >
                <td class="text-success"></#if>
                ${taskStepHistory.runStatus!?string}</td>
                <td>
                    <a class="btn btn-small btn-primary viewTaskLogs heading" id="${taskStepHistory.id}" href="#">logs</a>
                    <a class="btn btn-small btn-warning" href="delete/${taskStepHistory.id}">delete</a>
                </td>
                </tr>
                <tr class="content" id='content_${taskStepHistory.id}'>
                    <td colspan="6"><pre>${taskStepHistory.logs?string}</pre></td>
                </tr>
            </#list>
        </table>
    </div>

    </@com.page>
</#escape>