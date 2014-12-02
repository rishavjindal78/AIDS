<#import "/spring.ftl" as spring />
<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).parent().parent().remove();
        });
    });
</script>

<div class="content">
    <form role="form" name="team" action='<@spring.url "/rest/admin/team/update" />' method="POST">
        <input type="hidden" class="form-control" id="teamId" name="id" value="${model.team.id!?string}">
        <div class="form-group">
            <label for="exampleInputEmail1">Team Name</label>
            <input type="text" class="form-control" id="exampleInputEmail1" name="name"
                   placeholder="Enter Team Name e.g. Product Team" value="${model.team.name!?string}">
        </div>
        <div class="form-group">
            <label for="exampleInputPassword1">Team Description</label>
            <input type="text" class="form-control" id="exampleInputPassword1" name="description"
                   placeholder="Enter Team Description e.g. Product Team Description" value="${model.team.description!?string}">
        </div>
        <div class="form-group">
            <label for="exampleInputTags">Telegram ID</label>
            <input type="text" class="form-control" id="exampleInputTags" name="telegramId"
                   placeholder="Enter Telegram Group Chat ID" value="${model.team.telegramId!?string}">
        </div>
        <div class="form-group">
            <label for="exampleInputTags">Email ID</label>
            <input type="email" class="form-control" id="exampleInputTags" name="email"
                   placeholder="Enter Email for the Team" value="${model.team.email!?string}">
        </div>
        <#--<div class="checkbox">
            <label>
                <input type="checkbox"> Enable Me
            </label>
        </div>-->
        <button type="submit" class="btn btn-primary" id="save">Save</button>
        <button class="btn cancel btn-default" id="cancel">Cancel</button>
    </form>
</div>
</#escape>