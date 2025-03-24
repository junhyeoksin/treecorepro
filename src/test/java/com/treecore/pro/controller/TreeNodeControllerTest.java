package com.treecore.pro.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treecore.pro.model.TreeNode;
import com.treecore.pro.service.TreeNodeService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TreeNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TreeNodeService treeNodeService;

    private TreeNode rootNode;
    private TreeNode childNode;

    @BeforeEach
    void setUp() {
        // 루트 노드 설정
        rootNode = new TreeNode();
        rootNode.setId(1L);
        rootNode.setNodeId(1L);
        rootNode.setParentId(0L);
        rootNode.setPosition("0");
        rootNode.setLeftBound(1L);
        rootNode.setRightBound(4L);
        rootNode.setLevel(0);
        rootNode.setTitle("루트 노드");
        rootNode.setCreatedDate(LocalDateTime.now());

        // 자식 노드 설정
        childNode = new TreeNode();
        childNode.setId(2L);
        childNode.setNodeId(2L);
        childNode.setParentId(1L);
        childNode.setPosition("0");
        childNode.setLeftBound(2L);
        childNode.setRightBound(3L);
        childNode.setLevel(1);
        childNode.setTitle("자식 노드");
        childNode.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void testGetAllNodes() throws Exception {
        List<TreeNode> nodes = Arrays.asList(rootNode, childNode);
        when(treeNodeService.getAllNodes()).thenReturn(nodes);

        mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("루트 노드"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("자식 노드"));

        verify(treeNodeService, times(1)).getAllNodes();
    }

    @Test
    void testGetNodeById() throws Exception {
        when(treeNodeService.getNodeById(1L)).thenReturn(rootNode);

        mockMvc.perform(get("/api/nodes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("루트 노드"));

        verify(treeNodeService, times(1)).getNodeById(1L);
    }

    @Test
    void testGetNodeByIdNotFound() throws Exception {
        when(treeNodeService.getNodeById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/nodes/999"))
                .andExpect(status().isNotFound());

        verify(treeNodeService, times(1)).getNodeById(999L);
    }

    @Test
    void testCreateNode() throws Exception {
        TreeNode newNode = new TreeNode();
        newNode.setTitle("새 노드");
        newNode.setParentId(0L);
        newNode.setPosition("0");
        newNode.setLeftBound(1L);
        newNode.setRightBound(2L);
        newNode.setLevel(0);
        newNode.setNodeId(3L);
        newNode.setCreatedDate(LocalDateTime.now());
        
        when(treeNodeService.createNode(any(TreeNode.class))).thenReturn(3L);

        mockMvc.perform(post("/api/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNode)))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));

        verify(treeNodeService, times(1)).createNode(any(TreeNode.class));
    }

    @Test
    void testUpdateNode() throws Exception {
        TreeNode updatedNode = new TreeNode();
        updatedNode.setId(1L);
        updatedNode.setTitle("수정된 노드");
        updatedNode.setParentId(0L);
        updatedNode.setPosition("0");
        updatedNode.setLeftBound(1L);
        updatedNode.setRightBound(2L);
        updatedNode.setLevel(0);
        updatedNode.setNodeId(1L);
        updatedNode.setCreatedDate(LocalDateTime.now());
        
        when(treeNodeService.getNodeById(1L)).thenReturn(rootNode);

        mockMvc.perform(put("/api/nodes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedNode)))
                .andExpect(status().isOk());

        verify(treeNodeService, times(1)).getNodeById(1L);
        verify(treeNodeService, times(1)).updateNode(any(TreeNode.class));
    }

    @Test
    void testUpdateNodeNotFound() throws Exception {
        TreeNode updatedNode = new TreeNode();
        updatedNode.setId(999L);
        updatedNode.setTitle("수정된 노드");
        updatedNode.setParentId(0L);
        updatedNode.setPosition("0");
        updatedNode.setLeftBound(1L);
        updatedNode.setRightBound(2L);
        updatedNode.setLevel(0);
        updatedNode.setNodeId(999L);
        updatedNode.setCreatedDate(LocalDateTime.now());
        
        when(treeNodeService.getNodeById(999L)).thenReturn(null);

        mockMvc.perform(put("/api/nodes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedNode)))
                .andExpect(status().isNotFound());

        verify(treeNodeService, times(1)).getNodeById(999L);
        verify(treeNodeService, never()).updateNode(any(TreeNode.class));
    }

    @Test
    void testDeleteNode() throws Exception {
        when(treeNodeService.getNodeById(1L)).thenReturn(rootNode);

        mockMvc.perform(delete("/api/nodes/1"))
                .andExpect(status().isOk());

        verify(treeNodeService, times(1)).getNodeById(1L);
        verify(treeNodeService, times(1)).deleteNode(1L);
    }

    @Test
    void testCreateRootNode() throws Exception {
        when(treeNodeService.createRootNode("새 루트")).thenReturn(rootNode);

        mockMvc.perform(post("/api/nodes/root")
                .param("title", "새 루트"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("루트 노드"));

        verify(treeNodeService, times(1)).createRootNode("새 루트");
    }

    @Test
    void testAddChildNode() throws Exception {
        TreeNode newChild = new TreeNode();
        newChild.setTitle("새 자식 노드");
        newChild.setParentId(1L);
        newChild.setPosition("0");
        newChild.setLeftBound(1L);
        newChild.setRightBound(2L);
        newChild.setLevel(1);
        newChild.setNodeId(3L);
        newChild.setCreatedDate(LocalDateTime.now());
        
        when(treeNodeService.addChildNode(eq(1L), any(TreeNode.class))).thenReturn(3L);

        mockMvc.perform(post("/api/nodes/children/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newChild)))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));

        verify(treeNodeService, times(1)).addChildNode(eq(1L), any(TreeNode.class));
    }

    @Test
    void testGetChildNodes() throws Exception {
        List<TreeNode> children = Arrays.asList(childNode);
        when(treeNodeService.getChildNodes(1L)).thenReturn(children);

        mockMvc.perform(get("/api/nodes/children/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].title").value("자식 노드"));

        verify(treeNodeService, times(1)).getChildNodes(1L);
    }

    @Test
    void testSearchNodesByTitle() throws Exception {
        List<TreeNode> nodes = Arrays.asList(rootNode, childNode);
        when(treeNodeService.searchNodesByTitle("노드")).thenReturn(nodes);

        mockMvc.perform(get("/api/nodes/search")
                .param("title", "노드"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(treeNodeService, times(1)).searchNodesByTitle("노드");
    }
} 