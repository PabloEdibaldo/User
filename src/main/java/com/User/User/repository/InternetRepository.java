package com.User.User.repository;

import com.User.User.models.Internet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternetRepository extends JpaRepository<Internet,Long> {


}
