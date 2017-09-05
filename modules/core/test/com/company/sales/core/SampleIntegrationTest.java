package com.company.sales.core;

import com.company.sales.SalesTestContainer;
import com.company.sales.entity.Customer;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SampleIntegrationTest {

    @ClassRule
    public static SalesTestContainer cont = SalesTestContainer.Common.INSTANCE;

    private Metadata metadata;
    private Persistence persistence;
    private DataManager dataManager;

    @Before
    public void setUp() throws Exception {
        metadata = cont.metadata();
        persistence = cont.persistence();
        dataManager = AppBeans.get(DataManager.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadUser() {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<User> query = em.createQuery(
                    "select u from sec$User u where u.login = :userLogin", User.class);
            query.setParameter("userLogin", "admin");
            List<User> users = query.getResultList();
            tx.commit();
            assertEquals(1, users.size());
        }
    }

    @Test
    public void testCreateCustomer() {
        Customer customer = metadata.create(Customer.class);
        customer.setName("Test Customer");

        dataManager.commit(customer);

        Customer loaded = dataManager.load(LoadContext.create(Customer.class)
                .setId(customer.getId()).setView(View.LOCAL));

        assertNotNull(loaded);
        assertEquals(customer.getName(), loaded.getName());

        dataManager.remove(loaded);
    }
}