<#escape x as x?html>
<#--<#noescape>-->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="utf-8">
    <title>Live Servers, TestMD</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Expires" CONTENT="-1">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script type="text/javascript" src="js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="js/jquery.form.js"></script>
    <script type="text/javascript" src="js/jQuery.download.js"></script>

    <!-- Le styles -->
    <script src="js/bootstrap.min.js"></script>
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <script type="text/javascript" charset="utf-8">
        var cacheId;
        // prepare the form when the DOM is ready
        $(document).ready(function () {
            // bind form using ajaxForm
            $('#htmlForm').ajaxForm({
                target: '#htmlExampleTarget',
                success: function () {
                    $('#htmlExampleTarget').fadeIn('slow');
                }
            });
        });

        var lpStart = function () {
            var jqxhr = $.get("dashboard/healthMonitor", { id: 0, cacheId: cacheId },
                    function (data) {
                        if (data != undefined) {
                            cacheId = data.cacheId;
                            var content = '';

                            $.each(data.serverApps, function (index, serverApp) {
                                content += '<tr class="info"><td colspan="2"><h4>' + (index + 1) + '. ' + serverApp.name + getSpanForStatus(serverApp.status)+'<h4></td></tr>';
//                                content += '<tr>';
//                                content += '<td><h6><a href="javascript:downloadFile('+serverApp.id+')">' + serverApp.id + '</a></h6></td>';
                                var teamContacts = '<address>'
                                        + '<strong>' + trimOrBlank(serverApp.leadDeveloper) + '</strong><br />'
                                        + '<a mailto=' + trimOrBlank(serverApp.contactDL) + '>' + trimOrBlank(serverApp.contactDL) + '</a>' + '<br />'
                                        + '<strong> Chat Channel : </strong>' + trimOrBlank(serverApp.chatChannel) + '<br />'
                                        + '</address>';
//                                content += '<td class="" rowspan='+serverApp.serverComponents.length+'>'+teamContacts+'</td>';

                                $.each(serverApp.componentGroups, function (index, group) {
                                    content += '<tr><td class="warning" rowspan="' + group.componentList.length + '"><p><h5>' + group.groupName + '<small>'+getSpanForStatus(group.status)+'</small></h5></p></td>';
                                    $.each(group.componentList, function (index, component) {
                                        var uniqueId = serverApp.id + '-' + index;
                                        content += '<td class="' + getClassForServerAppStatus(component.status) + '"><h6>' + component.name + ' [' + component.status + ']<sup>' + trimOrBlank(component.lastStatusUpdateTime) + '</sup></td></tr><tr>';
                                    });
                                });
                                content += '</tr>';
//                                console.log(content);
                            });
                            $("#serverTable").empty().append(content);
                        } else {
                            $("#serverTable").empty();
                        }
                        lpStart();
                    }, "json");

            jqxhr.error(function () {
            });
        };
        $(document).ready(lpStart);

        $(document).ready(function () {
//            $("resultDisplayTable").tablesorter({ sortList:[[ 1, 0 ]] });
//            $("resultDisplayTable").tablesorter();
        });

        $("body").ajaxError(
                function (e, request) {
                    if (request.status == 403 || request.status == 404) {
//                        updateDiv();
                    }
                }
        );

        //        setInterval('updateDiv()', 10000);

        function updateDiv() {
            $.post("dashboard/refresh")
//            window.location.reload(true);
        }

        function downloadFile(id) {
            $.download('dashboard/fileDownload', 'filename=mySpreadsheet&format=xls&id=' + id, 'GET');
        }

        function closeDiv() {
            $("#serverTable").empty();
        }

        $("#refreshMyPage").click(function () {
            alert("Handler for .click() called.");
        });

        var trimOrBlank = function (input) {
            if (input == null) {
                return '';
            }
            return input;
        }
        var getServerType = function (serverType) {
            var classname;
            switch (serverType) {
                case 'PROD':
                    classname = ' <span class="label warning">' + serverType + '</span> ';
                    break;
                case 'BCP':
                case 'DEV':
                case 'NOT_AUTHENTICATED':
                case 'UNKNOWN':
                default:
                    classname = ' <span class="label notice">' + serverType + '</span> ';
                    break;
            }
            return classname;
        }

        var getClassForServerAppStatus = function (serverAppStatus) {
            var classname;
            switch (serverAppStatus) {
                case 'UP':
                    classname = 'btn btn-success btn-lg btn-block';
                    break;
                case 'UNKNOWN':
                    classname = 'btn btn-warning btn-lg btn-block';
                    break;
                case 'DOWN':
                case 'NOT_AUTHENTICATED':
                    classname = 'btn btn-danger btn-lg btn-block';
                    break;
                default:
                    classname = 'btn btn-danger btn-lg btn-block';
            }
            return classname;
        }

        var getSpanForStatus = function (serverStatus) {
            var spanValue;
            switch (serverStatus) {
                case 'UP' :
                    spanValue = ' <span class="text-muted label label-success">up</span>';
                    break;
                case 'DOWN':
                    spanValue = ' <span class="label label-danger">down</span>';
                    break;

                default :
                    spanValue = ' '
            }
            return spanValue;
        }

    </script>
</head>

<body>
<div class="">
    <h1>
        <small>Server Health Dashboard</small>
        <button type="button" onclick="javascript:updateDiv()" class="btn btn-primary">Refresh Status</button>
    </h1>

<#--<button type="button" class="btn btn-primary btn-lg btn-block">TestM Dist Dev</button>

<!-- Stack the columns on mobile by making one full-width and the other half-width &ndash;&gt;
<div class="row">
    <div class="col-xs-12 col-md-8">.col-xs-12 .col-md-8</div>
    <div class="col-xs-6 col-md-4">.col-xs-6 .col-md-4</div>
</div>

<!-- Columns start at 50% wide on mobile and bump up to 33.3% wide on desktop &ndash;&gt;
<div class="row">
    <div class="col-xs-6 col-md-4">.col-xs-6 .col-md-4</div>
    <div class="col-xs-6 col-md-4">.col-xs-6 .col-md-4</div>
    <div class="col-xs-6 col-md-4">.col-xs-6 .col-md-4</div>
</div>

<!-- Columns are always 50% wide, on mobile and desktop &ndash;&gt;
<div class="row">
    <div class="col-xs-6"><button type="button" class="btn btn-success">R</button>UI Server 1</div>
    <div class="col-xs-6"><button type="button" class="btn btn-success">R</button>UI Server 2</div>
    <div class="col-xs-6"><button type="button" class="btn btn-primary btn-lg btn-block">UI Server 2</button></div>
</div>-->
    <div class="table-responsive">
        <table class="table table-condensed" id="resultDisplayTable">
            <tbody id="serverTable">
            <th>Component Group</th>
            <th>Component</th>
            </tbody>
        </table>
    </div>
    <h3>Legends</h3>
    <span class="label-danger">Server Down</span>
    <span class="label-success">Server Up</span>
    <span class="label-warning">Unknown Problem</span>
    <span class="label-info">Notice</span>
</div>
<footer>
    <p>&copy; TestM</p>
</footer>
</div>
</body>
</html>
<#--</#noescape>-->
</#escape>