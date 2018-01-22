package org.certifiedsecure.chat.persistence.dao;

import org.certifiedsecure.chat.persistence.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provide access to the messages
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
}
