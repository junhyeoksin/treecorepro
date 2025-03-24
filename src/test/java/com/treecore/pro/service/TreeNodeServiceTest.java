package com.treecore.pro.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.treecore.pro.dao.TreeNodeDao;
import com.treecore.pro.model.TreeNode;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TreeNodeServiceTest {

    @Mock
    private TreeNodeDao treeNodeDao;

    @InjectMocks
    private TreeNodeServiceImpl treeNodeService;

    private TreeNode rootNode;
    private TreeNode childNode;

    @BeforeEach
    void setUp() {
        // 루트 노드 설정
        rootNode = new TreeNode();
        rootNode.setC_id(1L);
        rootNode.setC_parentid(0L);
        rootNode.setC_position("0");
        rootNode.setC_left(1L);
        rootNode.setC_right(4L);
        rootNode.setC_level(0);
        rootNode.setC_title("루트 노드");
        rootNode.setC_insdate(LocalDateTime.now());

        // 자식 노드 설정
        childNode = new TreeNode();
        childNode.setC_id(2L);
        childNode.setC_parentid(1L);
        childNode.setC_position("0");
        childNode.setC_left(2L);
        childNode.setC_right(3L);
        childNode.setC_level(1);
        childNode.setC_title("자식 노드");
        childNode.setC_insdate(LocalDateTime.now());
    }

    @Test
    void testGetNodeById() {
        // given
        when(treeNodeDao.getById(1L)).thenReturn(rootNode);
        
        // when
        TreeNode result = treeNodeService.getNodeById(1L);
        
        // then
        assertNotNull(result);
        assertEquals(1L, result.getC_id());
        assertEquals("루트 노드", result.getC_title());
        verify(treeNodeDao, times(1)).getById(1L);
    }

    @Test
    void testGetAllNodes() {
        // given
        List<TreeNode> nodes = Arrays.asList(rootNode, childNode);
        when(treeNodeDao.getAll()).thenReturn(nodes);
        
        // when
        List<TreeNode> result = treeNodeService.getAllNodes();
        
        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getC_id());
        assertEquals(2L, result.get(1).getC_id());
        verify(treeNodeDao, times(1)).getAll();
    }

    @Test
    void testCreateNode() {
        // given
        TreeNode newNode = new TreeNode();
        newNode.setC_title("새 노드");
        
        doAnswer(invocation -> {
            TreeNode node = invocation.getArgument(0);
            node.setC_id(3L);
            return null;
        }).when(treeNodeDao).save(any(TreeNode.class));
        
        // when
        Long id = treeNodeService.createNode(newNode);
        
        // then
        assertEquals(3L, id);
        assertNotNull(newNode.getC_insdate());
        verify(treeNodeDao, times(1)).save(newNode);
    }

    @Test
    void testCreateRootNode() {
        // given
        doAnswer(invocation -> {
            TreeNode node = invocation.getArgument(0);
            node.setC_id(1L);
            return null;
        }).when(treeNodeDao).save(any(TreeNode.class));
        when(treeNodeDao.getById(1L)).thenReturn(rootNode);
        
        // when
        TreeNode result = treeNodeService.createRootNode("루트 노드");
        
        // then
        assertNotNull(result);
        assertEquals(1L, result.getC_id());
        assertEquals("루트 노드", result.getC_title());
        assertEquals(0, result.getC_level());
        assertEquals(0L, result.getC_parentid());
        verify(treeNodeDao, times(1)).save(any(TreeNode.class));
        verify(treeNodeDao, times(1)).getById(1L);
    }

    @Test
    void testSearchNodesByTitle() {
        // given
        String title = "노드";
        List<TreeNode> nodes = Arrays.asList(rootNode, childNode);
        when(treeNodeDao.findNodesByTitle(title)).thenReturn(nodes);
        
        // when
        List<TreeNode> result = treeNodeService.searchNodesByTitle(title);
        
        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(treeNodeDao, times(1)).findNodesByTitle(title);
    }
} 