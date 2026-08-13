package com.han.community.dto.report;

import com.han.community.entity.report.ReportResolution;
import com.han.community.entity.sanction.SanctionReason;
import com.han.community.entity.sanction.SanctionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReportResolveRequestDto(

        @NotEmpty List<Long> reportIds,
        @NotNull ReportResolution contentActionType,
        SanctionReason reason,
        @NotBlank String reasonDetail,
        @Valid Sanction sanction
) {

    public record Sanction(
            @NotNull SanctionType sanctionType,
            Integer durationDays,
            @NotBlank String memo
    ) {}
}
