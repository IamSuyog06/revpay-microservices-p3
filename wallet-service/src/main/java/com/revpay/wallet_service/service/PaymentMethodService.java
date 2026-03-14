package com.revpay.wallet_service.service;

import com.revpay.wallet_service.dto.PaymentMethodRequest;
import com.revpay.wallet_service.dto.PaymentMethodResponse;
import com.revpay.wallet_service.entity.PaymentMethod;
import com.revpay.wallet_service.enums.CardType;
import com.revpay.wallet_service.repository.PaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentMethodService.class);

    private final PaymentMethodRepository
            paymentMethodRepository;

    public PaymentMethodService(
            PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public PaymentMethodResponse addPaymentMethod(
            Long userId, PaymentMethodRequest request) {
        logger.info("Adding payment method for userId: {}",
                userId);

        // Extract last 4 digits only
        String lastFour = request.getCardNumber()
                .substring(request.getCardNumber().length() - 4);

        // Check duplicate card for same user
        if (paymentMethodRepository
                .existsByUserIdAndLastFourDigitsAndExpiryMonthAndExpiryYear(
                        userId, lastFour,
                        request.getExpiryMonth(),
                        request.getExpiryYear())) {
            throw new RuntimeException(
                    "This card is already added");
        }

        PaymentMethod method = new PaymentMethod();
        method.setUserId(userId);
        method.setCardHolderName(request.getCardHolderName());
        method.setLastFourDigits(lastFour);
        method.setExpiryMonth(request.getExpiryMonth());
        method.setExpiryYear(request.getExpiryYear());
        method.setCardType(
                CardType.valueOf(request.getCardType()));
        method.setBillingAddress(request.getBillingAddress());
        // CVV is NEVER stored

        // If this is the first card, set as default
        List<PaymentMethod> existing =
                paymentMethodRepository
                        .findByUserIdAndIsActiveTrue(userId);
        if (existing.isEmpty()) {
            method.setDefault(true);
        }

        PaymentMethod saved =
                paymentMethodRepository.save(method);
        logger.info("Payment method added for userId: {},"
                + " lastFour: {}", userId, lastFour);
        return mapToResponse(saved);
    }

    public List<PaymentMethodResponse> getPaymentMethods(
            Long userId) {
        logger.debug("Fetching payment methods for userId: {}",
                userId);
        return paymentMethodRepository
                .findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deletePaymentMethod(Long userId, Long cardId) {
        logger.info("Deleting payment method id: {}"
                + " for userId: {}", cardId, userId);

        PaymentMethod method = paymentMethodRepository
                .findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment method not found"));

        method.setActive(false);

        // If deleted card was default, set another as default
        if (method.isDefault()) {
            List<PaymentMethod> remaining =
                    paymentMethodRepository
                            .findByUserIdAndIsActiveTrue(userId)
                            .stream()
                            .filter(m -> !m.getId().equals(cardId))
                            .collect(Collectors.toList());

            if (!remaining.isEmpty()) {
                remaining.get(0).setDefault(true);
                paymentMethodRepository
                        .save(remaining.get(0));
            }
        }

        paymentMethodRepository.save(method);
        logger.info("Payment method deleted: {}", cardId);
    }

    public PaymentMethodResponse setDefaultPaymentMethod(
            Long userId, Long cardId) {
        logger.info("Setting default payment method id: {}"
                + " for userId: {}", cardId, userId);

        // Remove current default
        paymentMethodRepository
                .findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(current -> {
                    current.setDefault(false);
                    paymentMethodRepository.save(current);
                });

        // Set new default
        PaymentMethod method = paymentMethodRepository
                .findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment method not found"));

        method.setDefault(true);
        PaymentMethod saved =
                paymentMethodRepository.save(method);
        logger.info("Default payment method set: {}", cardId);
        return mapToResponse(saved);
    }

    private PaymentMethodResponse mapToResponse(
            PaymentMethod method) {
        PaymentMethodResponse response =
                new PaymentMethodResponse();
        response.setId(method.getId());
        response.setUserId(method.getUserId());
        response.setCardHolderName(method.getCardHolderName());
        response.setMaskedCardNumber(
                "XXXX XXXX XXXX " + method.getLastFourDigits());
        response.setExpiryMonth(method.getExpiryMonth());
        response.setExpiryYear(method.getExpiryYear());
        response.setCardType(method.getCardType().name());
        response.setBillingAddress(method.getBillingAddress());
        response.setDefault(method.isDefault());
        response.setCreatedAt(method.getCreatedAt());
        return response;
    }
}