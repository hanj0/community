package com.han.community.debug;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;

import java.time.Instant;
import java.util.Set;

public record SessionInfo(
        String id,
        String username,
        Instant creationTime,
        Instant lastAccessedTime,
        long maxInactiveSeconds,
        Long ttlSeconds,
        Set<String> attributeNames
) {
    static SessionInfo from(Session session, Long ttl) {

        SecurityContext ctx = session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );
        String username = (ctx != null && ctx.getAuthentication() != null)
                ? ctx.getAuthentication().getName()
                : null;

        return new SessionInfo(
                session.getId(),
                username,
                session.getCreationTime(),
                session.getLastAccessedTime(),
                session.getMaxInactiveInterval().toSeconds(),
                ttl,
                session.getAttributeNames()
        );
    }
}
