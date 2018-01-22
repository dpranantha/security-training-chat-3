package org.certifiedsecure.chat.persistence.dao;

import java.util.List;

import org.certifiedsecure.chat.persistence.model.Channel;
import org.certifiedsecure.chat.persistence.model.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provide access to the channels
 */
public interface ChannelRepository extends JpaRepository<Channel, Long> {
	/**
	 * Return list of channels of a realm
	 * 
	 * @param realm
	 *            Realm to return channels of
	 * @return list of channels
	 */
	List<Channel> findByRealm(Realm realm);
}
