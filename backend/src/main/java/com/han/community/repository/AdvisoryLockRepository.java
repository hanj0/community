package com.han.community.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AdvisoryLockRepository {

    @PersistenceContext
    private EntityManager em;

    public void lock(long... keys) {

        long hash = 1L;
        for(long key : keys) {
            hash = 31L * hash + key;
        }

        em.createNativeQuery("SELECT pg_advisory_xact_lock(:key)")
                .setParameter("key", hash)
                .getSingleResult();
    }
}
