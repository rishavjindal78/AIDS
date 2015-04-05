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
<style type="text/css">
    input:required:invalid, input:focus:invalid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAeVJREFUeNqkU01oE1EQ/mazSTdRmqSxLVSJVKU9RYoHD8WfHr16kh5EFA8eSy6hXrwUPBSKZ6E9V1CU4tGf0DZWDEQrGkhprRDbCvlpavan3ezu+LLSUnADLZnHwHvzmJlvvpkhZkY7IqFNaTuAfPhhP/8Uo87SGSaDsP27hgYM/lUpy6lHdqsAtM+BPfvqKp3ufYKwcgmWCug6oKmrrG3PoaqngWjdd/922hOBs5C/jJA6x7AiUt8VYVUAVQXXShfIqCYRMZO8/N1N+B8H1sOUwivpSUSVCJ2MAjtVwBAIdv+AQkHQqbOgc+fBvorjyQENDcch16/BtkQdAlC4E6jrYHGgGU18Io3gmhzJuwub6/fQJYNi/YBpCifhbDaAPXFvCBVxXbvfbNGFeN8DkjogWAd8DljV3KRutcEAeHMN/HXZ4p9bhncJHCyhNx52R0Kv/XNuQvYBnM+CP7xddXL5KaJw0TMAF8qjnMvegeK/SLHubhpKDKIrJDlvXoMX3y9xcSMZyBQ+tpyk5hzsa2Ns7LGdfWdbL6fZvHn92d7dgROH/730YBLtiZmEdGPkFnhX4kxmjVe2xgPfCtrRd6GHRtEh9zsL8xVe+pwSzj+OtwvletZZ/wLeKD71L+ZeHHWZ/gowABkp7AwwnEjFAAAAAElFTkSuQmCC); background-position: right top; background-repeat: no-repeat; -moz-box-shadow: none; }
    input:required:valid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=); background-position: right top; background-repeat: no-repeat; }
</style>

    <fieldset>
        <form name="user" action="${rc.contextPath}/server/editAgent/${model.agent.id!?string}" method="post">
            <div class="modal-body">
            <input type="hidden" class="form-control" placeholder="ID" name="id" value="${model.agent.id!?string}" readonly>
            <div class="form-group">
                <label for="exampleInputEmail1">Agent Name</label>
                <input type="text" class="form-control" placeholder="Agent Name" name="name" value="${model.agent.name!?string}" required>
            </div>

            <div class="form-group">
                <label for="agentDescriptionInput">Agent Description</label>
                <input id="agentDescriptionInput" type="text" class="form-control input" placeholder="Description" name="description" value="${model.agent.description!?string}">
            </div>

            <div class="form-group">
                <label for="agentBaseUrlInput">Agent Base Url</label>
                <input id="agentBaseUrlInput" type="text" class="form-control input" placeholder="Base Url e.g. http://localhost:9291/" name="baseUrl" value="${model.agent.baseUrl!?string}" required pattern="https?://.+">
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