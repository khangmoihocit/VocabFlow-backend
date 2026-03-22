package com.khangmoihocit.VocabFlow.core.utils;

import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.core.security.UserDetailsCustom;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserDetailUtil {
    public static UserDetailsCustom get(){
        UserDetailsCustom userDetailsCustom =
                (UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userDetailsCustom == null) throw new AppException(ErrorCode.USER_DETAIL_IS_NULL);
        return userDetailsCustom;
    }
}
