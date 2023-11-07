package ca.gbc.productservice;


import ca.gbc.productservice.dto.ProductRequest;
import ca.gbc.productservice.dto.ProductResponse;
import ca.gbc.productservice.model.Product;
import ca.gbc.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Apple IPad 2023")
                .description("Apple IPad version 2023")
                .price(BigDecimal.valueOf(12000))
                .build();
    }
    private List<Product> getProductList() {
        List<Product> products = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        Product product = Product.builder()
                .id(uuid.toString())
                .name("Apple IPad 2023")
                .description("Apple IPad version 2023")
                .price(BigDecimal.valueOf(12000))
                .build();
        products.add(product);
        return products;
    }

    private String convertObjectToString(List<ProductResponse> productList) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(productList);
    }

    private List<ProductResponse> convertObjectToString(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<ProductResponse>>() {});
    }

    @Test
    void createProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestJSONString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestJSONString))
                .andExpect(MockMvcResultMatchers.status().isCreated()
        );

        // Assertion
        Assertions.assertTrue(productRepository.findAll().size() > 0);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("Apple IPad 2023"));
        List<Product> products = mongoTemplate.find(query, Product.class);
        Assertions.assertTrue(products.size() == 1);

        System.out.println("productRepository.findAll().size() " + productRepository.findAll().size());
    }

    @Test
    void getAllProducts() throws Exception {
        productRepository.saveAll(getProductList());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/product")
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNode.size();
        int expectedSize = getProductList().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void updateProduct() throws Exception {
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Apple IPad 2023")
                .description("Apple IPad version 2023")
                .price(BigDecimal.valueOf(12000))
                .build();

        productRepository.save(savedProduct);

        savedProduct.setName("Apple IPad 2022");

        String productRequestString = objectMapper.writeValueAsString(savedProduct);

        //WHEN

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/product/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString));

        //THEN
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        resultActions.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedProduct.getId()));
        Product storedProduct = mongoTemplate.findOne(query, Product.class);

        Assertions.assertEquals(savedProduct.getName(), storedProduct.getName());
    }

    @Test
    void deleteProduct() throws Exception {
        //GIVEN
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Apple IPad 2023")
                .description("Apple IPad version 2023")
                .price(BigDecimal.valueOf(12000))
                .build();

        productRepository.save(savedProduct);


        //WHEN
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/product/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON));


        //THEN
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        resultActions.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedProduct.getId()));
        Long productCount = mongoTemplate.count(query, Product.class);

        Assertions.assertEquals(0, productCount);
    }
}
