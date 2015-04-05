<#escape x as x?html>
<script type="text/javascript">
</script>
<style type="text/css">
    input:required:invalid, input:focus:invalid {
        background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAeVJREFUeNqkU01oE1EQ/mazSTdRmqSxLVSJVKU9RYoHD8WfHr16kh5EFA8eSy6hXrwUPBSKZ6E9V1CU4tGf0DZWDEQrGkhprRDbCvlpavan3ezu+LLSUnADLZnHwHvzmJlvvpkhZkY7IqFNaTuAfPhhP/8Uo87SGSaDsP27hgYM/lUpy6lHdqsAtM+BPfvqKp3ufYKwcgmWCug6oKmrrG3PoaqngWjdd/922hOBs5C/jJA6x7AiUt8VYVUAVQXXShfIqCYRMZO8/N1N+B8H1sOUwivpSUSVCJ2MAjtVwBAIdv+AQkHQqbOgc+fBvorjyQENDcch16/BtkQdAlC4E6jrYHGgGU18Io3gmhzJuwub6/fQJYNi/YBpCifhbDaAPXFvCBVxXbvfbNGFeN8DkjogWAd8DljV3KRutcEAeHMN/HXZ4p9bhncJHCyhNx52R0Kv/XNuQvYBnM+CP7xddXL5KaJw0TMAF8qjnMvegeK/SLHubhpKDKIrJDlvXoMX3y9xcSMZyBQ+tpyk5hzsa2Ns7LGdfWdbL6fZvHn92d7dgROH/730YBLtiZmEdGPkFnhX4kxmjVe2xgPfCtrRd6GHRtEh9zsL8xVe+pwSzj+OtwvletZZ/wLeKD71L+ZeHHWZ/gowABkp7AwwnEjFAAAAAElFTkSuQmCC);
        background-position: right top;
        background-repeat: no-repeat;
        -moz-box-shadow: none;
    }

    input:required:valid {
        background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=);
        background-position: right top;
        background-repeat: no-repeat;
    }
</style>

    <div>
        <fieldset>
            <form id="user" class="form-horizontal" name="taskStepDTO" modelAttribute="taskStepDTO"
                  action="../addTaskStep/0" method="post">
                <div class="modal-body">
                    <input type="hidden" class="form-control" name="taskId" value="${model.taskId}" readonly="true">
                    <input type="hidden" class="form-control" name="taskClass" value="${model.selectedClass}">
                    <div class="form-group">
                        <label for="inputSequenceNumber">Execution #</label>
                        <input id="inputSequenceNumber" type="text" class="form-control" name="sequence" placeholder="sequence #" required pattern="\d+">
                    </div>

                    <div class="form-group">
                        <label for="inputStepDescription">Step Description</label>
                        <input id="inputStepDescription" type="text" class="form-control" placeholder="description" name="description"">
                    </div>

                    <div class="form-group">
                        <label>Input Variables Group</label>
                        <table name="inputParamsMap" class="table table-striped table-condensed">
                            <#list model["inputParams"] as fieldProperties>
                                <tr>
                                <#--<td><label for="input${fieldProperties.displayName?string}" class="col-lg-3 control-label">${fieldProperties.displayName?string}</label></td>-->
                                    <td hidden="true">${fieldProperties.displayName?string}</td>
                                    <td>
                                        <#if fieldProperties.type == 'textarea'>
                                            <label for="input${fieldProperties.displayName?string}">${fieldProperties_index+1}. ${fieldProperties.displayName?string}</label>
                                            <textarea id="input${fieldProperties.displayName?string}" class="form-control"
                                                  name="inputParamsMap['${fieldProperties.name?string}']"
                                                  placeholder="${fieldProperties.name?string}" rows="10"/></textarea>
                                        <#elseif fieldProperties.type == 'date'>

                                        <#else>
                                            <label for="input${fieldProperties.displayName?string}">${fieldProperties_index+1}. ${fieldProperties.displayName?string}</label>
                                            <input id="input${fieldProperties.displayName?string}" class="form-control" type="${fieldProperties.type?string}" name="inputParamsMap['${fieldProperties.name?string}']" placeholder="${fieldProperties.name?string}"
                                            <#if fieldProperties.required?? && fieldProperties.required?string=="true">required</#if> />
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                        </table>
                    </div>

                    <div class="form-group">
                        <label>Output Variables Group</label>
                        <table name="outputParamsMap" class="table table-striped table-condensed">
                            <#list model["outputParams"] as fieldProperties>
                                <tr>
                                    <td hidden="">${fieldProperties.displayName?string}</td>
                                    <td>
                                        <label for="outputParamsMap['${fieldProperties.name?string}']">${fieldProperties_index+1}. ${fieldProperties.displayName?string}</label>
                                        <input type="text" class="form-control" name="outputParamsMap['${fieldProperties.name?string}']"
                                            placeholder="${fieldProperties.name?string}"/>
                                    </td>
                                </tr>
                            </#list>
                        </table>
                    </div>

                </div>
                <div class="modal-footer">
                    <div class="control-group">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary" id="addTaskStepSubmitButton">Save</button>
                    </div>
                </div>
            </form>
        </fieldset>
    </div>
</#escape>