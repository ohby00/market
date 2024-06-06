package com.osio.quantityservice.repository;

import com.osio.quantityservice.entity.Quantity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuantityRepository extends JpaRepository<Quantity, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT p FROM Quantity p WHERE p.id = :quantityId")
    Quantity findByIdForUpdate(@Param("quantityId") Long quantityId);
}
