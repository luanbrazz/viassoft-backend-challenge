package com.viassoft.emailservice.repository;

import com.viassoft.emailservice.entity.EmailAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuditRepository extends JpaRepository<EmailAudit, Long> {
}