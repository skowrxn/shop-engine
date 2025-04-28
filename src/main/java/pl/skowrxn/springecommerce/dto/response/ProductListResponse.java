package pl.skowrxn.springecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.skowrxn.springecommerce.dto.ProductDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {

    private List<ProductDTO> products;
    private int page;
    private int pageSize;
    private int totalPages;
    private Long totalElements;
    private boolean lastPage;

}
