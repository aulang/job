package cn.aulang.job.admin.controller;

import cn.aulang.common.web.WebExceptionHandler;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理类
 *
 * @author wulang
 */
@Slf4j
@RestControllerAdvice
public class JobExceptionHandler extends WebExceptionHandler {

    @ExceptionHandler(value = JobException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<String> exceptionHandler(JobException e) {
        log.warn(e.getMessage());
        return Response.fail(e.getMessage());
    }
}
