package org.coursera.cybersecurity.capstone.group8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthAudit {
	private Logger log = LoggerFactory.getLogger(AuthAudit.class);

	@EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
         
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        WebAuthenticationDetails details = 
                (WebAuthenticationDetails) auditEvent.getData().get("details");
        log.info(auditEvent.getType() + " " + auditEvent.getPrincipal() + " " + details.getRemoteAddress());
    }
}
