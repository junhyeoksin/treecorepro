package com.treecore.pro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.treecore.pro.dao.TreeNodeDao;
import com.treecore.pro.model.TreeNode;

import lombok.RequiredArgsConstructor;

/**
 * 트리 노드 서비스 구현 클래스
 */
@Service("treeNodeServiceImpl")
@Primary
@Transactional
@RequiredArgsConstructor
public class TreeNodeServiceImpl implements TreeNodeService {

    private final TreeNodeDao treeNodeDao;

    @Override
    public TreeNode getNodeById(Long id) {
        return treeNodeDao.getById(id);
    }

    @Override
    public List<TreeNode> getAllNodes() {
        return treeNodeDao.getAll();
    }

    @Override
    public Long createNode(TreeNode node) {
        node.setC_insdate(LocalDateTime.now());
        treeNodeDao.save(node);
        return node.getC_id();
    }

    @Override
    public void updateNode(TreeNode node) {
        treeNodeDao.update(node);
    }

    @Override
    public void deleteNode(Long id) {
        TreeNode node = treeNodeDao.getById(id);
        if (node == null) {
            return;
        }
        
        // 자식 노드 확인 및 삭제
        if (node.hasChildren()) {
            Long width = node.getC_right() - node.getC_left() + 1;
            List<TreeNode> descendants = treeNodeDao.findNodesInRange(node.getC_left(), node.getC_right());
            
            // 자식 노드 삭제
            for (TreeNode descendant : descendants) {
                treeNodeDao.delete(descendant);
            }
            
            // 남은 노드들의 경계값 업데이트
            String updateLeftHql = "UPDATE TreeNode SET c_left = c_left - :width WHERE c_left > :right";
            String updateRightHql = "UPDATE TreeNode SET c_right = c_right - :width WHERE c_right > :right";
            
            treeNodeDao.bulkUpdate(updateLeftHql, width, node.getC_right());
            treeNodeDao.bulkUpdate(updateRightHql, width, node.getC_right());
        } else {
            // 단일 노드 삭제
            treeNodeDao.delete(node);
            
            // 남은 노드들의 경계값 업데이트
            String updateLeftHql = "UPDATE TreeNode SET c_left = c_left - 2 WHERE c_left > :right";
            String updateRightHql = "UPDATE TreeNode SET c_right = c_right - 2 WHERE c_right > :right";
            
            treeNodeDao.bulkUpdate(updateLeftHql, node.getC_right());
            treeNodeDao.bulkUpdate(updateRightHql, node.getC_right());
        }
    }

    @Override
    public List<TreeNode> getChildNodes(Long parentId) {
        return treeNodeDao.findChildrenByParentId(parentId);
    }

    @Override
    public List<TreeNode> searchNodesByTitle(String title) {
        return treeNodeDao.findNodesByTitle(title);
    }

    @Override
    public TreeNode createRootNode(String title) {
        TreeNode rootNode = new TreeNode();
        rootNode.setC_id(1L);
        rootNode.setC_parentid(0L);  // 루트 노드는 부모가 없음
        rootNode.setC_position("0");
        rootNode.setC_left(1L);
        rootNode.setC_right(2L);
        rootNode.setC_level(0);
        rootNode.setC_title(title);
        rootNode.setC_insdate(LocalDateTime.now());
        
        treeNodeDao.save(rootNode);
        return treeNodeDao.getById(1L);
    }

    @Override
    public Long addChildNode(Long parentId, TreeNode node) {
        // 부모 노드 조회
        TreeNode parentNode = treeNodeDao.getById(parentId);
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node not found with id: " + parentId);
        }
        
        // 현재 자식 노드 수 조회
        List<TreeNode> siblings = treeNodeDao.findChildrenByParentId(parentId);
        int position = siblings.size();
        
        // 노드 경계값 업데이트를 위한 설정
        Long right = parentNode.getC_right();
        
        // 새 노드 삽입을 위한 공간 확보
        String updateLeftHql = "UPDATE TreeNode SET c_left = c_left + 2 WHERE c_left >= :right";
        String updateRightHql = "UPDATE TreeNode SET c_right = c_right + 2 WHERE c_right >= :right";
        
        treeNodeDao.bulkUpdate(updateLeftHql, right);
        treeNodeDao.bulkUpdate(updateRightHql, right);
        
        // 새 노드 설정
        node.setC_parentid(parentId);
        node.setC_position(String.valueOf(position));
        node.setC_left(right);
        node.setC_right(right + 1);
        node.setC_level(parentNode.getC_level() + 1);
        node.setC_insdate(LocalDateTime.now());
        
        // 부모 노드의 오른쪽 경계 업데이트
        parentNode.setC_right(parentNode.getC_right() + 2);
        treeNodeDao.update(parentNode);
        
        // 새 노드 저장
        treeNodeDao.save(node);
        return node.getC_id();
    }

    @Override
    public void moveNode(Long nodeId, Long newParentId, String position) {
        TreeNode node = treeNodeDao.getById(nodeId);
        TreeNode newParent = treeNodeDao.getById(newParentId);
        
        if (node == null || newParent == null) {
            throw new IllegalArgumentException("Node or new parent not found");
        }
        
        // 노드를 이동할 수 없는 경우 체크
        if (node.getC_left() < newParent.getC_left() && node.getC_right() > newParent.getC_right()) {
            throw new IllegalArgumentException("Cannot move a node to one of its descendants");
        }
        
        // 이동 노드와 그 자손들의 너비
        Long width = node.getC_right() - node.getC_left() + 1;
        
        // 현재 노드의 위치 정보 저장
        Long oldLeft = node.getC_left();
        Long oldRight = node.getC_right();
        Long oldParentId = node.getC_parentid();
        int oldLevel = node.getC_level();
        
        // 새 부모의 위치 정보
        Long newParentRight = newParent.getC_right();
        int newLevel = newParent.getC_level() + 1;
        
        // 1. 이동할 트리 노드를 임시 공간으로 옮기기(음수 값 사용)
        String tempMoveHql = "UPDATE TreeNode SET " +
                "c_left = (c_left - :oldLeft) * -1, " +
                "c_right = (c_right - :oldLeft) * -1, " +
                "c_level = c_level - :oldLevel + :newLevel " +
                "WHERE c_left >= :oldLeft AND c_right <= :oldRight";
        
        treeNodeDao.bulkUpdate(tempMoveHql, oldLeft, oldLeft, oldLevel, newLevel, oldLeft, oldRight);
        
        // 2. 원래 위치의 공간 제거
        String updateLeftAfterRemove = "UPDATE TreeNode SET c_left = c_left - :width " +
                "WHERE c_left > :oldRight";
        String updateRightAfterRemove = "UPDATE TreeNode SET c_right = c_right - :width " +
                "WHERE c_right > :oldRight";
        
        treeNodeDao.bulkUpdate(updateLeftAfterRemove, width, oldRight);
        treeNodeDao.bulkUpdate(updateRightAfterRemove, width, oldRight);
        
        // 3. 새 위치의 공간 확보
        Long newLeft;
        if (oldLeft > newParentRight) { // 이동 후 인덱스가 감소하는 경우 보정
            newLeft = newParentRight - width;
        } else {
            newLeft = newParentRight - 1;
        }
        
        String updateLeftForInsert = "UPDATE TreeNode SET c_left = c_left + :width " +
                "WHERE c_left > :newLeft";
        String updateRightForInsert = "UPDATE TreeNode SET c_right = c_right + :width " +
                "WHERE c_right > :newLeft";
        
        treeNodeDao.bulkUpdate(updateLeftForInsert, width, newLeft);
        treeNodeDao.bulkUpdate(updateRightForInsert, width, newLeft);
        
        // 4. 임시 공간의 노드들을 새 위치로 이동
        String moveToFinalHql = "UPDATE TreeNode SET " +
                "c_left = (c_left * -1) + :newLeft, " +
                "c_right = (c_right * -1) + :newLeft, " +
                "c_parentid = :newParentId " +
                "WHERE c_left < 0";
        
        treeNodeDao.bulkUpdate(moveToFinalHql, newLeft, newLeft, newParentId);
        
        // 5. 이동한 노드 업데이트
        List<TreeNode> siblings = treeNodeDao.findChildrenByParentId(newParentId);
        int positionIndex = siblings.size() - 1; // 마지막 위치로 이동
        
        node = treeNodeDao.getById(nodeId); // 변경된 노드 정보 다시 로드
        node.setC_position(String.valueOf(positionIndex));
        
        // 이동된 노드와 자손 노드들의 위치값 업데이트
        if (position != null && !position.isEmpty() && siblings.size() > 1) {
            int requestedPosition = Integer.parseInt(position);
            if (requestedPosition >= 0 && requestedPosition < siblings.size()) {
                // 형제 노드들 재정렬
                for (int i = 0; i < siblings.size(); i++) {
                    TreeNode sibling = siblings.get(i);
                    if (sibling.getC_id().equals(nodeId)) {
                        sibling.setC_position(String.valueOf(requestedPosition));
                    } else {
                        int siblingPos = Integer.parseInt(sibling.getC_position());
                        if (siblingPos >= requestedPosition) {
                            sibling.setC_position(String.valueOf(siblingPos + 1));
                            treeNodeDao.update(sibling);
                        }
                    }
                }
                node.setC_position(String.valueOf(requestedPosition));
            }
        }
        
        treeNodeDao.update(node);
    }

    @Override
    public Long copyNode(Long sourceNodeId, Long targetParentId, String position) {
        // 원본 노드와 대상 부모 노드 조회
        TreeNode sourceNode = treeNodeDao.getById(sourceNodeId);
        TreeNode targetParentNode = treeNodeDao.getById(targetParentId);
        
        if (sourceNode == null || targetParentNode == null) {
            throw new IllegalArgumentException("Source node or target parent node not found");
        }
        
        // 원본 노드의 자손 노드들 조회
        List<TreeNode> descendants = null;
        if (sourceNode.hasChildren()) {
            descendants = treeNodeDao.findNodesInRange(sourceNode.getC_left(), sourceNode.getC_right());
        }
        
        // 새 노드 생성
        TreeNode newNode = sourceNode.copy();
        newNode.setC_id(null); // ID는 자동 생성되도록 null로 설정
        newNode.setC_parentid(targetParentId);
        
        // 대상 위치에 새 노드 추가
        Long newNodeId = addChildNode(targetParentId, newNode);
        
        // 자손 노드들을 새 노드 아래로 복사
        if (descendants != null && !descendants.isEmpty()) {
            TreeNode newNodeWithId = treeNodeDao.getById(newNodeId);
            
            for (TreeNode descendant : descendants) {
                if (!descendant.getC_id().equals(sourceNodeId)) { // 원본 노드 제외
                    TreeNode newDescendant = descendant.copy();
                    newDescendant.setC_id(null);
                    
                    // 상대적 위치 계산
                    int levelDiff = descendant.getC_level() - sourceNode.getC_level();
                    newDescendant.setC_level(newNodeWithId.getC_level() + levelDiff);
                    
                    if (descendant.getC_parentid().equals(sourceNodeId)) {
                        newDescendant.setC_parentid(newNodeId);
                    } else {
                        // 부모 노드 ID 매핑 필요
                        // 이 부분은 실제 구현에서 복잡할 수 있음
                    }
                    
                    treeNodeDao.save(newDescendant);
                }
            }
        }
        
        return newNodeId;
    }

    @Override
    public List<TreeNode> getDescendants(Long nodeId) {
        TreeNode node = treeNodeDao.getById(nodeId);
        if (node == null) {
            return new ArrayList<>();
        }
        return treeNodeDao.findDescendants(node);
    }

    @Override
    public List<TreeNode> getAncestors(Long nodeId) {
        TreeNode node = treeNodeDao.getById(nodeId);
        if (node == null) {
            return new ArrayList<>();
        }
        return treeNodeDao.findAncestors(node);
    }

    @Override
    public void bulkInsertNodes(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            if (node.getC_insdate() == null) {
                node.setC_insdate(LocalDateTime.now());
            }
        }
        treeNodeDao.bulkInsert(nodes);
    }
} 