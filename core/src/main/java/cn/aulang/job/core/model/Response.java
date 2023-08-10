package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回值
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS_CODE = 200;
    /**
     * 失败
     */
    public static final int FAIL_CODE = 500;
    /**
     * 网络错误
     */
    public static final int NET_ERROR = -1;

    /**
     * 代码
     */
    private int code;
    /**
     * 失败消息
     */
    private String message;
    /**
     * 成功数据
     */
    private T data;

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(T data) {
        this.code = SUCCESS_CODE;
        this.data = data;
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    public boolean isFail() {
        return code == FAIL_CODE;
    }

    public boolean isNetError() {
        return code == NET_ERROR;
    }

    public static <T> Response<T> success() {
        return new Response<>(null);
    }

    public static <T> Response<T> success(T content) {
        return new Response<>(content);
    }

    public static <T> Response<T> fail() {
        return new Response<>(FAIL_CODE, null);
    }

    public static <T> Response<T> fail(String message) {
        return new Response<>(FAIL_CODE, message);
    }

    public static <T> Response<T> fail(int code, String message) {
        return new Response<>(code, message);
    }

    public static <T> Response<T> netError() {
        return new Response<>(NET_ERROR, null);
    }

    public static <T> Response<T> netError(String message) {
        return new Response<>(NET_ERROR, message);
    }
}
