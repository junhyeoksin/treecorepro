-- 트리 노드 테이블 생성
DROP TABLE IF EXISTS tree_node;
CREATE TABLE tree_node (
    c_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    c_parentid BIGINT NOT NULL,
    c_position VARCHAR(50) NOT NULL,
    c_left BIGINT NOT NULL,
    c_right BIGINT NOT NULL,
    c_level INT NOT NULL,
    c_title VARCHAR(255),
    c_type VARCHAR(50),
    c_insdate TIMESTAMP
);

-- 기본 인덱스 생성
CREATE INDEX idx_tree_node_parent ON tree_node(c_parentid);
CREATE INDEX idx_tree_node_left_right ON tree_node(c_left, c_right);
CREATE INDEX idx_tree_node_level ON tree_node(c_level);

-- 초기 데이터 삽입 (루트 노드)
INSERT INTO tree_node (c_id, c_parentid, c_position, c_left, c_right, c_level, c_title, c_type, c_insdate) 
VALUES (1, 0, '0', 1, 2, 0, '루트 노드', 'root', CURRENT_TIMESTAMP()); 