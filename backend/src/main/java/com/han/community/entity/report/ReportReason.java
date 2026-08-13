package com.han.community.entity.report;

public enum ReportReason {

    SPAM,       // 스팸/광고/도배
    ABUSE,      // 욕설/비방/혐오 표현
    SEXUAL,     // 음란물/선정적
    VIOLENCE,   // 폭력적
    DISGUSTING, // 역겨움/혐오
    PRIVACY,    // 개인정보 관련
    ILLEGAL,    // 불법행위
    COPYRIGHT,  // 저작권문제
    OTHER;

    public boolean requiresDetail() {
        return this == OTHER;
    }
}
