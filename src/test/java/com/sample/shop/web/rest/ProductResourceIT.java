package com.sample.shop.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sample.shop.IntegrationTest;
import com.sample.shop.domain.Category;
import com.sample.shop.domain.Product;
import com.sample.shop.domain.WishList;
import com.sample.shop.repository.ProductRepository;
import com.sample.shop.repository.search.ProductSearchRepository;
import com.sample.shop.service.criteria.ProductCriteria;
import com.sample.shop.service.dto.ProductDTO;
import com.sample.shop.service.mapper.ProductMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_ADDED = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_MODIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_MODIFIED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_MODIFIED = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/products";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .title(DEFAULT_TITLE)
            .keywords(DEFAULT_KEYWORDS)
            .description(DEFAULT_DESCRIPTION)
            .rating(DEFAULT_RATING)
            .dateAdded(DEFAULT_DATE_ADDED)
            .dateModified(DEFAULT_DATE_MODIFIED);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);
        return product;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        productSearchRepository.deleteAll();
        assertThat(productSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProduct.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProduct.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testProduct.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        productRepository.saveAndFlush(product);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setTitle(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()))
            .andExpect(jsonPath("$.dateModified").value(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        UUID id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title equals to DEFAULT_TITLE
        defaultProductShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the productList where title equals to UPDATED_TITLE
        defaultProductShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultProductShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the productList where title equals to UPDATED_TITLE
        defaultProductShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title is not null
        defaultProductShouldBeFound("title.specified=true");

        // Get all the productList where title is null
        defaultProductShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByTitleContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title contains DEFAULT_TITLE
        defaultProductShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the productList where title contains UPDATED_TITLE
        defaultProductShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title does not contain DEFAULT_TITLE
        defaultProductShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the productList where title does not contain UPDATED_TITLE
        defaultProductShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByKeywordsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords equals to DEFAULT_KEYWORDS
        defaultProductShouldBeFound("keywords.equals=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords equals to UPDATED_KEYWORDS
        defaultProductShouldNotBeFound("keywords.equals=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    void getAllProductsByKeywordsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords in DEFAULT_KEYWORDS or UPDATED_KEYWORDS
        defaultProductShouldBeFound("keywords.in=" + DEFAULT_KEYWORDS + "," + UPDATED_KEYWORDS);

        // Get all the productList where keywords equals to UPDATED_KEYWORDS
        defaultProductShouldNotBeFound("keywords.in=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    void getAllProductsByKeywordsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords is not null
        defaultProductShouldBeFound("keywords.specified=true");

        // Get all the productList where keywords is null
        defaultProductShouldNotBeFound("keywords.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByKeywordsContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords contains DEFAULT_KEYWORDS
        defaultProductShouldBeFound("keywords.contains=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords contains UPDATED_KEYWORDS
        defaultProductShouldNotBeFound("keywords.contains=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    void getAllProductsByKeywordsNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords does not contain DEFAULT_KEYWORDS
        defaultProductShouldNotBeFound("keywords.doesNotContain=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords does not contain UPDATED_KEYWORDS
        defaultProductShouldBeFound("keywords.doesNotContain=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description equals to DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description is not null
        defaultProductShouldBeFound("description.specified=true");

        // Get all the productList where description is null
        defaultProductShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description contains DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description contains UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description does not contain DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description does not contain UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating equals to DEFAULT_RATING
        defaultProductShouldBeFound("rating.equals=" + DEFAULT_RATING);

        // Get all the productList where rating equals to UPDATED_RATING
        defaultProductShouldNotBeFound("rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating in DEFAULT_RATING or UPDATED_RATING
        defaultProductShouldBeFound("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING);

        // Get all the productList where rating equals to UPDATED_RATING
        defaultProductShouldNotBeFound("rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is not null
        defaultProductShouldBeFound("rating.specified=true");

        // Get all the productList where rating is null
        defaultProductShouldNotBeFound("rating.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is greater than or equal to DEFAULT_RATING
        defaultProductShouldBeFound("rating.greaterThanOrEqual=" + DEFAULT_RATING);

        // Get all the productList where rating is greater than or equal to UPDATED_RATING
        defaultProductShouldNotBeFound("rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is less than or equal to DEFAULT_RATING
        defaultProductShouldBeFound("rating.lessThanOrEqual=" + DEFAULT_RATING);

        // Get all the productList where rating is less than or equal to SMALLER_RATING
        defaultProductShouldNotBeFound("rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is less than DEFAULT_RATING
        defaultProductShouldNotBeFound("rating.lessThan=" + DEFAULT_RATING);

        // Get all the productList where rating is less than UPDATED_RATING
        defaultProductShouldBeFound("rating.lessThan=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllProductsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is greater than DEFAULT_RATING
        defaultProductShouldNotBeFound("rating.greaterThan=" + DEFAULT_RATING);

        // Get all the productList where rating is greater than SMALLER_RATING
        defaultProductShouldBeFound("rating.greaterThan=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded equals to DEFAULT_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.equals=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded equals to UPDATED_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.equals=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded in DEFAULT_DATE_ADDED or UPDATED_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.in=" + DEFAULT_DATE_ADDED + "," + UPDATED_DATE_ADDED);

        // Get all the productList where dateAdded equals to UPDATED_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.in=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is not null
        defaultProductShouldBeFound("dateAdded.specified=true");

        // Get all the productList where dateAdded is null
        defaultProductShouldNotBeFound("dateAdded.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is greater than or equal to DEFAULT_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.greaterThanOrEqual=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is greater than or equal to UPDATED_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.greaterThanOrEqual=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is less than or equal to DEFAULT_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.lessThanOrEqual=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is less than or equal to SMALLER_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.lessThanOrEqual=" + SMALLER_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is less than DEFAULT_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.lessThan=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is less than UPDATED_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.lessThan=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllProductsByDateAddedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is greater than DEFAULT_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.greaterThan=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is greater than SMALLER_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.greaterThan=" + SMALLER_DATE_ADDED);
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified equals to DEFAULT_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.equals=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified equals to UPDATED_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.equals=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified in DEFAULT_DATE_MODIFIED or UPDATED_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.in=" + DEFAULT_DATE_MODIFIED + "," + UPDATED_DATE_MODIFIED);

        // Get all the productList where dateModified equals to UPDATED_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.in=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is not null
        defaultProductShouldBeFound("dateModified.specified=true");

        // Get all the productList where dateModified is null
        defaultProductShouldNotBeFound("dateModified.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is greater than or equal to DEFAULT_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.greaterThanOrEqual=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is greater than or equal to UPDATED_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.greaterThanOrEqual=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is less than or equal to DEFAULT_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.lessThanOrEqual=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is less than or equal to SMALLER_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.lessThanOrEqual=" + SMALLER_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is less than DEFAULT_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.lessThan=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is less than UPDATED_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.lessThan=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByDateModifiedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is greater than DEFAULT_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.greaterThan=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is greater than SMALLER_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.greaterThan=" + SMALLER_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByWishListIsEqualToSomething() throws Exception {
        WishList wishList;
        if (TestUtil.findAll(em, WishList.class).isEmpty()) {
            productRepository.saveAndFlush(product);
            wishList = WishListResourceIT.createEntity(em);
        } else {
            wishList = TestUtil.findAll(em, WishList.class).get(0);
        }
        em.persist(wishList);
        em.flush();
        product.setWishList(wishList);
        productRepository.saveAndFlush(product);
        UUID wishListId = wishList.getId();

        // Get all the productList where wishList equals to wishListId
        defaultProductShouldBeFound("wishListId.equals=" + wishListId);

        // Get all the productList where wishList equals to UUID.randomUUID()
        defaultProductShouldNotBeFound("wishListId.equals=" + UUID.randomUUID());
    }

    @Test
    @Transactional
    void getAllProductsByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            productRepository.saveAndFlush(product);
            category = CategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        product.addCategory(category);
        productRepository.saveAndFlush(product);
        UUID categoryId = category.getId();

        // Get all the productList where category equals to categoryId
        defaultProductShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the productList where category equals to UUID.randomUUID()
        defaultProductShouldNotBeFound("categoryId.equals=" + UUID.randomUUID());
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        productSearchRepository.save(product);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProduct.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProduct.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testProduct.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Product> productSearchList = IterableUtils.toList(productSearchRepository.findAll());
                Product testProductSearch = productSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProductSearch.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
                assertThat(testProductSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProductSearch.getRating()).isEqualTo(UPDATED_RATING);
                assertThat(testProductSearch.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
                assertThat(testProductSearch.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
            });
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(UUID.randomUUID());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(UUID.randomUUID());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(UUID.randomUUID());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.dateAdded(UPDATED_DATE_ADDED).dateModified(UPDATED_DATE_MODIFIED);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProduct.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProduct.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testProduct.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProduct.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProduct.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testProduct.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(UUID.randomUUID());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(UUID.randomUUID());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(UUID.randomUUID());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        productRepository.save(product);
        productSearchRepository.save(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProduct() throws Exception {
        // Initialize the database
        product = productRepository.saveAndFlush(product);
        productSearchRepository.save(product);

        // Search the product
        restProductMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));
    }
}
