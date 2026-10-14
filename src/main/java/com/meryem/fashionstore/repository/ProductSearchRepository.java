package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, Long> {

    @Query("""
        {
          "match": {
            "name": "?0"
          }
        }
        """)
    Page<ProductDocument> searchProducts(
            String query,
            Pageable pageable
    );


    @Query("""
        {
          "bool": {
            "must": [
              {
                "match": {
                  "name": "?0"
                }
              }
            ],
            "filter": [
              {
                "range": {
                  "price": {
                    "lte": "?1"
                  }
                }
              }
            ]
          }
        }
        """)
    Page<ProductDocument> searchProductsWithPriceFilter(
            String query,
            Double maxPrice,
            Pageable pageable
    );


    @Query("""
        {
          "match": {
            "name": {
              "query": "?0",
              "fuzziness": "AUTO"
            }
          }
        }
        """)
    Page<ProductDocument> searchProductsFuzzy(
            String query,
            Pageable pageable
    );


    @Query("""
        {
          "bool": {
            "should": [
              {
                "match": {
                  "name": {
                    "query": "?0",
                    "boost": 3
                  }
                }
              },
              {
                "match": {
                  "description": {
                    "query": "?0",
                    "boost": 2
                  }
                }
              },
              {
                "match": {
                  "categoryName": {
                    "query": "?0",
                    "boost": 1
                  }
                }
              }
            ],
            "minimum_should_match": 1
          }
        }
        """)
    Page<ProductDocument> searchProductsWithShould(
            String query,
            Pageable pageable
    );
}