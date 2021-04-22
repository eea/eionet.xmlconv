document.addEventListener("visibilitychange", event => {
    $.ajax({
        type: "POST",
        url: '/restapi/worker/fail'
    });
})