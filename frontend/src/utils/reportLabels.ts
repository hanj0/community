import type {
  ReportContentAction,
  ReportReason,
  ReportResolution,
  ReportTargetType,
  ReportUserAction,
  ReportUserActionReason,
} from '../types';

export const TARGET_LABEL: Record<ReportTargetType, string> = {
  POST: '게시글',
  COMMENT: '댓글',
};

export const REASON_LABEL: Record<ReportReason, string> = {
  SPAM: '스팸/광고',
  ABUSE: '욕설/비방',
  SEXUAL: '음란물/선정성',
  ILLEGAL: '불법 정보',
  ETC: '기타',
};

/** 처리완료 안에서의 세부 결과. 서버가 resolution을 안 내려주면 '처리완료'로만 표시한다. */
export const RESOLUTION_LABEL: Record<ReportResolution, string> = {
  REJECTED: '반려',
  CONTENT_DELETED: '삭제',
  TARGET_ALREADY_DELETED: '대상 삭제됨',
};

export const CONTENT_ACTION_LABEL: Record<ReportContentAction, string> = {
  REJECTED: '반려',
  CONTENT_DELETED: '삭제',
  TARGET_ALREADY_DELETED: '대상 이미 삭제됨',
};

export const USER_ACTION_LABEL: Record<ReportUserAction, string> = {
  NONE: '제재 없음',
  WARNING: '경고',
  SUSPEND_7D: '7일 정지',
  PERMANENT_BAN: '영구 정지',
};

export const USER_ACTION_REASON_LABEL: Record<ReportUserActionReason, string> = {
  REPEATED: '반복 위반',
  SEVERE_ONCE: '중대 위반 1회',
  MULTIPLE_CONTENT: '다중 콘텐츠 위반',
  COMBINED: '복합 위반',
  ETC: '기타',
};
