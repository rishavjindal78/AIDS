<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            $('#spane_edit_agent').empty();
            e.preventDefault();
            e.stopPropagation();
//            $(this).parent().parent().parent().remove();
        });
    });
</script>

<div class="alert alert-warning">
    <fieldset>
        <legend class="text-muted">Edit Agent - ${model.agent.name!?string}</legend>
        <form class="form-horizontal" name="user" action="${rc.contextPath}/server/editAgent/${model.agent.id!?string}" method="post">
            <div class="form-group">
                <div class="col-lg-1">
                    <input type="text" class="form-control" placeholder="ID" name="id" value="${model.agent.id!?string}" readonly>
                </div>
                <div class="col-lg-2">
                    <input type="text" class="form-control" placeholder="Name" name="name" value="${model.agent.name!?string}">
                </div>
                <div class="col-lg-3">
                    <input type="text" class="form-control input" placeholder="Description" name="description"
                           value="${model.agent.description!?string}">
                </div>
                <div class="col-lg-3">
                    <input type="text" class="form-control input" placeholder="Base Url e.g. http://localhost:9291/" name="baseUrl"
                           value="${model.agent.baseUrl!?string}">
                </div>
                <div class="col-lg-6">
                    <#--<label for="agentPropertiesTextArea">Agent Properties</label>-->
                    <textarea id="agentPropertiesTextArea" type="text" class="form-control input-sm" placeholder="Agent properties to override" name="agentProperties.properties"
                           rows="5">${(model.agent.agentProperties.properties)!?string}</textarea>
                </div>
                <div class="control-group">
                    <button type="submit" class="btn btn-primary" id="save">Save</button>
                    <button class="btn cancel btn-default" id="cancel">Cancel</button>
                </div>
            </div>
        </form>
    </fieldset>
</div>
</#escape>