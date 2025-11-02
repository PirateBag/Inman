package com.inman.repository;

import com.inman.entity.Item;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ItemSpecifications {

    public static Specification<Item> withDynamicQuery(Item searchCriteria) {

        return (Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchCriteria == null) {
                return cb.conjunction(); // Returns all items if null
            }

            // Search by summaryId (partial match, case-insensitive)
            if (searchCriteria.getSummaryId() != null && !searchCriteria.getSummaryId().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("summaryId")),
                        "%" + searchCriteria.getSummaryId().toLowerCase() + "%"));
            }

            // Search by description (partial match, case-insensitive)
            if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + searchCriteria.getDescription().toLowerCase() + "%"));
            }

            // Search by unitCost (exact match if > 0)
            if (searchCriteria.getUnitCost() > 0) {
                predicates.add(cb.equal(root.get("unitCost"), searchCriteria.getUnitCost()));
            }

            // Search by sourcing type
            if (searchCriteria.getSourcing() != null) {
                predicates.add(cb.equal(root.get("sourcing"), searchCriteria.getSourcing()));
            }

            // Search by leadTime (exact match if > 0)
            if (searchCriteria.getLeadTime() > 0) {
                predicates.add(cb.equal(root.get("leadTime"), searchCriteria.getLeadTime()));
            }

            // Search by maxDepth (exact match if > 0)
            if (searchCriteria.getMaxDepth() > 0) {
                predicates.add(cb.equal(root.get("maxDepth"), searchCriteria.getMaxDepth()));
            }

            // Search by quantityOnHand (exact match if > 0)
            if (searchCriteria.getQuantityOnHand() > 0) {
                predicates.add(cb.equal(root.get("quantityOnHand"), searchCriteria.getQuantityOnHand()));
            }

            // Search by minimumOrderQuantity (exact match if > 0)
            if (searchCriteria.getMinimumOrderQuantity() > 0) {
                predicates.add(cb.equal(root.get("minimumOrderQuantity"), searchCriteria.getMinimumOrderQuantity()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}