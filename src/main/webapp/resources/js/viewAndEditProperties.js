var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data() {
        return {
            sortBy: "name",
            dialog: false,
            dialogDelete: false,
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
            return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
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

    created () {
        this.initialize()
    },

    methods: {
        initialize () {
            this.readPropertiesEntries();
        },

        editItem(item) {
            this.editedIndex = this.properties.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialog = true
        },

        deleteItem(item) {
            this.editedIndex = this.properties.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialogDelete = true
        },

        deleteItemConfirm() {
            this.properties.splice(this.editedIndex, 1)
            this.closeDelete()
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
            const data = {
                id: this.editedItem.id,
                name: this.editedItem.name,
                type: this.editedItem.type,
                value: this.editedItem.value,
                description: this.editedItem.description
            };
            await axios.post("/restapi/properties/add", data);
            this.close();
            this.readPropertiesEntries();
        },

        //Reading data from API method.
        readPropertiesEntries() {
            axios
                .get(
                    "/restapi/properties/all"
                )
                .then((response) => {
                    this.properties = response.data;
                });
        }
    },
})