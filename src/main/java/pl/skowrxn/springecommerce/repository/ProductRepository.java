package pl.skowrxn.springecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.skowrxn.springecommerce.entity.Category;
import pl.skowrxn.springecommerce.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByNameLikeIgnoreCase(String keyword, Pageable pageable);

}
