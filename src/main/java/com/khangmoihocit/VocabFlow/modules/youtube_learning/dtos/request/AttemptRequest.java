package com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttemptRequest {
    @NotNull
    Long segmentId;
    String dictationUserText;
}
