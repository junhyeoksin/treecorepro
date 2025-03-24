package com.treecore.pro.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.treecore.pro.model.TreeNode;

/**
 * 트리 서비스 인터페이스
 */
public interface TreeService {

    /**
     * 노드를 조회합니다.
     * @param node 조회할 노드
     * @return 조회된 노드
     */
    TreeNode getNode(TreeNode node);

    /**
     * 노드를 추가합니다.
     * @param node 추가할 노드
     * @return 추가된 노드
     */
    TreeNode addNode(TreeNode node);

    /**
     * 노드를 수정합니다.
     * @param node 수정할 노드
     * @return 수정된 노드 수
     */
    int updateNode(TreeNode node);

    /**
     * 노드를 삭제합니다.
     * @param node 삭제할 노드
     * @return 삭제된 노드 수
     */
    int removeNode(TreeNode node);

    /**
     * 하위 노드를 조회합니다.
     * @param node 상위 노드
     * @return 하위 노드 목록
     */
    List<TreeNode> getChildNode(TreeNode node);

    /**
     * 노드를 이동합니다.
     * @param node 이동할 노드
     * @param request HTTP 요청
     * @return 이동된 노드 수
     */
    TreeNode moveNode(TreeNode node, HttpServletRequest request) throws Exception;
} 