import { useState } from 'react';
import type { CommentData } from '../../types';
import { getAvatarStyle } from '../../utils/avatar';
import { formatRelativeTime } from '../../utils/time';
import CommentInput from './CommentInput';

interface CommentItemProps {
  comment: CommentData;
  isReply?: boolean;
}

export default function CommentItem({ comment, isReply = false }: CommentItemProps) {
  const [showReplies, setShowReplies] = useState(true);
  const [showInput, setShowInput] = useState(false);
  const [likes, setLikes] = useState(comment.likes);
  const [dislikes, setDislikes] = useState(comment.dislikes);
  const [liked, setLiked] = useState(false);
  const [disliked, setDisliked] = useState(false);

  const { av, ab, ac } = getAvatarStyle(comment.author);

  const toggleLike = () => {
    if (liked) { setLikes(l => l - 1); setLiked(false); }
    else { setLikes(l => l + 1); setLiked(true); if (disliked) { setDislikes(d => d - 1); setDisliked(false); } }
  };

  const toggleDislike = () => {
    if (disliked) { setDislikes(d => d - 1); setDisliked(false); }
    else { setDislikes(d => d + 1); setDisliked(true); if (liked) { setLikes(l => l - 1); setLiked(false); } }
  };

  const isMention = comment.content.startsWith('@');
  const body = isMention
    ? (
      <>
        <span className="mnt">{comment.content.split(' ')[0]} </span>
        {comment.content.split(' ').slice(1).join(' ')}
      </>
    )
    : comment.content;

  return (
    <>
      <div className={'cmt' + (isReply ? ' rp' : '')}>
        <div className="cmar">
          <div className="cma">
            <div className="cmav" style={{ background: ab, color: ac }}>{av}</div>
            <div>
              <div className="cmnm">
                {comment.author}
                {comment.author === '작성자' && <span className="wrbg">작성자</span>}
              </div>
              <div className="cmtm">{formatRelativeTime(comment.createdAt)}</div>
            </div>
          </div>
          <div className="cmacts">
            <button className={'cmb' + (liked ? ' lk' : '')} onClick={toggleLike}>▲ {likes}</button>
            <button className={'cmb' + (disliked ? ' dk' : '')} onClick={toggleDislike}>▼ {dislikes}</button>
            {!isReply && <button className="cmb" onClick={() => setShowInput(v => !v)}>답글</button>}
            <button className="cmb">신고</button>
          </div>
        </div>
        <div className="cmtxt">{body}</div>
        {!isReply && comment.replies.length > 0 && (
          <button className="rpt" onClick={() => setShowReplies(v => !v)}>
            {showReplies ? '▾ 답글 숨기기' : '▸ 답글 보기'}
            <span className="rpb">{comment.replies.length}</span>
          </button>
        )}
      </div>
      {!isReply && showReplies && comment.replies.map(r => (
        <CommentItem key={r.id} comment={r} isReply={true} />
      ))}
      {showInput && !isReply && (
        <div style={{ paddingLeft: 48, background: 'var(--bg2)', borderBottom: '.5px solid var(--b)' }}>
          <CommentInput
            placeholder={`@${comment.author}에게 답글 작성...`}
            onSubmit={() => setShowInput(false)}
          />
        </div>
      )}
    </>
  );
}
