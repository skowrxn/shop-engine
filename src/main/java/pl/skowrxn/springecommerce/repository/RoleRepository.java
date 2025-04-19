package pl.skowrxn.springecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skowrxn.springecommerce.entity.Role;
import pl.skowrxn.springecommerce.entity.RoleType;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByRoleType(RoleType roleType);

}
