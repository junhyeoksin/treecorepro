package com.treecore.pro.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.treecore.pro.interceptor.HibernateQueryInterceptor;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${spring.jpa.show-sql}")
    private String hibernateShowSql;
    
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hibernateDdlAuto;
    
    @Value("${spring.jpa.properties.hibernate.format_sql:false}")
    private String hibernateFormatSql;
    
    @Value("${spring.jpa.properties.hibernate.cache.use_second_level_cache:true}")
    private String hibernateUseSecondLevelCache;
    
    @Value("${spring.jpa.properties.hibernate.cache.use_query_cache:true}")
    private String hibernateUseQueryCache;
    
    @Value("${spring.jpa.properties.hibernate.cache.region.factory_class:org.hibernate.cache.ehcache.EhCacheRegionFactory}")
    private String hibernateCacheRegionFactoryClass;

    @Autowired
    private DataSource dataSource;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.treecore.pro.model");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    @Primary
    @DependsOn("sessionFactory")
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    @Bean
    public HibernateQueryInterceptor hibernateQueryInterceptor() {
        return new HibernateQueryInterceptor();
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.show_sql", hibernateShowSql);
        properties.put("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        properties.put("hibernate.format_sql", hibernateFormatSql);
        
        // 캐시 설정
        properties.put("hibernate.cache.use_second_level_cache", hibernateUseSecondLevelCache);
        properties.put("hibernate.cache.use_query_cache", hibernateUseQueryCache);
        properties.put("hibernate.cache.region.factory_class", hibernateCacheRegionFactoryClass);
        
        // 기타 설정
        properties.put("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
        properties.put("hibernate.enable_lazy_load_no_trans", "true");
        
        // 인터셉터 설정
        properties.put("hibernate.session_factory.interceptor", hibernateQueryInterceptor());

        return properties;
    }
} 