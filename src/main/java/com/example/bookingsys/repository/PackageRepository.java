package com.example.bookingsys.repository;

import com.example.bookingsys.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    // Fetch packages by country code
    List<Package> findByCountryCode(String countryCode);

    // Fetch packages by user ID
    List<Package> findByUserId(Long userId);

}
