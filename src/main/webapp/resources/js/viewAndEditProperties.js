var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "name",
            dialog: false,
            dialogDelete: false,
            select: null,
            loading: true,
            readonly: false,
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
            properties: [],
            editedIndex: -1,
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
    computed: {
        formTitle() {
            return this.editedIndex === -1 ? 'New Property' : 'Edit Property'
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

    methods: {
        editItem(item) {
            this.editedIndex = this.properties.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.readonly = true;
            this.dialog = true
        },

        deleteItem(item) {
            this.editedIndex = this.properties.indexOf(item)
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
            await axios.post("/restapi/properties/add", this.editedItem);
            this.close();
            this.readPropertiesEntries();
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
        }
    },

    //this will trigger in the onReady State
    mounted() {
        this.readPropertiesEntries();
    }
})