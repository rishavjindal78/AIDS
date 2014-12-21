<#import "common.ftl" as com>
<#import "common-macro.ftl" as comMacro>
<#escape x as x?html>
    <@com.page activeTab="documents">
    <style>
        table {
            font-size: 13px;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $(".button-expand").click(function () {
                var div = $(this).next();
                div.slideToggle();
            });

            $("#form").submit(function () {
                $.post($(this).attr("action"), $(this).serialize(), function (html) {
                    $("#formsContent").replaceWith(html);
                    $('html, body').animate({ scrollTop: $("#message").offset().top }, 500);
                });
                return false;
            });
        });
    </script>

    <BR>
    <form name="search" action="${rc.contextPath}/documents/search" method="GET">
        <div class="row">
            <div class="col-xs-7">
                <input type="text" name="query" placeholder="search criteria" class="form-control"
                       value="${model.query!''}"/>
            </div>
            <div class="col-xs-2">
                <input type="submit" class="btn btn-primary" value="Search"/>
            </div>
        </div>
    </form>
    <a href="#" class="button-expand btn btn-info btn-xs">+ Add New Documents</a>
    <div style="display: none;">
        <fieldset>
            <form name="upload" action="${rc.contextPath}/documents/upload" method="post" enctype="multipart/form-data">
                <div class="row">
                    <div class="col-xs-4">
                        <input type="text" name="description" placeholder="description" class="form-control"/>
                    </div>
                    <div class="col-xs-3">
                        <input type="text" name="tags" placeholder="tags" class="form-control"/>
                    </div>
                    <div class="col-xs-3">
                        <input type="file" name="file" placeholder="FilePath" class="form-sm"/>
                    </div>
                    <div class="col-xs-2">
                        <input type="submit" class="btn btn-primary" value="Upload"/>
                    </div>
                </div>
            </form>
        </fieldset>
    </div>
    <div>${model.message!''}</div>
    <br/>
    <table class="table table-striped table-responsive">
        <tbody>
            <#list model["documents"] as doc>
            <tr>
                <td width="3%">${doc_index+1}</td>
                <td>
                    <a href="#" class="button-expand"><b>${doc.description!''}</b></a>

                    <div style="display: none;">
                        <p> FileID : ${doc.id!''}<BR>
                            FileName : ${doc.name!''}<BR>
                            Upload Time : ${(doc.uploadDate?date?string("dd-MMM-yyyy hh:mm a"))!''} Tags [${doc.tags!''}
                            ]<BR>
                            Total downloads : ${doc.downloads!''}

                        <form action="${rc.contextPath}/documents/delete/${doc.id}" method="POST"><input type="submit" class="btn btn-xs"
                                                                                   value="Delete"
                                                                                   onclick="return confirm('Please click on to confirm ?')"/>
                        </form>
                        </p>
                    </div>
                </td>
                <td width="10%"><a href="${rc.contextPath}/documents/download/${doc.id}" class="btn btn-success btn-sm">Download</a></td>
            </tr>
            </#list>
        </tbody>
    </table>
    </div>
    </@com.page>
</#escape>