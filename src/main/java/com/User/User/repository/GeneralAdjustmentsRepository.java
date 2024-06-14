package com.User.User.repository;

import com.User.User.models.GeneralAdjustments.ConfigEm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralAdjustmentsRepository extends JpaRepository<ConfigEm,Long> {
}
