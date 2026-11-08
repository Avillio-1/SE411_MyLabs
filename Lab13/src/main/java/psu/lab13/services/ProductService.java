package psu.lab13.services;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import psu.lab13.model.Product;
import psu.lab13.repositories.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll(Sort.by("id"));
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }
}
