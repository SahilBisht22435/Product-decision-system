
package com.peds.peds.repository;

import com.peds.peds.model.Product;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository
        extends MongoRepository<Product, String> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByPriceLessThanEqual(double price);

    @Query(
            value = "{ 'category': { $regex: '^?0$', $options: 'i' }, " + "'price': { $gt: ?1, $lte: ?2 } }"
    )
    List<Product> findByCategoryAndPriceRange(
            String category,
            double minPrice,
            double maxPrice);
}

// For example, if the method receives:
// - Category: LAPTOP
// - Minimum price: ₹30,000
// - Maximum price: ₹60,000
// It retrieves laptops priced above ₹30,000 and at or below ₹60,000, using a case-insensitive category match.