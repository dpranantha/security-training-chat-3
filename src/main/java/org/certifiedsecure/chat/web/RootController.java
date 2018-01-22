package org.certifiedsecure.chat.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.certifiedsecure.chat.persistence.dao.ChannelRepository;
import org.certifiedsecure.chat.persistence.model.Channel;
import org.certifiedsecure.chat.security.ChatUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller for /
 */
@Controller
@RequestMapping("/")
public class RootController {
	@Autowired
	private ChannelRepository channelRepository;

	/**
	 * GET handler for /: Render default template
	 * 
	 * @param model
	 *            Model containing variables for the template
	 * @param principal
	 *            Current user
	 * @return Template to render
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String application(Model model, @AuthenticationPrincipal ChatUserPrincipal principal) {
		if (principal == null) {
			model.addAttribute("username", "");
			model.addAttribute("channels", "");
		} else {
			model.addAttribute("username", principal.getUsername());
			List<Channel> channels = channelRepository.findByRealm(principal.getUser().getRealm());
			Map<String, Long> channelList = new HashMap<String, Long>();
			for (Channel channel : channels) {
				channelList.put(channel.getName(), channel.getId());
			}
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				model.addAttribute("channels", objectMapper.writeValueAsString(channelList));
			} catch (JsonProcessingException e) {
				model.addAttribute("channels", "");
			}
		}
		return "application";
	}
}