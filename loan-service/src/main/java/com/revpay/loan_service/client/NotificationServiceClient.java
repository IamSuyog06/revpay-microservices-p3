package com.revpay.loan_service.client;

import com.revpay.loan_service.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationServiceClient {

    @PostMapping("/api/notifications/create")
    void createNotification(
            @RequestBody NotificationRequest request);
}