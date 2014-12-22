<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).parent().parent().parent().remove();
        });
    });
</script>

<div class="well">
    <fieldset>
        <legend>Edit TaskStep - ${model.stepData.description!?string}</legend>
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
                <label for="taskSequence" class="col-lg-2 control-label">Sequence</label>

                <div class="col-lg-10">
                    <input id="taskSequence" type="text" class="form-control" name="sequence" value="${model.stepData.sequence}">
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