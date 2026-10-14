package com.meryem.fashionstore.service;

import com.meryem.fashionstore.document.ProductDocument;
import com.meryem.fashionstore.entity.Product;
import com.meryem.fashionstore.repository.ProductRepository;
import com.meryem.fashionstore.repository.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductSearchSyncService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductSearchSyncService.class);

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;


    public ProductSearchSyncService(
            ProductRepository productRepository,
            ProductSearchRepository productSearchRepository
    ) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
    }

    @Transactional(readOnly = true)
    public int syncProductsToElasticsearch() {

        log.info("PRODUCT SYNC - Reading products from database");

        List<Product> products = productRepository.findAllByIsActiveTrue();

        List<ProductDocument> documents = products.stream()
                .map(this::toDocument)
                .toList();

        log.info("PRODUCT SYNC - {} products loaded from database", documents.size());
        productSearchRepository.saveAll(documents);
        log.info("PRODUCT SYNC - {} products indexed into Elasticsearch", documents.size());

        return documents.size();
    }
    private ProductDocument toDocument(Product product) {
        return new ProductDocument(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStore().getStoreName(),
                product.getCategory().getName()

        );

    }

}

