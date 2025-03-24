package com.treecore.pro.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Hibernate DAO의 기본 추상 클래스
 * @param <T> 엔티티 타입
 * @param <ID> ID 타입
 */
public abstract class AbstractHibernateDao<T, ID extends Serializable> {

    private SessionFactory sessionFactory;

    /**
     * 엔티티 클래스를 반환합니다.
     * @return 엔티티 클래스
     */
    protected abstract Class<T> getEntityClass();

    /**
     * 세션 팩토리를 설정합니다.
     * @param sessionFactory 세션 팩토리
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 현재 세션을 반환합니다.
     * @return 현재 세션
     */
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * ID로 엔티티를 조회합니다.
     * @param id 엔티티 ID
     * @return 조회된 엔티티
     */
    public T getById(ID id) {
        return getCurrentSession().get(getEntityClass(), id);
    }

    /**
     * 단일 조건으로 엔티티를 조회합니다.
     * @param predicate 조회 조건
     * @return 조회된 엔티티
     */
    public T getUnique(Predicate predicate) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getEntityClass());
        Root<T> root = criteria.from(getEntityClass());
        criteria.where(predicate);
        return getCurrentSession().createQuery(criteria).uniqueResult();
    }

    /**
     * 여러 조건으로 엔티티를 조회합니다.
     * @param predicates 조회 조건들
     * @return 조회된 엔티티
     */
    public T getUnique(Predicate... predicates) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getEntityClass());
        Root<T> root = criteria.from(getEntityClass());
        criteria.where(predicates);
        return getCurrentSession().createQuery(criteria).uniqueResult();
    }

    /**
     * 조건에 해당하는 모든 엔티티를 조회합니다.
     * @param predicates 조회 조건들
     * @return 엔티티 목록
     */
    public List<T> getList(Predicate... predicates) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getEntityClass());
        Root<T> root = criteria.from(getEntityClass());
        criteria.where(predicates);
        return getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * 모든 엔티티를 조회합니다.
     * @return 엔티티 목록
     */
    public List<T> getAll() {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getEntityClass());
        Root<T> root = criteria.from(getEntityClass());
        return getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * 조건에 해당하는 엔티티 수를 조회합니다.
     * @param predicates 조회 조건들
     * @return 엔티티 수
     */
    public Long count(Predicate... predicates) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<T> root = criteria.from(getEntityClass());
        criteria.select(builder.count(root));
        criteria.where(predicates);
        return getCurrentSession().createQuery(criteria).uniqueResult();
    }

    /**
     * 엔티티를 저장합니다.
     * @param entity 저장할 엔티티
     */
    public void save(T entity) {
        getCurrentSession().persist(entity);
    }

    /**
     * 엔티티를 수정합니다.
     * @param entity 수정할 엔티티
     */
    public void update(T entity) {
        getCurrentSession().merge(entity);
    }

    /**
     * 엔티티를 저장하거나 수정합니다.
     * @param entity 저장하거나 수정할 엔티티
     */
    public void saveOrUpdate(T entity) {
        getCurrentSession().merge(entity);
    }

    /**
     * 엔티티를 삭제합니다.
     * @param entity 삭제할 엔티티
     */
    public void delete(T entity) {
        getCurrentSession().remove(entity);
    }

    /**
     * ID로 엔티티를 삭제합니다.
     * @param id 삭제할 엔티티의 ID
     */
    public void deleteById(ID id) {
        T entity = getById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    /**
     * HQL 쿼리를 실행하여 대량 업데이트를 수행합니다.
     * @param queryString HQL 쿼리
     * @param values 쿼리 파라미터
     * @return 영향을 받은 레코드 수
     */
    public int bulkUpdate(String queryString, Object... values) {
        return getCurrentSession().createQuery(queryString).executeUpdate();
    }

    /**
     * 여러 엔티티를 일괄 저장합니다.
     * @param entities 저장할 엔티티 목록
     */
    public void bulkInsert(List<T> entities) {
        for (T entity : entities) {
            getCurrentSession().persist(entity);
        }
    }
} 