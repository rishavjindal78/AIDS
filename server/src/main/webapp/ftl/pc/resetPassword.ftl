<#import "/spring.ftl" as spring />
<#escape x as x?html>
<html>
<head>
    <title>AIDS Login</title>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/navbar-fixed-top.css'/>"/>
    <script src="<@spring.url '/js/jquery-2.1.0.min.jss'/>"></script>
    <script src="<@spring.url '/bootstrap/js/bootstrap.min.js'/>"></script>
</head>
<body onload="document.login_form.username.focus();">
<div class="container">
    <fieldset>
        <form name="login_form" class="form-horizontal" role="form" action="${rc.contextPath}/user/reset" method="POST">
            <h3 class="text-muted">Reset Password</h3>
            <br/>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Username</label>
                <div class="col-sm-4">
                    <input type="text" name='username' class="form-control" id="inputEmail3" placeholder="Username">
                </div>
            </div>
            <div class="form-group">
                <label for="inputPassword3" class="col-sm-2 control-label">Password</label>
                <div class="col-sm-4">
                    <input type="password" name='password' class="form-control" id="inputPassword3" placeholder="Password">
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" name="submit" class="btn btn-primary">Save</button>
                    <a class="btn btn-link" type="button" href="${rc.contextPath}/user/login">Login</a>
                </div>
            </div>
        </form>
    </fieldset>

    <#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
        <div><span style='color:red;'>
            Your login attempt was not successful, try again.<br />
            Reason :   ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
        </span>
        </div>
    </#if>
</div>
</body>
</html>
</#escape>