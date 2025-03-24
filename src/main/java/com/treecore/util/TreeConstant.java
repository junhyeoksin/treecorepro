package com.treecore.util;

/**
 * 트리 관련 상수 정의 클래스
 * 기존 egovframework 프로젝트의 TreeConstant 클래스와 호환되는 상수들
 */
public class TreeConstant {
    
    // 루트 노드 ID
    public static final Long ROOT_CID = 1L;
    
    // 첫 번째 노드 ID
    public static final Long First_Node_CID = 2L;
    
    // 기본 노드 타입
    public static final String Leaf_Node_TYPE = "default";
    
    // 폴더 노드 타입
    public static final String Folder_Node_TYPE = "folder";
    
    // 최대 파일 업로드 크기 (10MB)
    public static final Long MAX_UPLOAD_FILESIZE = 10 * 1024 * 1024L;
    
    // 요구사항 테이블 접두사
    public static final String REQADD_PREFIX_TABLENAME = "T_REQADD_";
} 