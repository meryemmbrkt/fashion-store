package com.meryem.fashionstore.service;

import com.meryem.fashionstore.entity.*;
import com.meryem.fashionstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Eğer veri zaten varsa, başlatma yapma
        if (userRepository.count() > 0) {
            return;
        }

        // ==================== STORE ADMIN 1 ====================
        User admin1 = new User();
        admin1.setFirstName("Elif");
        admin1.setLastName("Kaya");
        admin1.setEmail("elif.kaya@fashionstore.com");
        admin1.setPassword(passwordEncoder.encode("admin123"));  // ✅ Hash'lendi
        admin1.setRole(Role.STORE_ADMIN);  // ✅ Role set edildi
        User savedAdmin1 = userRepository.save(admin1);

        // Basket otomatik oluştur
        Basket basket1 = new Basket();
        basket1.setUser(savedAdmin1);
        basket1.setTotalPrice(BigDecimal.ZERO);
        basketRepository.save(basket1);

        // ==================== STORE ADMIN 2 ====================
        User admin2 = new User();
        admin2.setFirstName("Zeynep");
        admin2.setLastName("Demir");
        admin2.setEmail("zeynep.demir@fashionstore.com");
        admin2.setPassword(passwordEncoder.encode("admin123"));
        admin2.setRole(Role.STORE_ADMIN);
        User savedAdmin2 = userRepository.save(admin2);

        Basket basket2 = new Basket();
        basket2.setUser(savedAdmin2);
        basket2.setTotalPrice(BigDecimal.ZERO);
        basketRepository.save(basket2);

        // ==================== REGULAR CUSTOMER ====================
        User customer1 = new User();
        customer1.setFirstName("Ayşe");
        customer1.setLastName("Yılmaz");
        customer1.setEmail("ayse.yilmaz@example.com");
        customer1.setPassword(passwordEncoder.encode("customer123"));
        customer1.setRole(Role.CUSTOMER);
        User savedCustomer1 = userRepository.save(customer1);

        Basket basket3 = new Basket();
        basket3.setUser(savedCustomer1);
        basket3.setTotalPrice(BigDecimal.ZERO);
        basketRepository.save(basket3);

        // ==================== STORE 1: PRESTIGE (Premium Mağaza) ====================
        Store store1 = Store.builder()
                .user(savedAdmin1)
                .storeName("PRESTIGE COUTURE")
                .link("https://prestige-couture.com")
                .build();
        Store savedStore1 = storeRepository.save(store1);

        // Store 1 Kategoriler
        Category perfumeCategory = Category.builder()
                .store(savedStore1)
                .name("Parfüm & Koku")
                .build();
        Category savedPerfumeCategory = categoryRepository.save(perfumeCategory);

        Category clothingCategory = Category.builder()
                .store(savedStore1)
                .name("Giyim")
                .build();
        Category savedClothingCategory = categoryRepository.save(clothingCategory);

        Category accessoriesCategory = Category.builder()
                .store(savedStore1)
                .name("Aksesuar")
                .build();
        Category savedAccessoriesCategory = categoryRepository.save(accessoriesCategory);

        // Store 1 Ürünler: Parfüm & Koku
        Product perfume1 = Product.builder()
                .name("Soft Vanilla Luxe")
                .description("Yumuşak vanilya notalarıyla başlayan, oud ve ambergris ile biten niş parfüm. Kısıtlı seri.")
                .price(BigDecimal.valueOf(2850.00))
                .stock(15)
                .store(savedStore1)
                .category(savedPerfumeCategory)
                .build();
        productRepository.save(perfume1);

        Product perfume2 = Product.builder()
                .name("Dark Rose Mystique")
                .description("Koyu gül ve tütün kombinasyonu. Gece kullanımı için ideal. Sahibinin kişiliğini yansıtan koku.")
                .price(BigDecimal.valueOf(2650.00))
                .stock(20)
                .store(savedStore1)
                .category(savedPerfumeCategory)
                .build();
        productRepository.save(perfume2);

        Product perfume3 = Product.builder()
                .name("Ocean Breeze EDP")
                .description("Deniz suyu, limon ve driftwood notalarıyla yazı konuşan ferah koku.")
                .price(BigDecimal.valueOf(1950.00))
                .stock(25)
                .store(savedStore1)
                .category(savedPerfumeCategory)
                .build();
        productRepository.save(perfume3);

        // Store 1 Ürünler: Giyim
        Product jacket1 = Product.builder()
                .name("Leather Perfection Jacket")
                .description("İtalyan deri ile işlenmiş oversize kesim vintage deri ceket. Ölümsüz stil parçası.")
                .price(BigDecimal.valueOf(5950.00))
                .stock(8)
                .store(savedStore1)
                .category(savedClothingCategory)
                .build();
        productRepository.save(jacket1);

        Product coat1 = Product.builder()
                .name("Camel Dream Coat")
                .description("100% kaşmir blendli lüks kış paltosu. Minimal estetiye mükemmel uyum.")
                .price(BigDecimal.valueOf(6800.00))
                .stock(5)
                .store(savedStore1)
                .category(savedClothingCategory)
                .build();
        productRepository.save(coat1);

        Product dress1 = Product.builder()
                .name("Silk Evening Dress")
                .description("Yumuşak ipek kumaştan dikili, zeminde işlemeli gece elbisesi. Özel günler için.")
                .price(BigDecimal.valueOf(4500.00))
                .stock(6)
                .store(savedStore1)
                .category(savedClothingCategory)
                .build();
        productRepository.save(dress1);

        // Store 1 Ürünler: Aksesuar
        Product necklace1 = Product.builder()
                .name("Pearl Elegance Necklace")
                .description("Akoya incisi ve 18K altın kombinasyonu. İhtişamlı ve şık kolye.")
                .price(BigDecimal.valueOf(3450.00))
                .stock(12)
                .store(savedStore1)
                .category(savedAccessoriesCategory)
                .build();
        productRepository.save(necklace1);

        Product handbag1 = Product.builder()
                .name("Signature Leather Bag")
                .description("El çantası. Döşeme deri, içerisinde 3 bölme, metal tokalar ile öne çıkan modern tasarım.")
                .price(BigDecimal.valueOf(3200.00))
                .stock(10)
                .store(savedStore1)
                .category(savedAccessoriesCategory)
                .build();
        productRepository.save(handbag1);

        Product sunglasses1 = Product.builder()
                .name("UV Protective Sunglasses")
                .description("Vintage güneş gözlüğü. 100% UV koruma, hafif metal çerçeve.")
                .price(BigDecimal.valueOf(1200.00))
                .stock(30)
                .store(savedStore1)
                .category(savedAccessoriesCategory)
                .build();
        productRepository.save(sunglasses1);

        // ==================== STORE 2: STREETWEAR JUNCTION ====================
        Store store2 = Store.builder()
                .user(savedAdmin2)
                .storeName("STREETWEAR JUNCTION")
                .link("https://streetwear-junction.com")
                .build();
        Store savedStore2 = storeRepository.save(store2);

        // Store 2 Kategoriler
        Category streetwearCategory = Category.builder()
                .store(savedStore2)
                .name("Streetwear")
                .build();
        Category savedStreetCategory = categoryRepository.save(streetwearCategory);

        Category sportsCategory = Category.builder()
                .store(savedStore2)
                .name("Spor & Aktif Giyim")
                .build();
        Category savedSportsCategory = categoryRepository.save(sportsCategory);

        Category sneakersCategory = Category.builder()
                .store(savedStore2)
                .name("Ayakkabı")
                .build();
        Category savedSneakersCategory = categoryRepository.save(sneakersCategory);

        // Store 2 Ürünler: Streetwear
        Product hoodie1 = Product.builder()
                .name("Vintage Graphic Hoodie")
                .description("Sokak modası temalı, kalın pamuk kumaştan hoodie. Y2K estetik.")
                .price(BigDecimal.valueOf(850.00))
                .stock(50)
                .store(savedStore2)
                .category(savedStreetCategory)
                .build();
        productRepository.save(hoodie1);

        Product tshirt1 = Product.builder()
                .name("Cyberpunk Oversized Tee")
                .description("Arka yüzü tamamen acid-wash baskılı, rahat kesim tişört. Uniseks.")
                .price(BigDecimal.valueOf(450.00))
                .stock(80)
                .store(savedStore2)
                .category(savedStreetCategory)
                .build();
        productRepository.save(tshirt1);

        Product tshirt2 = Product.builder()
                .name("Anime Aesthetic Tee")
                .description("Retro anime çizim detaylı beyaz pamuk tişört. Collector's item.")
                .price(BigDecimal.valueOf(520.00))
                .stock(45)
                .store(savedStore2)
                .category(savedStreetCategory)
                .build();
        productRepository.save(tshirt2);

        // Store 2 Ürünler: Spor & Aktif Giyim
        Product leggings1 = Product.builder()
                .name("Performance Running Leggings")
                .description("Nem emici teknoloji ile spor için özel tasarlanmış tayt. Yoga ve koşu uyumlu.")
                .price(BigDecimal.valueOf(680.00))
                .stock(35)
                .store(savedStore2)
                .category(savedSportsCategory)
                .build();
        productRepository.save(leggings1);

        Product jacket2 = Product.builder()
                .name("Windbreaker Sport Jacket")
                .description("Rüzgar geçirmez hafif ceket. Koşu ve outdoor sporları için ideal.")
                .price(BigDecimal.valueOf(1250.00))
                .stock(22)
                .store(savedStore2)
                .category(savedSportsCategory)
                .build();
        productRepository.save(jacket2);

        // Store 2 Ürünler: Ayakkabı
        Product sneakers1 = Product.builder()
                .name("Premium White Sneaker")
                .description("Klasik beyaz spor ayakkabı. Deri ve kanvas karışımı, yüksek kalite.")
                .price(BigDecimal.valueOf(1850.00))
                .stock(28)
                .store(savedStore2)
                .category(savedSneakersCategory)
                .build();
        productRepository.save(sneakers1);

        Product sneakers2 = Product.builder()
                .name("Urban Black High Top")
                .description("Yüksek bilekli siyah sneaker. Sokak modası için harika eşleşme.")
                .price(BigDecimal.valueOf(1550.00))
                .stock(40)
                .store(savedStore2)
                .category(savedSneakersCategory)
                .build();
        productRepository.save(sneakers2);

        Product boots1 = Product.builder()
                .name("Chelsea Boots Black")
                .description("Klasik siyah çelik uçlu bot. Hem casual hem formal kullanılabilir.")
                .price(BigDecimal.valueOf(2200.00))
                .stock(15)
                .store(savedStore2)
                .category(savedSneakersCategory)
                .build();
        productRepository.save(boots1);

        System.out.println("✅ Veriler başarıyla yüklendi!");
        System.out.println("📌 Test için kullanabileceğiniz hesaplar:");
        System.out.println("   STORE_ADMIN: elif.kaya@fashionstore.com / admin123");
        System.out.println("   STORE_ADMIN: zeynep.demir@fashionstore.com / admin123");
        System.out.println("   CUSTOMER: ayse.yilmaz@example.com / customer123");
    }
}