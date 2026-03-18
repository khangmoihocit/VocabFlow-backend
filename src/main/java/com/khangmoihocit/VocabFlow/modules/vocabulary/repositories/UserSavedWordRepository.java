package com.khangmoihocit.VocabFlow.modules.vocabulary.repositories;

import com.khangmoihocit.VocabFlow.core.enums.AnkiStatus;
import com.khangmoihocit.VocabFlow.modules.user.entities.User;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.UserSavedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSavedWordRepository extends JpaRepository<UserSavedWord, Long>, JpaSpecificationExecutor<UserSavedWord> {

    boolean existsByUserIdAndDictionaryWordId(UUID userId, Long wordId);

    Optional<UserSavedWord> findByIdAndUserId(Long id, UUID userId);

    List<UserSavedWord> findByUserIdAndAnkiStatus(UUID userId, AnkiStatus ankiStatus);
}
