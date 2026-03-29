package com.khangmoihocit.VocabFlow.modules.youtube_learning.services.Impl;

import com.khangmoihocit.VocabFlow.core.utils.UserDetailUtil;
import com.khangmoihocit.VocabFlow.modules.user.entities.User;
import com.khangmoihocit.VocabFlow.modules.user.repositories.UserRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.AttemptRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.UserSegmentAttemptResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.UserSegmentAttempt;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoSegment;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers.UserSegmentAttemptMapper;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.UserSegmentAttemptRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.VideoSegmentRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.services.UserSegmentAttemptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j(topic = "Video segment attempts SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSegmentAttemptServiceImpl implements UserSegmentAttemptService {
    UserSegmentAttemptRepository userSegmentAttemptRepository;
    VideoSegmentRepository videoSegmentRepository;
    UserSegmentAttemptMapper userSegmentAttemptMapper;
    UserRepository userRepository;

    @Override
    @Transactional
    public List<UserSegmentAttemptResponse> save(List<AttemptRequest> requests) {
        UUID currentUserId = UserDetailUtil.get().getId();
        User user = userRepository.findById(currentUserId).orElseThrow();

        List<Long> segmentIds = requests.stream()
                .map(AttemptRequest::getSegmentId)
                .collect(Collectors.toList());

        // Lấy các VideoSegment (chuỗi gốc)
        Map<Long, VideoSegment> segmentMap = videoSegmentRepository.findAllById(segmentIds).stream()
                .collect(Collectors.toMap(VideoSegment::getId, Function.identity()));

        // Lấy các Attempt cũ của user này (để Update thay vì Insert mới nếu đã học rồi)
        Map<Long, UserSegmentAttempt> existingAttempts = userSegmentAttemptRepository
                .findByUserIdAndSegmentIdIn(currentUserId, segmentIds).stream()
                .collect(Collectors.toMap(attempt -> attempt.getSegment().getId(), Function.identity()));

        List<UserSegmentAttempt> attemptsToSave = new ArrayList<>();

        for (AttemptRequest request : requests) {
            VideoSegment segment = segmentMap.get(request.getSegmentId());
            if (segment == null) {
                log.warn("Segment ID {} không tồn tại", request.getSegmentId());
                continue;
            }

            // Tính toán điểm Dictation
            Integer score = calculateDictationScore(segment.getEnglishText(), request.getDictationUserText());

            // Upsert: Lấy bản ghi cũ hoặc tạo mới
            UserSegmentAttempt attempt = existingAttempts.getOrDefault(
                    request.getSegmentId(),
                    UserSegmentAttempt.builder()
                            .user(user)
                            .segment(segment)
                            .shadowingScore(0)
                            .isMastered(false)
                            .build()
            );

            // Cập nhật thông tin mới
            attempt.setDictationUserText(request.getDictationUserText());
            attempt.setDictationScore(score);
            attempt.setUpdatedAt(LocalDateTime.now());

            // Nếu đạt 100% dictation và shadowing cũng cao thì có thể set Mastered
            // attempt.setIsMastered(score == 100 && attempt.getShadowingScore() > 80);

            attemptsToSave.add(attempt);
        }

        List<UserSegmentAttempt> savedAttempts = userSegmentAttemptRepository.saveAll(attemptsToSave);

        return savedAttempts.stream()
                .map(userSegmentAttemptMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Hàm chuẩn hóa và tính điểm phần trăm giống nhau giữa 2 chuỗi
     */
    private Integer calculateDictationScore(String originalText, String userText) {
        if (originalText == null) return 0;
        if (userText == null) userText = "";

        // Chuẩn hóa: Đưa về chữ thường, xóa tất cả các ký tự không phải chữ/số (dấu câu), và gom khoảng trắng thừa
        String normalizedOriginal = originalText.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().trim().replaceAll("\\s+", " ");
        String normalizedUser = userText.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().trim().replaceAll("\\s+", " ");

        if (normalizedOriginal.isEmpty()) return 100;
        if (normalizedUser.isEmpty()) return 0;

        int distance = calculateLevenshteinDistance(normalizedOriginal, normalizedUser);
        int maxLength = Math.max(normalizedOriginal.length(), normalizedUser.length());

        // Công thức tính phần trăm: (1 - khoảng_cách / độ_dài_max) * 100
        double percentage = (1.0 - (double) distance / maxLength) * 100;

        return (int) Math.round(Math.max(0, percentage));
    }

    /**
     * Thuật toán Levenshtein Distance (Khoảng cách chỉnh sửa)
     */
    private int calculateLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
        }
        return dp[a.length()][b.length()];
    }
}