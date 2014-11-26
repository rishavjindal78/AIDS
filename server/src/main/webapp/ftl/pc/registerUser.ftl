<#import "/spring.ftl" as spring />
<#escape x as x?html>
<html>
<head>
    <title>AIDS Login</title>
    <link href="${rc.getContextPath()}/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${rc.getContextPath()}/resources/bootstrap/css/navbar-fixed-top.css" rel="stylesheet">
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.0.min.js"></script>
    <script src="${rc.getContextPath()}/resources/bootstrap/js/bootstrap.min.js"></script>
</head>
<body onload="document.login_form.username.focus();">
<div class="container">
    <fieldset>
        <form name="login_form" class="form-horizontal" role="form" action="${rc.contextPath}/rest/user/register" method="POST">
            <h3 class="text-muted">Registration Form</h3>
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
                <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
                <div class="col-sm-4">
                    <input type="text" name='name' class="form-control" id="inputEmail3" placeholder="name">
                </div>
            </div>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Email</label>
                <div class="col-sm-4">
                    <input type="email" name='email' class="form-control" id="inputEmail3" placeholder="Email">
                </div>
            </div>
            <div class="form-group">
                <label for="TelegramId" class="col-sm-2 control-label">TelegramId</label>
                <div class="col-sm-4">
                    <input type="text" name='telegramId' class="form-control" id="TelegramId" placeholder="TelegramId">
                </div>
            </div>
            <div class="form-group">
                <label for="phone" class="col-sm-2 control-label">Phone #</label>
                <div class="col-sm-4">
                    <input type="text" name='phone' class="form-control" id="phone" placeholder="Phone Number">
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="enabled"> Enable me
                        </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" name="submit" class="btn btn-primary">Register</button>
                    <a class="btn btn-link" type="button" href="/rest/server/login">Login</a>
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