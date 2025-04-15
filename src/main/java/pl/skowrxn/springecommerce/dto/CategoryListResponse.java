package pl.skowrxn.springecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListResponse {

    private List<CategoryDTO> categories;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;
    private boolean lastPage;

}
