<#--<#import "/spring.ftl" as spring />-->
<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        /*$(".cancel").click(function (e) {
            $('#span_task_agent').empty();
            e.preventDefault();
            e.stopPropagation();
//            $(this).parent().parent().parent().remove();
        });

        $('#addAgentSubmitButton').click(function(e){
            e.preventDefault();
            alert("submit form");

            $.post('http://path/to/post',
                    $('#addAgentForm').serialize(),
                    function(data, status, xhr){
                        // do something here with response;
                    });

        });*/
    });
</script>
    <fieldset>
        <form id="addAgentForm" class="form-horizontal" name="multiAgentVO"
              action="${rc.contextPath}/server/taskStep/addAgent/${model['taskStepId']?string}" method="post">
            <div class="modal-body">
            <div class="form-group">
                <div class="col-lg-6">
                <#--<@spring.formMultiSelect path="agents" options="" attributes=""/>-->
                <#--<#assign selectedLangs = spring.status.value?default(" ")>-->
                    <label for="agentList">Select Agent(s)</label>
                    <select id="agentList" class="form-control" name="agents" multiple="true">
                        <#list model["agents"] as agent>
                            <#if model.selectedAgents?seq_contains(agent) >
                                <#assign isSelected = true>
                            <#else>
                                <#assign isSelected = false>
                            </#if>
                            <option value="${agent.id}"<#if isSelected> selected="selected"</#if>>${agent.name}</option>
                        <#--<option value="${value?html}"<#if isSelected> selected="selected"</#if>>${languages[value]?html}-->
                        </#list>
                    </select>
                </div>
            </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary" id="addAgentSubmitButton">Save</button>
            </div>
        </form>
    </fieldset>

</#escape>