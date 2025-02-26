package me.carmelo.theforums.model.specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.dto.UserSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSpecification {

    public static Specification<User> build(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getIdFilter() != null) {
                predicates.add(cb.equal(root.get("id"), criteria.getIdFilter()));
            }
            if (criteria.getUsernameFilter() != null && !criteria.getUsernameFilter().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + criteria.getUsernameFilter().toLowerCase() + "%"
                ));
            }
            if (criteria.getEmailFilter() != null && !criteria.getEmailFilter().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + criteria.getEmailFilter().toLowerCase() + "%"
                ));
            }
            if (criteria.getPhoneNumberFilter() != null && !criteria.getPhoneNumberFilter().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("phoneNumber")),
                        "%" + criteria.getPhoneNumberFilter().toLowerCase() + "%"
                ));
            }

            if (criteria.getRolesFilter() != null && !criteria.getRolesFilter().isEmpty()) {
                Join<User, Role> roleJoin = root.join("roles", JoinType.LEFT);
                List<String> lowerCaseRoles = criteria.getRolesFilter().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());

                Expression<String> roleNameExpr = cb.lower(roleJoin.get("name"));
                Predicate rolesInPredicate = roleNameExpr.in(lowerCaseRoles);
                predicates.add(rolesInPredicate);

                if (query != null) query.distinct(true);
            }

            if (criteria.getPermissionsFilter() != null && !criteria.getPermissionsFilter().isEmpty()) {
                Join<User, Role> roleJoin = root.join("roles", JoinType.LEFT);
                Join<Role, Permission> permJoin = roleJoin.join("permissions", JoinType.LEFT);

                List<String> lowerCasePerms = criteria.getPermissionsFilter().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());

                Expression<String> permNameExpr = cb.lower(permJoin.get("name"));
                Predicate permsInPredicate = permNameExpr.in(lowerCasePerms);
                predicates.add(permsInPredicate);

                if (query != null) query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
