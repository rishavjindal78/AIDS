<#import "/spring.ftl" as spring />
<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="teams">
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
            $.get('/rest/server/editAgent/' + id, function (data) {
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

        var cacheId;

        var lpStart = function () {
            $("table tr td:nth-child(5)").each(function () {
//                alert($(this).text());
            });
        };
        $(document).ready(lpStart);
    </script>
    <div class="heading btn-link">Add New Team</div>
    <div class="content">
        <form role="form" name="agent" action='<@spring.url "/rest/admin/team/create" />' method="POST">
            <div class="form-group">
                <label for="exampleInputEmail1">Team Name</label>
                <input type="text" class="form-control" id="exampleInputEmail1" name="name"
                       placeholder="Enter Team Name e.g. Product Team">
            </div>
            <div class="form-group">
                <label for="exampleInputPassword1">Team Description</label>
                <input type="text" class="form-control" id="exampleInputPassword1" name="description"
                       placeholder="Enter Team Description e.g. Product Team Description">
            </div>
            <div class="form-group">
                <label for="exampleInputTags">Telegram ID</label>
                <input type="text" class="form-control" id="exampleInputTags" name="telegramId"
                       placeholder="Enter Telegram Group Chat ID">
            </div>
            <div class="form-group">
                <label for="exampleInputTags">Email ID</label>
                <input type="email" class="form-control" id="exampleInputTags" name="email"
                       placeholder="Enter Email for the Team">
            </div>
            <div class="checkbox">
                <label>
                    <input type="checkbox"> Enable Me
                </label>
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn btn-default">Reset</button>
        </form>
    </div>
    <h2 class="sub-header text-muted">Teams</h2>

    <div class="table-responsive">
        <table class="table table-striped">
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Description</th>
                <th>TelegramID</th>
                <th>Email</th>
            <#--<th>Comments</th>-->
            </tr>
            <#list model["teams"] as team>
                <tr>
                    <td>${team_index+1} &nbsp;<a href="#" onclick="editAgent('${team.id}')"><span
                            class="glyphicon glyphicon-edit"/></a></td>
                    <td>${team.name?string}</td>
                    <td>${team.description?string}</td>
                    <td>${team.telegramId?string}</td>
                    <td>${team.email?string}</td>
                <#--<td>${debt.comments?size}</td>-->
                </tr>
            </#list>
        </table>
        <span id="spane_edit_agent"></span>
    </div>
    </@com.page>
</#escape>