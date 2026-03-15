package com.khangmoihocit.VocabFlow.modules.vocabulary.services.Impl;

import com.khangmoihocit.VocabFlow.modules.vocabulary.services.DictionaryWordService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j(topic = "DICTIONARY WORD SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictionaryWordServiceImpl implements DictionaryWordService {
}
