package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.Status;
import com.gnimty.communityapiserver.domain.chat.entity.User;

public interface UserRepositoryCustom {

	void updateStatus(User me, Status status);
}
