package com.khangmoihocit.VocabFlow.modules.vocabulary.services.Impl;

import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.core.mapper.PageMapper;
import com.khangmoihocit.VocabFlow.core.security.UserDetailsCustom;
import com.khangmoihocit.VocabFlow.core.specification.GenericSpecificationBuilder;
import com.khangmoihocit.VocabFlow.core.utils.SortUtil;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.request.VocabularyGroupRequest;
import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.VocabularyGroupResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.VocabularyGroup;
import com.khangmoihocit.VocabFlow.modules.vocabulary.mappers.VocabularyGroupMapper;
import com.khangmoihocit.VocabFlow.modules.vocabulary.repositories.VocabularyGroupRepository;
import com.khangmoihocit.VocabFlow.modules.vocabulary.services.VocabularyGroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j(topic = "VOCABULARY GROUP SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VocabularyGroupServiceImpl implements VocabularyGroupService {
    VocabularyGroupRepository vocabularyGroupRepository;
    VocabularyGroupMapper vocabularyGroupMapper;
    PageMapper pageMapper;

    @Override
    public PageResponse<VocabularyGroupResponse> findAll(int pageNo, int pageSize, String sort, String keyword) {
        UserDetailsCustom userDetails = (UserDetailsCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, SortUtil.createSort(sort));
        GenericSpecificationBuilder<VocabularyGroup> builder = new GenericSpecificationBuilder<>();
        builder.with("userId", "=", userDetails.getId());
        if(StringUtils.hasText(keyword)) builder.with("name", "=", keyword);

        Specification<VocabularyGroup> specification = builder.build();
        Page<VocabularyGroup> vocabularyGroupPage = vocabularyGroupRepository.findAll(specification, pageable);
        List<VocabularyGroupResponse> vocabularyGroupResponseList = vocabularyGroupMapper.toVocabularyResponse(vocabularyGroupPage.getContent());

        return pageMapper.toPageResponse(vocabularyGroupPage, vocabularyGroupResponseList);
    }

    @Override
    public VocabularyGroupResponse create(VocabularyGroupRequest request) {
        UserDetailsCustom userDetails = (UserDetailsCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        VocabularyGroup vocabularyGroup = VocabularyGroup.builder()
                .userId(userDetails.getId())
                .name(request.getName())
                .build();

        vocabularyGroup = vocabularyGroupRepository.save(vocabularyGroup);
        return vocabularyGroupMapper.toVocabularyResponse(vocabularyGroup);
    }

    @Override
    @Transactional
    public VocabularyGroupResponse update(VocabularyGroupRequest request, Long id) {
        UserDetailsCustom userDetails = (UserDetailsCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        VocabularyGroup vocabularyGroup = vocabularyGroupRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(()->new AppException(ErrorCode.VOCABULARY_GROUP_NOT_EXISTS));

        vocabularyGroup.setName(request.getName());

        vocabularyGroup = vocabularyGroupRepository.save(vocabularyGroup);
        return vocabularyGroupMapper.toVocabularyResponse(vocabularyGroup);
    }

    @Override
    public void deleteById(Long id) {
        UserDetailsCustom userDetails = (UserDetailsCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        VocabularyGroup vocabularyGroup = vocabularyGroupRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(()->new AppException(ErrorCode.VOCABULARY_GROUP_NOT_EXISTS));
        vocabularyGroupRepository.delete(vocabularyGroup);
    }
}
