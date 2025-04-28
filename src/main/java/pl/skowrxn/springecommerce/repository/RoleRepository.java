package pl.skowrxn.springecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skowrxn.springecommerce.entity.Role;
import pl.skowrxn.springecommerce.entity.RoleType;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findRolesByRoleType(RoleType roleType);

}

