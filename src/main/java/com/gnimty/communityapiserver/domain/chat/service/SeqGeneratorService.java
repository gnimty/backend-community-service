package com.gnimty.communityapiserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeqGeneratorService {

	private final MongoOperations mongoOperations;


}
