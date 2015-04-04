<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        /*  $(".cancel").click(function (e) {
              e.preventDefault();
              e.stopPropagation();
              $(this).parent().parent().parent().remove();
          });*/

        /*  $('.datetimepicker').datepicker({
              format: 'dd/mm/yyyy'
          });*/
    });
</script>

<div>
    <fieldset>
        <form class="form-horizontal" name="agent" action="${rc.contextPath}/server/addAgent/${model['task.id']?string}"
              method="post">
            <div class="modal-body">
                <div class="form-group">
                    <div class="col-lg-6">
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
                    <div class="control-group">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary" id="addAgentSubmitButton">Save</button>
                    </div>
                </div>
        </form>
    </fieldset>
</div>
</#escape>