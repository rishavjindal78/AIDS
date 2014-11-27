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

        $('#selectedTeam').on('change', function() {
            var newTeam = this.value;
            $.post("/rest/server/team", { teamId: newTeam}, function (data) {
//                $("#results").html('<div class="alert alert-success">Job Submitted Successfully - ' + url + '</div>');
            });
        });
    });

</script>

<div class="container">
    <div id="nav">
        <ul class="nav nav-pills pull-right">
            <li id="agents"><a href="${rc.getContextPath()}/rest/server/agents">Agents</a></li>
            <li id="tasks"><a href='<@spring.url "/rest/server/index" />'>Tasks</a></li>
            <li id="history"><a href='<@spring.url "/rest/server/taskHistory" />'>History</a></li>
        <@security.authorize ifAllGranted="ROLE_ADMIN">
            <li id="teams"><a href="/rest/admin/team/index">Teams</a></li>
            <li id="settings"><a href="/rest/admin/index">Admin</a></li>
        </@security.authorize>
            <li id="home"><a href="/rest/home/index">
            <#if Session["LOGGED_USER"]?exists>
            ${Session["LOGGED_USER"].name}
            <#elseif Session["SPRING_SECURITY_CONTEXT"]?exists>
            ${Session["SPRING_SECURITY_CONTEXT"].authentication.name}
            <#else>
                Anonymous
            </#if>
            </a></li>
        <@security.authorize ifAllGranted="ROLE_USER">
            <li><select id="selectedTeam" class="form-control" name="id">
                <#list Session["LOGGED_USER"].teamList as team>
                    <option value="${team.id}">${team.name}</option>
                </#list>
            </select>
            </li>
        </@security.authorize>
            <li id="logout"><a id="logoutLink" href="/rest/j_spring_security_logout"><span
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

    <!--div class="well">
        <fieldset>
            <legend>Search Resources</legend>
            <form id="user" class="form-inline" name="resource" modelAttribute="resource" action="/rest/todo-resources/search" method="post">
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