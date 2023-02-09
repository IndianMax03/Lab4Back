package com.manu.lab4.repositories;

import com.manu.lab4.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer> {

    public List<Hit> findAllByUserId(long userId);

    public void deleteAllByUserId(long userId);

}
