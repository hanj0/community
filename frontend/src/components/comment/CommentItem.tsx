import { useState } from 'react';
import type { CommentData, ReactionType } from '../../types';
import { fetchReplies, updateComment, deleteComment, createComment, setCommentReaction, deleteCommentReaction } from '../../api/posts';
import { getAvatarStyle } from '../../utils/avatar';
import { formatRelativeTime } from '../../utils/time';
import { useAuth } from '../../context/AuthContext';
import { useReaction } from '../../hooks/useReaction';
import CommentInput from './CommentInput';
import ReportModal from '../common/ReportModal';

interface CommentItemProps {
  comment: CommentData;
  postId: number;
  isReply?: boolean;
}

export default function CommentItem({ comment, postId, isReply = false }: CommentItemProps) {
  const { user } = useAuth();
  const [repliesOpen, setRepliesOpen] = useState(false);
  const [replies, setReplies] = useState<CommentData[]>([]);
  const [repliesLoaded, setRepliesLoaded] = useState(false);
  const [repliesLoading, setRepliesLoading] = useState(false);
  const [showInput, setShowInput] = useState(false);
  const { reaction, likeCount, dislikeCount, pending, react } = useReaction({
    initialReaction: comment.reactionType,
    initialLikeCount: comment.likeCount ?? 0,
    initialDislikeCount: comment.dislikeCount ?? 0,
    onSet: (type: ReactionType) => setCommentReaction(comment.id, type),
    onCancel: () => deleteCommentReaction(comment.id),
    isAuthorized: !!user,
    onUnauthorized: () => window.dispatchEvent(new CustomEvent('auth:unauthorized')),
  });
  const [content, setContent] = useState(comment.content);
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState('');
  const [editSaving, setEditSaving] = useState(false);
  const [isConfirmingDelete, setIsConfirmingDelete] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [deleted, setDeleted] = useState(false);
  const [isReporting, setIsReporting] = useState(false);

  const isCommentAuthor = user?.id === comment.authorInfo?.id;

  const handleStartEdit = () => {
    setEditText(content);
    setIsEditing(true);
  };

  const handleCancelEdit = () => setIsEditing(false);

  const handleDelete = async () => {
    if (deleteLoading) return;
    setDeleteLoading(true);
    try {
      await deleteComment(comment.id);
      setDeleted(true);
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleSaveEdit = async () => {
    if (editSaving) return;
    setEditSaving(true);
    try {
      await updateComment(comment.id, editText);
      setContent(editText);
      setIsEditing(false);
    } finally {
      setEditSaving(false);
    }
  };

  const author = comment.authorInfo?.username ?? '';
  const { av, ab, ac } = getAvatarStyle(author || '?');

  const handleToggleReplies = async () => {
    if (!repliesLoaded) {
      setRepliesLoading(true);
      try {
        const data = await fetchReplies(comment.id);
        setReplies(data);
        setRepliesLoaded(true);
      } finally {
        setRepliesLoading(false);
      }
    }
    setRepliesOpen(v => !v);
  };

  const isMention = content.startsWith('@');
  const body = isMention
    ? (
      <>
        <span className="mnt">{content.split(' ')[0]} </span>
        {content.split(' ').slice(1).join(' ')}
      </>
    )
    : content;

  if (deleted) return null;

  return (
    <>
      <div className={'cmt' + (isReply ? ' rp' : '')}>
        <div className="cmar">
          <div className="cma">
            <div className="cmav" style={{ background: ab, color: ac }}>{av}</div>
            <div>
              <div className="cmnm">
                {author}
                {author === '작성자' && <span className="wrbg">작성자</span>}
              </div>
              <div className="cmtm">{formatRelativeTime(comment.createdAt)}</div>
            </div>
          </div>
          <div className="cmacts">
            <button className={'cmb' + (reaction === 'LIKE' ? ' lk' : '')} onClick={() => react('LIKE')} disabled={pending}>▲ {likeCount}</button>
            <button className={'cmb' + (reaction === 'DISLIKE' ? ' dk' : '')} onClick={() => react('DISLIKE')} disabled={pending}>▼ {dislikeCount}</button>
            {!isReply && <button className="cmb" onClick={() => setShowInput(v => !v)}>답글</button>}
            {isCommentAuthor && !isEditing && (
              <>
                <button className="cmb" onClick={handleStartEdit}>수정</button>
                {isConfirmingDelete
                  ? <>
                      <span style={{ fontSize: 12, color: 'var(--t2)' }}>삭제할까요?</span>
                      <button className="cmb dng" onClick={handleDelete} disabled={deleteLoading}>
                        {deleteLoading ? '삭제 중...' : '삭제'}
                      </button>
                      <button className="cmb" onClick={() => setIsConfirmingDelete(false)}>취소</button>
                    </>
                  : <button className="cmb dng" onClick={() => setIsConfirmingDelete(true)}>삭제</button>
                }
              </>
            )}
            {user && !isCommentAuthor && (
              <button className="cmb dng" onClick={() => setIsReporting(true)}>
                신고
              </button>
            )}
          </div>
        </div>
        {isEditing
          ? (
            <div style={{ padding: '8px 0' }}>
              <textarea
                className="edt-content"
                value={editText}
                onChange={e => setEditText(e.target.value)}
                rows={3}
                style={{ fontSize: 13 }}
              />
              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', paddingTop: 6 }}>
                <button className="cmb" onClick={handleCancelEdit}>취소</button>
                <button className="cmb" style={{ color: 'var(--primary)' }} onClick={handleSaveEdit} disabled={editSaving}>
                  {editSaving ? '저장 중...' : '저장'}
                </button>
              </div>
            </div>
          )
          : <div className="cmtxt">{body}</div>
        }
        {!isReply && comment.replyCount > 0 && (
          <button className="rpt" onClick={handleToggleReplies} disabled={repliesLoading}>
            {repliesLoading
              ? '불러오는 중...'
              : repliesOpen
                ? '▾ 답글 숨기기'
                : `▸ 답글 보기`}
            {!repliesLoading && <span className="rpb">{comment.replyCount}</span>}
          </button>
        )}
      </div>
      {!isReply && repliesOpen && replies.map(r => (
        <CommentItem key={r.id} comment={r} postId={postId} isReply={true} />
      ))}
      {showInput && !isReply && (
        <div style={{ paddingLeft: 48, background: 'var(--bg2)', borderBottom: '.5px solid var(--b)' }}>
          <CommentInput
            placeholder={`@${author}에게 답글 작성...`}
            onSubmit={async (text) => {
              const newReply = await createComment(postId, text, comment.id);
              setReplies(prev => [...prev, newReply]);
              setRepliesLoaded(true);
              setRepliesOpen(true);
              setShowInput(false);
            }}
          />
        </div>
      )}
      {isReporting && (
        <ReportModal targetType="COMMENT" targetId={comment.id} onClose={() => setIsReporting(false)} />
      )}
    </>
  );
}
