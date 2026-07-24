import { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { PostDetail, PostSummary, CommentData } from '../types';
import { fetchPostDetail, fetchComments, createComment, fetchHotPosts, fetchPosts, updatePost, deletePost, toggleBookmark, setPostReaction, deletePostReaction } from '../api/posts';
import { useChannels } from '../hooks/useChannels';
import { useReaction } from '../hooks/useReaction';
import { useAuth } from '../context/AuthContext';
import { formatRelativeTime } from '../utils/time';
import { rankColor } from '../utils/rank';
import ChTag from '../components/common/ChTag';
import ReportModal from '../components/common/ReportModal';
import CommentItem from '../components/comment/CommentItem';
import CommentInput from '../components/comment/CommentInput';

const COMMENT_PAGE_SIZE = 20;

export default function PostDetailPage() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const postId = Number(id);
  const channels = useChannels();
  const { user } = useAuth();

  const [post, setPost] = useState<PostDetail | null>(null);
  const [postLoading, setPostLoading] = useState(true);
  const [postError, setPostError] = useState<string | null>(null);

  const [bookmarked, setBookmarked] = useState(false);
  const { reaction, likeCount, dislikeCount, pending: reactionPending, react, reset: resetReaction } = useReaction({
    initialReaction: null,
    initialLikeCount: 0,
    initialDislikeCount: 0,
    onSet: type => setPostReaction(postId, type),
    onCancel: () => deletePostReaction(postId),
    isAuthorized: !!user,
    onUnauthorized: () => window.dispatchEvent(new CustomEvent('auth:unauthorized')),
  });

  const [isEditingPost, setIsEditingPost] = useState(false);
  const [editTitle, setEditTitle] = useState('');
  const [editContent, setEditContent] = useState('');
  const [editSaving, setEditSaving] = useState(false);

  const [isConfirmingDelete, setIsConfirmingDelete] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [isReporting, setIsReporting] = useState(false);

  const [comments, setComments] = useState<CommentData[]>([]);
  const [commentPage, setCommentPage] = useState(0);
  const [commentHasMore, setCommentHasMore] = useState(true);
  const [commentLoading, setCommentLoading] = useState(false);

  const [hotPosts, setHotPosts] = useState<PostSummary[]>([]);
  const [channelPosts, setChannelPosts] = useState<PostSummary[]>([]);

  const colorMap = useMemo(
    () => Object.fromEntries(channels.map(c => [c.id, c.color])),
    [channels],
  );

  useEffect(() => {
    setPostLoading(true);
    setPostError(null);
    fetchPostDetail(postId)
      .then(p => {
        setPost(p);
        resetReaction({ reaction: p.reactionType, likeCount: p.likeCount, dislikeCount: p.dislikeCount });
        setBookmarked(p.bookmarked ?? false);
      })
      .catch(e => setPostError(e.message))
      .finally(() => setPostLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId]);

  const loadMoreComments = useCallback(async (nextPage: number) => {
    if (commentLoading) return;
    setCommentLoading(true);
    try {
      const res = await fetchComments(postId, { page: nextPage, size: COMMENT_PAGE_SIZE });
      setComments(prev => nextPage === 0 ? res.data : [...prev, ...res.data]);
      setCommentHasMore(nextPage + 1 < res.meta.totalPages);
      setCommentPage(nextPage + 1);
    } catch {
      // ignore
    } finally {
      setCommentLoading(false);
    }
  }, [postId, commentLoading]);

  useEffect(() => {
    loadMoreComments(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId]);

  const sentinelRef = useRef<HTMLDivElement>(null);
  const observerRef = useRef<IntersectionObserver | null>(null);

  useEffect(() => {
    if (!sentinelRef.current) return;
    observerRef.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && commentHasMore && !commentLoading) {
        loadMoreComments(commentPage);
      }
    }, { threshold: 0.1 });
    observerRef.current.observe(sentinelRef.current);
    return () => observerRef.current?.disconnect();
  }, [commentHasMore, commentLoading, commentPage, loadMoreComments]);

  useEffect(() => {
    if (!post) return;
    fetchHotPosts({ size: 5 })
      .then(res => setHotPosts(res.data))
      .catch(() => {});
    fetchPosts({ channelId: post.channelId, sort: 'likes', size: 6 })
      .then(res => setChannelPosts(res.data.filter(p => p.id !== post.id).slice(0, 5)))
      .catch(() => {});
  }, [post]);

  const handleAddComment = async (text: string) => {
    try {
      const newComment = await createComment(postId, text);
      setComments(prev => [...prev, newComment]);
    } catch {
      // ignore
    }
  };

  const handleStartEditPost = () => {
    if (!post) return;
    setEditTitle(post.title);
    setEditContent(post.content);
    setIsEditingPost(true);
  };

  const handleCancelEditPost = () => {
    setIsEditingPost(false);
  };

  const handleSavePost = async () => {
    if (!post || editSaving) return;
    setEditSaving(true);
    try {
      await updatePost(post.id, { title: editTitle, content: editContent });
      setPost(p => p ? { ...p, title: editTitle, content: editContent } : p);
      setIsEditingPost(false);
    } finally {
      setEditSaving(false);
    }
  };

  const handleDeletePost = async () => {
    if (!post || deleteLoading) return;
    setDeleteLoading(true);
    try {
      await deletePost(post.id);
      if (window.history.length > 1) {
        navigate(-1);
      } else {
        navigate('/all', { replace: true });
      }
    } finally {
      setDeleteLoading(false);
    }
  };

  const totalComments = comments.reduce((a, c) => a + 1 + c.replyCount, 0);

  if (postLoading) {
    return (
      <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--t3)', fontSize: 14 }}>
        <div className="spin" style={{ margin: '0 auto 12px' }} />
        불러오는 중...
      </div>
    );
  }

  if (postError || !post) {
    return (
      <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--t3)', fontSize: 14 }}>
        {postError ?? '존재하지 않는 게시글입니다.'}
        <br />
        <button
          style={{ marginTop: 12, fontSize: 13, color: 'var(--primary)', background: 'none', border: 'none', cursor: 'pointer' }}
          onClick={() => navigate('/')}
        >
          홈으로 돌아가기
        </button>
      </div>
    );
  }

  const channelColor = colorMap[post.channelId] ?? '#888';
  const isPostAuthor = user?.id === post.authorId;

  return (
    <div className="dtlyt">
      <div className="dtmn">
        <div className="bc">
          <span className="bcl" onClick={() => navigate('/')}>홈</span>
          <span className="bcs">/</span>
          <span className="bcl">{post.channelName}</span>
          <span className="bcs">/</span>
          <span style={{ color: 'var(--t2)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: 340 }}>
            {post.title}
          </span>
        </div>

        <div className="dtc">
          <div className="dthd">
            <div className="dtbg">
              <ChTag channel={post.channelName} channelColor={channelColor} />
              {post.isPinned && <span className="bpin">고정</span>}
            </div>
            {isEditingPost
              ? <input
                  className="edt-title"
                  value={editTitle}
                  onChange={e => setEditTitle(e.target.value)}
                />
              : <div className="dttl">{post.title}</div>
            }
            <div className="dtar">
              <div className="dta">
                <div className="aav">{post.authorName.slice(0, 1)}</div>
                <div>
                  <div className="anm">{post.authorName}</div>
                  <div className="asb">{formatRelativeTime(post.createdAt)} · 조회 {post.viewCount.toLocaleString()}</div>
                </div>
              </div>
              {isPostAuthor && !isEditingPost && (
                <div className="rac">
                  {isConfirmingDelete
                    ? <>
                        <span style={{ fontSize: 12, color: 'var(--t2)' }}>정말 삭제할까요?</span>
                        <button className="ac dng" onClick={handleDeletePost} disabled={deleteLoading}>
                          {deleteLoading ? '삭제 중...' : '삭제'}
                        </button>
                        <button className="ac" onClick={() => setIsConfirmingDelete(false)}>취소</button>
                      </>
                    : <>
                        <button className="ac" onClick={handleStartEditPost}>수정</button>
                        <button className="ac dng" onClick={() => setIsConfirmingDelete(true)}>삭제</button>
                      </>
                  }
                </div>
              )}
            </div>
          </div>

          {isEditingPost
            ? (
              <div className="dtbd">
                <textarea
                  className="edt-content"
                  value={editContent}
                  onChange={e => setEditContent(e.target.value)}
                  rows={12}
                />
                <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', paddingTop: 8 }}>
                  <button className="ac" onClick={handleCancelEditPost}>취소</button>
                  <button className="ac" style={{ color: 'var(--primary)' }} onClick={handleSavePost} disabled={editSaving}>
                    {editSaving ? '저장 중...' : '저장'}
                  </button>
                </div>
              </div>
            )
            : (
              <div className="dtbd">
                {post.content.split('\n').map((line, i) => <p key={i}>{line}</p>)}
              </div>
            )
          }

          <div className="dtac">
            <div className="vg">
              <button
                className={'vbtn2' + (reaction === 'LIKE' ? ' lk' : '')}
                onClick={() => react('LIKE')}
                disabled={reactionPending}
              >
                ♥ 좋아요 {likeCount.toLocaleString()}
              </button>
              <button
                className={'vbtn2' + (reaction === 'DISLIKE' ? ' dk' : '')}
                onClick={() => react('DISLIKE')}
                disabled={reactionPending}
              >
                👎 싫어요 {dislikeCount.toLocaleString()}
              </button>
              <button className={'vbtn2' + (bookmarked ? ' bk' : '')} onClick={() => { toggleBookmark(postId, !bookmarked).then(ok => { if (ok) setBookmarked(v => !v); }).catch(() => {}); }}>
                {bookmarked ? '★' : '☆'} 북마크
              </button>
            </div>
            <div className="rac">
              <button className="ac">공유</button>
              {user && !isPostAuthor && (
                <button className="ac dng" onClick={() => setIsReporting(true)}>
                  신고
                </button>
              )}
            </div>
          </div>
        </div>

        <div className="cmtc">
          <div className="cmthd">
            <span style={{ fontSize: 14, fontWeight: 500, color: 'var(--t1)' }}>댓글</span>
            <span style={{ fontSize: 12, color: 'var(--t3)' }}>{totalComments}개</span>
          </div>
          {comments.map(c => <CommentItem key={c.id} comment={c} postId={postId} />)}
          {commentLoading && <div className="lrow"><div className="spin" /><span>불러오는 중...</span></div>}
          {commentHasMore && !commentLoading && <div ref={sentinelRef} style={{ height: 1 }} />}
          <CommentInput
            onSubmit={handleAddComment}
            onFocus={user ? undefined : () => window.dispatchEvent(new CustomEvent('auth:unauthorized'))}
          />
        </div>
      </div>

      <div className="dtsb">
        <div className="card">
          <div className="chd"><span className="ct">{post.channelName} 채널 인기글</span></div>
          <div className="sbb" style={{ padding: '6px 14px 10px' }}>
            {channelPosts.length === 0
              ? <div style={{ fontSize: 12, color: 'var(--t3)', padding: '8px 0' }}>추천 글이 없습니다.</div>
              : channelPosts.map(r => (
                <div key={r.id} className="rci" onClick={() => navigate(`/posts/${r.id}`)}>
                  <div className="rct">{r.title}</div>
                  <div className="rcm"><span>♥ {r.likeCount}</span><span>댓글 {r.commentCount}</span></div>
                </div>
              ))
            }
          </div>
        </div>

        <div className="card">
          <div className="chd"><span className="ct">지금 인기글</span><span className="bhot">HOT</span></div>
          <div className="sbb" style={{ padding: '6px 14px 10px' }}>
            {hotPosts.map((r, i) => (
              <div key={r.id} className="rci" onClick={() => navigate(`/posts/${r.id}`)}>
                <div style={{ display: 'flex', gap: 6, alignItems: 'flex-start' }}>
                  <span style={{ fontSize: 12, fontWeight: 700, color: rankColor(i + 1), flexShrink: 0, width: 16 }}>{i + 1}</span>
                  <div className="rct" style={{ flex: 1 }}>{r.title}</div>
                </div>
                <div className="rcm" style={{ paddingLeft: 22 }}>
                  <span>♥ {r.likeCount}</span><span>댓글 {r.commentCount}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <button
          className="mbtn"
          style={{ background: 'var(--bg)', border: '.5px solid var(--b)', borderRadius: 12 }}
          onClick={() => navigate(-1)}
        >
          ← 목록으로
        </button>
      </div>

      {isReporting && (
        <ReportModal targetType="POST" targetId={post.id} onClose={() => setIsReporting(false)} />
      )}
    </div>
  );
}
