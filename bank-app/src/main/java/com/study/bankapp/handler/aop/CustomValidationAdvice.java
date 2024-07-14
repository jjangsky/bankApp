package com.study.bankapp.handler.aop;

import com.study.bankapp.dto.ResponseDto;
import com.study.bankapp.handler.ex.CustomValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect // 관점으로 등록
public class CustomValidationAdvice {
    /**
     * Json 객체로 넘어오는 건 Put과 Post 요청밖에 없어서 두개만 검즘
     */

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){

    }
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){

    }

    // pointCut을 실행하는 Method

    /**
     * `@PostMapping` 또는 `@PutMapping` 이 붙은 어노테이션이 실행이 될 때
     * BindingResult 매개변수가 존재하고 에러가 존재하면 해당 메소드를 실행하지만
     * 에러가 없을 경우 정상 로직 작동
     */
    @Around("postMapping() || putMapping()") // `@Around`는 joinPoint의 전후 제어가 가능함
    public Object validationAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs(); // joinPoint의 매개변수
        for(Object arg : args){
            if(arg instanceof BindingResult){
                BindingResult bindingResult = (BindingResult) arg;

                if(bindingResult.hasErrors()){
                    Map<String, String> errorMap = new HashMap<>();

                    for(FieldError error : bindingResult.getFieldErrors()){
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성검사 실패", errorMap);
                }
            }
        }
        return pjp.proceed();
    }
}
