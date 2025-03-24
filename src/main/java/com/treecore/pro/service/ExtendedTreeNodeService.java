package com.treecore.pro.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.treecore.pro.model.TreeNode;
import com.treecore.pro.model.TreeNodeSearch;

/**
 * 확장된 트리 노드 서비스 인터페이스
 */
public interface ExtendedTreeNodeService {

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
    int moveNode(TreeNode node, HttpServletRequest request);

    /**
     * 노드를 검색합니다.
     * @param searchDTO 검색 조건
     * @return 검색된 노드 목록
     */
    List<TreeNode> searchNode(TreeNodeSearch searchDTO);

    /**
     * 노드를 복사합니다.
     * @param node 복사할 노드
     * @param request HTTP 요청
     * @return 복사된 노드 수
     */
    int copyNode(TreeNode node, HttpServletRequest request);

    /**
     * 노드를 일괄 추가합니다.
     * @param nodes 추가할 노드 목록
     * @return 추가된 노드 수
     */
    int bulkInsert(List<TreeNode> nodes);

    /**
     * 노드를 일괄 수정합니다.
     * @param nodes 수정할 노드 목록
     * @return 수정된 노드 수
     */
    int bulkUpdate(List<TreeNode> nodes);
} 