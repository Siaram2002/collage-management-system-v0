package com.cms.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cms.college.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
