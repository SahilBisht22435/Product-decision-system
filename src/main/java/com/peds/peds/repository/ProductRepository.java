
package com.peds.peds.repository;

import com.peds.peds.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository
        extends MongoRepository<Product, String> {
    
    List<Product> findByCategoryIgnoreCase(String category);
List<Product> findAll();
    List<Product> findByPriceLessThanEqual(double price);
}
