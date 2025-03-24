package com.treecore.pro.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 트리 노드 엔티티
 * 계층적 트리 구조를 표현하는 기본 엔티티입니다.
 */
@Entity
@Table(name = "tree_node")
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
public class TreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 노드의 고유 id, 1부터 시작(Root Node) */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TreeNodeSequence")
    @Column(name = "c_id")
    private Long c_id;

    /** 노드의 부모 id, 0부터 시작(Root Node) */
    @Column(name = "c_parentid")
    private Long c_parentid;

    /** Parent의 몇 번째 자식인지를 나타냄. 0부터 시작 */
    @Column(name = "c_position")
    private String c_position;

    /** 노드의 left 위치, 1부터 시작(Root Node) */
    @Column(name = "c_left")
    private Long c_left;

    /** 노드의 right 위치, 자식이 없다면 left + 1의 값을 가진다. */
    @Column(name = "c_right")
    private Long c_right;

    /** 노드의 depth, 0부터 시작 */
    @Column(name = "c_level")
    private Integer c_level;

    /** Node 의 title */
    @Column(name = "c_title")
    private String c_title;

    @Column(name = "c_type")
    private String c_type;

    @Transient
    private long ref;

    @Transient
    private long copy;

    @Transient
    private long multiCounter;

    @Transient
    private long status;

    @Transient
    private String ajaxMessage;

    @Transient
    private String childcount;

    @Transient
    private String searchStr;

    @Transient
    private long idif;

    @Transient
    private long ldif;

    @Transient
    private long spaceOfTargetNode;

    @Transient
    private List<Long> c_idsByChildNodeFromNodeById;

    @Transient
    private long fixCopyId;

    @Transient
    private long fixCopyPosition;

    @Transient
    private long rightPositionFromNodeByRef;

    @Transient
    private TreeNode nodeById;

    @Transient
    private long idifLeft;

    @Transient
    private long idifRight;

    @Transient
    private long id;

    @Transient
    private final HashMap<String, String> attr = new HashMap<>();

    /** 생성일시 */
    @Column(name = "c_insdate")
    private LocalDateTime c_insdate;

    @Transient
    private List<TreeNode> children;

    // Getters and Setters
    public Long getC_id() {
        return c_id;
    }

    public void setC_id(Long c_id) {
        this.c_id = c_id;
    }

    public Long getC_parentid() {
        return c_parentid;
    }

    public void setC_parentid(Long c_parentid) {
        this.c_parentid = c_parentid;
    }

    public String getC_position() {
        return c_position;
    }

    public void setC_position(String c_position) {
        this.c_position = c_position;
    }

    public Long getC_left() {
        return c_left;
    }

    public void setC_left(Long c_left) {
        this.c_left = c_left;
    }

    public Long getC_right() {
        return c_right;
    }

    public void setC_right(Long c_right) {
        this.c_right = c_right;
    }

    public Integer getC_level() {
        return c_level;
    }

    public void setC_level(Integer c_level) {
        this.c_level = c_level;
    }

    public String getC_title() {
        return c_title;
    }

    public void setC_title(String c_title) {
        this.c_title = c_title;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public LocalDateTime getC_insdate() {
        return c_insdate;
    }

    public void setC_insdate(LocalDateTime c_insdate) {
        this.c_insdate = c_insdate;
    }

    @Transient
    public String getData() {
        return c_title;
    }

    @Transient
    public boolean isCopied() {
        return this.getCopy() == 1;
    }

    @Transient
    public long getRef() {
        return ref;
    }

    public void setRef(long ref) {
        this.ref = ref;
    }

    @Transient
    public long getCopy() {
        return copy;
    }

    public void setCopy(long copy) {
        this.copy = copy;
    }

    @Transient
    public long getMultiCounter() {
        return multiCounter;
    }

    public void setMultiCounter(long multiCounter) {
        this.multiCounter = multiCounter;
    }

    @Transient
    public String getState() {
        String returnCode;
        if (getChildcount() == null || getChildcount().equals(" ")) {
            returnCode = "update status";
        } else if (getChildcount().equals("InChild")) {
            returnCode = "closed";
        } else {
            returnCode = "leafNode";
        }
        return returnCode;
    }

    @Transient
    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    @Transient
    public String getChildcount() {
        if ((getC_right() - getC_left()) > 1) {
            return "InChild";
        }
        return "NoChild";
    }

    public void setChildcount(String childcount) {
        this.childcount = childcount;
    }

    @Transient
    public long getFixCopyId() {
        return fixCopyId;
    }

    public void setFixCopyId(long fixCopyId) {
        this.fixCopyId = fixCopyId;
    }

    @Transient
    public String getAjaxMessage() {
        return ajaxMessage;
    }

    public void setAjaxMessage(String ajaxMessage) {
        this.ajaxMessage = ajaxMessage;
    }

    @Transient
    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    @Transient
    public long getIdif() {
        return idif;
    }

    public void setIdif(long idif) {
        this.idif = idif;
    }

    @Transient
    public long getLdif() {
        return ldif;
    }

    public void setLdif(long ldif) {
        this.ldif = ldif;
    }

    @Transient
    public long getSpaceOfTargetNode() {
        return spaceOfTargetNode;
    }

    public void setSpaceOfTargetNode(long spaceOfTargetNode) {
        this.spaceOfTargetNode = spaceOfTargetNode;
    }

    @Transient
    public List<Long> getC_idsByChildNodeFromNodeById() {
        return c_idsByChildNodeFromNodeById;
    }

    public void setC_idsByChildNodeFromNodeById(List<Long> c_idsByChildNodeFromNodeById) {
        this.c_idsByChildNodeFromNodeById = c_idsByChildNodeFromNodeById;
    }

    @Transient
    public long getFixCopyPosition() {
        return fixCopyPosition;
    }

    public void setFixCopyPosition(long fixCopyPosition) {
        this.fixCopyPosition = fixCopyPosition;
    }

    @Transient
    public long getRightPositionFromNodeByRef() {
        return rightPositionFromNodeByRef;
    }

    public void setRightPositionFromNodeByRef(long rightPositionFromNodeByRef) {
        this.rightPositionFromNodeByRef = rightPositionFromNodeByRef;
    }

    @Transient
    public TreeNode getNodeById() {
        return nodeById;
    }

    public void setNodeById(TreeNode nodeById) {
        this.nodeById = nodeById;
    }

    @Transient
    public long getIdifLeft() {
        return idifLeft;
    }

    public void setIdifLeft(long idifLeft) {
        this.idifLeft = idifLeft;
    }

    @Transient
    public long getIdifRight() {
        return idifRight;
    }

    public void setIdifRight(long idifRight) {
        this.idifRight = idifRight;
    }

    @Transient
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Transient
    public HashMap<String, String> getAttr() {
        return attr;
    }

    @Transient
    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public Long getLeftBound() {
        return c_left;
    }

    public void setLeftBound(Long leftBound) {
        this.c_left = leftBound;
    }

    public Long getRightBound() {
        return c_right;
    }

    public void setRightBound(Long rightBound) {
        this.c_right = rightBound;
    }

    public Long getNodeId() {
        return c_id;
    }

    public void setNodeId(Long nodeId) {
        this.c_id = nodeId;
    }

    public Long getParentId() {
        return c_parentid;
    }

    public void setParentId(Long parentId) {
        this.c_parentid = parentId;
    }

    public String getPosition() {
        return c_position;
    }

    public void setPosition(String position) {
        this.c_position = position;
    }

    public Integer getLevel() {
        return c_level;
    }

    public void setLevel(Integer level) {
        this.c_level = level;
    }

    public String getTitle() {
        return c_title;
    }

    public void setTitle(String title) {
        this.c_title = title;
    }

    public String getType() {
        return c_type;
    }

    public void setType(String type) {
        this.c_type = type;
    }

    public LocalDateTime getCreatedDate() {
        return c_insdate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.c_insdate = createdDate;
    }

    public TreeNode copy() {
        TreeNode copy = new TreeNode();
        copy.setC_id(this.c_id);
        copy.setC_parentid(this.c_parentid);
        copy.setC_position(this.c_position);
        copy.setC_left(this.c_left);
        copy.setC_right(this.c_right);
        copy.setC_level(this.c_level);
        copy.setC_title(this.c_title);
        copy.setC_type(this.c_type);
        copy.setC_insdate(this.c_insdate);
        copy.getAttr().putAll(this.attr);
        return copy;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "c_id=" + c_id +
                ", c_parentid=" + c_parentid +
                ", c_position=" + c_position +
                ", c_left=" + c_left +
                ", c_right=" + c_right +
                ", c_level=" + c_level +
                ", c_title='" + c_title + '\'' +
                ", c_type='" + c_type + '\'' +
                ", c_insdate=" + c_insdate +
                '}';
    }
} 