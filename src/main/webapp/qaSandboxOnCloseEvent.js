$(window).on("beforeunload", function() {
    var origin = window.location.origin;
    $.ajax({
        async: false,
        type: "POST",
        url: origin + '/restapi/worker/fail'
    });
});