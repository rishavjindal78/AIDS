<#import "/spring.ftl" as spring />
<!doctype html>
<html lang="en">
<head>
    <style>

    </style>
    <meta charset="UTF-8">
    <title>AIDS Help</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/navbar-fixed-top.css'/>"/>

    <script src="${rc.getContextPath()}/js/jquery-2.1.1.min.js"></script>
    <script src="<@spring.url '/bootstrap/js/bootstrap.min.js'/>"></script>
    <script src="${rc.getContextPath()}/js/marked.min.js"></script>

    <link rel="stylesheet" href="${rc.getContextPath()}/js/styles/default.css">
    <script src="${rc.getContextPath()}/js/highlight.pack.js"></script>
    <#--<script>hljs.initHighlightingOnLoad();</script>-->

    <meta name="author" content="aids">
    <meta http-equiv="Content-type" content="text/html; charset=${.output_encoding}">
</head>
<body>
<#escape x as x?html>
<script type="text/javascript">
//    var markdownString = '```js\n console.log("hello"); \n```';

    function doUpdate() {
        $.get('${rc.contextPath}/help/${model.helpFileName}', {}, function (helpTxt) {
//            $('#wikioutput1').html(marked(markdownString));
            $('#wikioutput').html(marked(helpTxt));
            $("table").addClass('table table-condensed table-striped');
            hljs.initHighlighting();
        });
    }

    $(document).ready(function () {
        doUpdate();
    });

</script>

<div class="container">
    <ol class="breadcrumb">
        <li><a href="${rc.contextPath}/topicHelp/index.markdown">AIDS Help Topics</a></li>
        <li class="active"><a href="${rc.contextPath}/topicHelp/${model.helpFileName}">${model.helpFileName}</a></li>
    </ol>

    <h5>Help Topic - ${model.helpFileName}</h5>
    <#--<div id="wikioutput1"></div>-->
    <div id="wikioutput"></div>
</div>
</#escape>
</body>
</html>