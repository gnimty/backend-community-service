package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.mongodb.bulk.BulkWriteResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Override
    public BulkWriteResult bulkUpdate(List<User> users) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED, "user");

        for (User user : users) {
            Query query = new Query(Criteria.where("actualUserId").is(user.getActualUserId()));
            Update update = new Update()
                .set("profileIconId", user.getProfileIconId())
                .set("tier", user.getTier())
                .set("division", user.getDivision())
                .set("lp", user.getLp())
                .set("name", user.getName())
                .set("tagLine", user.getTagLine())
                .set("mostLanes", user.getMostLanes())
                .set("mostChampions", user.getMostChampions());

            bulkOperations.updateOne(query, update);
        }

        BulkWriteResult result = bulkOperations.execute();

        return result;
    }

}
