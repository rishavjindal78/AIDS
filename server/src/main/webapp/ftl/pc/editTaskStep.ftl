<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            $('#taskstep_row_'+${model.stepData.id!?string}).removeClass('alert-warning');
            $('#span_edit').empty();
            e.preventDefault();
            e.stopPropagation();
//            $(this).parent().parent().parent().remove();
        });
    });
</script>
<style type="text/css">
    input:required:invalid, input:focus:invalid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAeVJREFUeNqkU01oE1EQ/mazSTdRmqSxLVSJVKU9RYoHD8WfHr16kh5EFA8eSy6hXrwUPBSKZ6E9V1CU4tGf0DZWDEQrGkhprRDbCvlpavan3ezu+LLSUnADLZnHwHvzmJlvvpkhZkY7IqFNaTuAfPhhP/8Uo87SGSaDsP27hgYM/lUpy6lHdqsAtM+BPfvqKp3ufYKwcgmWCug6oKmrrG3PoaqngWjdd/922hOBs5C/jJA6x7AiUt8VYVUAVQXXShfIqCYRMZO8/N1N+B8H1sOUwivpSUSVCJ2MAjtVwBAIdv+AQkHQqbOgc+fBvorjyQENDcch16/BtkQdAlC4E6jrYHGgGU18Io3gmhzJuwub6/fQJYNi/YBpCifhbDaAPXFvCBVxXbvfbNGFeN8DkjogWAd8DljV3KRutcEAeHMN/HXZ4p9bhncJHCyhNx52R0Kv/XNuQvYBnM+CP7xddXL5KaJw0TMAF8qjnMvegeK/SLHubhpKDKIrJDlvXoMX3y9xcSMZyBQ+tpyk5hzsa2Ns7LGdfWdbL6fZvHn92d7dgROH/730YBLtiZmEdGPkFnhX4kxmjVe2xgPfCtrRd6GHRtEh9zsL8xVe+pwSzj+OtwvletZZ/wLeKD71L+ZeHHWZ/gowABkp7AwwnEjFAAAAAElFTkSuQmCC); background-position: right top; background-repeat: no-repeat; -moz-box-shadow: none; }
    input:required:valid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=); background-position: right top; background-repeat: no-repeat; }
</style>

<div class="alert alert-warning">
    <fieldset>
        <legend class="text-muted">${model.stepData.sequence}. Edit TaskStep - ${model.stepData.description!?string}</legend>
        <form id="editTaskStepData" class="form-horizontal" name="taskStepDTO" modelAttribute="taskStepDTO"
              action="../addTaskStep/${model.stepData.id}" method="post">
            <div class="form-group">
                <label for="taskDataId" class="col-lg-2 control-label">TaskData Id</label>

                <div class="col-lg-10">
                    <input type="text" id="taskDataId" class="form-control" name="taskId" value="${model.stepData.task.id}"
                           readonly="true">
                </div>
            </div>

            <div class="form-group">
                <label for="classNameId" class="col-lg-2 control-label">ClassName</label>

                <div class="col-lg-10">
                    <input id="classNameId" type="text" readonly="true" class="form-control" name="className"
                           value="${model.stepData.taskClass}">
                </div>
            </div>

            <div class="form-group">
                <label for="taskSequence" class="col-lg-2 control-label">Execution #</label>

                <div class="col-lg-10">
                    <input id="taskSequence" type="text" class="form-control" name="sequence" value="${model.stepData.sequence}" required pattern="\d+">
                </div>
            </div>

            <div class="form-group">
                <label for="taskDescriptionId" class="col-lg-2 control-label">Description</label>

                <div class="col-lg-10">
                    <input id="taskDescriptionId" type="text" class="form-control" name="description" value="${model.stepData.description!?string}">
                </div>
            </div>

            <div class="form-group">
                <table name="inputParamsMap" class="table">
                    <#list model["inputParams"] as fieldProperties>
                        <tr>
                            <td hidden="true">${fieldProperties.displayName!?string}</td>
                            <td>
                                <#if fieldProperties.type == 'textarea'>
                                    <label for="inputParamsMap['${fieldProperties.name?string}']">${fieldProperties.displayName!fieldProperties.name!?string}</label>
                                    <textarea class="form-control" id="inputParamsMap['${fieldProperties.name?string}']" name="inputParamsMap['${fieldProperties.name?string}']" placeholder="Enter text ..." rows="10">${fieldProperties.value?string}</textarea>
                                <#elseif fieldProperties.type == 'date'>

                                 <#else>
                                    <label for="inputParamsMap['${fieldProperties.name?string}']">${fieldProperties.displayName!fieldProperties.name!?string}</label>
                                    <input type="text" id="inputParamsMap['${fieldProperties.name?string}']" class="form-control" name="inputParamsMap['${fieldProperties.name?string}']" value="${fieldProperties.value?string}"/>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                </table>
            </div>
<#--<hr>-->
            <div class="form-group">
                <table name="outputParamsMap" class="table">
                    <#list model["outputParams"] as fieldProperties>
                        <tr>
                            <td hidden="true">${fieldProperties.displayName!?string}</td>
                            <td>
                                <label for="outputParamsMap['${fieldProperties.name?string}']">${fieldProperties.name!?string}</label>
                                <input id="outputParamsMap['${fieldProperties.name?string}']" type="text" class="form-control" name="outputParamsMap['${fieldProperties.name?string}']"
                                       value="${fieldProperties.value?string}"/>
                            </td>
                        </tr>
                    </#list>
                </table>
            </div>
            <div class="control-group">
                <button type="submit" class="btn btn-primary" id="save">Save</button>
                <button class="btn cancel btn-default" id="cancel">Cancel</button>
            </div>
        </form>
    </fieldset>
</div>
</#escape>