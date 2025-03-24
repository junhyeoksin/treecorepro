package com.treecore.pro.service;

import java.util.List;

import com.treecore.pro.model.TreeNode;

/**
 * 트리 노드 서비스 인터페이스
 */
public interface TreeNodeService {

    /**
     * ID로 노드를 조회합니다.
     * @param id 노드 ID
     * @return 조회된 노드
     */
    TreeNode getNodeById(Long id);
    
    /**
     * 모든 노드를 조회합니다.
     * @return 노드 목록
     */
    List<TreeNode> getAllNodes();
    
    /**
     * 새 노드를 생성합니다.
     * @param node 생성할 노드
     * @return 생성된 노드의 ID
     */
    Long createNode(TreeNode node);
    
    /**
     * 노드를 업데이트합니다.
     * @param node 업데이트할 노드
     */
    void updateNode(TreeNode node);
    
    /**
     * 노드를 삭제합니다.
     * @param id 삭제할 노드의 ID
     */
    void deleteNode(Long id);
    
    /**
     * 부모 ID로 자식 노드를 조회합니다.
     * @param parentId 부모 노드 ID
     * @return 자식 노드 목록
     */
    List<TreeNode> getChildNodes(Long parentId);
    
    /**
     * 제목으로 노드를 검색합니다.
     * @param title 검색할 제목
     * @return 검색 결과 목록
     */
    List<TreeNode> searchNodesByTitle(String title);
    
    /**
     * 루트 노드를 생성합니다.
     * @param title 루트 노드의 제목
     * @return 생성된 루트 노드
     */
    TreeNode createRootNode(String title);
    
    /**
     * 부모 노드 아래에 자식 노드를 추가합니다.
     * @param parentId 부모 노드 ID
     * @param node 추가할 자식 노드
     * @return 생성된 자식 노드의 ID
     */
    Long addChildNode(Long parentId, TreeNode node);
    
    /**
     * 노드를 다른 부모 노드로 이동합니다.
     * @param nodeId 이동할 노드 ID
     * @param newParentId 새 부모 노드 ID
     * @param position 새 위치
     */
    void moveNode(Long nodeId, Long newParentId, String position);
    
    /**
     * 노드를 복사합니다.
     * @param sourceNodeId 복사할 노드 ID
     * @param targetParentId 복사될 위치의 부모 노드 ID
     * @param position 복사될 위치
     * @return 복사된 노드의 ID
     */
    Long copyNode(Long sourceNodeId, Long targetParentId, String position);
    
    /**
     * 특정 노드의 자손을 조회합니다.
     * @param nodeId 조회할 노드 ID
     * @return 자손 노드 목록
     */
    List<TreeNode> getDescendants(Long nodeId);
    
    /**
     * 특정 노드의 조상을 조회합니다.
     * @param nodeId 조회할 노드 ID
     * @return 조상 노드 목록
     */
    List<TreeNode> getAncestors(Long nodeId);
    
    /**
     * 여러 노드를 일괄 추가합니다.
     * @param nodes 추가할 노드 목록
     */
    void bulkInsertNodes(List<TreeNode> nodes);
} 