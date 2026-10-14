package com.meryem.fashionstore.config;

import com.meryem.fashionstore.document.ProductDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("elasticsearch")
@Component
@RequiredArgsConstructor

public class ElasticsearchIndexConfig {

    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(ProductDocument.class);

        if (indexOps.exists()) {
            return;
        }

        Document ngramFilter = Document.create()
                .append("type", "edge_ngram")
                .append("min_gram", 2)
                .append("max_gram", 20);

        Document indexAnalyzer = Document.create()
                .append("type", "custom")
                .append("tokenizer", "standard")
                .append("filter", List.of("lowercase", "product_edge_ngram_filter"));

        Document searchAnalyzer = Document.create()
                .append("type", "custom")
                .append("tokenizer", "standard")
                .append("filter", List.of("lowercase"));

        Document analysis = Document.create()
                .append("filter", Document.create().append("product_edge_ngram_filter", ngramFilter))
                .append("analyzer", Document.create()
                        .append("product_index_analyzer", indexAnalyzer)
                        .append("product_search_analyzer", searchAnalyzer));

        Document settings = Document.create().append("analysis", analysis);

        indexOps.create(settings);
        Document mapping = indexOps.createMapping(ProductDocument.class);
        indexOps.putMapping(mapping);
    }
}