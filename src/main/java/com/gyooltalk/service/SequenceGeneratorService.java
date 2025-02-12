package com.gyooltalk.service;

import com.gyooltalk.entity.DatabaseSequences;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public long generateSequence(String sequenceName) {
        DatabaseSequences counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                DatabaseSequences.class
        );
        return counter != null ? counter.getSeq() : 1;
    }


}
