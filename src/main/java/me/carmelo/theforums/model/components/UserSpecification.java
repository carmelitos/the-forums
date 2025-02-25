package me.carmelo.theforums.model.components;

import jakarta.persistence.criteria.Predicate;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.dto.UserSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> build(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getIdFilter() != null)
                predicates.add(cb.equal(root.get("id"), criteria.getIdFilter()));

            if (criteria.getUsernameFilter() != null && !criteria.getUsernameFilter().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("username")),
                                "%" + criteria.getUsernameFilter().toLowerCase() + "%"
                        )
                );
            }

            if (criteria.getEmailFilter() != null && !criteria.getEmailFilter().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("email")),
                                "%" + criteria.getEmailFilter().toLowerCase() + "%"
                        )
                );
            }

            if (criteria.getPhoneNumberFilter() != null && !criteria.getPhoneNumberFilter().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("phoneNumber")),
                                "%" + criteria.getPhoneNumberFilter().toLowerCase() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
