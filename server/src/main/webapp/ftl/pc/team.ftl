<#import "/spring.ftl" as spring />
<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="team">
    <style>
        #inner {
            color: red;
            background-color: #000000;
        }
    </style>
    <script type="text/javascript">
        function executeFunction(url) {
            var userComment = prompt("Please enter comments for the run .. ?", "Test Run");
            if (userComment != null) {
                $("#results").empty();
                $.post(url, { comment: userComment}, function (data) {
                    $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
                });
            }
        }

        function editAgent(id) {
            $.get('${rc.contextPath}/server/editAgent/' + id, function (data) {
                $('#spane_edit_agent').empty();
                $('#spane_edit_agent').html(data);
//                $('#span_expense_edit_'+id).empty();
//                $('#span_expense_edit_'+id).html(data);
            });
        }

        $(document).ready(function () {
            $(".content").hide();
//            $(".content").css("display", "none");
            $(".heading").click(function () {
                $(this).next(".content").slideToggle(200);
            });
        });

        function addAgent(id) {
            $.get('${rc.contextPath}/admin/team/' + id+'/addUser', function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }
    </script>
    <h2 class="sub-header text-muted">Team - ${model.team.name!}</h2>
    <p>Team Description : ${model.team.description!}</p>
    <p>Team Email : ${model.team.email!}</p>
    <p>Telegram ID : ${model.team.telegramId!}</p>
    <a href="#" onclick="addAgent('${model.team.id?string}')" class="btn btn-sm btn-primary">Add User</a><span id="span_task_agent"></span>
    <div class="table-responsive">
        <table class="table table-striped">
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>TelegramID</th>
            </tr>
            <#list model["team"].userList as user>
                <tr>
                    <td>${user_index+1} &nbsp;<a href="#" onclick="editAgent('${user.id}')"><span
                            class="glyphicon glyphicon-edit"/></a></td>
                    <td>${user.name?string}</td>
                    <td>${user.username?string}</td>
                    <td>${user.email!?string}</td>
                    <td>${user.telegramId!?string}</td>
                </tr>
            </#list>
        </table>
        <span id="spane_edit_agent"></span>

    </div>
    </@com.page>
</#escape>