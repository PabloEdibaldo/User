package com.User.User.repository;

import com.User.User.models.ContentBilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentBillingRepository extends JpaRepository<ContentBilling,Long> {

    List<ContentBilling> findByIdBilling(Long idBilling);
}
