package com.netty.sever.error;

/**
 * @ClassName SeverException
 * @Description 业务异常
 * @Author EDZ
 * @Date 2020/5/19 15:12
 */
public class SeverException extends Exception {
    private int severCode;
    private String message;

    public SeverException() {
    }

    public SeverException(String message) {
        super(message);
        this.message = message;
    }

    public SeverException(int severCode, String message) {
        super(message);
        this.severCode = severCode;
        this.message = message;
    }

    public int getSeverCode() {
        return severCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
