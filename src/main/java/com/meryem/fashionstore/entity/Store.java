package com.meryem.fashionstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @Column(nullable = false)
    private String storeName;


    private String link;


    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "store")
    private List<Product> products = new ArrayList<>();


    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "store")
    private List<Category> categories = new ArrayList<>();


    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}