<#import "/spring.ftl" as spring />
<#macro page title="" activeTab="">
<!DOCTYPE html>
<html>
<head>
    <#if activeTab!="">
        <meta name="activeTab" content="${activeTab?string}"/></#if>
    <#if title!=""><title>${title?html}</title></#if>
    <link rel="Shortcut Icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <#--<link href="${rc.getContextPath()}/bootstrap/css/bootstrap.min.css" rel="stylesheet">-->
    <#--<link href="${rc.getContextPath()}/bootstrap/css/navbar-fixed-top.css" rel="stylesheet">-->
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/bootstrap/css/navbar-fixed-top.css'/>"/>

<#--<link href="${rc.getContextPath()}/resources/bootstrap/css/sticky-footer-navbar.css" rel="stylesheet">-->

    <script src="${rc.getContextPath()}/js/jquery-2.1.0.min.js"></script>
    <#--<script src="${rc.getContextPath()}/bootstrap/js/bootstrap.min.js"></script>-->
    <script src="<@spring.url '/bootstrap/js/bootstrap.min.js'/>"></script>

    <meta name="description" content="AIDS">
    <meta name="author" content="aids">
    <meta http-equiv="Content-type" content="text/html; charset=${.output_encoding}">

    <style type="text/css">
        #ajaxBusy {
            display: none;
            opacity: 0.9;
            /*margin: 0px 0px 0px -50px; /!* left margin is half width of the div, to centre it *!/*/
            /*padding: 30px 10px 10px 10px;*/
            position: fixed;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            text-align: center;
            background: rgba(0,0,0,0.6) url(${rc.contextPath}/images/ajax-loader-big.gif) no-repeat center center;
            border: 1px solid #8597d1;
            z-index: 5;
        }

        body {
            padding-top: 10px;
            padding-bottom: 20px;
        }

        .container {
            width: 90%;
        }

        .container-narrow > hr {
            margin: 30px 0;
        }

        input:required:invalid, input:focus:invalid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAeVJREFUeNqkU01oE1EQ/mazSTdRmqSxLVSJVKU9RYoHD8WfHr16kh5EFA8eSy6hXrwUPBSKZ6E9V1CU4tGf0DZWDEQrGkhprRDbCvlpavan3ezu+LLSUnADLZnHwHvzmJlvvpkhZkY7IqFNaTuAfPhhP/8Uo87SGSaDsP27hgYM/lUpy6lHdqsAtM+BPfvqKp3ufYKwcgmWCug6oKmrrG3PoaqngWjdd/922hOBs5C/jJA6x7AiUt8VYVUAVQXXShfIqCYRMZO8/N1N+B8H1sOUwivpSUSVCJ2MAjtVwBAIdv+AQkHQqbOgc+fBvorjyQENDcch16/BtkQdAlC4E6jrYHGgGU18Io3gmhzJuwub6/fQJYNi/YBpCifhbDaAPXFvCBVxXbvfbNGFeN8DkjogWAd8DljV3KRutcEAeHMN/HXZ4p9bhncJHCyhNx52R0Kv/XNuQvYBnM+CP7xddXL5KaJw0TMAF8qjnMvegeK/SLHubhpKDKIrJDlvXoMX3y9xcSMZyBQ+tpyk5hzsa2Ns7LGdfWdbL6fZvHn92d7dgROH/730YBLtiZmEdGPkFnhX4kxmjVe2xgPfCtrRd6GHRtEh9zsL8xVe+pwSzj+OtwvletZZ/wLeKD71L+ZeHHWZ/gowABkp7AwwnEjFAAAAAElFTkSuQmCC); background-position: right top; background-repeat: no-repeat; -moz-box-shadow: none; }
        input:required:valid { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=); background-position: right top; background-repeat: no-repeat; }

    </style>
    <script type="text/javascript">
        var scrollToElement = function (el, ms) {
            var speed = (ms) ? ms : 1500;
            $('html,body').animate({
                scrollTop: $(el).offset().top
            }, speed);
        }

        $(document).ready(function () {
            $('body').append('<div id="ajaxBusy"><p id="ajaxBusyMsg">Please wait...</p></div>');
            // AJAX activity indicator bound to ajax start/stop document events
            $(document).ajaxStart(function () {
                $('#ajaxBusy').show();
            }).ajaxStop(function () {
                $('#ajaxBusy').hide();
            });
        });
    </script>
</head>
<body>
    <#include "header.ftl"/>
<div class="container">
    <#if title!=""><p class="lead">${title?html}</p></#if>
    <#nested>
    <#include "footer.ftl"/>
</div>
</body>
</html>
</#macro>