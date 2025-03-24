package com.treecore.pro.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 트리 노드 검색용 DTO 클래스
 * 원본 JsTreeHibernateSearchDTO에 대응하는 클래스
 */
@Getter
@Setter
@NoArgsConstructor
public class TreeNodeSearch {

    /** 현재 페이지 */
    private int pageIndex = 1;
    
    /** 페이지당 데이터 수 */
    private int pageUnit = 10;
    
    /** 페이지 크기 */
    private int pageSize = 10;
    
    /** 첫 페이지 인덱스 */
    private int firstIndex = 1;
    
    /** 마지막 페이지 인덱스 */
    private int lastIndex = 1;
    
    /** 전체 페이지 수 */
    private int totalPageCount = 0;
    
    /** 시작 행번호 */
    private int startRowNum = 0;
    
    /** 종료 행번호 */
    private int endRowNum = 0;
    
    /** 쿼리 조건 목록 */
    private List<Predicate> predicates = new ArrayList<>();
    
    /** 정렬 목록 */
    private List<Order> orders = new ArrayList<>();
    
    /** 노드 ID */
    private Long id;
    
    /** 부모 노드 ID */
    private Long parentId;
    
    /** 노드 제목 */
    private String title;
    
    /** 상태 */
    private String state;
    
    /** 노드 타입 */
    private String type;
    
    /** 노드 레벨 */
    private Integer level;
    
    /** 검색어 */
    private String searchString;
    
    /** 노드 위치 */
    private String position;
    
    /**
     * 여러 필드 검색 조건을 추가합니다.
     * @param builder CriteriaBuilder 인스턴스
     * @param root 엔티티 루트
     */
    public void setupSearch(CriteriaBuilder builder, Root<TreeNode> root) {
        predicates.clear();
        
        if (id != null) {
            predicates.add(builder.equal(root.get("c_id"), id));
        }
        
        if (parentId != null) {
            predicates.add(builder.equal(root.get("c_parentid"), parentId));
        }
        
        if (title != null && !title.isEmpty()) {
            predicates.add(builder.like(root.get("c_title"), "%" + title + "%"));
        }
        
        if (type != null && !type.isEmpty()) {
            predicates.add(builder.equal(root.get("c_type"), type));
        }
        
        if (level != null) {
            predicates.add(builder.equal(root.get("c_level"), level));
        }
        
        if (searchString != null && !searchString.isEmpty()) {
            predicates.add(builder.like(root.get("c_title"), "%" + searchString + "%"));
        }
    }
    
    /**
     * 페이징 정보를 설정합니다.
     * @param pageIndex 페이지 번호
     * @param pageUnit 페이지당 항목 수
     */
    public void setPageInfo(int pageIndex, int pageUnit) {
        this.pageIndex = pageIndex;
        this.pageUnit = pageUnit;
        this.firstIndex = (pageIndex - 1) * pageUnit;
        this.lastIndex = pageIndex * pageUnit;
    }
    
    /**
     * 정렬 조건을 추가합니다.
     * @param builder CriteriaBuilder 인스턴스
     * @param root 엔티티 루트
     * @param propertyName 정렬할 필드명
     * @param isAsc 오름차순 여부
     */
    public void addOrder(CriteriaBuilder builder, Root<TreeNode> root, String propertyName, boolean isAsc) {
        Path<?> path = root.get(propertyName);
        if (isAsc) {
            this.orders.add(builder.asc(path));
        } else {
            this.orders.add(builder.desc(path));
        }
    }
    
    /**
     * 모든 검색 조건을 단일 Predicate으로 결합합니다.
     * @param builder CriteriaBuilder 인스턴스
     * @return 결합된 Predicate
     */
    public Predicate toSinglePredicate(CriteriaBuilder builder) {
        if (predicates.isEmpty()) {
            return null;
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
} 