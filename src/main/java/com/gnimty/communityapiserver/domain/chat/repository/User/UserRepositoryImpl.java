package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.mongodb.bulk.BulkWriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Override
    public BulkWriteResult bulkUpdate(List<User> users) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED, "user");

        for (User user : users) {
            Query query = new Query(Criteria.where("actualUserId").is(user.getActualUserId()));
            Update update = new Update();
            setIfNotNull(update, "name", user.getName());
            setIfNotNull(update, "tagLine", user.getTagLine());
            setIfNotNull(update, "internalTagName", user.getInternalTagName());
            setIfNotNull(update, "nowStatus", user.getNowStatus());
            setIfNotNull(update, "selectedStatus", user.getSelectedStatus());
            setIfNotNull(update, "profileIconId", user.getProfileIconId());
            setIfNotNull(update, "puuid", user.getPuuid());
            // 솔로 랭크
            setIfNotNull(update, "tier", user.getTier());
            setIfNotNull(update, "division", user.getDivision());
            setIfNotNull(update, "lp", user.getLp());
            setIfNotNull(update, "mmr", user.getMmr());
            setIfNotNull(update, "frequentLane1", user.getFrequentLane1());
            setIfNotNull(update, "frequentLane2", user.getFrequentLane2());
            setIfNotNull(update, "frequentChampionId1", user.getFrequentChampionId1());
            setIfNotNull(update, "frequentChampionId2", user.getFrequentChampionId2());
            setIfNotNull(update, "frequentChampionId3", user.getFrequentChampionId3());
            // 자유 랭크
            setIfNotNull(update, "tier", user.getTierFlex());
            setIfNotNull(update, "division", user.getDivisionFlex());
            setIfNotNull(update, "lp", user.getLpFlex());
            setIfNotNull(update, "mmr", user.getMmrFlex());
            setIfNotNull(update, "frequentLane1Flex", user.getFrequentLane1Flex());
            setIfNotNull(update, "frequentLane2Flex", user.getFrequentLane2Flex());
            setIfNotNull(update, "frequentChampionId1Flex", user.getFrequentChampionId1Flex());
            setIfNotNull(update, "frequentChampionId2Flex", user.getFrequentChampionId2Flex());
            setIfNotNull(update, "frequentChampionId3Flex", user.getFrequentChampionId3Flex());

            bulkOperations.updateOne(query, update);
        }

        return bulkOperations.execute();
    }

    private void setIfNotNull(Update update, String field, Object value) {
        if (value != null) {
            update.set(field, value);
        }
    }

}
