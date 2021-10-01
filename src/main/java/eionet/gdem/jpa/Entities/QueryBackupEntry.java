package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_BACKUP")
public class QueryBackupEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BACKUP_ID")
    private Integer backupId;

    @Column(name = "OBJECT_ID")
    private Integer objectId;

    @Column(name = "F_TIMESTAMP")
    private Timestamp fTimestamp;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "USER")
    private String user;

    public QueryBackupEntry() {
    }

    public QueryBackupEntry(QueryBackupEntryBuilder builder) {
        this.backupId = builder.backupId;
        this.objectId = builder.objectId;
        this.fTimestamp = builder.fTimestamp;
        this.fileName = builder.fileName;
        this.user = builder.user;
    }

    public static class QueryBackupEntryBuilder {

        private Integer backupId;
        private Integer objectId;
        private Timestamp fTimestamp;
        private String fileName;
        private String user;

        public QueryBackupEntryBuilder setBackupId(Integer backupId) {
            this.backupId = backupId;
            return this;
        }

        public QueryBackupEntryBuilder setObjectId(Integer objectId) {
            this.objectId = objectId;
            return this;
        }

        public QueryBackupEntryBuilder setfTimestamp(Timestamp fTimestamp) {
            this.fTimestamp = fTimestamp;
            return this;
        }

        public QueryBackupEntryBuilder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public QueryBackupEntryBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public QueryBackupEntry build() {
            return new QueryBackupEntry(this);
        }
    }

    public Integer getBackupId() {
        return backupId;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public Timestamp getfTimestamp() {
        return fTimestamp;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUser() {
        return user;
    }
}
