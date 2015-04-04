<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            $('#agent_row_'+${model.agent.id!?string}).removeClass('alert-warning');
//            $('#spane_edit_agent').empty();
//            e.preventDefault();
//            e.stopPropagation();
//            $(this).parent().parent().parent().remove();
        });
    });
</script>


    <fieldset>
        <form name="user" action="${rc.contextPath}/server/editAgent/${model.agent.id!?string}" method="post">
            <div class="modal-body">
            <input type="hidden" class="form-control" placeholder="ID" name="id" value="${model.agent.id!?string}" readonly>
            <div class="form-group">
                <label for="exampleInputEmail1">Agent Name</label>
                <input type="text" class="form-control" placeholder="Agent Name" name="name" value="${model.agent.name!?string}">
            </div>

            <div class="form-group">
                <label for="agentDescriptionInput">Agent Description</label>
                <input id="agentDescriptionInput" type="text" class="form-control input" placeholder="Description" name="description" value="${model.agent.description!?string}">
            </div>

            <div class="form-group">
                <label for="agentBaseUrlInput">Agent Base Url</label>
                <input id="agentBaseUrlInput" type="text" class="form-control input" placeholder="Base Url e.g. http://localhost:9291/" name="baseUrl" value="${model.agent.baseUrl!?string}">
            </div>
            <div class="form-group">
                <label for="agentPropertiesTextArea">Agent Properties</label>
                    <textarea id="agentPropertiesTextArea" type="text" class="form-control input-sm" placeholder="Agent properties to override" name="agentProperties.properties"
                              rows="5">${(model.agent.agentProperties.properties)!?string}</textarea>
            </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default cancel" id="cancel" data-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary" id="save">Save</button>
            </div>
        </form>
    </fieldset>
</#escape>