package com.payroc.hospitaloperations.dao;

import org.springframework.stereotype.Repository;

import com.payroc.hospitaloperations.entity.RequestCache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class RequestCacheDao {

    @PersistenceContext
    private EntityManager em;
    public RequestCache getByKey(String key) {
        try {
        return em.createNamedQuery(RequestCache.GET_BY_KEY, RequestCache.class)
                 .setParameter("key", key)
                 .getSingleResult();
        } catch (Exception e) {
        	return null;
        }
    }

    public void save(RequestCache requestCache) {
        em.persist(requestCache);
    }
}