package com.gnimty.communityapiserver.domain.chat.repository.User;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long>, UserRepositoryCustom {

	Optional<User> findByActualUserId(Long userId);

	List<User> findAll();

}
