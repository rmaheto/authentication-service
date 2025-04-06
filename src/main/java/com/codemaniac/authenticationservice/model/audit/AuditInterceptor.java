package com.codemaniac.authenticationservice.model.audit;

import com.codemaniac.authenticationservice.security.SecurityUtils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class AuditInterceptor {

  @PrePersist
  public void setCreationAudit(Object entity) {
    if (entity instanceof Auditable auditable) {
      Audit audit = auditable.getAudit();
      audit.setCreates(SecurityUtils.getCurrentUsername(), Audit.PROGRAM);
    }
  }

  @PreUpdate
  public void setUpdateAudit(Object entity) {
    if (entity instanceof Auditable auditable) {
      Audit audit = auditable.getAudit();
      if (ObjectUtils.isEmpty(audit)) {
        audit = new Audit();
      }
      audit.setUpdates(SecurityUtils.getCurrentUsername(), Audit.PROGRAM);
    }
  }
}
