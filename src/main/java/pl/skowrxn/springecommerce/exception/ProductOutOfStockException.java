package pl.skowrxn.springecommerce.exception;

import pl.skowrxn.springecommerce.entity.Product;

public class ProductOutOfStockException extends RuntimeException {

    private Product product;
    private Integer quantity;
    private Integer requiredQuantity;

    public ProductOutOfStockException(Product product, Integer quantity, Integer requiredQuantity) {
        super("Product " + product.getName() + " is out of stock. Available: " + quantity + ", Required: " + requiredQuantity);
        this.product = product;
        this.quantity = quantity;
        this.requiredQuantity = requiredQuantity;
    }

}
