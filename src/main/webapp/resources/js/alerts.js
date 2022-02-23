var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "severity.id",
            sortDesc: true,
            dialog: false,
            dialogDelete: false,
            select: null,
            loading: true,
            severityValues: ["LOW","MEDIUM","CRITICAL"],
            headers: [
                { text: "Severity", value: "severity"},
                { text: "Description", value: "description", sortable: false },
                { text: "Notification sent to uns", value: "notificationSentToUns", sortable: false },
                { text: "Occurrence date", value: "occurrenceDateMod", sortable: true },
                { text: "Actions", value: "actions", sortable: false },
            ],
            alerts: [],
            editedItem: {
                id: '',
                severity: '',
                description: '',
                notificationSentToUns: false,
                occurrenceDateMod: ''
            },
            defaultItem: {
                id: '',
                severity: '',
                description: '',
                notificationSentToUns: false,
                occurrenceDateMod: ''
            },
        };
    },
    watch: {
        deep: true,
        dialog(val) {
            val || this.close()
        },
        dialogDelete(val) {
            val || this.closeDelete()
        },
    },

    methods: {
        editItem(item) {
            this.editedItem = Object.assign({}, item)
            this.dialog = true
        },

        deleteItem(item) {
            this.editedItem = Object.assign({}, item)
            this.dialogDelete = true
        },

        async deleteItemConfirm() {
            await axios.delete("/restapi/alerts/delete/" + this.editedItem.id);
            this.closeDelete();
            this.readAlertsEntries();
        },

        close() {
            this.readonly = false
            this.dialog = false
            this.$nextTick(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
            })
        },

        closeDelete() {
            this.dialogDelete = false
            this.$nextTick(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
            })
        },

        async save() {
            await axios.post("/restapi/alerts/add", this.editedItem);
            this.close();
            this.readAlertsEntries();
        },

        //Reading data from API method.
        readAlertsEntries() {
            this.loading = true;
            axios
                .get(
                    "/restapi/alerts/get/all"
                )
                .then((response) => {
                    this.loading = false;
                    this.alerts = response.data;
                });
        }
    },

    //this will trigger in the onReady State
    mounted() {
        this.readAlertsEntries();
    }
})