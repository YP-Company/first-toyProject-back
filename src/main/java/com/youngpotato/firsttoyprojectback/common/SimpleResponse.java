package com.youngpotato.firsttoyprojectback.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleResponse<T> {

    private List<T> content;

    private int size;

    public static <T> SimpleResponse<T> of(List<T> content) {
        SimpleResponse<T> response = new SimpleResponse<>();
        response.content = content;
        response.size = CollectionUtils.isEmpty(content) ? 0 : content.size();

        return response;
    }
}
