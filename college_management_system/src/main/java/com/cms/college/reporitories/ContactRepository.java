package com.cms.college.reporitories;

import com.cms.college.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Contact entity
public interface ContactRepository extends JpaRepository<Contact, Long> {}
