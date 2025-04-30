package pl.skowrxn.springecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.skowrxn.springecommerce.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {


}
