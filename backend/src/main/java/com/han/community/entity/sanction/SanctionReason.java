package com.han.community.entity.sanction;

public enum SanctionReason {

    // 스팸/어뷰징
    SPAM_ADVERTISING,   // 광고성 게시물
    SPAM_FLOODING,      // 도배
    SPAM_BOT,           // 자동화 프로그램 사용

    // 언어폭력
    ABUSE_INSULT,       // 욕설/모욕
    ABUSE_HARASSMENT,   // 특정인 괴롭힘
    ABUSE_HATE,         // 혐오 표현
    ABUSE_THREAT,       // 협박

    // 컨텐츠
    CONTENT_SEXUAL,     // 음란물
    CONTENT_VIOLENCE,   // 폭력적 컨텐츠
    CONTENT_DISGUSTING, // 역거운 컨텐츠
    CONTENT_ILLEGAL,    // 불법 정보
    CONTENT_FALSE_INFO, // 허위 정보 유포

    // 권리 침해
    PRIVACY_EXPOSURE,   // 개인정보 노출
    COPYRIGHT_VIOLATION,// 저작권 침해
    IMPERSONATION,      // 사칭

    // 시스템/운영
    MULTI_ACCOUNT,      // 다중 계정 운영
    BAN_EVASION,        // 제재 회피
    REPORT_ABUSE,       // 신고 기능 악용
    RULE_VIOLATION,     // 기타 운영정책 위반
}
