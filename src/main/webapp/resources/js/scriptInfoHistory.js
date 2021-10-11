var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            queryId: document.querySelector("#queryId").defaultValue,
            historyEntries: [],
            expanded: [],
            item: null,
            options: {},
            search: '',
            loading: true,
            headers: [
                {text: "id", value: "id"},
                {text: "Upper limit", value: "upperLimit"},
                {text: "Url", value: "url"},
                {text: "Active", value: "active"},
                {text: "Asynchronous execution", value: "asynchronousExecution"},
                {text: "Username", value: "user"},
                {text: "Short name", value: "shortName"},
                {text: "Query filename", value: "queryFileName"},
                {text: "Version", value: "version"},
                {text: "Backup Id", value: "queryBackupEntry.backupId"},
                {text: '', value: 'data-table-expand' }
            ],
        };
    },
//    this one will populate new data set when user changes current page.
    watch: {
        options: {
            handler() {
                this.readHistoryEntries(this.queryId);
            },
        },
        deep: true,
    },
    methods: {
        //Reading data from API method.
        readHistoryEntries(queryId) {
            this.loading = true;
            axios
                .get(
                    "/restapi/scripts/history/" + queryId
                )
                .then((response) => {
                    this.loading = false;
                    this.historyEntries = response.data;
                });
        }
    },
    //this will trigger in the onReady State
    mounted() {
        this.readHistoryEntries(this.queryId);
    }
})