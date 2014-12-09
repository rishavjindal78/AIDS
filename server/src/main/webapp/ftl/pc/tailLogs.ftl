<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logs Tail</title>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.0.min.js"></script>
</head>
<body>
<script type="text/javascript">
    var start = 0;
    function doUpdate() {
        $.get('${rc.contextPath}/server/getMemoryLogs/${model.taskStepRun.id}', {start: start}, function (logVO) {
            if (logVO.logs.length > 4) {
                start = start + logVO.logs.length;
                $("#logOutputDiv").append(logVO.logs);
            }
            if (logVO.status != 'FINISHED') {
                setTimeout("doUpdate()", 2000);
            }
        });
    }
    $(document).ready(function () {
        doUpdate();
    });
</script>

<div>
    <h5>Log Tail - ${model.taskStepRun.taskStep.description!''}</h5>
    <span><pre id="logOutputDiv"></pre></span>
</div>
</body>
</html>