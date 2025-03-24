package com.treecore.pro.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Order;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.treecore.pro.model.TreeNode;
import com.treecore.pro.model.TreeNodeSearch;

/**
 * 트리 노드 데이터 접근 구현체
 */
@Repository
public class TreeNodeDaoImpl extends AbstractHibernateDao<TreeNode, Long> implements TreeNodeDao {
    
    private static final long serialVersionUID = 1L;
    
    @Autowired
    public TreeNodeDaoImpl(SessionFactory sessionFactory) {
        setSessionFactory(sessionFactory);
    }
    
    @Override
    protected Class<TreeNode> getEntityClass() {
        return TreeNode.class;
    }
    
    @Override
    public TreeNode getById(Long id) {
        return super.getById(id);
    }
    
    @Override
    public TreeNode getUnique(Predicate... predicates) {
        return super.getUnique(predicates);
    }
    
    @Override
    public List<TreeNode> getByCriteria(Predicate[] predicates, Order[] orders) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        
        if (predicates != null && predicates.length > 0) {
            criteria.where(predicates);
        }
        
        if (orders != null && orders.length > 0) {
            criteria.orderBy(orders);
        }
        
        return getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public void save(TreeNode node) {
        super.save(node);
    }
    
    @Override
    public void update(TreeNode node) {
        super.update(node);
    }
    
    @Override
    public void delete(TreeNode node) {
        super.delete(node);
    }
    
    @Override
    public List<TreeNode> getAll() {
        return super.getAll();
    }
    
    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }
    
    @Override
    public List<TreeNode> findChildrenByParentId(Long parentId) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.equal(root.get("c_parentid"), parentId));
        criteria.orderBy(builder.asc(root.get("c_position")));
        return getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public List<TreeNode> findNodesByTitle(String title) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(builder.like(root.get("c_title"), "%" + title + "%"));
        return getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public List<TreeNode> findNodesInRange(Long left, Long right) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(
            builder.and(
                builder.greaterThanOrEqualTo(root.get("c_left"), left),
                builder.lessThanOrEqualTo(root.get("c_right"), right)
            )
        );
        return getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public List<TreeNode> findDescendants(TreeNode node) {
        return findNodesInRange(node.getC_left(), node.getC_right());
    }
    
    @Override
    public List<TreeNode> findAncestors(TreeNode node) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        criteria.where(
            builder.and(
                builder.lessThanOrEqualTo(root.get("c_left"), node.getC_left()),
                builder.greaterThanOrEqualTo(root.get("c_right"), node.getC_right())
            )
        );
        criteria.orderBy(builder.asc(root.get("c_left")));
        return getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public void bulkInsert(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            getCurrentSession().persist(node);
        }
    }
    
    @Override
    public int bulkUpdate(String queryString, Object... values) {
        org.hibernate.query.Query query = getCurrentSession().createQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        return query.executeUpdate();
    }
    
    @Override
    public List<TreeNode> getBySearch(TreeNodeSearch search) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<TreeNode> criteria = builder.createQuery(TreeNode.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        
        search.setupSearch(builder, root);
        Predicate predicate = search.toSinglePredicate(builder);
        if (predicate != null) {
            criteria.where(predicate);
        }
        
        if (!search.getOrders().isEmpty()) {
            criteria.orderBy(search.getOrders());
        }
        
        return getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public int countBySearch(TreeNodeSearch search) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<TreeNode> root = criteria.from(TreeNode.class);
        
        search.setupSearch(builder, root);
        Predicate predicate = search.toSinglePredicate(builder);
        if (predicate != null) {
            criteria.where(predicate);
        }
        
        criteria.select(builder.count(root));
        
        return getCurrentSession().createQuery(criteria).getSingleResult().intValue();
    }
}