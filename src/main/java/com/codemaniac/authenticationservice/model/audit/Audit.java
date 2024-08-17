package com.codemaniac.authenticationservice.model.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class Audit implements Serializable {
    private static final long serialVersionUID = 9048986408898460455L;
    public static final String PROGRAM = "WEB-APP";
    public static final String SYSTEM = "SYSTEM";
    public static final String RECORD_STATUS_DELETED = "D";
    public static final String RECORD_STATUS_ACTIVE = "A";

    @Column(name = "CREATED_BY")
    private String createdBy = "";

    @Column(name = "CREATE_MODULE")
    private String createModule = "";

    @Column(name = "CREATE_TIMESTAMP")
    private LocalDateTime createTimestamp;

    @Column(name = "RECORD_STATUS")
    private String recordStatus = RECORD_STATUS_ACTIVE;

    @Column(name = "UPDATED_BY")
    private String updatedBy = "";

    @Column(name = "UPDATE_MODULE")
    private String updateModule = "";

    @Column(name = "UPDATE_TIMESTAMP")
    private LocalDateTime updateTimestamp;

    public Audit() {
        // Default.
    }

    public Audit(String newCreatedBy, String newCreateModule) {
        createdBy = newCreatedBy;
        createModule = newCreateModule;
        createTimestamp = LocalDateTime.now();
        recordStatus = RECORD_STATUS_ACTIVE;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("createdBy", this.createdBy);
        builder.append("createModule", this.createModule);
        builder.append("createTimestamp", (this.createTimestamp != null) ? this.createTimestamp : null);

        builder.append("updatedBy", this.updatedBy);
        builder.append("updateModule", this.updateModule);
        builder.append("updateTimestamp", (this.updateTimestamp != null) ? this.updateTimestamp : null);

        builder.append("recordStatus", this.recordStatus);

        return builder.toString();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreateModule() {
        return createModule;
    }

    public LocalDateTime getCreateTimestamp() {
        return createTimestamp;
    }

    public String getLastModifiedBy() {
        if ((updatedBy != null) && (updatedBy.length() > 0)) {
            return updatedBy;
        }

        return createdBy;
    }

    public LocalDateTime getLastModifiedByTimestamp() {
        if (updateTimestamp != null) {
            return updateTimestamp;
        }

        return createTimestamp;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateModule() {
        return updateModule;
    }

    public LocalDateTime getUpdateTimestamp() {
        return updateTimestamp;
    }

    public boolean isRecordActive() {
        return RECORD_STATUS_ACTIVE.equals(recordStatus);
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreateModule(String createModule) {
        this.createModule = createModule;
    }

    public void setCreates(String newCreatedBy, String newCreateModule) {
        createdBy = newCreatedBy;
        createModule = newCreateModule;
        createTimestamp = LocalDateTime.now();
    }

    public void setCreateTimestamp(LocalDateTime createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdateModule(String updateModule) {
        this.updateModule = updateModule;
    }

    public void setUpdates(String newUpdatedBy, String newUpdateModule) {
        this.updatedBy = newUpdatedBy;
        this.updateModule = newUpdateModule;
        this.updateTimestamp = LocalDateTime.now();
    }

    public void setUpdateTimestamp(LocalDateTime updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}

