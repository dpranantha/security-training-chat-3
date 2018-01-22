package org.certifiedsecure.chat.persistence.dao;

import org.certifiedsecure.chat.persistence.model.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provide access to the realms
 */
public interface RealmRepository extends JpaRepository<Realm, Long> {
	/**
	 * Search for realm by name
	 * 
	 * @param name
	 *            Name of the realm to search for
	 * @return the realm that was found
	 */
	Realm findByName(String name);
}
