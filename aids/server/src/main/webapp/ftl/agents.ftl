<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="agents">

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

        function editExpense(id) {
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
    </script>
    <div class="container">
        <div class="heading btn-link">Add New Agent</div>
        <div class="content">
            <form role="form" name="agent" action="register" method="POST">
                <div class="form-group">
                    <label for="exampleInputEmail1">Task Name</label>
                    <input type="text" class="form-control" id="exampleInputEmail1" name="name" placeholder="Enter Agent Name">
                </div>
                <div class="form-group">
                    <label for="exampleInputPassword1">Task Description</label>
                    <input type="text" class="form-control" id="exampleInputPassword1" name="description" placeholder="Enter Agent Description">
                </div>
                <div class="form-group">
                    <label for="exampleInputTags">Base Url</label>
                    <input type="text" class="form-control" id="exampleInputTags" name="baseUrl" placeholder="Enter Base Url">
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox"> Enable Me
                    </label>
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        </div>
        <h2 class="sub-header">Agents</h2>

        <div class="table-responsive">
            <table class="table table-striped">
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Base URL</th>
                    <th>Status</th>
                <#--<th>Comments</th>-->
                </tr>
                <#list model["agents"] as agent>
                    <tr>
                        <td>${agent_index+1} &nbsp;<a href="#" onclick="editExpense('${agent.id}')"><span
                                class="glyphicon glyphicon-edit"/></a></td>
                        <td>${agent.name?string}</td>
                        <td>${agent.description?string}</td>
                        <td>${agent.baseUrl?string}</td>
                        <td>${agent.status!''?string}</td>
                    <#--<td>${debt.comments?size}</td>-->
                    </tr>
                </#list>
            </table>
            <span id="spane_edit_agent"></span>
        </div>
    </div>
    </@com.page>
</#escape>