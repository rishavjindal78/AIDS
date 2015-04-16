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

    <style type="text/css">
        input:required:invalid, input:focus:invalid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAeVJREFUeNqkU01oE1EQ/mazSTdRmqSxLVSJVKU9RYoHD8WfHr16kh5EFA8eSy6hXrwUPBSKZ6E9V1CU4tGf0DZWDEQrGkhprRDbCvlpavan3ezu+LLSUnADLZnHwHvzmJlvvpkhZkY7IqFNaTuAfPhhP/8Uo87SGSaDsP27hgYM/lUpy6lHdqsAtM+BPfvqKp3ufYKwcgmWCug6oKmrrG3PoaqngWjdd/922hOBs5C/jJA6x7AiUt8VYVUAVQXXShfIqCYRMZO8/N1N+B8H1sOUwivpSUSVCJ2MAjtVwBAIdv+AQkHQqbOgc+fBvorjyQENDcch16/BtkQdAlC4E6jrYHGgGU18Io3gmhzJuwub6/fQJYNi/YBpCifhbDaAPXFvCBVxXbvfbNGFeN8DkjogWAd8DljV3KRutcEAeHMN/HXZ4p9bhncJHCyhNx52R0Kv/XNuQvYBnM+CP7xddXL5KaJw0TMAF8qjnMvegeK/SLHubhpKDKIrJDlvXoMX3y9xcSMZyBQ+tpyk5hzsa2Ns7LGdfWdbL6fZvHn92d7dgROH/730YBLtiZmEdGPkFnhX4kxmjVe2xgPfCtrRd6GHRtEh9zsL8xVe+pwSzj+OtwvletZZ/wLeKD71L+ZeHHWZ/gowABkp7AwwnEjFAAAAAElFTkSuQmCC); background-position: right top; background-repeat: no-repeat; -moz-box-shadow: none; }
        input:required:valid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=); background-position: right top; background-repeat: no-repeat; }
    </style>
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
                    <@spring.formInput  "user.name", "type='text' class='form-control' placeholder='Full Name' required"/>
                    <@spring.showErrors "<br>" "color:red;"/>
                </div>
            </div>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Email</label>
                <div class="col-sm-4">
                    <@spring.formInput  "user.email", "class='form-control' placeholder='Email Address'", "email"/>
                    <@spring.showErrors "<br>" "color:red;"/>
                </div>
            </div>
            <div class="form-group">
                <label for="TelegramId" class="col-sm-2 control-label">TelegramId</label>
                <div class="col-sm-4">
                    <@spring.formInput  "user.telegramId", "class='form-control' placeholder='TelegramId' required"/>
                    <@spring.showErrors "<br>" "color:red;"/>
                </div>
            </div>
            <div class="form-group">
                <label for="phone" class="col-sm-2 control-label">Phone #</label>
                <div class="col-sm-4">
                    <@spring.formInput  "user.phone", "class='form-control' placeholder='Phone Number'"/>
                    <@spring.showErrors "<br>" "color:red;"/>
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