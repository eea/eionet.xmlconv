var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "field",
            dialog: false,
            dialogDelete: false,
            select: null,
            queryId: document.querySelector("#queryId").defaultValue,
            loading: true,
            fieldValues: [
                "script url",
                "schema url",
                "xml file",
                "xml file size",
            ],
            typeValues: [
                "match exactly",
                "greater than",
                "smaller than",
            ],
            headers: [
                {text: "Field", value: "field"},
                {text: "Type", value: "type", sortable: false},
                {text: "Value", value: "value", sortable: false},
                {text: "Description", value: "description", sortable: false},
                {text: "Enabled", value: "enabled", sortable: false},
                {text: "Actions", value: "actions", sortable: false},
            ],
            rules: [],
            editedIndex: -1,
            editedItem: {
                id: '',
                field: '',
                type: '',
                value: '',
                description: '',
                enabled: false
            },
            defaultItem: {
                id: '',
                field: '',
                type: '',
                value: '',
                description: '',
                enabled: false
            },
        };
    },
    computed: {
        formTitle() {
            return this.editedIndex === -1 ? 'New Rule' : 'Edit Rule'
        },
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

    created() {
        this.initialize()
    },

    methods: {
        initialize() {
            this.readRuleEntries();
        },

        editItem(item) {
            this.editedIndex = this.rules.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialog = true
        },

        deleteItem(item) {
            this.editedIndex = this.rules.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialogDelete = true
        },

        async deleteItemConfirm() {
            await axios.delete("/restapi/scriptRules/delete/" + this.editedItem.id);
            this.closeDelete();
            this.readRuleEntries();
        },

        close() {
            this.dialog = false
            this.$nextTick(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
                this.editedIndex = -1
            })
        },

        closeDelete() {
            this.dialogDelete = false
            this.$nextTick(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
                this.editedIndex = -1
            })
        },

        async save() {
            await axios.post("/restapi/scriptRules/add/" + this.queryId + "/", this.editedItem);
            this.close();
            this.readRuleEntries();
            window.location.reload();
        },

        //Reading data from API method.
        readRuleEntries() {
            this.loading = true;
            axios
                .get("/restapi/scriptRules/get/" + this.queryId)
                .then((response) => {
                    this.loading = false;
                    this.rules = response.data;
                });
        }
    },

    //this will trigger in the onReady State
    mounted() {
        this.readRuleEntries();
    }
})