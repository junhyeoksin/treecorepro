package com.treecore.pro.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treecore.pro.model.TreeNode;
import com.treecore.pro.service.TreeNodeService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MockTreeNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TreeNodeService treeNodeService;

    @Test
    void testGetAllNodes() throws Exception {
        mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateNode() throws Exception {
        TreeNode newNode = new TreeNode();
        newNode.setTitle("테스트 노드");
        newNode.setNodeId(1L);
        newNode.setParentId(0L);
        newNode.setPosition("0");
        newNode.setLeftBound(1L);
        newNode.setRightBound(2L);
        newNode.setLevel(0);
        newNode.setCreatedDate(LocalDateTime.now());
        
        when(treeNodeService.createNode(any(TreeNode.class))).thenReturn(1L);

        mockMvc.perform(post("/api/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNode)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetNodeById() throws Exception {
        TreeNode node = new TreeNode();
        node.setId(1L);
        node.setTitle("테스트 노드");
        node.setNodeId(1L);
        node.setParentId(0L);
        node.setPosition("0");
        node.setLeftBound(1L);
        node.setRightBound(2L);
        node.setLevel(0);
        node.setCreatedDate(LocalDateTime.now());
        
        when(treeNodeService.getNodeById(anyLong())).thenReturn(node);

        mockMvc.perform(get("/api/nodes/1"))
                .andExpect(status().isOk());
    }
} 