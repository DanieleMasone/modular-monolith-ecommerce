package com.dmasone.identity.ecommerceapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dmasone.identity.catalog.application.ProductQueryService;
import com.dmasone.identity.catalog.application.ProductView;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(
        classes = EcommerceApplication.class,
        properties = "spring.docker.compose.enabled=false"
)
@AutoConfigureMockMvc
class EcommerceApplicationIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("ecommerce")
            .withUsername("ecommerce")
            .withPassword("ecommerce");

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:8-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerInfrastructure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.cache.type", () -> "redis");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetData() {
        clearCaches();
        jdbcTemplate.update("delete from payment_attempts");
        jdbcTemplate.update("delete from customer_orders");
        jdbcTemplate.update("update catalog_products set name = 'iPhone 15', available_quantity = 10, version = 0 where id = 1");
        jdbcTemplate.update("update catalog_products set name = 'MacBook Air', available_quantity = 5, version = 0 where id = 2");
        jdbcTemplate.update("update catalog_products set name = 'Mechanical Keyboard', available_quantity = 20, version = 0 where id = 3");
    }

    @Test
    void flywayMigratesPostgresAndSeedsProducts() {
        Integer migrationCount = jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history where success = true",
                Integer.class
        );
        Integer productCount = jdbcTemplate.queryForObject("select count(*) from catalog_products", Integer.class);

        assertThat(migrationCount).isPositive();
        assertThat(productCount).isEqualTo(3);
    }

    @Test
    void productReadModelEndpointReturnsSeededProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sku").value("SKU-IPHONE-15"))
                .andExpect(jsonPath("$[0].availableQuantity").value(10));
    }

    @Test
    void openApiDocumentExposesPublicApiEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Modular Monolith E-commerce API"))
                .andExpect(jsonPath("$.paths['/api/products'].get").exists())
                .andExpect(jsonPath("$.paths['/api/orders'].post").exists())
                .andExpect(jsonPath("$.paths['/api/orders/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/payments/{orderId}'].get").exists());
    }

    @Test
    void productQueryUsesRedisBackedCache() {
        List<ProductView> firstRead = productQueryService.findAll();

        jdbcTemplate.update("update catalog_products set name = 'Cache Probe Product' where id = 1");

        List<ProductView> cachedRead = productQueryService.findAll();
        assertThat(cachedRead.getFirst().name()).isEqualTo(firstRead.getFirst().name());

        cacheManager.getCache("catalogProducts").clear();
        List<ProductView> freshRead = productQueryService.findAll();
        assertThat(freshRead.getFirst().name()).isEqualTo("Cache Probe Product");
    }

    @Test
    void orderPlacementEndpointReservesStockPublishesEventAndCreatesPayment() throws Exception {
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID orderId = UUID.fromString(JsonPath.read(response, "$.id"));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PLACED"));

        mockMvc.perform(get("/api/payments/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("AUTHORIZED"));

        Integer remainingStock = jdbcTemplate.queryForObject(
                "select available_quantity from catalog_products where id = 1",
                Integer.class
        );
        assertThat(remainingStock).isEqualTo(8);
    }

    @Test
    void orderPlacementRejectsInvalidQuantity() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void orderPlacementRejectsUnknownProduct() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 999,
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void orderPlacementRejectsInsufficientStock() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 99
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"))
                .andExpect(jsonPath("$.message").value("Insufficient stock for product 1"));
    }

    private void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
