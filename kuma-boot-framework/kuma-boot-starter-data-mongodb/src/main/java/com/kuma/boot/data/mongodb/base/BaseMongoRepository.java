package com.kuma.boot.data.mongodb.base;

import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaseMongoRepository<E extends BaseMongoEntity, ID extends Serializable> extends MongoRepository<E, ID> {
}
