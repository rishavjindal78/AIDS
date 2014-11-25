<#import "/spring.ftl" as spring />
<#escape x as x?html>
<html>
<head>
    <title>Spring Security Login</title>
    <link href="${rc.getContextPath()}/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${rc.getContextPath()}/resources/bootstrap/css/navbar-fixed-top.css" rel="stylesheet">

    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.0.min.js"></script>
    <script src="${rc.getContextPath()}/resources/bootstrap/js/bootstrap.min.js"></script>
</head>
<body onload="document.login_form.j_username.focus();">
<h1>Spring Security Login (Freemarker)</h1>

<form name="login_form" action="${rc.contextPath}/rest/j_spring_security_check" method="POST">
    <table>
        <tr>
            <td>User:</td>
            <td><input type='text' name='j_username' value=''/></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='j_password' value=''/></td>
        </tr>
        <tr>
            <td><input type="checkbox" class="form-control" name="_spring_security_remember_me"/></td>
            <td>Don't ask for my password for two weeks</td>
        </tr>
        <tr>
            <td colspan='1'><input type="submit" class="form-control" id="submit" name="submit" placeholder="Login">
            </td>
        </tr>
        <tr>
            <td colspan='1'><input type="reset" class="form-control" id="reset" name="reset" placeholder="Reset"></td>
        </tr>
    </table>
</form>
    <#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
    <div><span style='color:red;'>
            Your login attempt was not successful, try again.<br />
            Reason :   ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
        </span>
    </div>
    </#if>
</body>
</html>
</#escape>