package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table(name = "T_SCHEMA")
public class SchemaEntry {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "SCHEMA_ID")
    private Integer schemaId;

    @Column(name = "XML_SCHEMA")
    private String xmlSchema;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DTD_PUBLIC_ID")
    private String dtdPublicId;

    @Column(name = "VALIDATE")
    private String validate;

    @Column(name = "SCHEMA_LANG")
    private String schemaLang;

    @Column(name = "EXPIRE_DATE")
    private LocalDate expireDate;

    @Column(name = "BLOCKER")
    private String blocker;

    @Column(name = "MAX_EXECUTION_TIME")
    private BigInteger maxExecutionTime;

    public SchemaEntry() {
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public SchemaEntry setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
        return this;
    }

    public String getXmlSchema() {
        return xmlSchema;
    }

    public SchemaEntry setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SchemaEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDtdPublicId() {
        return dtdPublicId;
    }

    public SchemaEntry setDtdPublicId(String dtdPublicId) {
        this.dtdPublicId = dtdPublicId;
        return this;
    }

    public String getValidate() {
        return validate;
    }

    public SchemaEntry setValidate(String validate) {
        this.validate = validate;
        return this;
    }

    public String getSchemaLang() {
        return schemaLang;
    }

    public SchemaEntry setSchemaLang(String schemaLang) {
        this.schemaLang = schemaLang;
        return this;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public SchemaEntry setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public String getBlocker() {
        return blocker;
    }

    public SchemaEntry setBlocker(String blocker) {
        this.blocker = blocker;
        return this;
    }

    public BigInteger getMaxExecutionTime() {
        return maxExecutionTime;
    }

    public SchemaEntry setMaxExecutionTime(BigInteger maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
        return this;
    }
}











