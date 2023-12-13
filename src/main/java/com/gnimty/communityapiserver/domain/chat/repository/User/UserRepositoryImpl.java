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
public class UserRepositoryImpl implements UserRepositoryCustom {

	@Autowired
	private final MongoTemplate mongoTemplate;

	@Override
	public BulkWriteResult bulkUpdate(List<User> users) {
		BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED, "user");

		for (User user : users) {
			Query query = new Query(Criteria.where("actualUserId").is(user.getActualUserId()));
			Update update = new Update()
				.set("name", user.getName())
				.set("tagLine", user.getTagLine())
				.set("internalTagName", user.getInternalTagName())
				.set("status", user.getStatus())
				.set("profileIconId", user.getProfileIconId())
				.set("puuid", user.getPuuid())
				// 솔로 랭크
				.set("tier", user.getTier())
				.set("division", user.getDivision())
				.set("lp", user.getLp())
				.set("mmr", user.getMmr())
				.set("frequentLane1", user.getFrequentLane1())
				.set("frequentLane2", user.getFrequentLane2())
				.set("frequentChampionId1", user.getFrequentChampionId1())
				.set("frequentChampionId2", user.getFrequentChampionId2())
				.set("frequentChampionId3", user.getFrequentChampionId3())
				// 자유 랭크
				.set("tier", user.getTierFlex())
				.set("division", user.getDivisionFlex())
				.set("lp", user.getLpFlex())
				.set("mmr", user.getMmrFlex())
				.set("frequentLane1Flex", user.getFrequentLane1Flex())
				.set("frequentLane2Flex", user.getFrequentLane2Flex())
				.set("frequentChampionId1Flex", user.getFrequentChampionId1Flex())
				.set("frequentChampionId2Flex", user.getFrequentChampionId2Flex())
				.set("frequentChampionId3Flex", user.getFrequentChampionId3Flex());

			bulkOperations.updateOne(query, update);
		}

		return bulkOperations.execute();
	}

}
