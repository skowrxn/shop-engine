package pl.skowrxn.springecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.skowrxn.springecommerce.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
