<!DOCTYPE html>
<#assign security=JspTaglibs["/WEB-INF/tlds/security.tld"] />
<html>
<#escape x as x?html>
<body>
This is PC version
    <#if Session["SPRING_SECURITY_CONTEXT"]?exists>
    <ul>
        <li>${Session["SPRING_SECURITY_CONTEXT"].authentication.name}</li>
        <li>
            <#assign authorities = Session["SPRING_SECURITY_CONTEXT"].authentication.authorities />
			<#list authorities as authority>
        ${authority}
        </#list>
        </li>
    </ul>
        <@security.authentication property="name" ></@security.authentication>
        <@security.authorize ifAnyGranted="MTXTU-TestPerm,ROLE_USER"> Welcome to Runtime.</@security.authorize>
    </#if>
</#escape>
</body>
</html>