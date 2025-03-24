package com.treecore.pro.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Order;

import com.treecore.pro.model.TreeNode;
import com.treecore.pro.model.TreeNodeSearch;

/**
 * 트리 노드 DAO 인터페이스
 */
public interface TreeNodeDao extends Serializable {
    /**
     * ID로 노드를 조회합니다.
     * @param id 노드 ID
     * @return 노드 엔티티
     */
    TreeNode getById(Long id);

    /**
     * 조건에 맞는 단일 노드를 조회합니다.
     * @param predicates 조회 조건
     * @return 노드 엔티티
     */
    TreeNode getUnique(Predicate... predicates);

    /**
     * 조건에 맞는 노드 목록을 조회합니다.
     * @param predicates 조회 조건
     * @param orders 정렬 조건
     * @return 노드 목록
     */
    List<TreeNode> getByCriteria(Predicate[] predicates, Order[] orders);

    /**
     * 노드를 저장합니다.
     * @param node 저장할 노드
     */
    void save(TreeNode node);

    /**
     * 노드를 수정합니다.
     * @param node 수정할 노드
     */
    void update(TreeNode node);

    /**
     * 노드를 삭제합니다.
     * @param node 삭제할 노드
     */
    void delete(TreeNode node);

    /**
     * 모든 노드를 조회합니다.
     * @return 노드 목록
     */
    List<TreeNode> getAll();

    /**
     * ID로 노드를 삭제합니다.
     * @param id 삭제할 노드 ID
     */
    void deleteById(Long id);

    /**
     * 부모 ID로 자식 노드 목록을 조회합니다.
     * @param parentId 부모 노드 ID
     * @return 자식 노드 목록
     */
    List<TreeNode> findChildrenByParentId(Long parentId);

    /**
     * 제목으로 노드를 검색합니다.
     * @param title 검색할 제목
     * @return 검색된 노드 목록
     */
    List<TreeNode> findNodesByTitle(String title);

    /**
     * 범위 내의 노드를 조회합니다.
     * @param left 왼쪽 값
     * @param right 오른쪽 값
     * @return 범위 내의 노드 목록
     */
    List<TreeNode> findNodesInRange(Long left, Long right);

    /**
     * 노드의 모든 하위 노드를 조회합니다.
     * @param node 기준 노드
     * @return 하위 노드 목록
     */
    List<TreeNode> findDescendants(TreeNode node);

    /**
     * 노드의 모든 상위 노드를 조회합니다.
     * @param node 기준 노드
     * @return 상위 노드 목록
     */
    List<TreeNode> findAncestors(TreeNode node);

    /**
     * 여러 노드를 일괄 저장합니다.
     * @param nodes 저장할 노드 목록
     */
    void bulkInsert(List<TreeNode> nodes);

    /**
     * HQL 쿼리를 실행하여 대량 업데이트를 수행합니다.
     * @param queryString HQL 쿼리
     * @param values 쿼리 파라미터
     * @return 영향을 받은 레코드 수
     */
    int bulkUpdate(String queryString, Object... values);

    /**
     * 검색 조건으로 트리 노드를 조회합니다.
     * @param search 검색 조건
     * @return 트리 노드 목록
     */
    List<TreeNode> getBySearch(TreeNodeSearch search);

    /**
     * 검색 조건에 맞는 트리 노드의 개수를 조회합니다.
     * @param search 검색 조건
     * @return 트리 노드 개수
     */
    int countBySearch(TreeNodeSearch search);
} 