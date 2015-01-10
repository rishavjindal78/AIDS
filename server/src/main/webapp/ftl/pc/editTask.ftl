<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            $("#span_task_edit").empty();
//            $("#serverResults").empty();
            e.preventDefault();
            e.stopPropagation();
//            $(this).parent().parent().parent().parent().remove();
        });
    });

    function fetchPredictions(id) {
        $("#serverResults").empty();
        $.get('${rc.getContextPath()}/server/schedule', {cronString: $('#' + id).val()}, function (data) {
            var schedules = '';
            $.each(data, function (i, item) {
                schedules += '<li>' + item + '</li>';
            });
            $("#serverResults").html('<div class="alert alert-info">Next 10 Scheduled Dates  - <ul>' + schedules + '</ul></div>');
        });
    }
</script>

<div class="alert alert-warning">
    <fieldset>
        <legend class="text-muted">Edit Task - ${model.task.name!?string}</legend>
        <form class="form-horizontal" name="user"
              action="${rc.contextPath}/server/team/${model.team.id}/addTask/${model.task.id!?string}" method="post">
            <div class="form-group">
            <#--<label class="col-lg-2 control-label">Amount</label>-->
                <div class="col-lg-2">
                    <input type="text" class="form-control" placeholder="ID" name="id" value="${model.task.id!?string}"
                           readonly>
                </div>
                <div class="col-lg-2">
                    <input type="text" class="form-control" placeholder="Name" name="name"
                           value="${model.task.name!?string}">
                </div>
                <div class="col-lg-4">
                    <input type="text" class="form-control input" placeholder="Description" name="description"
                           value="${model.task.description!?string}">
                </div>
                <div class="col-lg-2">
                    <input type="text" class="form-control input" placeholder="Tags" name="tags"
                           value="${model.task.tags!?string}">
                </div>
                <div class="col-lg-6">
                <#--<label for="agentPropertiesTextArea">Agent Properties</label>-->
                    <textarea id="agentPropertiesTextArea" type="text" class="form-control input-sm"
                              placeholder="Agent properties to override" name="taskProperties.properties"
                              rows="5">${(model.task.taskProperties.properties)!?string}</textarea>
                </div>
                <div class="col-lg-4">
                    <input type="text" id="${model.task.id}_schedule" class="form-control input"
                           placeholder="Cron Trigger for Schedule" name="schedule"
                           value="${model.task.schedule!?string}">
                    <a class="btn-link" onclick="fetchPredictions('${model.task.id}_schedule')">Predictions</a>
                    <a target="_blank"
                       href="http://quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06">Tutorial-1</a>
                    <a target="_blank"
                       href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html">Tutorial-2</a>
                    <a target="_blank" href="http://quartz-scheduler.org/api/2.2.0/org/quartz/CronTrigger.html">Tutorial-3</a>
                    <a target="_blank" href="http://www.cronmaker.com/">Tutorial-4</a>
                </div>
                <div class="control-group">
                    <button type="submit" class="btn btn-primary" id="save">Save</button>
                    <button class="btn cancel btn-default" id="cancel">Cancel</button>
                </div>
            <#--<div class="col-lg-2">
                <select class="form-control" name="transactionType">
                    <option value="PAY"  <#if 'PAY' == model.expense.transactionType?string?upper_case >selected="selected"</#if>>PAY
                    </option>
                    <option value="RECEIVE"
                            <#if 'RECEIVE' == model.expense.transactionType?string?upper_case >selected="selected"</#if>>
                        RECEIVE
                    </option>
                </select>
            </div>
            <div class="col-lg-3">
                <select id="username" class="form-control" name="user.id">
                    <#list model["users"] as user>
                        <option value="${user.id}"
                                <#if user.id == model.expense.user.id >selected="selected"</#if>>${user.firstName} ${user.lastName}</option>
                    </#list>
                </select>
            </div>-->
            </div>
        <#--<div class="form-group">
            <div class="col-lg-2">
                <select id="account" class="form-control" name="account.id">
                    <#list model["accounts"] as account>
                        <option value="${account.id}"
                                <#if account.id == model.expense.account.id >selected="selected"</#if>>${account.name}</option>
                    </#list>
                </select>
            </div>
            <div class="col-lg-2">
                <input type="text" name="settlementDate" class="form-control datetimepicker"
                       value="<#if model.expense.settlementDate??>${model.expense.settlementDate?date?string("dd/MM/yyyy")!''} <#else>${.now?date?string("dd/MM/yyyy")!''}</#if>">
            </div>

        </div>-->
        </form>
    </fieldset>
    <span id="serverResults"></span>
</div>
</#escape>