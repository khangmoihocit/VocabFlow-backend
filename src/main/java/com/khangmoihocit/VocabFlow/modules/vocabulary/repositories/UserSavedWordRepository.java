package com.khangmoihocit.VocabFlow.modules.vocabulary.repositories;

import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.UserSavedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSavedWordRepository extends JpaRepository<UserSavedWord, Long> {

    boolean existsByUserIdAndDictionaryWordId(UUID userId, Long wordId);
}
