<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="agents">
    <style  type="text/css">
        #inner {
            color: red;
            background-color: #000000;
        }

        #ajaxBusy {
            display: none;
            opacity: 0.9;
            /*margin: 0px 0px 0px -50px; /!* left margin is half width of the div, to centre it *!/*/
            /*padding: 30px 10px 10px 10px;*/
            position: fixed;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            text-align: center;
            background: rgba(0,0,0,0.6) url(${rc.contextPath}/images/ajax-loader-big.gif) no-repeat center center;
            border: 1px solid #8597d1;
            z-index: 5;
        }

        input:required:invalid, input:focus:invalid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAeVJREFUeNqkU01oE1EQ/mazSTdRmqSxLVSJVKU9RYoHD8WfHr16kh5EFA8eSy6hXrwUPBSKZ6E9V1CU4tGf0DZWDEQrGkhprRDbCvlpavan3ezu+LLSUnADLZnHwHvzmJlvvpkhZkY7IqFNaTuAfPhhP/8Uo87SGSaDsP27hgYM/lUpy6lHdqsAtM+BPfvqKp3ufYKwcgmWCug6oKmrrG3PoaqngWjdd/922hOBs5C/jJA6x7AiUt8VYVUAVQXXShfIqCYRMZO8/N1N+B8H1sOUwivpSUSVCJ2MAjtVwBAIdv+AQkHQqbOgc+fBvorjyQENDcch16/BtkQdAlC4E6jrYHGgGU18Io3gmhzJuwub6/fQJYNi/YBpCifhbDaAPXFvCBVxXbvfbNGFeN8DkjogWAd8DljV3KRutcEAeHMN/HXZ4p9bhncJHCyhNx52R0Kv/XNuQvYBnM+CP7xddXL5KaJw0TMAF8qjnMvegeK/SLHubhpKDKIrJDlvXoMX3y9xcSMZyBQ+tpyk5hzsa2Ns7LGdfWdbL6fZvHn92d7dgROH/730YBLtiZmEdGPkFnhX4kxmjVe2xgPfCtrRd6GHRtEh9zsL8xVe+pwSzj+OtwvletZZ/wLeKD71L+ZeHHWZ/gowABkp7AwwnEjFAAAAAElFTkSuQmCC); background-position: right top; background-repeat: no-repeat; -moz-box-shadow: none; }
        input:required:valid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=); background-position: right top; background-repeat: no-repeat; }

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

        function editAgent(id, description) {
            $("#myModalLabelForAgent").text("Edit Agent - " + description);
            $('.agent_row').removeClass('alert-warning');
            $('#agent_row_'+id).addClass('alert-warning');
            $.get('${rc.contextPath}/server/editAgent/' + id, function (data) {
                $('#span_edit_agent').empty();
                $('#span_edit_agent').html(data);
                $('#span_edit_agent').focus();
                $('#myModalForAgent').modal({
                    keyboard: true
                });
            });
        }

        function checkStatus() {
            var userInput = confirm("Do you want to check status of all agents ?");
            if (userInput) {
                $("#results").empty();
                $.post('${rc.contextPath}/server/agent/checkStatus', {}, function (data) {
                    $("#results").html('<div class="alert alert-success small">Agent Status Check Triggered, please refresh the page after few seconds.</div>');
                });
            }
        }

        function deleteAgent(agentId, agentName) {
            var option = confirm("Are you sure to delete the Agent : " + agentName + " ?");
            if (option == true) {
                var url = '${rc.getContextPath()}/server/agent/' + agentId;
                $("#results").empty();
                $.ajax({
                    url: url,
                    type: 'delete',
                    success: function (data) {
                        $("table#agentsTable tr#agent_row_" + agentId).remove();
                        $("#results").html('<div class="alert alert-success">Agent Deleted Successfully - ' + url + '</div>');
                    },
                    error: function(xhr, textStatus, errorThrown){
                        alert('request failed due to unknown reason : '+textStatus);
                    }
                });
            }
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

        $('body').append('<div id="ajaxBusy"><p id="ajaxBusyMsg">Please wait...</p></div>');

        // AJAX activity indicator bound to ajax start/stop document events
        $(document).ajaxStart(function () {
            $('#ajaxBusy').show();
        }).ajaxStop(function () {
            $('#ajaxBusy').hide();
        });

    </script>
    <#--<div class="container">-->
    <div class="heading alert alert-info" style="padding: 6px;margin-bottom: 2px; margin-top: 2px;">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span><strong> Add New Agent</strong>
    </div>
    <div class="content alert alert-warning">
        <form role="form" name="agent" action="register" method="POST">
            <div class="form-group" style="width: 60%;">
                <label for="exampleInputEmail1">Agent Name</label>
                <input type="text" class="form-control" id="exampleInputEmail1" name="name" required
                       placeholder="Enter Agent Name e.g. Build Machine Agent">
            </div>
            <div class="form-group" style="width: 60%;">
                <label for="exampleInputPassword1">Agent Description</label>
                <input type="text" class="form-control" id="exampleInputPassword1" name="description"
                       placeholder="Enter Agent Description e.g. Dev Server">
            </div>
            <div class="form-group" style="width: 60%;">
                <label for="exampleInputTags">Agent Base Url</label>
                <input type="url" class="form-control" id="exampleInputTags" name="baseUrl" required pattern="https?://.+"
                       placeholder="Enter Base Url e.g. http://localhost:9291/agent/">
            </div>
            <div class="form-group" style="width: 60%;">
                <label for="agentPropertiesTextArea">Agent Properties</label>
                    <textarea id="agentPropertiesTextArea" type="text" class="form-control input-sm" placeholder="Agent properties to override"
                              name="agentProperties.properties" rows="5"></textarea>
            </div>
            <div class="checkbox">
                <label>
                    <input type="checkbox"> Enable It
                </label>
            </div>
            <button type="submit" class="btn btn-primary">Save</button>
            <button type="reset" class="btn btn-default">Reset</button>
        </form>
    </div>
    <h3 class="sub-header text-muted">Configured Agents</h3>

    <div class="table">
        <table id="agentsTable" class="table table-striped">
            <tr>
                <th width="5%">#</th>
                <th width="20%">Agent Name</th>
                <th width="30%">Description</th>
                <th width="25%">Base URL</th>
                <th width="10%">Status <a href="#" onclick="checkStatus()"><span class="glyphicon glyphicon-refresh"/></a></th>
                <th width="10%">Operation</th>
            <#--<th>Comments</th>-->
            </tr>
            <#list model["agents"] as agent>
                <tr id="agent_row_${agent.id}" class="agent_row">
                    <td>${agent_index+1} &nbsp;<a href="#" onclick="editAgent('${agent.id}','${agent.name!}')"><span
                            class="glyphicon glyphicon-edit"/></a></td>
                    <td>${agent.name?string}</td>
                    <td>${agent.description?string}</td>
                    <td>${agent.baseUrl?string}</td>
                    <td id="td_${agent.id}">
                        <#if agent.status?exists && agent.status == 'UP'>
                            <span class="label label-success" id="${agent.id}">${agent.status.version!'N/A'?string}</span>
                        <#else>
                            <span class="label label-danger" id="${agent.id}">${agent.status!'N/A'?string}</span>
                        </#if>
                    </td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-sm btn-default dropdown-toggle"
                                    data-toggle="dropdown" aria-expanded="false">Action<span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="#" onclick="editAgent('${agent.id}', '${agent.name!}')">edit</a></li>
                                <li class="divider"></li>
                                <li><a href="#" onclick="deleteAgent('${agent.id}','${agent.name}')">delete</a></li>
                            </ul>
                        </div>
                    </td>
                <#--<td>${debt.comments?size}</td>-->
                </tr>
            </#list>
        </table>
        <span id="results"></span>
    </div>
    <!-- Modal for add agent -->

    <div class="modal fade" id="myModalForAgent" tabindex="-1" role="dialog" aria-labelledby="myModalLabelForAgent" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="alert alert-warning">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title text-muted" id="myModalLabelForAgent">Save Agent</h4>
                </div>
                <span id="span_edit_agent" />
                </div>
            </div>
        </div>
    </div>
    </@com.page>
</#escape>