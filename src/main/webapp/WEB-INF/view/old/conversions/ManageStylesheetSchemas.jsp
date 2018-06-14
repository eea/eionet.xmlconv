<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<script type="text/javascript">
    //<![CDATA[
    var schemaIterator = 1;
    $(function () {
        function initAutoComplete(control) {
            $.ajax({
                url: "<c:url value="/api/getXMLSchemas" />",
                dataType: "xml",
                method: "GET",
                success: function (xmlDocument) {
                    var data = $("schema", xmlDocument).map(function () {
                        return {value: $(this).text()};
                    }).get();
                    $(control).each(function () {
                        $(this).autocomplete({
                            source: data
                            , minLength: 1
                        });
                    });
                }
            });
        }

        $(document).on('click', '#addSchema', function () {
            if ($(".newSchemaContainer").length == 1 && $(".newSchemaContainer").is(":hidden")) {
                $(".newSchemaContainer").show();
                $(".newSchemaContainer > input").focus();
            }
            else {
                schemaIterator++;
                var newInput = $("#newSchemaPrototype").clone(true);
                newInput.attr("id", "");
                newInput.children("input").attr("id", "schema_" + schemaIterator);
                newInput.children("input").attr("name", "newSchemas");
                newInput.attr("class", "newSchemaContainer");
                newInput.appendTo("#newSchemasContainer");
                newInput.children("input").focus();
                initAutoComplete(newInput.children("input"));
                event.preventDefault();
            }
        });
        $(document).on('click', '.delNewSchemaLink', function () {
            if ($(".newSchemaContainer").length > 1) {
                $(this).parent().remove();
            }
            else {
                $(".newSchemaContainer > input").val("");
                $(".newSchemaContainer").hide();
            }
        });
        $(document).on('click', '.delSchemaLink', function () {
            $(this).parent().remove();
        });

        initAutoComplete($("#txtSchemaUrl"));
        initAutoComplete($("#schema_1"));
        if ($(".newSchemaContainer").length == 1 && $("#txtSchemaUrl").length == 0) {
            $(".newSchemaContainer").hide();
        }

    });
    //]]>
</script>

<a href="#" id="addSchema">Add new XML Schema</a>
<div style="display: none">
  <div id="newSchemaPrototype">
    <input type="url" style="width: 400px;" class="newSchema"/>
    <a href='#' class="delNewSchemaLink"><img style='border: 0' src='<c:url value="/static/images/button_remove.gif" />'
                                              alt='Remove'/></a>
    <br/>
  </div>
</div>
<script>
    $("#stylesheetForm").validate({
        messages: {
            newSchemas: "<spring:message code='label.stylesheet.warning.noturl'/>"
        },
        invalidHandler: function () {
            $("#stylesheetForm")[0].submit();
        }
    });
</script>
