package com.User.User.repository;

import com.User.User.models.Servers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Servers,Long> {
}
