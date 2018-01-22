package org.certifiedsecure.chat.web;

import org.certifiedsecure.chat.persistence.dao.ChannelRepository;
import org.certifiedsecure.chat.persistence.model.Channel;
import org.certifiedsecure.chat.security.ChatUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for /channel
 */
@RestController
@RequestMapping("/channel")
public class ChannelController {
	@Autowired
	private ChannelRepository channelRepository;

	/**
	 * GET handler for /channel: Return all channels with their messages, etc.
	 * 
	 * Authorization: Users will only see channels they have read permission for
	 * 
	 * @param principal
	 *            Current user
	 * @return List of all channels
	 */
	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	@PostFilter("hasPermission(filterObject, 'read')")
	@Transactional
	public Iterable<Channel> getChannels(@AuthenticationPrincipal ChatUserPrincipal principal) {
		return this.channelRepository.findByRealm(principal.getUser().getRealm());
	}
}