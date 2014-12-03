<#escape x as x?html>
    <style type="text/css">
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            /*background-color: #f5f5f5;*/
        }

        .form-signin {
            max-width: 300px;
            padding: 19px 29px 29px;
            margin: 0 auto 20px;
            background-color: #fff;
            border: 1px solid #e5e5e5;
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            border-radius: 5px;
            -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
            -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
            box-shadow: 0 1px 2px rgba(0,0,0,.05);
        }
        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            margin-bottom: 10px;
        }
        .form-signin input[type="text"],
        .form-signin input[type="password"] {
            font-size: 16px;
            height: auto;
            margin-bottom: 15px;
            padding: 7px 9px;
        }

    </style>

    <form class="form-signin" name="agent" action="${rc.getContextPath()}/server/register" method="POST">
        <h2 class="form-signin-heading">Please Register Agent Here</h2>
        <input type="text" class="input-block-level" name="name" placeholder="Name">
        <input type="text" class="input-block-level" name="description" placeholder="Description">
        <input type="text" class="input-block-level" name="baseUrl" placeholder="Base URL">
        <button class="btn btn-large btn-primary" type="submit">Register</button>
        <button class="btn btn-default" type="reset">Reset</button>
    </form>

    <table class="table table-striped table-condensed">
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Description</th>
            <th>Base URL</th>
            <#--<th>Comments</th>-->
        </tr>
        <#list model["agents"] as agent>
            <tr>
                <td>${agent_index+1}</td>
                <td>${agent.name?string}</td>
                <td>${agent.description?string}</td>
                <td>${agent.baseUrl?string}</td>
                <#--<td>${debt.comments?size}</td>-->
            </tr>
        </#list>
    </table>
</#escape>