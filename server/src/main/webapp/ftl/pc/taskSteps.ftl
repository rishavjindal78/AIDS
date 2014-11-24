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
                $.get('../edit/' + this.id, function (data) {
                    $('#span_edit').empty();
                    $('#span_edit').html(data);
                });
            });

            $(".cancel").click(function () {
                alert('button clicked external');
                $(this).slideUp();
            });

            $("input:checkbox").change(function () {
                if ($(this).is(":checked")) {
                    $.ajax({
                        url: '../updateTaskStep',
                        type: 'POST',
                        data: { id: $(this).attr("id"), active: "1" }
                    });
                } else {
                    $.ajax({
                        url: '../updateTaskStep',
                        type: 'POST',
                        data: { id: $(this).attr("id"), active: "0" }
                    });
                }
            });

        });

        function execute(url) {
            var userComment = prompt("Please enter comments for the run .. ?", "Test Run");
            if (userComment != null) {
                $("#results").empty();
                $.post(url, { comment: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
                });
            }
        }

        function addAgent(taskStepId) {
            $.get('/rest/server/taskStep/addAgent/'+taskStepId, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }

        function removeAgent(agentId, taskId) {
            $.post('/rest/server/taskStep/removeAgent/'+taskId+'/'+agentId, function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }
    </script>

    <br/>
    <legend>Task Details</legend>
    <p>Task Name : ${model.task.name}</p>
    <p>Task Description : ${model.task.description}</p>

    <a href="../addTaskStep/${model.task.id?string}" class="btn btn-mini btn-primary">Add Step</a>
    <a href="#" onclick="execute('../run/${model.task.id?string}')" class="btn btn-mini  btn btn-danger">Run</a>
    <a href="../taskHistory/${model.task.id}" class="btn btn-mini  btn btn-danger">history</a>

    <table class="table table-striped">
        <tr>
            <th>Sequence</th>
            <th>Task Step</th>
            <th>Description</th>
            <th>Active</th>
            <th>Agents</th>
            <th>Operation</th>
        </tr>
        <#list model.task.stepDataList as taskStep>
            <tr>
                <td>${taskStep.sequence?string}</td>
                <td>${taskStep.taskClass?string}</td>
                <td>${taskStep.description!?string}</td>
                <td><input type="checkbox" id="${taskStep.id}"
                           <#if taskStep.active?? && taskStep.active?string=="true">checked="true"</#if></td>
                <td>
                    <#list taskStep.agentList as agent>
                        <form class="form-horizontal" name="agent" action="/rest/server/taskStep/removeAgent/${taskStep.id?string}/${agent.id}" method="post">
                            <span class="label label-primary">${agent.name}</span><button type="submit" class="btn btn-link" id="save">X</button>
                        </form>
                    </#list>
                </td>
                <td><a class="taskStepEdit" id="${taskStep.id?string}">edit</a>
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
    </@com.page>
</#escape>