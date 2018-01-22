package org.certifiedsecure.chat.web;

import java.util.concurrent.TimeUnit;

import org.certifiedsecure.chat.persistence.dao.UserRepository;
import org.certifiedsecure.chat.security.ChatUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for /avatar
 */
@RestController
@RequestMapping("/avatar")
public class AvatarController {
	@Autowired
	private UserRepository userRepository;

	/**
	 * GET controller for /avatar: Return the avatar of a user
	 * 
	 * Authorization: Users can only view the avatar of users within the same realm
	 * 
	 * @param principal
	 *            Current user
	 * @param user_id
	 *            Id of the user to return the avatar of
	 * @return The avatar of the user
	 */
	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated() and #principal.isInSameRealmAsUser(#user_id)")
	@Transactional
	public ResponseEntity<byte[]> read(@AuthenticationPrincipal ChatUserPrincipal principal,
			@RequestParam("user_id") Long user_id) {
		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
				.body(userRepository.findOne(user_id).getAvatar());
	}
}