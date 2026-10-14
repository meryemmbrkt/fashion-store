package com.meryem.fashionstore.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/products-settings.json")
public class ProductDocument {

        @Id
        private Long id;

        @Field(type = FieldType.Text,
                analyzer = "product_analyzer",
                searchAnalyzer = "product_search_analyzer")
        private String name;

        @Field(type = FieldType.Text,
                analyzer = "product_analyzer",
                searchAnalyzer = "product_search_analyzer")
        private String description;

        @Field(type = FieldType.Double)
        private BigDecimal price;

        @Field(type = FieldType.Integer)
        private Integer stock;

        @Field(type = FieldType.Text,
                analyzer = "product_analyzer",
                searchAnalyzer = "product_search_analyzer")
        private String storeName;

        @Field(type = FieldType.Text,
                analyzer = "product_analyzer",
                searchAnalyzer = "product_search_analyzer")
        private String categoryName;
}