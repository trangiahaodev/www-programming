package iuh.wwwprogramming;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.service.*;
import iuh.wwwprogramming.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest @ActiveProfiles("auth-test") @Transactional
class AdminCatalogRegressionTest {
    @Autowired ProductService products;
    @Autowired CategoryService categories;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;
    @Test void categoryAndProductCrudRemainCompatibleWithCustomerCatalog() {
        String code=UUID.randomUUID().toString().substring(0,16);
        var category=categories.createCategory(CategoryCreateDTO.builder().categoryCode(code).name("Category " + code).active(true).build());
        var created=products.createProduct(ProductCreateDTO.builder().productCode(code).name("Product " + code)
            .categoryId(category.getId()).price(new BigDecimal("125000")).stockQuantity(7).imageUrl("/IMG/anh1.png").active(true).build());
        productRepository.flush();
        var customer=products.getProductDetail(created.getId());
        assertThat(customer.getPrice()).isEqualTo(125000d);
        assertThat(customer.getStock()).isEqualTo(7);
        assertThat(customer.getImage()).isEqualTo("/IMG/anh1.png");
        var edit=products.getProductForEdit(created.getId());
        edit.setActive(false); edit.setStockQuantity(3);
        products.updateProduct(created.getId(),edit); productRepository.flush();
        assertThat(products.getProducts(code,null,PageRequest.of(0,10)).getContent()).hasSize(1);
        assertThat(products.getProductForEdit(created.getId()).getStockQuantity()).isEqualTo(3);
        assertThat(products.searchProducts(code,null,null,PageRequest.of(0,10))).isEmpty();
        products.deleteProduct(created.getId()); productRepository.flush();
        var categoryEdit=categories.getCategoryForUpdate(category.getId()); categoryEdit.setName("Updated " + code);
        categories.updateCategory(category.getId(),categoryEdit);
        categories.deleteCategory(category.getId()); categoryRepository.flush();
        assertThat(productRepository.existsById(created.getId())).isFalse();
        assertThat(categoryRepository.existsById(category.getId())).isFalse();
    }
}
