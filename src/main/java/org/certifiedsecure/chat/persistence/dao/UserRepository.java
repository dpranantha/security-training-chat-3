package org.certifiedsecure.chat.persistence.dao;

import org.certifiedsecure.chat.persistence.model.Realm;
import org.certifiedsecure.chat.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provide access to the users
 */
public interface UserRepository extends JpaRepository<User, Long> {
	/**
	 * Search user based on username and realm
	 * 
	 * @param username
	 *            Username to search for
	 * @param realm
	 *            Realm to search for
	 * @return user that was found
	 */
	User findByUsernameAndRealm(String username, Realm realm);

	/**
	 * Search user based on token
	 * 
	 * @param token
	 *            Token to search for
	 * @return user that was found
	 */
	User findByToken(String token);
}
