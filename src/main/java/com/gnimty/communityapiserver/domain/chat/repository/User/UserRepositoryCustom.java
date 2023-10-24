package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.global.constant.Status;

public interface UserRepositoryCustom {
	void updateStatus(User me, Status status);
}
