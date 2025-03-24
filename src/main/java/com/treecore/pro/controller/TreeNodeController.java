package com.treecore.pro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.treecore.pro.model.TreeNode;
import com.treecore.pro.service.TreeNodeService;

/**
 * 트리 노드 컨트롤러
 */
@RestController
@RequestMapping("/api/nodes")
public class TreeNodeController {

    @Autowired
    @Qualifier("treeNodeServiceImpl")
    private TreeNodeService treeNodeService;
    
    /**
     * 모든 노드를 조회합니다.
     * @return 모든 노드 목록
     */
    @GetMapping
    public ResponseEntity<List<TreeNode>> getAllNodes() {
        List<TreeNode> nodes = treeNodeService.getAllNodes();
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * ID로 노드를 조회합니다.
     * @param id 노드 ID
     * @return 조회된 노드
     */
    @GetMapping("/{id}")
    public ResponseEntity<TreeNode> getNodeById(@PathVariable Long id) {
        TreeNode node = treeNodeService.getNodeById(id);
        if (node != null) {
            return ResponseEntity.ok(node);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 새 노드를 생성합니다.
     * @param node 생성할 노드
     * @return 생성된 노드 ID
     */
    @PostMapping
    public ResponseEntity<Long> createNode(@RequestBody TreeNode node) {
        Long id = treeNodeService.createNode(node);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
    
    /**
     * 노드를 업데이트합니다.
     * @param id 업데이트할 노드 ID
     * @param node 업데이트 정보
     * @return 응답 상태
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNode(@PathVariable Long id, @RequestBody TreeNode node) {
        TreeNode existingNode = treeNodeService.getNodeById(id);
        if (existingNode == null) {
            return ResponseEntity.notFound().build();
        }
        
        node.setId(id);
        treeNodeService.updateNode(node);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 노드를 삭제합니다.
     * @param id 삭제할 노드 ID
     * @return 응답 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        TreeNode existingNode = treeNodeService.getNodeById(id);
        if (existingNode == null) {
            return ResponseEntity.notFound().build();
        }
        
        treeNodeService.deleteNode(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 자식 노드를 조회합니다.
     * @param parentId 부모 노드 ID
     * @return 자식 노드 목록
     */
    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<TreeNode>> getChildNodes(@PathVariable Long parentId) {
        List<TreeNode> children = treeNodeService.getChildNodes(parentId);
        return ResponseEntity.ok(children);
    }
    
    /**
     * 제목으로 노드를 검색합니다.
     * @param title 검색할 제목
     * @return 검색 결과 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<TreeNode>> searchNodesByTitle(@RequestParam String title) {
        List<TreeNode> nodes = treeNodeService.searchNodesByTitle(title);
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 루트 노드를 생성합니다.
     * @param title 루트 노드 제목
     * @return 생성된 루트 노드
     */
    @PostMapping("/root")
    public ResponseEntity<TreeNode> createRootNode(@RequestParam String title) {
        TreeNode rootNode = treeNodeService.createRootNode(title);
        return ResponseEntity.status(HttpStatus.CREATED).body(rootNode);
    }
    
    /**
     * 자식 노드를 추가합니다.
     * @param parentId 부모 노드 ID
     * @param node 추가할 자식 노드
     * @return 생성된 자식 노드 ID
     */
    @PostMapping("/children/{parentId}")
    public ResponseEntity<Long> addChildNode(@PathVariable Long parentId, @RequestBody TreeNode node) {
        Long id = treeNodeService.addChildNode(parentId, node);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
    
    /**
     * 노드를 이동합니다.
     * @param id 이동할 노드 ID
     * @param newParentId 새 부모 노드 ID
     * @param position 새 위치
     * @return 응답 상태
     */
    @PutMapping("/{id}/move")
    public ResponseEntity<Void> moveNode(
            @PathVariable Long id,
            @RequestParam Long newParentId,
            @RequestParam(defaultValue = "last") String position) {
        try {
            treeNodeService.moveNode(id, newParentId, position);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 노드를 복사합니다.
     * @param id 복사할 노드 ID
     * @param targetParentId 복사될 위치의 부모 노드 ID
     * @param position 복사될 위치
     * @return 복사된 노드 ID
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<Long> copyNode(
            @PathVariable Long id,
            @RequestParam Long targetParentId,
            @RequestParam(defaultValue = "last") String position) {
        try {
            Long newId = treeNodeService.copyNode(id, targetParentId, position);
            return ResponseEntity.status(HttpStatus.CREATED).body(newId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 노드의 자손을 조회합니다.
     * @param id 조회할 노드 ID
     * @return 자손 노드 목록
     */
    @GetMapping("/{id}/descendants")
    public ResponseEntity<List<TreeNode>> getDescendants(@PathVariable Long id) {
        try {
            List<TreeNode> descendants = treeNodeService.getDescendants(id);
            return ResponseEntity.ok(descendants);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 노드의 조상을 조회합니다.
     * @param id 조회할 노드 ID
     * @return 조상 노드 목록
     */
    @GetMapping("/{id}/ancestors")
    public ResponseEntity<List<TreeNode>> getAncestors(@PathVariable Long id) {
        try {
            List<TreeNode> ancestors = treeNodeService.getAncestors(id);
            return ResponseEntity.ok(ancestors);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 