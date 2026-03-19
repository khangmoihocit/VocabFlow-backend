package com.khangmoihocit.VocabFlow.modules.user.services;

import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.modules.user.dtos.response.UserResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserResponse> getAll();
    PageResponse<UserResponse> getUsers(int pageNo, int pageSize, String sort, String keyword);
}
