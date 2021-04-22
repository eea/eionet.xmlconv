(function() {
    $(window).on("beforeunload", function (event) {
        $.ajax({
            type: "POST",
            url: '/restapi/worker/fail'
        });
    })
})();