package com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DictionaryWordResponse {
    Long id;
    String word;
    String partOfSpeech;
    String pronunciation;
    String meaningVi;
}
