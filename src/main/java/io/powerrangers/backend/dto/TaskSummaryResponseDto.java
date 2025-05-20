package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskSummaryResponseDto {
    private int year;
    private int month;
    private List<DailySummary> dailySummaries;

    @Getter
    @AllArgsConstructor
    public static class DailySummary {
        private String date;
        private int taskCount;
    }
}
