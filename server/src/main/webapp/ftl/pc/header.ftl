<#assign security=JspTaglibs["/WEB-INF/tlds/security.tld"] />
<#import "/spring.ftl" as spring />
<script>
    var activeTab = $('meta[name="activeTab"]').attr('content');
    $(document).ready(function () {
        $('#nav ul').children('li').removeClass();
        $("#" + activeTab).addClass('active');
    });

    $(document).ready(function () {
        $('#refreshLink').click(function () {
            location.reload();
        });

        $('#selectedTeam').on('change', function () {
            $("#team_form").submit();
//            var newTeam = this.value;
//            $.post("${rc.contextPath}/server/team", { teamId: newTeam}, function (data) {
//                location.reload();
//                $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
        });
    });

</script>

<div class="container">
    <form class="form-inline" id="team_form" action="${rc.contextPath}/server/team" method="post">
        <div id="nav">
            <ul class="nav nav-pills pull-right">
            <@security.authorize ifAllGranted="ROLE_USER">
                <li id="agents"><a href="${rc.getContextPath()}/server/team/${Session['SELECTED_TEAM'].id}/agents">Agents</a>
                </li>
                <li id="tasks"><a
                        href='<@spring.url "/server/team/${Session['SELECTED_TEAM'].id}/tasks" />'>Tasks</a>
                </li>
                <li id="history"><a
                        href='<@spring.url "/server/team/${Session['SELECTED_TEAM'].id}/taskHistory" />'>History</a>
                </li>
                <li id="documents"><a
                        href='<@spring.url "/documents/team/${Session['SELECTED_TEAM'].id}/search" />'>Docs</a>
                </li>
                <li id="settings"><a
                        href='<@spring.url "/server/team/${Session['SELECTED_TEAM'].id}/settings" />'>Settings</a>
                </li>
            </@security.authorize>
            <@security.authorize ifAllGranted="ROLE_ADMIN">
                <li id="teams"><a href='<@spring.url "/admin/team/index" />'>Teams</a></li>
                <li id="settings"><a href="${rc.contextPath}/admin/settings">Settings</a></li>
            </@security.authorize>
                <#--<li id="home"><a href='${rc.contextPath}/user/profile/${Session["SPRING_SECURITY_CONTEXT"].authentication.name}'>-->
                <#--<#if Session["LOGGED_USER"]?exists>-->
                <#--${Session["LOGGED_USER"].name}-->
                <#--<#elseif Session["SPRING_SECURITY_CONTEXT"]?exists>-->
                <#--${Session["SPRING_SECURITY_CONTEXT"].authentication.name}-->
                <#--<#else>-->
                    <#--Anonymous-->
                <#--</#if></a>-->
                <#--</li>-->
            <@security.authorize ifAllGranted="ROLE_USER">
                <li><select id="selectedTeam" class="form-control" name="teamId" onchange="submit()">
                    <#list Session["LOGGED_USER"].teamList as team>
                        <option value="${team.id}"
                                <#if team.id == Session['SELECTED_TEAM'].id>selected="selected"</#if>>${team.name}</option>
                    </#list>
                </select>
                </li>

            </@security.authorize>
                <li id="logout"><a id="logoutLink" href="${rc.contextPath}/j_spring_security_logout"><span
                        class="glyphicon glyphicon-log-out"></span></a></li>
            </ul>
            <h3 class="text-muted"><@spring.message code="application.title"/></h3>
            <!--form class="navbar-form navbar-right form-inline" action="search">
                <div class="col-lg-3">
                    <input type="text" class="form-control" placeholder="type to search here">
                </div>
                <button type="submit" class="btn btn-default">Search</button>
            </form-->
        </div>
    </form>
    <!--div class="well">
        <fieldset>
            <legend>Search Resources</legend>
            <form id="user" class="form-inline" name="resource" modelAttribute="resource" action="/todo-resources/search" method="post">
                <div class="row">
                    <div class="col-lg-8">
                        <input type="text" class="form-control" placeholder="Type something to Search.." name="search"">
                    </div>
                    <button type="submit" class="btn btn-default" id="save">Search</button>
                    <a class="btn btn-default" href="add">Create Article</a>
                </div>
            </form>
        </fieldset>
    </div-->
<#--<hr>-->
</div>