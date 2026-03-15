package com.revpay.transaction_service.controller;

import com.revpay.transaction_service.dto.MoneyRequestDTO;
import com.revpay.transaction_service.dto.MoneyRequestResponse;
import com.revpay.transaction_service.service.MoneyRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/money-requests")
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    public MoneyRequestController(
            MoneyRequestService moneyRequestService) {
        this.moneyRequestService = moneyRequestService;
    }

    @PostMapping("/{userId}/create")
    public ResponseEntity<MoneyRequestResponse> create(
            @PathVariable Long userId,
            @Valid @RequestBody MoneyRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(moneyRequestService
                        .createRequest(userId, dto));
    }

    @GetMapping("/{userId}/incoming")
    public ResponseEntity<List<MoneyRequestResponse>>
    incoming(@PathVariable Long userId) {
        return ResponseEntity.ok(
                moneyRequestService
                        .getIncomingRequests(userId));
    }

    @GetMapping("/{userId}/outgoing")
    public ResponseEntity<List<MoneyRequestResponse>>
    outgoing(@PathVariable Long userId) {
        return ResponseEntity.ok(
                moneyRequestService
                        .getOutgoingRequests(userId));
    }

    @PutMapping("/{userId}/accept/{requestId}")
    public ResponseEntity<MoneyRequestResponse> accept(
            @PathVariable Long userId,
            @PathVariable Long requestId,
            @RequestParam String pin) {
        return ResponseEntity.ok(
                moneyRequestService
                        .acceptRequest(userId, requestId, pin));
    }

    @PutMapping("/{userId}/decline/{requestId}")
    public ResponseEntity<MoneyRequestResponse> decline(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(
                moneyRequestService
                        .declineRequest(userId, requestId));
    }

    @PutMapping("/{userId}/cancel/{requestId}")
    public ResponseEntity<MoneyRequestResponse> cancel(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(
                moneyRequestService
                        .cancelRequest(userId, requestId));
    }
}