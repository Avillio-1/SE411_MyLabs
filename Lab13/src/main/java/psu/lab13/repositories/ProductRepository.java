package psu.lab13.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import psu.lab13.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>,
        PagingAndSortingRepository<Product, Integer> {
}
