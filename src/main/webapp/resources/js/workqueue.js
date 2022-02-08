var app = new Vue({
    el: '#workqueueApp',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "jobId",
            sortDesc: false,
            jobEntries: [],
            expanded: [],
            selected: [],
            item: null,
            options: {},
            search: '',
            loading: true,
            headers: [
                {text: "Job Id", value: "jobId", sortable: true},
                {text: "Document URL", value: "urlName", sortable: true},
                {text: "Query script", value: "scriptFile", sortable: true},
                {text: "Job Result", value: "resultFile", sortable: true},
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
                this.getJobEntries();
            },
        },
        deep: true,
    },
    //    this one will populate new data set when user changes current page.
    methods: {
        //Reading data from API method.
        getJobEntries() {
            this.loading = true;
            axios
                .get(
                    "/restapi/workqueueData/getAllJobs"
                )
                .then((response) => {
                    this.loading = false;
                    this.jobEntries = response.data;
                });
        }
    },
    //this will trigger in the onReady State
    mounted() {
        this.getJobEntries();
    }
})