package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
