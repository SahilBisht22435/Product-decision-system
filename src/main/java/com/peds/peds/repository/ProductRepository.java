
package com.peds.peds.repository;

import com.peds.peds.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository
        extends MongoRepository<Product, String> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByPriceLessThanEqual(double price);

    @Query("{ 'category': { $regex: ?0, $options: 'i' }, 'price': { $gt: ?1, $lte: ?2 } }")
    List<Product> findByCategoryAndPriceRange(
            String category,
            double minPrice,
            double maxPrice);
}
