package com.treecore.pro.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.treecore.pro.util.SessionUtil;

/**
 * 라우팅 테이블 인터셉터
 * 트리 노드 테이블을 동적으로 변경하기 위한 인터셉터
 */
@Component
public class RouteTableInterceptor implements HandlerInterceptor {

    private static final String DEFAULT_TABLE_NAME = "tree_node";
    private static final String TABLE_NAME_KEY = "treeNodeTableName";

    /**
     * 트리 노드 테이블 이름을 설정합니다.
     * @param session HTTP 세션
     * @param tableName 테이블 이름
     */
    public static void setTreeNodeReplaceTableName(HttpSession session, String tableName) {
        if (session != null) {
            session.setAttribute(TABLE_NAME_KEY, tableName);
        }
    }

    /**
     * 트리 노드 테이블 이름을 가져옵니다.
     * @param session HTTP 세션
     * @return 테이블 이름
     */
    public static String getTreeNodeReplaceTableName(HttpSession session) {
        if (session != null) {
            Object tableName = session.getAttribute(TABLE_NAME_KEY);
            return tableName != null ? (String) tableName : DEFAULT_TABLE_NAME;
        }
        return DEFAULT_TABLE_NAME;
    }

    /**
     * SQL 준비 단계에서 테이블 이름을 동적으로 변경합니다.
     * @param sql 원본 SQL 쿼리
     * @return 변경된 SQL 쿼리
     */
    public String onPrepareStatement(String sql) {
        if (sql != null && sql.contains(DEFAULT_TABLE_NAME)) {
            String tableName = (String) SessionUtil.getSessionValue(TABLE_NAME_KEY);
            return sql.replace(DEFAULT_TABLE_NAME, tableName != null ? tableName : DEFAULT_TABLE_NAME);
        }
        return sql;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SessionUtil.setRequest(request);
        SessionUtil.setResponse(response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionUtil.removeRequest();
        SessionUtil.removeResponse();
    }
} 