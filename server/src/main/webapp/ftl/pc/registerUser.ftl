<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<#assign htmlEscape = true in spring>
<#escape x as x?html>
<html>
<head>
    <title>AIDS Login</title>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/navbar-fixed-top.css'/>"/>
    <script src="<@spring.url '/js/jquery-2.1.0.min.js'/>"></script>
    <script src="<@spring.url '/bootstrap/js/bootstrap.min.js'/>"></script>
</head>
<body onload="document.login_form.username.focus();">
    <@spring.bind "user" />
    <@spring.showErrors '*', 'errors' />
    <#--<#if spring.status.error>
    <div class="errors">
        There were problems with the data you entered:
        <ul>
            <#list spring.status.errorMessages as error>
                <li style="color:red;">${error}</li>
            </#list>
        </ul>
    </div>
    <#else>
    <div class="errors">
        &lt;#&ndash;There are no errors.&ndash;&gt;
    </div>
    </#if>-->

<div class="container">
    <fieldset>
        <form id="user" name="login_form" class="form-horizontal" role="form" action="${rc.contextPath}/user/register" method="POST">
            <h3 class="text-muted">User Registration Form</h3>
            <br/>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Username</label>
                <#--<@spring.bind "user.username" />-->
                <div class="col-sm-4">
                    <#--<input type="text" name='username' class="form-control" id="inputEmail3" placeholder="Username" required>-->
                    <@spring.formInput  "user.username", "class='form-control' placeholder='username' required", "text" />
                    <@spring.showErrors "<br>" "color:red;"/>
                    <#--<#list spring.status.errorMessages as error> <b style="color:red;">${error}</b> <br> </#list>-->
                    <br>
                </div>
            </div>
            <div class="form-group">
                <label for="inputPassword3" class="col-sm-2 control-label">Password</label>
                <div class="col-sm-4">
                    <#--<input type="password" name='password' class="form-control" id="inputPassword3" placeholder="Password" required>-->
                    <@spring.formPasswordInput  "user.password", "class='form-control' placeholder='@124544kjkj' required"/>
                    <@spring.showErrors "<br>" "color:red;"/>
                </div>
            </div>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
                <div class="col-sm-4">
                    <input type="text" name='name' class="form-control" id="inputEmail3" placeholder="name" required>
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
                    <input type="text" name='telegramId' class="form-control" id="TelegramId" placeholder="TelegramId" required>
                </div>
            </div>
            <div class="form-group">
                <label for="phone" class="col-sm-2 control-label">Phone #</label>
                <div class="col-sm-4">
                    <input type="text" name='phone' class="form-control" id="phone" placeholder="Phone Number" required>
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
                    <a class="btn btn-link" type="button" href="${rc.contextPath}/user/login">Login</a>
                </div>
            </div>
        </form>
    </fieldset>



    <#if spring.status.error>
        <ul>
            <#list spring.status.errors.globalErrors as error>
                <li>${error.defaultMessage}</li>
            </#list>
        </ul>
    </#if>

</div>
</body>
</html>
</#escape>