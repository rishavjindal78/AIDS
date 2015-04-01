<#import "/spring.ftl" as spring />
<#escape x as x?html>
<html>
<head>
    <title>AIDS Login</title>
    <link href="${rc.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${rc.contextPath}/bootstrap/css/navbar-fixed-top.css" rel="stylesheet" type="text/css">
    <#--<script src="${rc.contextPath}/js/jquery-2.1.0.min.js" type="text/jsx"></script>-->
    <#--<script src="${rc.contextPath}/bootstrap/js/bootstrap.min.js" type="text/jsx"></script>-->
</head>
<body onload="document.login_form.j_username.focus();">
<div class="container">
    <form name="login_form" class="form-horizontal" role="form" action="${rc.contextPath}/j_spring_security_check"
          method="POST">
        <h3 class="text-muted">Autonomous Integrated Deployment Software</h3>
        <br/>

        <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">Username</label>

            <div class="col-sm-4">
                <input type="text" name='j_username' class="form-control" id="inputEmail3" placeholder="Username">
            </div>
        </div>
        <div class="form-group">
            <label for="inputPassword3" class="col-sm-2 control-label">Password</label>

            <div class="col-sm-4">
                <input type="password" name='j_password' class="form-control" id="inputPassword3"
                       placeholder="Password">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="_spring_security_remember_me"> Remember me
                    </label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" name="submit" class="btn btn-primary">Sign in</button>
                <a class="btn btn-link" type="button" href="${rc.contextPath}/user/register">Register Here</a>
            </div>
        </div>
    </form>
    <#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
        <div><span style='color:red;'>
            Your login attempt was not successful, try again.<br/>
            Reason :   ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
        </span>
        </div>
    </#if>
</div>
</body>
</html>
</#escape>