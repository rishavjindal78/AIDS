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

            //add more file components if Add is clicked
            $('.addFile').click(function () {
                var fileIndex = $(this).next('.fileTable').children().length;
                console.info("add new file holder - " + fileIndex);
                $(this).next('.fileTable').append('<li>' + '   <input type="file" name="files[' + fileIndex + ']" />' + '</li>');
            });
        });

        function editDocument(id) {
            $.get('${rc.contextPath}/documents/edit/' + id, function (data) {
                $('#edit_document_' + id).empty();
                $('#edit_document_' + id).html(data);
            });
        }

        function deleteDocument(docId) {
            var option = confirm("Are you sure to delete the Document : " + docId + " ?");
            if (option == true) {
                document.getElementById("documentDeleteForm_" + docId).submit();
            }
        }

    </script>

    <BR>
    <form name="search" action="${rc.contextPath}/documents/team/${Session['SELECTED_TEAM'].id}/search" method="GET">
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
            <form class="form-horizontal" name="upload"
                  action="${rc.contextPath}/documents/team/${Session['SELECTED_TEAM'].id}/upload"
                  method="post"
                  enctype="multipart/form-data">
                <div class="form-group">
                    <label for="documentDescriptionId" class="col-sm-2 control-label">Description</label>

                    <div class="col-sm-6">
                        <input id="documentDescriptionId" type="text" name="description" placeholder="description"
                               class="form-control"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="documentTagsId" class="col-sm-2 control-label">Tags</label>

                    <div class="col-sm-6">
                        <input id="documentTagsId" type="text" name="tags" placeholder="tags" class="form-control"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="documentFileId" class="col-sm-2 control-label">File</label>

                    <div class="col-sm-6">
                        <input id="documentFileId" type="file" name="file" placeholder="FilePath" class="form-sm"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"></label>
                    <input type="submit" class="btn btn-primary" value="Save"/>
                    <button type="reset" class="btn cancel btn-default" id="cancel">Cancel</button>
                </div>
            </form>
        </fieldset>
    </div>
    <br/>
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
                            <BR>
                            <a class="btn btn-xs" href="${rc.contextPath}/documents/download/${doc.id}">Download</a>
                            <form id="documentDeleteForm_${doc.id}" action="${rc.contextPath}/documents/team/${Session['SELECTED_TEAM'].id}/delete/${doc.id}" method="POST">
                                <input type="submit" class="btn btn-xs" value="Delete" onclick="return confirm('Please click ok to confirm ?')"/>
                            </form>
                        </p>
                    </div>
                    <span id="edit_document_${doc.id!''}"></span>
                </td>
                <td width="150">
                    <div class="btn-group">
                        <button type="button" class="btn btn-info dropdown-toggle"
                                data-toggle="dropdown"> Action <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="${rc.contextPath}/documents/download/${doc.id}">Download</a></li>
                            <li><a href="#" onclick="editDocument('${doc.id}')">Edit</a></li>
                            <li><a href="#" onclick="deleteDocument('${doc.id}')">Delete</a></li>
                            <#--<li class="divider"></li>-->
                            <#--<li><a href="${rc.getContextPath()}/server/delete/${td.id}">Delete</a></li>-->
                        </ul>
                    </div>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
    <span id="message" style="color: green">${model.message!''}</span>
    </@com.page>
</#escape>