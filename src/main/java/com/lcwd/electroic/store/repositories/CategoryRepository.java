package com.lcwd.electroic.store.repositories;

import com.lcwd.electroic.store.entities.Category;
import com.lcwd.electroic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
