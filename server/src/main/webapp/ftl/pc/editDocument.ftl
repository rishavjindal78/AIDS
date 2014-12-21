<#escape x as x?html>
<script type="text/javascript">
    $(document).ready(function () {
        $(".cancel").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).parent().parent().parent().remove();
        });
    });
</script>

<div>
    <fieldset>
        <form class="form-horizontal" name="upload" action="${rc.contextPath}/documents/upload/${model.document.id}" method="post"
              enctype="multipart/form-data">
            <div class="form-group">
                <label for="documentDescriptionId" class="col-sm-2 control-label">Description</label>

                <div class="col-sm-8">
                    <input id="documentDescriptionId" type="text" name="description" placeholder="description"
                           class="form-control" value="${model.document.description!''}"/>
                </div>
            </div>
            <div class="form-group">
                <label for="documentTagsId" class="col-sm-2 control-label">Tags</label>

                <div class="col-sm-6">
                    <input id="documentTagsId" type="text" name="tags" placeholder="tags" class="form-control"
                            value="${model.document.tags!''}"/>
                </div>
            </div>
            <div class="form-group">
                <label for="documentFileId" class="col-sm-2 control-label">Tags</label>

                <div class="col-sm-6">
                    <input id="documentFileId" type="file" name="file" placeholder="FilePath" class="form-sm"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label"></label>
                <input type="submit" class="btn btn-primary" value="Save"/>
                <button class="btn cancel btn-default" id="cancel">Cancel</button>
            </div>
</div>
</form>
</fieldset>
</div>
</#escape>