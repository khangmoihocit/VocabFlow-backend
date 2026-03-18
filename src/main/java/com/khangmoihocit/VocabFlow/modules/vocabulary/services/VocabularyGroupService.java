package com.khangmoihocit.VocabFlow.modules.vocabulary.services;

import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.VocabularyGroupRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.VocabularyGroupResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.VocabularyGroup;

public interface VocabularyGroupService {
    PageResponse<VocabularyGroupResponse> findAll(int pageNo, int pageSize, String sort, String keyword);
    VocabularyGroupResponse create(VocabularyGroupRequest request);
    VocabularyGroupResponse update(VocabularyGroupRequest request, Long id);
    void deleteById(Long id);
}
