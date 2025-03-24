package com.treecore.service;

import java.io.Serializable;
import java.util.List;

/**
 * 트리 서비스 인터페이스
 * 기존 egovframework의 TreeService 인터페이스와 호환되는 메소드들을 제공합니다.
 */
public interface TreeService extends Serializable {

    /**
     * 노드를 조회합니다.
     * @param entity 조회할 노드 엔티티
     * @return 조회된 노드 엔티티
     * @throws Exception 예외 발생 시
     */
    <T> T getNode(T entity) throws Exception;
    
    /**
     * 자식 노드 목록을 조회합니다.
     * @param entity 부모 노드 엔티티
     * @return 자식 노드 목록
     * @throws Exception 예외 발생 시
     */
    <T> List<T> getChildNode(T entity) throws Exception;
    
    /**
     * 노드를 추가합니다.
     * @param entity 추가할 노드 엔티티
     * @return 추가된 노드 엔티티
     * @throws Exception 예외 발생 시
     */
    <T> T addNode(T entity) throws Exception;
    
    /**
     * 노드를 수정합니다.
     * @param entity 수정할 노드 엔티티
     * @return 수정된 노드 엔티티
     * @throws Exception 예외 발생 시
     */
    <T> T updateNode(T entity) throws Exception;
    
    /**
     * 노드를 삭제합니다.
     * @param entity 삭제할 노드 엔티티
     * @throws Exception 예외 발생 시
     */
    <T> void removeNode(T entity) throws Exception;
} 