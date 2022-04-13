var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "name",
            sortByTimeout: "name",
            dialog: false,
            dialogDelete: false,
            select: null,
            loading: true,
            types: [
                "Integer",
                "Long",
                "Big_Integer",
                "String",
                "Date"
            ],
            headers: [
                { text: "Name", value: "name"},
                { text: "Type", value: "type", sortable: false },
                { text: "Value", value: "value", sortable: false },
                { text: "Description", value: "description", sortable: false },
                { text: "Actions", value: "actions", sortable: false },
            ],
            timeoutPropertiesHeaders: [
                { text: "Name", value: "name"},
                { text: "Value", value: "value", width: '20%' },
                { text: "Description", value: "description"},
            ],
            properties: [],
            timeoutProperties: [],
            editedItem: {
                id: '',
                name: '',
                type: '',
                value: '',
                description: ''
            },
            defaultItem: {
                id: '',
                name: '',
                type: '',
                value: '',
                description: ''
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
            await axios.delete("/restapi/properties/delete/" + this.editedItem.id);
            this.closeDelete();
            this.readPropertiesEntries();
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
            if (this.editedItem.type=='Integer' && this.editedItem.value==0) {
                alert("Value 0 is not allowed");
            } else {
                await axios.post("/restapi/properties/add", this.editedItem);
                this.close();
                this.readPropertiesEntries();
            }
        },

        //Reading data from API method.
        readPropertiesEntries() {
            this.loading = true;
            axios
                .get(
                    "/restapi/properties/get/all"
                )
                .then((response) => {
                    this.loading = false;
                    this.properties = response.data;
                });
        },

        //Retrieve all timeout properties
        getAllTimeoutProperties() {
            this.loading = true;
            axios
                .get(
                    "/restapi/timeoutProperties/get/all"
                )
                .then((response) => {
                    this.loading = false;
                    this.timeoutProperties = response.data;
                });
        }
    },

    //this will trigger in the onReady State
    mounted() {
        this.readPropertiesEntries();
        this.getAllTimeoutProperties();
    }
})