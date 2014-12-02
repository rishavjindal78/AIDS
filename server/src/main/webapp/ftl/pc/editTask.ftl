<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).parent().parent().parent().remove();
        });

        /*$('.datetimepicker').datepicker({
            format: 'dd/mm/yyyy'
        });*/
    });
</script>

<div>
    <fieldset>
        <form class="form-horizontal" name="user" action="/rest/server/team/${model.team.id}/addTask/${model.task.id!?string}" method="post">
            <div class="form-group">
            <#--<label class="col-lg-2 control-label">Amount</label>-->
                <div class="col-lg-2">
                    <input type="text" class="form-control" placeholder="ID" name="id" value="${model.task.id!?string}" readonly>
                </div>
                <div class="col-lg-2">
                    <input type="text" class="form-control" placeholder="Name" name="name" value="${model.task.name!?string}">
                </div>
                <div class="col-lg-4">
                    <input type="text" class="form-control input" placeholder="Description" name="description"
                           value="${model.task.description!?string}">
                </div>
                <div class="col-lg-2">
                    <input type="text" class="form-control input" placeholder="Tags" name="tags"
                           value="${model.task.tags!?string}">
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
</div>
</#escape>