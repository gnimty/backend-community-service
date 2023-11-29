package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.global.constant.Status;
import com.mongodb.bulk.BulkWriteResult;
import java.util.List;

public interface UserRepositoryCustom {

    BulkWriteResult bulkUpdate(List<User> users);
}
