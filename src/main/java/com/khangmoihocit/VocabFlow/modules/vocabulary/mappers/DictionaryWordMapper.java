package com.khangmoihocit.VocabFlow.modules.vocabulary.mappers;

import com.khangmoihocit.VocabFlow.modules.vocabulary.dtos.response.LookupResponse;
import com.khangmoihocit.VocabFlow.modules.vocabulary.entities.DictionaryWord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DictionaryWordMapper {

    @Mapping(source = "id", target = "dictionaryWordId") //map khác trường
    LookupResponse toLookupResponse(DictionaryWord word);
}
