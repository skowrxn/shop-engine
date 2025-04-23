package pl.skowrxn.springecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skowrxn.springecommerce.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {


}
