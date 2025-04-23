package pl.skowrxn.springecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skowrxn.springecommerce.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    

}
