var app = new Vue({
    el: '#main',
    vuetify: new Vuetify(),
    data() {
        return {
            scriptsHistoryEntries: [],
            options: {},
            headers: [
                {text: "Query id", value: "queryEntry.queryId"},
                {text: "Short name", value: "shortName"},
                {text: "Query filename", value: "queryFileName"},
                {text: "Version", value: "version"}
            ],
        };
    },
    //this one will populate new data set when user changes current page.
    watch: {
        options: {
            handler() {
                this.readDataFromAPI();
            },
        },
        deep: true,
    },
    methods: {
        //Reading data from API method.
        readDataFromAPI() {
            axios
                .get(
                    "/restapi/scripts/history/all"
                )
                .then((response) => {
                    this.scriptsHistoryEntries = response.data;
                });
        }
    },
    //this will trigger in the onReady State
    mounted() {
        this.readDataFromAPI();
    }
})