package com.blue.zelda.fw.config;

import cn.dev33.satoken.context.SaHolder;
import com.blue.zelda.fw.core.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MultipartException.class)
    public Result<ResponseEntity<Void>> handlerException(MultipartException e) {
        return Result.fail("文件太大了");
    }

    @ExceptionHandler(value = NullPointerException.class)
    public Result<ResponseEntity<Void>> nullPointerException(NullPointerException e) {
        log.error("堆栈异常 = > ", e);
        log.error("null exception => http request uri => {},message => {}", SaHolder.getRequest().getUrl(), e.getLocalizedMessage());
        return Result.fail(e.getLocalizedMessage());
    }
//
//    @ExceptionHandler(value= NoResourceFoundException.class)
//    public Result<ResponseEntity<Void>> noResourceFoundException(NoResourceFoundException e){
//        // 修复：只用路径匹配favicon，不走完整URL
//        String path = SaHolder.getRequest().getRequestPath();
//        if ("/favicon.ico".equals(path)) {
//            return Result.ok(null);
//        }
//
//        // 你原有日志完全保留不动
//        log.error("堆栈异常 = > ", e);
//        log.error("no resource found exception => http request uri => {},message => {}",
//                SaHolder.getRequest().getUrl(),
//                e.getLocalizedMessage());
//        return Result.fail(e.getLocalizedMessage());
//    }

    /**
     * 全局异常处理 保底
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("全局异常：", e);
        return Result.fail("服务器异常");
    }

}