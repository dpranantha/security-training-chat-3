package org.certifiedsecure.chat.web;

import java.sql.Timestamp;

import org.certifiedsecure.chat.persistence.dao.ChannelRepository;
import org.certifiedsecure.chat.persistence.dao.MessageRepository;
import org.certifiedsecure.chat.persistence.model.Channel;
import org.certifiedsecure.chat.persistence.model.Message;
import org.certifiedsecure.chat.security.ChatUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for /message
 */
@RestController
@RequestMapping("/message")
public class MessageController {
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private MessageRepository messageRepository;

	/**
	 * POST handler for /message: Add a message to a channel
	 * 
	 * Authorization: Users can only add messages for channels they have write
	 * permission for
	 * 
	 * @param principal
	 *            Current user
	 * @param channel_id
	 *            Id of the channel to add the message to
	 * @param message_text
	 *            Text of the message
	 * @return Created message
	 */
	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated() and hasPermission(#channel_id, 'Channel', 'write')")
	@Transactional
	public Message addMessage(@AuthenticationPrincipal ChatUserPrincipal principal,
			@RequestParam("channel") Long channel_id, @RequestParam("message") String message_text) {
		Channel channel = channelRepository.findOne(channel_id);
		Message message = new Message(channel, principal.getUser(), message_text,
				new Timestamp(System.currentTimeMillis()));
		message = messageRepository.save(message);
		return message;
	}
}
