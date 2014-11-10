<!DOCTYPE html>
<html lang="en">
<#escape x as x?html>
<head>
    <link href="${rc.getContextPath()}/rest/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${rc.getContextPath()}/rest/resources/bootstrap/css/navbar-fixed-top.css" rel="stylesheet">

    <script src="${rc.getContextPath()}/rest/resources/js/jquery-2.1.0.min.js"></script>
    <script src="${rc.getContextPath()}/rest/resources/bootstrap/js/bootstrap.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
This is mobile version
<#--<div class="container">-->
<div class="container-fluid">
    <div class="row">
        <div class="col-md-10"><button type="button" class="btn btn-primary">Success</button></div>
        <div class="col-md-10"><button type="button" class="btn btn-danger">Danger</button></div>
        <div class="col-md-4">col-md-4</div>
        <button type="button" class="btn btn-primary btn-lg btn-block">Block level button</button>
        <button type="button" class="btn btn-default btn-lg btn-block">Block level button</button>
    </div>

    <table class="table table-striped">
       <tr class="info">
           <td>First Column</td>
           <td>Second Column</td>
           <td>Third Column</td>
       </tr>
        <tr>
            <td class="active">First Column</td>
            <td class="success">Second Column</td>
            <td class="warning">Third Column</td>
        </tr>
        <tr>
            <button type="button" class="btn btn-primary btn-lg btn-block">Block level button</button>
            <button type="button" class="btn btn-default btn-lg btn-block">Block level button</button>
            <button type="button" class="btn btn-success btn-lg btn-block">Block level button</button>
            <button type="button" class="btn btn-warning btn-lg btn-block">Block level button</button>
            <button type="button" class="btn btn-danger btn-lg btn-block">Block level button</button>
        </tr>

    </table>
    <p class="bg-primary">...</p>
    <p class="bg-success">...</p>
    <p class="bg-info">...</p>
    <p class="bg-warning">...</p>
    <p class="bg-danger">...</p>
</div>


</body>
</#escape>
</html>