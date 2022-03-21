let stompClient = null;
var app = new Vue({
    el: '#workqueueApp',
    vuetify: new Vuetify(),
    data() {
        return {
            radioGroup: "",
            sortBy: ["jobId"],
            sortDesc: [true],
            jobEntries: [],
            totalJobEntries: 0,
            selected: [],
            selectedStatusesForSearch: [],
            searchedStatuses: [],
            expanded: [],
            item: null,
            infoMessage : null,
            options: {},
            keyword: '',
            searchedKeyword: '',
            permissions: null,
            username: null,
            loading: true,
            statuses: ['DOWNLOADING SOURCE', 'JOB RECEIVED', 'PROCESSING', 'READY', 'FATAL ERROR', 'RECOVERABLE ERROR', 'INTERRUPTED', 'CANCELLED BY USER', 'DELETED'],
            headers: [
                {text: "Job Id", value: "jobId", sortable: true},
                {text: "Document URL", value: "url", sortable: true},
                {text: "Query script", value: "script_file", sortable: true},
                {text: "Job Result", value: "result_file", sortable: false},
                {text: "Status", value: "statusName", sortable: true},
                {text: "Started at", value: "timestamp", sortable: true},
                {text: "Instance", value: "instance", sortable: true},
                {text: "Duration", value: "durationInProgress", sortable: true},
                {text: "Job type", value: "jobType", sortable: true},
                {text: "Worker", value: "jobExecutorName", sortable: true}
            ],
        };
    },
    //    this one will populate new data set when user changes current page.
    watch: {
        options: {
            handler() {
                const { sortBy, sortDesc, page, itemsPerPage } = this.options
                this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, this.radioGroup, this.searchedKeyword, this.searchedStatuses);
            },
        },
        deep: true
    },
    //    this one will populate new data set when user changes current page.
    methods: {
        //Reading data from API method.
        getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, searchParameter, searchKeyword, searchedStatuses) {
            this.loading = true;
            axios
                .get(
                    "/restapi/workqueueData/getWorkqueuePageInfo?page=" + page + "&itemsPerPage=" + itemsPerPage + "&sortBy=" + sortBy + "&sortDesc=" + sortDesc +"&searchParam=" + searchParameter +"&keyword=" + searchKeyword
                +"&statuses=" + searchedStatuses
                )
                .then((response) => {
                    this.loading = false;
                    this.jobEntries = response.data.jobMetadataList;
                    this.totalJobEntries = response.data.totalJobEntries;
                    this.permissions = response.data.workqueuePermissions;
                    this.username = response.data.username;
                    //add parameters to url
                    var url = new URL(window.location.href);
                    url.searchParams.set('page', page);
                    url.searchParams.set('itemsPerPage', itemsPerPage);
                    url.searchParams.set('sortBy', sortBy);
                    url.searchParams.set('sortDesc', sortDesc);
                    url.searchParams.set('searchParam', searchParameter);
                    if(searchParameter == "statusName"){
                        url.searchParams.set('keyword', "");
                    }
                    else{
                        url.searchParams.set('keyword', searchKeyword);
                    }
                    url.searchParams.set('statuses', searchedStatuses);
                    //set url without reloading
                    window.history.pushState(null, null, url.href);
                });
        },
        restartJobs () {
            if(this.selected.length == 0){
                this.infoMessage = "No jobs were selected";
                return;
            }
            if(confirm("Are you sure you want to restart the selected jobs?")) {
                //call java method to restart jobs
                axios.post("/restapi/workqueueData/restart", this.selected)
                    .then((response) => {
                        this.infoMessage = response.data;
                        this.options.sortBy = ["jobId"];
                        this.options.sortDesc = [true];
                        this.options.page = 1;
                        this.options.itemsPerPage = 25;
                        const { sortBy, sortDesc, page, itemsPerPage } = this.options;
                        this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, "", "", []);
                    });
            }
        },
        deleteJobs () {
            if(this.selected.length == 0){
                this.infoMessage = "No jobs were selected";
                return;
            }
            if(confirm("Are you sure you want to delete the selected jobs?")) {
                //call java method to delete jobs
                axios.post("/restapi/workqueueData/delete", this.selected)
                    .then((response) => {
                        this.infoMessage = response.data;
                        this.options.sortBy = ["jobId"];
                        this.options.sortDesc = [true];
                        this.options.page = 1;
                        this.options.itemsPerPage = 25;
                        const { sortBy, sortDesc, page, itemsPerPage } = this.options;
                        this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, "", "", []);
                    });
            }
        },
        csvExport() {
            axios.post("/restapi/workqueueData/exportToCsv", this.jobEntries, {responseType: 'blob'})
                .then((response) => {
                    const url = window.URL.createObjectURL(new Blob([response.data]));
                    const link = document.createElement("a");
                    link.href = url;
                    link.setAttribute("download", "QA jobs workqueue.csv");
                    document.body.appendChild(link);
                    link.click();
                })
        },
        onExpand({ item, value }) {
            if(value){
                //item is expanded
                let jobId = item.jobId;
                axios.get("/restapi/workqueueData/getJobDetails/" + jobId)
                    .then((response) => {
                        item.job_history_metadata_list = response.data;
                    })
            }
        },
        search() {
            this.searchedKeyword = this.keyword;
            this.searchedStatuses = this.selectedStatusesForSearch;
            this.options.sortBy = ["jobId"];
            this.options.sortDesc = [true];
            this.options.page = 1;
            this.options.itemsPerPage = 25;
            const { sortBy, sortDesc, page, itemsPerPage } = this.options;
            this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, this.radioGroup, this.searchedKeyword, this.searchedStatuses);
        }
    },
    //this will trigger in the onReady State
    mounted() {
        var url = new URL(window.location.href);
        if(url.searchParams.get("sortBy") != null){
            this.options.sortBy = [url.searchParams.get("sortBy")];
        }
        else{
            this.options.sortBy = ["jobId"];
        }
        if(url.searchParams.get("sortDesc") != null){
            this.options.sortDesc = [url.searchParams.get("sortDesc")];
        }
        else{
            this.options.sortDesc = [true];
        }
        if(url.searchParams.get("page") != null){
            this.options.page = [url.searchParams.get("page")];
        }
        else{
            this.options.page = 1;
        }
        if(url.searchParams.get("itemsPerPage") != null){
            this.options.itemsPerPage = [url.searchParams.get("itemsPerPage")];
        }
        else{
            this.options.itemsPerPage = 25;
        }
        if(url.searchParams.get("searchParam") != null){
            this.radioGroup = [url.searchParams.get("searchParam")];
        }
        else{
            this.radioGroup = "";
        }
        if(url.searchParams.get("keyword") != null){
            this.searchedKeyword = [url.searchParams.get("keyword")];
        }
        else{
            this.searchedKeyword = "";
        }
        if(url.searchParams.get("statuses") != null){
            this.searchedStatuses = [url.searchParams.get("statuses")];
        }
        else{
            this.searchedStatuses = "";
        }
        this.getWorkqueuePageInfo(this.options.sortBy, this.options.sortDesc, this.options.page, this.options.itemsPerPage, this.radioGroup, this.searchedKeyword, this.searchedStatuses);
        let mustRefreshResults = false;

        this.$nextTick(function() {
            let socket = new SockJS("/websocket/workqueue/tableChanged");
            stompClient = Stomp.over(socket);
            stompClient.connect(
                {},
                function(frame) {
                    stompClient.subscribe("/websocket", function(val) {
                        mustRefreshResults = JSON.parse(val.body);
                        if(mustRefreshResults){
                            var url = new URL(window.location.href);
                            window.location.href = url.href;

                        }
                    });
                }
            );
        });
    }
})
