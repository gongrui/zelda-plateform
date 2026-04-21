package com.blue.zelda.fw.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dict<T> {
    /**
     * 值
     */
    private T value;

    /**
     * 标题
     */
    private String label;
}
