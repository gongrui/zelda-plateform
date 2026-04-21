package com.blue.zelda.fw.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 验证型异常
 *
 * @author gongrui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ValidException  extends RuntimeException{

    private int code;

    public ValidException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    public ValidException(String message) {
        super(message);
    }

    public ValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidException(Throwable cause) {
        super(cause);
    }

    public ValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
