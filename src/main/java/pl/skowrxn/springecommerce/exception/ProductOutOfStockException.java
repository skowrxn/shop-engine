package pl.skowrxn.springecommerce.exception;

import pl.skowrxn.springecommerce.entity.Product;

public class ProductOutOfStockException extends RuntimeException {

    private Product product;
    private Long quantity;
    private Long requiredQuantity;

    public ProductOutOfStockException(Product product, Long quantity, Long requiredQuantity) {
        super("Product " + product.getName() + " is out of stock. Available: " + quantity + ", Required: " + requiredQuantity);
        this.product = product;
        this.quantity = quantity;
        this.requiredQuantity = requiredQuantity;
    }

}
