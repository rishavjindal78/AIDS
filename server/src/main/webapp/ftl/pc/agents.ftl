<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="agents">
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

        var cacheId;

        var lpStart = function () {
            $("table tr td:nth-child(5)").each(function () {
//                alert($(this).text());
            });

            /*var jqxhr = $.get("healthMonitor", { id: 0, cacheId: cacheId },
                    function (data) {
                        if (data != undefined) {
                            cacheId = data.cacheId;
                            var content = '';
                            $.each(data.serverApps, function (index, serverApp) {
                                content += '<tr>';
                                content += '<td><h6><a href="javascript:downloadFile(' + serverApp.id + ')">' + serverApp.id + '</a></h6></td>';
                                var teamContacts = '<address>'
                                        + '<strong>' + trimOrBlank(serverApp.leadDeveloper) + '</strong><br />'
                                        + '<a mailto=' + trimOrBlank(serverApp.contactDL) + '>' + trimOrBlank(serverApp.contactDL) + '</a>' + '<br />'
                                        + '<strong> Chat Channel : </strong>' + trimOrBlank(serverApp.chatChannel) + '<br />'
                                        + '</address>';
//                                content += '<td class="" rowspan='+serverApp.serverComponents.length+'>'+teamContacts+'</td>';

                                var componentContent = '';
                                $.each(serverApp.serverComponents, function (index, component) {
                                    var uniqueId = serverApp.id + '-' + index;
                                    content += '<td><div class="' + getClassForServerAppStatus(component.status) + '"><p><strong>' + component.name + '</strong><sup> [' + component.status + ']</sup><BR/>' + getServerType(component.serverType) + trimOrBlank(component.lastStatusUpdateTime) + '</p></div></td>';
                                });
                                content += '</tr>';
                                console.log(content);
                            });
                            $("#serverTable").empty().append(content);
                        } else {
                            $("#serverTable").empty();
                        }
                        lpStart();
                    }, "json");

            jqxhr.error(function () {
                console.log("erro occurred fetching the agent status from server");
            });*/
        };
        $(document).ready(lpStart);
    </script>
    <#--<div class="container">-->
        <div class="heading btn-link">Add New Agent</div>
        <div class="content">
            <form role="form" name="agent" action="register" method="POST">
                <div class="form-group">
                    <label for="exampleInputEmail1">Task Name</label>
                    <input type="text" class="form-control" id="exampleInputEmail1" name="name"
                           placeholder="Enter Agent Name e.g. Build Machine Agent">
                </div>
                <div class="form-group">
                    <label for="exampleInputPassword1">Task Description</label>
                    <input type="text" class="form-control" id="exampleInputPassword1" name="description"
                           placeholder="Enter Agent Description e.g. Dev Server">
                </div>
                <div class="form-group">
                    <label for="exampleInputTags">Base Url</label>
                    <input type="text" class="form-control" id="exampleInputTags" name="baseUrl"
                           placeholder="Enter Base Url e.g. http://localhost:9291/">
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox"> Enable Me
                    </label>
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        </div>
        <h2 class="sub-header text-muted">Agents</h2>

        <div class="table-responsive">
            <table class="table table-striped">
                <tr>
                    <th width="5%">#</th>
                    <th width="30%">Name</th>
                    <th width="35%">Description</th>
                    <th width="20%">Base URL</th>
                    <th width="10%">Status</th>
                <#--<th>Comments</th>-->
                </tr>
                <#list model["agents"] as agent>
                    <tr>
                        <td>${agent_index+1} &nbsp;<a href="#" onclick="editAgent('${agent.id}')"><span
                                class="glyphicon glyphicon-edit"/></a></td>
                        <td>${agent.name?string}</td>
                        <td>${agent.description?string}</td>
                        <td>${agent.baseUrl?string}</td>
                        <td id="td_${agent.id}">
                            <#if agent.status?exists && agent.status == 'UP'>
                                <span class="label label-success" id="${agent.id}">${agent.status!'N/A'?string}</span>
                            <#else>
                                <span class="label label-danger" id="${agent.id}">${agent.status!'N/A'?string}</span>
                            </#if>

                        </td>
                    <#--<td>${debt.comments?size}</td>-->
                    </tr>
                </#list>
            </table>
            <span id="spane_edit_agent"></span>
        </div>
    <#--</div>-->
    </@com.page>
</#escape>