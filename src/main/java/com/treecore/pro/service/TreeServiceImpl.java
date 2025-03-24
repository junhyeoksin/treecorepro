package com.treecore.pro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.treecore.pro.dao.TreeNodeDao;
import com.treecore.pro.model.TreeNode;

import lombok.RequiredArgsConstructor;

/**
 * 트리 서비스 구현 클래스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TreeNodeDao treeNodeDao;

    @Override
    public TreeNode getNode(TreeNode node) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_id"), node.getId()));
        return entityManager.createQuery(criteria).getSingleResult();
    }

    @Override
    public TreeNode addNode(TreeNode node) {
        node.setTitle(node.getTitle());
        node.setType(node.getType());
        treeNodeDao.save(node);
        return node;
    }

    @Override
    public int updateNode(TreeNode node) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_id"), node.getId()));
        
        TreeNode existingNode = entityManager.createQuery(criteria).getSingleResult();
        
        if (existingNode != null) {
            existingNode.setTitle(node.getTitle());
            existingNode.setType(node.getType());
            treeNodeDao.update(existingNode);
            return 1;
        }
        return 0;
    }

    @Override
    public int removeNode(TreeNode node) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_id"), node.getId()));
        
        TreeNode existingNode = entityManager.createQuery(criteria).getSingleResult();
        
        if (existingNode != null) {
            treeNodeDao.delete(existingNode);
            return 1;
        }
        return 0;
    }

    @Override
    public List<TreeNode> getChildNode(TreeNode node) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_parentid"), node.getId()));
        criteria.orderBy(builder.asc(root.get("c_position")));
        
        return entityManager.createQuery(criteria).getResultList();
    }

    @Override
    public TreeNode moveNode(TreeNode node, HttpServletRequest request) throws Exception {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_id"), node.getId()));
        
        TreeNode existingNode = entityManager.createQuery(criteria).getSingleResult();
        
        if (existingNode != null) {
            String newPosition = request.getParameter("position");
            Long newParentId = Long.parseLong(request.getParameter("parent"));
            
            // 현재 위치 저장
            String currentPosition = existingNode.getPosition();
            Long currentParentId = existingNode.getParentId();
            
            // 새로운 위치로 이동
            existingNode.setPosition(newPosition);
            existingNode.setParentId(newParentId);
            
            // 형제 노드들의 위치 조정
            updateSiblingPositions(currentParentId, currentPosition, newParentId, newPosition);
            
            treeNodeDao.update(existingNode);
            return existingNode;
        }
        return null;
    }

    /**
     * 형제 노드들의 위치를 조정합니다.
     */
    private void updateSiblingPositions(Long currentParentId, String currentPosition,
                                       Long newParentId, String newPosition) {
        // 이전 위치의 형제 노드들 위치 조정
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> oldSiblingsCriteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> oldSiblingsRoot = oldSiblingsCriteria.from(TreeNode.class);
        
        Predicate oldParentPredicate = builder.equal(oldSiblingsRoot.get("c_parentid"), currentParentId);
        Predicate oldPositionPredicate = builder.greaterThan(oldSiblingsRoot.get("c_position"), currentPosition);
        oldSiblingsCriteria.where(builder.and(oldParentPredicate, oldPositionPredicate));
        
        List<TreeNode> oldSiblings = entityManager.createQuery(oldSiblingsCriteria).getResultList();
        
        for (TreeNode sibling : oldSiblings) {
            int pos = Integer.parseInt(sibling.getPosition()) - 1;
            sibling.setPosition(String.valueOf(pos));
            treeNodeDao.update(sibling);
        }
        
        // 새 위치의 형제 노드들 위치 조정
        CriteriaQuery<TreeNode> newSiblingsCriteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> newSiblingsRoot = newSiblingsCriteria.from(TreeNode.class);
        
        Predicate newParentPredicate = builder.equal(newSiblingsRoot.get("c_parentid"), newParentId);
        Predicate newPositionPredicate = builder.greaterThanOrEqualTo(newSiblingsRoot.get("c_position"), newPosition);
        newSiblingsCriteria.where(builder.and(newParentPredicate, newPositionPredicate));
        
        List<TreeNode> newSiblings = entityManager.createQuery(newSiblingsCriteria).getResultList();
        
        for (TreeNode sibling : newSiblings) {
            int pos = Integer.parseInt(sibling.getPosition()) + 1;
            sibling.setPosition(String.valueOf(pos));
            treeNodeDao.update(sibling);
        }
    }
} 