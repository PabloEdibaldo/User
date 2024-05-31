package com.User.User.repository;

import com.User.User.models.MessageTwilio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageTwilio,Long> {
    MessageTwilio findByCaseMessage(int caseMessage);
}
