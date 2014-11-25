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
    });

</script>

<div class="container">
    <div id="nav">
        <ul class="nav nav-pills pull-right">
            <li id="agents"><a href="${rc.getContextPath()}/rest/server/agents">Agents</a></li>
            <li id="tasks"><a href="${rc.getContextPath()}/rest/server/index">Tasks</a></li>
            <li id="todo"><a href="/rest/todo/awaiting">Todo</a></li>
            <li id="settings"><a href="/rest/settings/index">Admin</a></li>
            <li id="home"><a href="/rest/home/index">
                <#if Session["SPRING_SECURITY_CONTEXT"]?exists>
                    ${Session["SPRING_SECURITY_CONTEXT"].authentication.name}
                <#else>
                    Anonymous
                </#if>
                </a></li>
            <li id="logout"><a id="logoutLink" href="/rest/j_spring_security_logout"><span class="glyphicon glyphicon-log-out"></span></a></li>
        </ul>
        <h3 class="text-muted">My AIDS</h3>
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