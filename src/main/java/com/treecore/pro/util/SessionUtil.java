package com.treecore.pro.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * HTTP 세션 관련 유틸리티 클래스
 */
public class SessionUtil {

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();

    private SessionUtil() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 현재 HTTP 세션에서 값을 가져옵니다.
     * 
     * @param key 값을 가져올 키
     * @return 키에 해당하는 값, 없으면 null
     */
    public static Object getSessionValue(String key) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return session.getAttribute(key);
    }
    
    /**
     * 현재 HTTP 세션에 값을 저장합니다.
     * 
     * @param key 저장할 키
     * @param value 저장할 값
     */
    public static void setSessionValue(String key, Object value) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return;
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute(key, value);
    }
    
    /**
     * 현재 HTTP 세션에서 값을 제거합니다.
     * 
     * @param key 제거할 키
     */
    public static void removeSessionValue(String key) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        
        session.removeAttribute(key);
    }
    
    /**
     * 현재 HTTP 요청을 가져옵니다.
     * 
     * @return 현재 HTTP 요청, 없으면 null
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = requestHolder.get();
        if (request != null) {
            return request;
        }
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        
        return attributes.getRequest();
    }
    
    /**
     * HTTP 요청 객체를 설정합니다.
     * 
     * @param request HTTP 요청 객체
     */
    public static void setRequest(HttpServletRequest request) {
        requestHolder.set(request);
    }
    
    /**
     * HTTP 요청 객체를 제거합니다.
     */
    public static void removeRequest() {
        requestHolder.remove();
    }
    
    /**
     * 현재 HTTP 응답을 가져옵니다.
     * 
     * @return 현재 HTTP 응답, 없으면 null
     */
    public static HttpServletResponse getResponse() {
        return responseHolder.get();
    }
    
    /**
     * HTTP 응답 객체를 설정합니다.
     * 
     * @param response HTTP 응답 객체
     */
    public static void setResponse(HttpServletResponse response) {
        responseHolder.set(response);
    }
    
    /**
     * HTTP 응답 객체를 제거합니다.
     */
    public static void removeResponse() {
        responseHolder.remove();
    }
} 