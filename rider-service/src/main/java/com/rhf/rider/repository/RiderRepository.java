package com.rhf.rider.repository;

import com.rhf.rider.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findByEmail(String email);
    Optional<Rider> findByPhone(String phone);
    List<Rider> findByNameContainingIgnoreCase(String name);
}
