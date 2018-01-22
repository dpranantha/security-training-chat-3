package org.certifiedsecure.chat.security;

import java.util.ArrayList;
import java.util.Collection;

import org.certifiedsecure.chat.persistence.dao.UserRepository;
import org.certifiedsecure.chat.persistence.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom UserDetails for Chat application
 */
public class ChatUserPrincipal implements UserDetails {
	private UserRepository userRepository;

	private static final long serialVersionUID = 996772007331934657L;
	private final User user;

	/**
	 * Constructor with user object and repository
	 * 
	 * @param user
	 *            User object for currently logged in user
	 * @param userRepository
	 *            Repository for users
	 */
	public ChatUserPrincipal(User user, UserRepository userRepository) {
		this.user = user;
		this.userRepository = userRepository;
	}

	/**
	 * Get the current User
	 * 
	 * @return current User
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Get the current user name
	 * 
	 * @return current user name
	 */
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	/**
	 * Get the current password
	 * 
	 * The password is the token
	 * 
	 * @return current token
	 */
	@Override
	public String getPassword() {
		return user.getToken();
	}

	/**
	 * Get list of authorizations.
	 * 
	 * Authorizations are hard coded to ROLE_USER
	 * 
	 * @return list of authorizations (hard coded to ["ROLE_USER"])
	 */
	@SuppressWarnings("serial")
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<GrantedAuthority>() {
			{
				add(new SimpleGrantedAuthority("ROLE_USER"));
			}
		};
	}

	/**
	 * Check if account is not expired
	 * 
	 * @return Always true
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Check if account is not locked
	 * 
	 * @return Always true
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Check if credentials are not expired
	 * 
	 * @return Always true
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Check if user is enabled
	 * 
	 * @return always true
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Authorization function to check if another user (by user id) is in the same
	 * realm as this user
	 * 
	 * @param user_id
	 *            Other user to check
	 * @return if the other user is within the same realm
	 */
	public boolean isInSameRealmAsUser(Long user_id) {
		User user = userRepository.findOne(user_id);
		if (user == null) {
			return false;
		}
		return user.getRealm().equals(this.getUser().getRealm());
	}
}