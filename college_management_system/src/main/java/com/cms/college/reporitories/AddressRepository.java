package com.cms.college.reporitories;


import com.cms.college.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Address entity
public interface AddressRepository extends JpaRepository<Address, Long> {}

