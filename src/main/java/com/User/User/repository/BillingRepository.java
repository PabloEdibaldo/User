package com.User.User.repository;

import com.User.User.models.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<Billing,Long> {
}
