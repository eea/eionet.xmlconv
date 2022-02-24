var app = new Vue({
    el: '#workqueueApp',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "jobId",
            sortDesc: false,
            jobEntries: [],
            totalJobEntries: 0,
            selected: [],
            expanded: [],
            item: null,
            infoMessage : null,
            options: {},
            search: '',
            permissions: null,
            username: null,
            loading: true,
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
                this.getWorkqueuePageInfo("");
            },
        },
        deep: true
    },
    //    this one will populate new data set when user changes current page.
    methods: {
        //Reading data from API method.
        getWorkqueuePageInfo(keyword) {
            this.loading = true;
            const { sortBy, sortDesc, page, itemsPerPage } = this.options
            axios
                .get(
                    "/restapi/workqueueData/getWorkqueuePageInfo?page=" + page + "&itemsPerPage=" + itemsPerPage + "&sortBy=" + sortBy + "&sortDesc=" + sortDesc+"&keyword=" + keyword
                )
                .then((response) => {
                    this.loading = false;
                    this.jobEntries = response.data.jobMetadataList;
                    this.totalJobEntries = response.data.totalJobEntries;
                    this.permissions = response.data.workqueuePermissions;
                    this.username = response.data.username;
                    this.selected = [];
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
                        this.getWorkqueuePageInfo("");
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
                        this.getWorkqueuePageInfo("");
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
                if(item.job_history_metadata_list == null) {
                    axios.get("/restapi/workqueueData/getJobDetails/" + jobId)
                        .then((response) => {
                            item.job_history_metadata_list = response.data;
                        })
                }

            }
        },
        search() {
            this.getWorkqueuePageInfo(this.search);
        }
    },
    //this will trigger in the onReady State
    mounted() {
        this.getWorkqueuePageInfo("");
    }
})
