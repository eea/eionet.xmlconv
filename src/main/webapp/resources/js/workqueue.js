var app = new Vue({
    el: '#workqueueApp',
    vuetify: new Vuetify(),
    data() {
        return {
            radioGroup: 1,
            sortBy: ["jobId"],
            sortDesc: [true],
            jobEntries: [],
            totalJobEntries: 0,
            selected: [],
            expanded: [],
            item: null,
            infoMessage : null,
            options: {},
            keyword: '',
            searchedKeyword: '',
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
                const { sortBy, sortDesc, page, itemsPerPage } = this.options
                this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, this.radioGroup, this.searchedKeyword);
            },
        },
        deep: true
    },
    //    this one will populate new data set when user changes current page.
    methods: {
        //Reading data from API method.
        getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, searchParameter, searchKeyword) {
            this.loading = true;
            axios
                .get(
                    "/restapi/workqueueData/getWorkqueuePageInfo?page=" + page + "&itemsPerPage=" + itemsPerPage + "&sortBy=" + sortBy + "&sortDesc=" + sortDesc +"&searchParam=" + searchParameter +"&keyword=" + searchKeyword
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
                        this.options.sortBy = ["jobId"];
                        this.options.sortDesc = [true];
                        this.options.page = 1;
                        this.options.itemsPerPage = 25;
                        const { sortBy, sortDesc, page, itemsPerPage } = this.options;
                        this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, "", "");
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
                        this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, "", "");
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
            this.searchedKeyword = this.keyword;
            this.options.sortBy = ["jobId"];
            this.options.sortDesc = [true];
            this.options.page = 1;
            this.options.itemsPerPage = 25;
            const { sortBy, sortDesc, page, itemsPerPage } = this.options;
            this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, this.radioGroup, this.searchedKeyword);
        }
    },
    //this will trigger in the onReady State
    mounted() {
        const { sortBy, sortDesc, page, itemsPerPage } = this.options;
        this.getWorkqueuePageInfo(sortBy, sortDesc, page, itemsPerPage, "", "");
    }
})
