package com.sentinel.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventRetentionJob {
    private final SecurityEventService eventService;
    private final int retentionDays;

    public EventRetentionJob(
            SecurityEventService eventService,
            @Value("${sentinel.data-retention-days:30}") int retentionDays) {
        this.eventService = eventService;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "0 15 3 * * *")
    public void purgeExpiredRecords() {
        eventService.purgeExpiredData(retentionDays);
    }
}
