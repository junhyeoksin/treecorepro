package com.treecore.pro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Order;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.treecore.pro.dao.TreeNodeDao;
import com.treecore.pro.model.TreeNode;
import com.treecore.pro.model.TreeNodeSearch;

import lombok.RequiredArgsConstructor;

/**
 * 확장된 트리 노드 서비스 구현 클래스
 */
@Service("extendedTreeNodeService")
@Transactional
public class ExtendedTreeNodeServiceImpl implements ExtendedTreeNodeService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TreeNodeDao treeNodeDao;

    public ExtendedTreeNodeServiceImpl(TreeNodeDao treeNodeDao) {
        this.treeNodeDao = treeNodeDao;
    }

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
        node.setC_title(node.getTitle());
        node.setC_type(node.getType());
        node.setC_insdate(LocalDateTime.now());
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
            existingNode.setC_title(node.getTitle());
            existingNode.setC_type(node.getType());
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
    public int moveNode(TreeNode node, HttpServletRequest request) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_id"), node.getId()));
        
        TreeNode existingNode = entityManager.createQuery(criteria).getSingleResult();
        
        if (existingNode != null) {
            String newPosition = request.getParameter("position");
            Long newParentId = Long.parseLong(request.getParameter("parent"));
            
            // 현재 위치 저장
            String currentPosition = existingNode.getC_position();
            Long currentParentId = existingNode.getC_parentid();
            
            // 새로운 위치로 이동
            existingNode.setC_position(newPosition);
            existingNode.setC_parentid(newParentId);
            
            // 형제 노드들의 위치 조정
            updateSiblingPositions(currentParentId, currentPosition, newParentId, newPosition);
            
            treeNodeDao.update(existingNode);
            return 1;
        }
        return 0;
    }

    @Override
    public List<TreeNode> searchNode(TreeNodeSearch searchDTO) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        
        searchDTO.setupSearch(builder, root);
        
        Predicate predicate = searchDTO.toSinglePredicate(builder);
        if (predicate != null) {
            criteria.where(predicate);
        }
        
        if (!searchDTO.getOrders().isEmpty()) {
            criteria.orderBy(searchDTO.getOrders());
        }
        
        return entityManager.createQuery(criteria).getResultList();
    }

    @Override
    public int copyNode(TreeNode node, HttpServletRequest request) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_id"), node.getId()));
        
        TreeNode sourceNode = entityManager.createQuery(criteria).getSingleResult();
        
        if (sourceNode != null) {
            TreeNode newNode = new TreeNode();
            newNode.setC_title(sourceNode.getC_title() + " (copy)");
            newNode.setC_type(sourceNode.getC_type());
            newNode.setC_parentid(Long.parseLong(request.getParameter("parent")));
            newNode.setC_position(request.getParameter("position"));
            newNode.setC_insdate(LocalDateTime.now());
            
            treeNodeDao.save(newNode);
            return 1;
        }
        return 0;
    }

    @Override
    public int bulkInsert(List<TreeNode> nodes) {
        int count = 0;
        for (TreeNode node : nodes) {
            if (node.getC_insdate() == null) {
                node.setC_insdate(LocalDateTime.now());
            }
            treeNodeDao.save(node);
            count++;
        }
        return count;
    }

    @Override
    public int bulkUpdate(List<TreeNode> nodes) {
        int count = 0;
        for (TreeNode node : nodes) {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
            Root<TreeNode> root = criteria.from(TreeNode.class);
            criteria.where(builder.equal(root.get("c_id"), node.getId()));
            
            TreeNode existingNode = entityManager.createQuery(criteria).getSingleResult();
            
            if (existingNode != null) {
                existingNode.setC_title(node.getTitle());
                existingNode.setC_type(node.getType());
                treeNodeDao.update(existingNode);
                count++;
            }
        }
        return count;
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
        oldSiblingsCriteria.orderBy(builder.asc(oldSiblingsRoot.get("c_position")));
        
        List<TreeNode> oldSiblings = entityManager.createQuery(oldSiblingsCriteria).getResultList();
        
        for (TreeNode sibling : oldSiblings) {
            int pos = Integer.parseInt(sibling.getC_position()) - 1;
            sibling.setC_position(String.valueOf(pos));
            treeNodeDao.update(sibling);
        }
        
        // 새 위치의 형제 노드들 위치 조정
        CriteriaQuery<TreeNode> newSiblingsCriteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> newSiblingsRoot = newSiblingsCriteria.from(TreeNode.class);
        
        Predicate newParentPredicate = builder.equal(newSiblingsRoot.get("c_parentid"), newParentId);
        Predicate newPositionPredicate = builder.greaterThanOrEqualTo(newSiblingsRoot.get("c_position"), newPosition);
        newSiblingsCriteria.where(builder.and(newParentPredicate, newPositionPredicate));
        newSiblingsCriteria.orderBy(builder.asc(newSiblingsRoot.get("c_position")));
        
        List<TreeNode> newSiblings = entityManager.createQuery(newSiblingsCriteria).getResultList();
        
        for (TreeNode sibling : newSiblings) {
            int pos = Integer.parseInt(sibling.getC_position()) + 1;
            sibling.setC_position(String.valueOf(pos));
            treeNodeDao.update(sibling);
        }
    }
} 