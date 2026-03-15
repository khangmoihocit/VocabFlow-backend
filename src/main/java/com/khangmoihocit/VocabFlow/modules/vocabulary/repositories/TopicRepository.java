package com.khangmoihocit.VocabFlow.modules.vocabulary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<com.khangmoihocit.VocabFlow.modules.vocabulary.entities.Topic, Long> {
}
