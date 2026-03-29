package com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSegmentResponse {
    Long id;
    Integer segmentOrder;
    BigDecimal startTime;
    BigDecimal endTime;
    String englishText;
}
