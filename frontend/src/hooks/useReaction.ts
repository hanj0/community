import { useState } from 'react';
import type { ReactionType } from '../types';

interface UseReactionOptions {
  initialReaction: ReactionType | null;
  initialLikeCount: number;
  initialDislikeCount: number;
  /** 좋아요/싫어요 설정 요청. 성공 여부만 판단하고 카운트는 프론트에서 선반영한다. */
  onSet: (type: ReactionType) => Promise<void>;
  /** 동일한 반응을 다시 눌러 취소할 때의 요청. */
  onCancel: () => Promise<void>;
  /** 비로그인 등 권한이 없을 때 호출. */
  onUnauthorized?: () => void;
  /** false면 react 호출 시 onUnauthorized로 위임한다. */
  isAuthorized?: boolean;
}

/**
 * 좋아요/싫어요 낙관적 업데이트 공용 훅.
 * 선반영 → 서버 요청 → 실패 시 롤백. (카운트는 프론트에서 ±1로 계산, 새로고침 시 서버 값으로 동기화)
 */
export function useReaction(opts: UseReactionOptions) {
  const [reaction, setReaction] = useState<ReactionType | null>(opts.initialReaction);
  const [likeCount, setLikeCount] = useState(opts.initialLikeCount);
  const [dislikeCount, setDislikeCount] = useState(opts.initialDislikeCount);
  const [pending, setPending] = useState(false);

  /** 비동기로 도착하는 초기값을 동기화할 때 사용 (예: 게시글 상세 fetch 이후). */
  const reset = (next: { reaction: ReactionType | null; likeCount: number; dislikeCount: number }) => {
    setReaction(next.reaction);
    setLikeCount(next.likeCount);
    setDislikeCount(next.dislikeCount);
  };

  const react = async (type: ReactionType) => {
    if (pending) return;
    if (opts.isAuthorized === false) {
      opts.onUnauthorized?.();
      return;
    }

    const prev = { reaction, likeCount, dislikeCount };
    const isCancel = reaction === type;

    // 낙관적 선반영
    setReaction(isCancel ? null : type);
    setLikeCount(n => n - (prev.reaction === 'LIKE' ? 1 : 0) + (!isCancel && type === 'LIKE' ? 1 : 0));
    setDislikeCount(n => n - (prev.reaction === 'DISLIKE' ? 1 : 0) + (!isCancel && type === 'DISLIKE' ? 1 : 0));

    setPending(true);
    try {
      if (isCancel) {
        await opts.onCancel();
      } else {
        await opts.onSet(type);
      }
    } catch {
      // 롤백
      setReaction(prev.reaction);
      setLikeCount(prev.likeCount);
      setDislikeCount(prev.dislikeCount);
    } finally {
      setPending(false);
    }
  };

  return { reaction, likeCount, dislikeCount, pending, react, reset };
}
