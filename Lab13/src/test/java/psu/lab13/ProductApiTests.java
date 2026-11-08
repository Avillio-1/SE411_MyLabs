package psu.lab13;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import psu.lab13.model.Product;
import psu.lab13.repositories.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository repository;

    @Test
    void listsAllSeventeenSeedProductsAsJsonInIdOrder() throws Exception {
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(17)))
                .andExpect(jsonPath("$[*].id", contains(IntStream.rangeClosed(10, 26).boxed().toArray())))
                .andExpect(jsonPath("$[0].name").value("3d printer"))
                .andExpect(jsonPath("$[16].name").value("PC 08"));
    }

    @Test
    void findsOneProductById() throws Exception {
        mvc.perform(get("/api/v1/products/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("3d printer"))
                .andExpect(jsonPath("$.description").value("A beginner friendly 3d printer"))
                .andExpect(jsonPath("$.price").value(1840.25));
    }

    @Test
    void returnsNotFoundForAnUnknownPositiveId() throws Exception {
        mvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "-2147483648", "abc", "1.5", "2147483648"})
    void rejectsInvalidIdsWithBadRequest(String id) throws Exception {
        mvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void repositorySupportsPagingAndSorting() {
        var page = repository.findAll(PageRequest.of(1, 5, Sort.by("id")));
        assertThat(page.getTotalElements()).isEqualTo(17);
        assertThat(page.getContent()).extracting(Product::id).containsExactly(15, 16, 17, 18, 19);
    }

    @Test
    @Transactional
    void preservesExactDecimalMoneyThroughTheDatabase() {
        BigDecimal total = new BigDecimal("0.10").add(new BigDecimal("0.20"));
        Product saved = repository.save(new Product(null, "Decimal test", "Exact cents", total));
        assertThat(saved.id()).isGreaterThan(26);
        assertThat(repository.findById(saved.id()).orElseThrow().price())
                .isEqualTo(new BigDecimal("0.30"));
    }
}
