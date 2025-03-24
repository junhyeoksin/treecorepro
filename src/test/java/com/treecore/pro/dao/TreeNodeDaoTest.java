package com.treecore.pro.dao;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.treecore.pro.model.TreeNode;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TreeNodeDaoTest {

    @Autowired
    private TreeNodeDao treeNodeDao;

    @Test
    void testSave() {
        // given
        TreeNode node = createSampleNode("테스트 노드");
        
        // when
        treeNodeDao.save(node);
        
        // then
        assertNotNull(node.getC_id());
        assertTrue(node.getC_id() > 0);
        
        TreeNode savedNode = treeNodeDao.getById(node.getC_id());
        assertNotNull(savedNode);
        assertEquals("테스트 노드", savedNode.getC_title());
    }

    @Test
    void testGetById() {
        // given
        TreeNode node = createSampleNode("ID 조회 테스트");
        treeNodeDao.save(node);
        Long id = node.getC_id();
        
        // when
        TreeNode result = treeNodeDao.getById(id);
        
        // then
        assertNotNull(result);
        assertEquals(id, result.getC_id());
        assertEquals("ID 조회 테스트", result.getC_title());
    }
    
    @Test
    void testUpdate() {
        // given
        TreeNode node = createSampleNode("수정 전 노드");
        treeNodeDao.save(node);
        Long id = node.getC_id();
        
        // when
        TreeNode savedNode = treeNodeDao.getById(id);
        savedNode.setC_title("수정 후 노드");
        treeNodeDao.update(savedNode);
        
        // then
        TreeNode updatedNode = treeNodeDao.getById(id);
        assertEquals("수정 후 노드", updatedNode.getC_title());
    }
    
    @Test
    void testDelete() {
        // given
        TreeNode node = createSampleNode("삭제할 노드");
        treeNodeDao.save(node);
        Long id = node.getC_id();
        
        // when
        TreeNode savedNode = treeNodeDao.getById(id);
        treeNodeDao.delete(savedNode);
        
        // then
        TreeNode deletedNode = treeNodeDao.getById(id);
        assertNull(deletedNode);
    }
    
    @Test
    void testGetAll() {
        // given
        TreeNode node1 = createSampleNode("노드1");
        TreeNode node2 = createSampleNode("노드2");
        treeNodeDao.save(node1);
        treeNodeDao.save(node2);
        
        // when
        List<TreeNode> allNodes = treeNodeDao.getAll();
        
        // then
        assertNotNull(allNodes);
        assertTrue(allNodes.size() >= 2);
    }
    
    @Test
    void testFindNodesByTitle() {
        // given
        TreeNode node1 = createSampleNode("검색용 테스트 노드");
        TreeNode node2 = createSampleNode("다른 이름의 노드");
        treeNodeDao.save(node1);
        treeNodeDao.save(node2);
        
        // when
        List<TreeNode> searchResult = treeNodeDao.findNodesByTitle("검색용");
        
        // then
        assertNotNull(searchResult);
        assertEquals(1, searchResult.size());
        assertEquals("검색용 테스트 노드", searchResult.get(0).getC_title());
    }
    
    @Test
    void testFindChildrenByParentId() {
        // given
        TreeNode parentNode = createRootNode();
        treeNodeDao.save(parentNode);
        Long parentId = parentNode.getC_id();
        
        TreeNode childNode1 = createChildNode(parentId, "자식1");
        TreeNode childNode2 = createChildNode(parentId, "자식2");
        treeNodeDao.save(childNode1);
        treeNodeDao.save(childNode2);
        
        // when
        List<TreeNode> children = treeNodeDao.findChildrenByParentId(parentId);
        
        // then
        assertNotNull(children);
        assertEquals(2, children.size());
    }
    
    @Test
    void testBulkInsert() {
        // given
        TreeNode node1 = createSampleNode("벌크1");
        TreeNode node2 = createSampleNode("벌크2");
        TreeNode node3 = createSampleNode("벌크3");
        List<TreeNode> nodes = Arrays.asList(node1, node2, node3);
        
        // when
        treeNodeDao.bulkInsert(nodes);
        
        // then
        List<TreeNode> result = treeNodeDao.findNodesByTitle("벌크");
        assertEquals(3, result.size());
    }
    
    @Test
    void testFindNodesInRange() {
        // given
        TreeNode parentNode = createRootNode();
        treeNodeDao.save(parentNode);
        Long parentId = parentNode.getC_id();
        
        TreeNode childNode1 = createChildNode(parentId, "범위1");
        childNode1.setC_left(2L);
        childNode1.setC_right(3L);
        
        TreeNode childNode2 = createChildNode(parentId, "범위2");
        childNode2.setC_left(4L);
        childNode2.setC_right(5L);
        
        treeNodeDao.save(childNode1);
        treeNodeDao.save(childNode2);
        
        // when
        List<TreeNode> nodesInRange = treeNodeDao.findNodesInRange(2L, 5L);
        
        // then
        assertNotNull(nodesInRange);
        assertEquals(2, nodesInRange.size());
    }
    
    @Test
    void testFindDescendants() {
        // given
        TreeNode rootNode = createRootNode();
        rootNode.setC_left(1L);
        rootNode.setC_right(6L);
        treeNodeDao.save(rootNode);
        Long rootId = rootNode.getC_id();
        
        TreeNode child = createChildNode(rootId, "자식");
        child.setC_left(2L);
        child.setC_right(5L);
        treeNodeDao.save(child);
        Long childId = child.getC_id();
        
        TreeNode grandchild = createChildNode(childId, "손자");
        grandchild.setC_left(3L);
        grandchild.setC_right(4L);
        treeNodeDao.save(grandchild);
        
        rootNode = treeNodeDao.getById(rootId);
        
        // when
        List<TreeNode> descendants = treeNodeDao.findDescendants(rootNode);
        
        // then
        assertNotNull(descendants);
        assertEquals(2, descendants.size());
    }
    
    @Test
    void testFindAncestors() {
        // given
        TreeNode rootNode = createRootNode();
        rootNode.setC_left(1L);
        rootNode.setC_right(6L);
        treeNodeDao.save(rootNode);
        Long rootId = rootNode.getC_id();
        
        TreeNode child = createChildNode(rootId, "자식");
        child.setC_left(2L);
        child.setC_right(5L);
        treeNodeDao.save(child);
        Long childId = child.getC_id();
        
        TreeNode grandchild = createChildNode(childId, "손자");
        grandchild.setC_left(3L);
        grandchild.setC_right(4L);
        treeNodeDao.save(grandchild);
        Long grandchildId = grandchild.getC_id();
        
        grandchild = treeNodeDao.getById(grandchildId);
        
        // when
        List<TreeNode> ancestors = treeNodeDao.findAncestors(grandchild);
        
        // then
        assertNotNull(ancestors);
        assertEquals(2, ancestors.size());
    }
    
    // 헬퍼 메서드
    private TreeNode createSampleNode(String title) {
        TreeNode node = new TreeNode();
        node.setC_id(null);
        node.setC_parentid(0L);
        node.setC_position("0");
        node.setC_left(1L);
        node.setC_right(2L);
        node.setC_level(0);
        node.setC_title(title);
        node.setC_insdate(LocalDateTime.now());
        return node;
    }
    
    private TreeNode createRootNode() {
        TreeNode node = new TreeNode();
        node.setC_id(null);
        node.setC_parentid(0L);
        node.setC_position("0");
        node.setC_left(1L);
        node.setC_right(2L);
        node.setC_level(0);
        node.setC_title("루트 노드");
        node.setC_insdate(LocalDateTime.now());
        return node;
    }
    
    private TreeNode createChildNode(Long parentId, String title) {
        TreeNode node = new TreeNode();
        node.setC_id(null);
        node.setC_parentid(parentId);
        node.setC_position("0");
        node.setC_left(1L);
        node.setC_right(2L);
        node.setC_level(1);
        node.setC_title(title);
        node.setC_insdate(LocalDateTime.now());
        return node;
    }
} 