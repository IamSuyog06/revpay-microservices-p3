package com.revpay.transaction_service.repository;

import com.revpay.transaction_service.entity.MoneyRequest;
import com.revpay.transaction_service.enums.MoneyRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    // Incoming requests — someone requested from me
    List<MoneyRequest> findByRequestedFromIdOrderByCreatedAtDesc(
            Long requestedFromId);

    // Outgoing requests — I requested from someone
    List<MoneyRequest> findByRequesterIdOrderByCreatedAtDesc(
            Long requesterId);

    // Incoming pending requests
    List<MoneyRequest> findByRequestedFromIdAndStatus(
            Long requestedFromId, MoneyRequestStatus status);

    Optional<MoneyRequest> findByIdAndRequesterId(
            Long id, Long requesterId);

    Optional<MoneyRequest> findByIdAndRequestedFromId(
            Long id, Long requestedFromId);
}