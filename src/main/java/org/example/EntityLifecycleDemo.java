package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entity.Product;

public class EntityLifecycleDemo {

    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProductDatabase")) {
            JpaUtil jpaUtil = new JpaUtil(emf);

            jpaUtil.doInJpa(entityManager -> {
                Product product = new Product(); // entity is in Transient State
                product.setName("test product");
                product.setCurrentDiscount(39.99);
                entityManager.persist(product); // moved entity into Persistence State (Managed State)
            });

            jpaUtil.doInJpa(entityManager -> {
                Product product = entityManager.find(Product.class, 1L); // entity is in Persistence State
                product.setName("new test product");
                product.setCurrentDiscount(50D);
                entityManager.detach(product); // moved entity to Detached State
            });

            jpaUtil.doInJpa(entityManager -> {
                Product product = entityManager.find(Product.class, 1L);
                product.setName("new test product (updated)");
                entityManager.remove(product); // moved entity to Removed State
                entityManager.persist(product); // moved entity to Managed State
            });

            Product product;
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                product = new Product();
                product.setName("test product 2");
                product.setCurrentDiscount(44D);
                em.persist(product);
                em.getTransaction().commit();
            }

            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                product = em.merge(product);
                product.setName("new test product 2");
                em.getTransaction().commit();
            }
        }
    }
}