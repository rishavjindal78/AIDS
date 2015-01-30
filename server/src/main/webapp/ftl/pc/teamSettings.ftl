<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="settings">
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

    </script>
    <div class="heading btn-link">Team Properties - ${model.team.name!?string}</div>
    <div class="content">
        <fieldset>
            <form class="form-horizontal" name="upload"
                  action="${rc.contextPath}/server/team/${Session['SELECTED_TEAM'].id}/settings"
                  method="post"
                  enctype="multipart/form-data">
                <div class="form-group">
                    <label for="documentDescriptionId" class="col-sm-2 control-label">Name</label>

                    <div class="col-sm-6">
                        <input id="documentDescriptionId" type="text" name="name" placeholder="name"
                               class="form-control" value="${model.team.name!?string}"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="documentDescriptionId" class="col-sm-2 control-label">Description</label>

                    <div class="col-sm-6">
                        <input id="documentDescriptionId" type="text" name="description" placeholder="Description"
                               class="form-control" value="${model.team.description!?string}"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="documentTagsId" class="col-sm-2 control-label">TelegramId</label>

                    <div class="col-sm-6">
                        <input id="documentTagsId" type="text" name="telegramId" placeholder="TelegramId"
                               class="form-control"
                               value="${(model.team.telegramId)!?string}"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="documentFileId" class="col-sm-2 control-label">Properties</label>

                    <div class="col-sm-6">
                        <textarea id="agentPropertiesTextArea" type="text" class="form-control input-sm"
                                  placeholder="Agent properties to override" name="teamProperties.properties"
                                  rows="10">${(model.team.teamProperties.properties)!?string}</textarea>

                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"></label>
                    <input type="submit" class="btn btn-primary" value="Update"/>
                </div>
            </form>
        </fieldset>
    </div>

    <div class="heading btn-link">User Properties - ${model.user.name!}</div>
    <div class="content">
        <fieldset>
            <form name="login_form" class="form-horizontal" role="form" action="${rc.contextPath}/user/update"
                  method="POST">
                <input type="hidden" name='id' class="form-control" id="userId" value="${model.user.id?string}">

                <div class="form-group">
                    <label for="inputEmail3" class="col-sm-2 control-label">Username</label>
                    <div class="col-sm-4">
                        <input type="text" name='username' class="form-control" id="inputEmail3" placeholder="Username"
                               value="${model.user.username?string}">
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
                        <input type="text" name='name' class="form-control" id="inputEmail3" placeholder="name"
                               value="${model.user.name!?string}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="inputEmail3" class="col-sm-2 control-label">Email</label>
                    <div class="col-sm-4">
                        <input type="email" name='email' class="form-control" id="inputEmail3" placeholder="Email"
                               value="${model.user.email!?string}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="TelegramId" class="col-sm-2 control-label">TelegramId</label>
                    <div class="col-sm-4">
                        <input type="text" name='telegramId' class="form-control" id="TelegramId"
                               placeholder="TelegramId" value="${model.user.telegramId!?string}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="phone" class="col-sm-2 control-label">Phone #</label>
                    <div class="col-sm-4">
                        <input type="text" name='phone' class="form-control" id="phone" placeholder="Phone Number"
                               value="${model.user.phone!?string}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="documentFileId" class="col-sm-2 control-label">Properties</label>
                    <div class="col-sm-6">
                        <textarea id="userPropertiesTextArea" type="text" class="form-control input-sm"
                                  placeholder="User properties to override" name="userProperties.properties"
                                  rows="10">${(model.user.userProperties.properties)!?string}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" name="enabled"
                                       <#if model.user.enabled?string == 'true'>checked</#if>> Enable me
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
    </div>
    </@com.page>
</#escape>