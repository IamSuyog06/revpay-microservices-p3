package com.revpay.wallet_service.repository;

import com.revpay.wallet_service.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository
        extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserIdAndIsActiveTrue(Long userId);
    Optional<PaymentMethod> findByIdAndUserId(Long id, Long userId);
    Optional<PaymentMethod> findByUserIdAndIsDefaultTrue(Long userId);
    boolean existsByUserIdAndLastFourDigitsAndExpiryMonthAndExpiryYear(
            Long userId, String lastFourDigits,
            String expiryMonth, String expiryYear);
}