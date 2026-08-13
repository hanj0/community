import type {
  ReportReason,
  ReportResolution,
  ReportTargetType,
  SanctionReason,
  SanctionType,
} from '../types';

export const TARGET_LABEL: Record<ReportTargetType, string> = {
  POST: '게시글',
  COMMENT: '댓글',
};

export const REASON_LABEL: Record<ReportReason, string> = {
  SPAM: '스팸/광고',
  ABUSE: '욕설/비방',
  SEXUAL: '음란물/선정성',
  VIOLENCE: '폭력적 콘텐츠',
  DISGUSTING: '혐오감을 주는 콘텐츠',
  PRIVACY: '개인정보 노출',
  ILLEGAL: '불법 정보',
  COPYRIGHT: '저작권 침해',
  OTHER: '기타',
};

/**
 * 콘텐츠 조치 결과. 처리완료 이력 배지와 신고 처리 드로어의 "콘텐츠 조치" 선택지에서 함께 쓴다.
 * 서버가 resolution을 안 내려주면 '처리완료'로만 표시한다.
 */
export const RESOLUTION_LABEL: Record<ReportResolution, string> = {
  REJECTED: '반려',
  CONTENT_DELETED: '삭제',
};

/** 관리자가 처리 시 고르는 제재 사유(22종). 신고 등록 사유({@link REASON_LABEL})와는 다른 카테고리다. */
export const SANCTION_REASON_LABEL: Record<SanctionReason, string> = {
  SPAM_ADVERTISING: '광고성 게시물',
  SPAM_FLOODING: '도배',
  SPAM_BOT: '자동화 프로그램 사용',
  ABUSE_INSULT: '욕설/모욕',
  ABUSE_HARASSMENT: '특정인 괴롭힘',
  ABUSE_HATE: '혐오 표현',
  ABUSE_THREAT: '협박',
  CONTENT_SEXUAL: '음란물',
  CONTENT_VIOLENCE: '폭력적 컨텐츠',
  CONTENT_DISGUSTING: '역겨운 컨텐츠',
  CONTENT_ILLEGAL: '불법 정보',
  CONTENT_FALSE_INFO: '허위 정보 유포',
  PRIVACY_EXPOSURE: '개인정보 노출',
  COPYRIGHT_VIOLATION: '저작권 침해',
  IMPERSONATION: '사칭',
  MULTI_ACCOUNT: '다중 계정 운영',
  BAN_EVASION: '제재 회피',
  REPORT_ABUSE: '신고 기능 악용',
  RULE_VIOLATION: '기타 운영정책 위반',
};

/** 처리 사유 선택 UI를 카테고리별로 묶기 위한 그룹 정의. */
export const SANCTION_REASON_GROUPS: { label: string; options: SanctionReason[] }[] = [
  { label: '스팸/어뷰징', options: ['SPAM_ADVERTISING', 'SPAM_FLOODING', 'SPAM_BOT'] },
  { label: '언어폭력', options: ['ABUSE_INSULT', 'ABUSE_HARASSMENT', 'ABUSE_HATE', 'ABUSE_THREAT'] },
  {
    label: '컨텐츠',
    options: ['CONTENT_SEXUAL', 'CONTENT_VIOLENCE', 'CONTENT_DISGUSTING', 'CONTENT_ILLEGAL', 'CONTENT_FALSE_INFO'],
  },
  { label: '권리 침해', options: ['PRIVACY_EXPOSURE', 'COPYRIGHT_VIOLATION', 'IMPERSONATION'] },
  { label: '시스템/운영', options: ['MULTI_ACCOUNT', 'BAN_EVASION', 'REPORT_ABUSE', 'RULE_VIOLATION'] },
];

export const SANCTION_TYPE_LABEL: Record<SanctionType, string> = {
  WARNING: '경고',
  WRITE_BLOCK: '작성 제한',
  BAN: '영구 정지',
};
