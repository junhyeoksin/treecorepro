package com.treecore.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.treecore.pro.dao.TreeNodeDao;
import com.treecore.pro.model.TreeNode;

/**
 * 트리 서비스 구현 클래스
 * 기존 egovframework의 TreeServiceImpl 클래스와 호환되는 기능을 제공합니다.
 */
@Service("treeService")
@Primary
@Transactional
public class TreeServiceImpl {

    @PersistenceContext
    protected EntityManager entityManager;

    private final TreeNodeDao treeNodeDao;

    public TreeServiceImpl(TreeNodeDao treeNodeDao) {
        this.treeNodeDao = treeNodeDao;
    }

    /**
     * 노드를 저장합니다.
     * @param node 저장할 노드
     */
    public void save(TreeNode node) {
        treeNodeDao.save(node);
    }

    /**
     * 노드를 수정합니다.
     * @param node 수정할 노드
     */
    public void update(TreeNode node) {
        treeNodeDao.update(node);
    }

    /**
     * 노드를 삭제합니다.
     * @param node 삭제할 노드
     */
    public void delete(TreeNode node) {
        treeNodeDao.delete(node);
    }

    /**
     * ID로 노드를 조회합니다.
     * @param id 노드 ID
     * @return 조회된 노드
     */
    public TreeNode getById(Long id) {
        return treeNodeDao.getById(id);
    }

    /**
     * 모든 노드를 조회합니다.
     * @return 노드 목록
     */
    public List<TreeNode> getAll() {
        return treeNodeDao.getAll();
    }
} 