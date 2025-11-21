package com.example.cotato_networking.repository;

import com.example.cotato_networking.domain.Location;
import com.example.cotato_networking.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findAllByUser(User user);

    Optional<Location> findByIdAndUser(Long id, User user);

    Boolean existsByUserAndLocationName(User user, String locationName);
}
