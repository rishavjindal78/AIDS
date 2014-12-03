<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page activeTab="resources">
    <script type="text/javascript">
        $(document).ready(function () {
            /*onchange="this.form.submit()"*/
            $("#taskClassSelect").change(function () {
                window.location = '/rest/server/addTaskStep/${model.taskId}?taskClass=' + this.value;
            });
        });
    </script>

    <#--  <div class="well">
          <fieldset>
              <legend>Search TaskStep</legend>
              <form id="user" class="form-horizontal" name="resource" modelAttribute="resource" action="search" method="post">
                      <input type="text" class="input-xxlarge" placeholder="Type something to search.." name="search"">
                      <button type="submit" class="btn" id="save">Search</button>
              </form>
          </fieldset>
      </div>-->
    <div class="well">
        <fieldset>
            <legend>Add TaskStep</legend>
            <form id="user" class="form-horizontal" name="taskStepDTO" modelAttribute="taskStepDTO"
                  action="../addTaskStep/0" method="post">
                <div class="row">
                    <div class="col-lg-2">
                        <input type="text" class="form-control" name="taskId" value="${model.taskId}"
                               readonly="true">
                    </div>
                    <div class="col-lg-3">
                        <select id="taskClassSelect" class="form-control" name="taskClass">
                            <#list model["taskClasses"] as taskClass>
                                <option value="${taskClass}"
                                        <#if taskClass == model.selectedClass >selected="selected"</#if>>${taskClass}</option>
                            </#list>
                        </select>
                    </div>
                    <div class="col-lg-2">
                        <input type="text" class="form-control" name="sequence" placeholder="sequence #">
                    </div>
                    <div class="col-lg-5">
                        <input type="text" class="form-control" placeholder="description" name="description"">
                    </div>
                </div>
                <div class="form-group">
                    <table name="inputParamsMap" class="table">
                        <#list model["inputParams"] as fieldProperties>
                            <tr>
                            <#--<td><label for="input${fieldProperties.displayName?string}" class="col-lg-3 control-label">${fieldProperties.displayName?string}</label></td>-->
                                <td hidden="true">${fieldProperties.displayName?string}</td>
                                <td>
                                    <#if fieldProperties.type == 'textarea'>
                                        <label for="input${fieldProperties.displayName?string}">${fieldProperties.displayName?string}</label>
                                        <textarea id="input${fieldProperties.displayName?string}" class="form-control"
                                               name="inputParamsMap['${fieldProperties.name?string}']"
                                               placeholder="${fieldProperties.name?string}" rows="10"/></textarea>
                                    <#elseif fieldProperties.type == 'date'>

                                    <#else>
                                        <label for="input${fieldProperties.displayName?string}">${fieldProperties.displayName?string}</label>
                                        <input id="input${fieldProperties.displayName?string}" class="form-control" type="text" name="inputParamsMap['${fieldProperties.name?string}']" placeholder="${fieldProperties.name?string}"/>
                                    </#if>
                                </td>
                            </tr>
                        </#list>
                    </table>
                </div>

                <div class="form-group">
                    <table name="outputParamsMap" class="table">
                        <#list model["outputParams"] as fieldProperties>
                            <tr>
                                <td hidden="">${fieldProperties.displayName?string}</td>
                                <td><label
                                        for="outputParamsMap['${fieldProperties.name?string}']">${fieldProperties.displayName?string}</label><input
                                        type="text" class="form-control"
                                        name="outputParamsMap['${fieldProperties.name?string}']"
                                        placeholder="${fieldProperties.name?string}"/></td>
                            </tr>
                        </#list>
                    </table>
                </div>

                <div class="control-group">
                    <button type="submit" class="btn btn-primary" id="save">Save</button>
                    <a href="${model.referer!''}" class="btn btn-info">Cancel</a>
                </div>
            </form>
        </fieldset>
    </div>
    </@com.page>
</#escape>