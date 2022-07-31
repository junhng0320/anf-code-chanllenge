package com.anf.core.services;

import com.anf.core.models.User;

public interface ContentService {
	boolean commitUserDetails(User user);
	String getUserAgeRange();
	String getNewfeedsDump();
}
