<#--<#import "/spring.ftl" as spring />-->
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

<div>
    <fieldset>
        <form class="form-horizontal" name="multiAgentVO" action="${rc.contextPath}/server/taskStep/addAgent/${model['taskStepId']?string}" method="post">
            <div class="form-group">
                <div class="col-lg-2">
                    <#--<@spring.formMultiSelect path="agents" options="" attributes=""/>-->
                    <select id="agent" class="form-control" name="agents" multiple="true">
                        <#list model["agents"] as agent>
                            <option value="${agent.id}">${agent.name}</option>
                        </#list>
                    </select>
                </div>
                <div class="control-group">
                    <button type="submit" class="btn btn-primary" id="save">Save</button>
                    <button type="reset" class="btn cancel btn-default" id="cancel">Cancel</button>
                </div>
            </div>
        </form>
    </fieldset>
</div>
</#escape>