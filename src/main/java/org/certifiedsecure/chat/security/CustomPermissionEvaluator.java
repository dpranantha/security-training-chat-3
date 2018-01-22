package org.certifiedsecure.chat.security;

import java.io.Serializable;

import org.certifiedsecure.chat.persistence.dao.ChannelRepository;
import org.certifiedsecure.chat.persistence.model.Channel;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * Custom permission evaluator
 * 
 * Implement hasPermission(channel object, 'read/write') and
 * hasPermission(channel id, 'Channel', 'write/write')
 */
public class CustomPermissionEvaluator implements PermissionEvaluator {
	private ChannelRepository channelRepository;

	public CustomPermissionEvaluator(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}

	/**
	 * Implement hasPermission on object
	 * 
	 * @param auth
	 *            Current authentication
	 * @param targetDomainObject
	 *            Object to check permission for
	 * @param permission
	 *            Permission to verify
	 * @return result of permission check
	 */
	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
			return false;
		}
		if (!(targetDomainObject instanceof Channel)) {
			return false;
		}
		Channel target = (Channel) targetDomainObject;
		return hasPrivilege(auth, target, permission.toString().toUpperCase());
	}

	/**
	 * Implement hasPermission by id and type
	 * 
	 * @param auth
	 *            Current authentication
	 * @param targetId
	 *            Id of the object to check permission for
	 * @param targetType
	 *            Type of the object to check permission for
	 * @param permission
	 *            Permission to verify
	 * @return result of permission check
	 */
	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
			return false;
		}
		if (!(targetId instanceof Long)) {
			return false;
		}
		if (!(targetType.toUpperCase().equals(Channel.class.getSimpleName().toUpperCase()))) {
			return false;
		}
		Channel target = channelRepository.findOne((Long) targetId);
		if (target == null) {
			return false;
		}
		return hasPrivilege(auth, target, permission.toString().toUpperCase());
	}

	/**
	 * Implement the actual permission check for channels
	 * 
	 * @param auth
	 *            Current authentication
	 * @param target
	 *            Channel to check permission for
	 * @param permission
	 *            Permission to check (currently ignored)
	 * @return result of permission check
	 */
	private boolean hasPrivilege(Authentication auth, Channel target, String permission) {
		ChatUserPrincipal principal = (ChatUserPrincipal) auth.getPrincipal();
		if (!(principal.getUser().getRealm().equals(target.getRealm()))) {
			return false;
		}
		if (!target.getUsers().contains(principal.getUser())) {
			return false;
		}
		return true;
	}
}
