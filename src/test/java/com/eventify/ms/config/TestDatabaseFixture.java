package com.eventify.ms.config;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class TestDatabaseFixture {

    @Autowired private EntityManager em;

    @Transactional
    public void cleanDatabase() {
        em.createNativeQuery("TRUNCATE TABLE ticket_purchases CASCADE").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE tickets CASCADE").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE members CASCADE").executeUpdate();
    }
}
