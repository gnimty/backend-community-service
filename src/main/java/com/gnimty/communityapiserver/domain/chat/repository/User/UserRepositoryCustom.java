package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.mongodb.bulk.BulkWriteResult;
import java.util.List;

public interface UserRepositoryCustom {

    BulkWriteResult bulkUpdate(List<User> users);
}
