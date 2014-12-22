<#import "/spring.ftl" as spring />
<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="home">
    <style>
        #inner {
            color: red;
            background-color: #000000;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $(".content").hide();
//            $(".content").css("display", "none");
            $(".heading").click(function () {
                $(this).next(".content").slideToggle(200);
            });
        });

        function addAgent(id) {
            $.get('/admin/team/' + id + '/addUser', function (data) {
                $('#span_task_agent').empty();
                $('#span_task_agent').html(data);
            });
        }
    </script>
    <fieldset>
        <form name="login_form" class="form-horizontal" role="form" action="${rc.contextPath}/user/update"
              method="POST">
            <h3 class="text-muted">Profile Details - ${model.user.name!}</h3>
            <br/>

            <input type="hidden" name='id' class="form-control" id="userId" value="${model.user.id?string}">

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Username</label>

                <div class="col-sm-4">
                    <input type="text" name='username' class="form-control" id="inputEmail3" placeholder="Username" value="${model.user.username?string}">
                </div>
            </div>
            <div class="form-group">
                <label for="inputPassword3" class="col-sm-2 control-label">Password</label>

                <div class="col-sm-4">
                    <input type="password" name='password' class="form-control" id="inputPassword3"
                           placeholder="Password" value="${model.user.password?string}">
                </div>
            </div>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Name</label>

                <div class="col-sm-4">
                    <input type="text" name='name' class="form-control" id="inputEmail3" placeholder="name" value="${model.user.name!?string}">
                </div>
            </div>
            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Email</label>

                <div class="col-sm-4">
                    <input type="email" name='email' class="form-control" id="inputEmail3" placeholder="Email" value="${model.user.email!?string}">
                </div>
            </div>
            <div class="form-group">
                <label for="TelegramId" class="col-sm-2 control-label">TelegramId</label>

                <div class="col-sm-4">
                    <input type="text" name='telegramId' class="form-control" id="TelegramId" placeholder="TelegramId" value="${model.user.telegramId!?string}">
                </div>
            </div>
            <div class="form-group">
                <label for="phone" class="col-sm-2 control-label">Phone #</label>

                <div class="col-sm-4">
                    <input type="text" name='phone' class="form-control" id="phone" placeholder="Phone Number" value="${model.user.phone!?string}">
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="enabled" <#if model.user.enabled?string == 'true'>checked</#if>> Enable me
                        </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" name="submit" class="btn btn-primary">Update</button>
                </div>
            </div>
        </form>
    </fieldset>

        <#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
        <div><span style='color:red;'>
            Your login attempt was not successful, try again.<br/>
            Reason :   ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
        </span>
        </div>
        </#if>
    <span id="spane_edit_agent"></span>
    </@com.page>
</#escape>