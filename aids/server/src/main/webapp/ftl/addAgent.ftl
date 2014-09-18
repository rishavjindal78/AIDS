<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).parent().parent().parent().remove();
        });

      /*  $('.datetimepicker').datepicker({
            format: 'dd/mm/yyyy'
        });*/
    });
</script>

<div>
    <fieldset>
        <form class="form-horizontal" name="agent" action="/rest/server/addAgent/${model['task.id']?string}" method="post">
            <div class="form-group">
                <div class="col-lg-2">
                    <select id="agent" class="form-control" name="id">
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