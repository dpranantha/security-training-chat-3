package org.certifiedsecure.chat.security;

import org.certifiedsecure.chat.persistence.dao.UserRepository;
import org.certifiedsecure.chat.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service to load users from their tokens
 */
@Service
public class ChatUserDetailsService {
	@Autowired
	private UserRepository userRepository;

	/**
	 * Return ChatUserPrincipal based on token
	 * 
	 * @param token
	 *            token to look for
	 * @return principal with the user
	 */
	public UserDetails loadUserByToken(final String token) {
		final User user = userRepository.findByToken(token);
		if (user == null) {
			return null;
		}
		return new ChatUserPrincipal(user, userRepository);
	}
}