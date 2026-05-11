import { useState } from 'react';
import type { AnyPost, Comment } from '../types';
import { ALL_POSTS, FEED_POSTS, SAMPLE_COMMENTS, POST_BODY } from '../constants/data';
import { rankColor } from '../utils/rank';
import ChTag from '../components/common/ChTag';
import CommentItem from '../components/comment/CommentItem';
import CommentInput from '../components/comment/CommentInput';

interface PostDetailPageProps {
  post: AnyPost;
  onBack: () => void;
  onPostClick: (post: AnyPost) => void;
}

export default function PostDetailPage({ post, onBack, onPostClick }: PostDetailPageProps) {
  const [liked, setLiked] = useState(false);
  const [bookmarked, setBookmarked] = useState(false);
  const [likeCount, setLikeCount] = useState(post.likes);
  const [comments, setComments] = useState<Comment[]>(SAMPLE_COMMENTS);

  const allPool = [...ALL_POSTS, ...FEED_POSTS.filter(p => !p.isNotice)];
  const sameCh = allPool.filter(p => p.id !== post.id && p.channel === post.channel).slice(0, 5);
  const byAuthor = ALL_POSTS.filter(p => p.author === post.author && p.id !== post.id).slice(0, 3);

  const idx = ALL_POSTS.findIndex(p => p.id === post.id);
  const prevPost = idx > 0 ? ALL_POSTS[idx - 1] : null;
  const nextPost = idx < ALL_POSTS.length - 1 ? ALL_POSTS[idx + 1] : null;

  const isPin = 'isPin' in post ? post.isPin : false;

  const toggleLike = () => {
    setLiked(v => !v);
    setLikeCount(n => liked ? n - 1 : n + 1);
  };

  const handleAddComment = (text: string) => {
    setComments(prev => [...prev, {
      id: Date.now(),
      author: '김개발',
      av: '김',
      ab: '#B5D4F4',
      ac: '#0C447C',
      time: '방금',
      text,
      likes: 0,
      dislikes: 0,
      replies: [],
    }]);
  };

  return (
    <div className="dtlyt">
      <div className="dtmn">
        <div className="bc">
          <span className="bcl" onClick={onBack}>홈</span>
          <span className="bcs">/</span>
          <span className="bcl">{post.channel}</span>
          <span className="bcs">/</span>
          <span style={{ color: 'var(--t2)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: 340 }}>
            {post.title}
          </span>
        </div>

        <div className="dtc">
          <div className="dthd">
            <div className="dtbg">
              <ChTag channel={post.channel} channelColor={post.channelColor} />
              {isPin && <span className="bpin">고정</span>}
            </div>
            <div className="dttl">{post.title}</div>
            <div className="dtar">
              <div className="dta">
                <div className="aav">{post.author.slice(0, 1)}</div>
                <div>
                  <div className="anm">{post.author}</div>
                  <div className="asb">{post.time} · 조회 {post.views.toLocaleString()}</div>
                </div>
              </div>
            </div>
          </div>

          <div className="dtbd">
            {post.hasImg && <div className="dtimg">첨부 이미지</div>}
            {POST_BODY.map((p, i) => <p key={i}>{p}</p>)}
          </div>

          <div className="dtac">
            <div className="vg">
              <button className={'vbtn2' + (liked ? ' lk' : '')} onClick={toggleLike}>
                ♥ 좋아요 {likeCount.toLocaleString()}
              </button>
              <button className={'vbtn2' + (bookmarked ? ' bk' : '')} onClick={() => setBookmarked(v => !v)}>
                {bookmarked ? '★' : '☆'} 북마크
              </button>
            </div>
            <div className="rac">
              <button className="ac">공유</button>
              <button className="ac dng">신고</button>
            </div>
          </div>
        </div>

        <div className="dtc pnwrap" style={{ display: 'flex' }}>
          <div className="pni" style={{ opacity: prevPost ? 1 : 0.4 }} onClick={() => prevPost && onPostClick(prevPost)}>
            <div className="pnl">← 이전 글</div>
            <div className="pnt">{prevPost ? prevPost.title : '이전 글 없음'}</div>
          </div>
          <div className="pni" style={{ textAlign: 'right' }} onClick={() => nextPost && onPostClick(nextPost)}>
            <div className="pnl">다음 글 →</div>
            <div className="pnt">{nextPost ? nextPost.title : '다음 글 없음'}</div>
          </div>
        </div>

        <div className="cmtc">
          <div className="cmthd">
            <span style={{ fontSize: 14, fontWeight: 500, color: 'var(--t1)' }}>댓글</span>
            <span style={{ fontSize: 12, color: 'var(--t3)' }}>
              {comments.length + comments.reduce((a, c) => a + (c.replies?.length ?? 0), 0)}개
            </span>
          </div>
          {comments.map(c => <CommentItem key={c.id} comment={c} />)}
          <CommentInput onSubmit={handleAddComment} />
        </div>
      </div>

      <div className="dtsb">
        <div className="card">
          <div className="chd"><span className="ct">{post.channel} 채널 인기글</span></div>
          <div className="sbb" style={{ padding: '6px 14px 10px' }}>
            {sameCh.length === 0
              ? <div style={{ fontSize: 12, color: 'var(--t3)', padding: '8px 0' }}>추천 글이 없습니다.</div>
              : sameCh.map(r => (
                <div key={r.id} className="rci" onClick={() => onPostClick(r)}>
                  <div className="rct">{r.title}</div>
                  <div className="rcm"><span>♥ {r.likes}</span><span>댓글 {r.comments}</span></div>
                </div>
              ))
            }
          </div>
        </div>

        <div className="card">
          <div className="chd"><span className="ct">{post.author}의 다른 글</span></div>
          <div className="sbb" style={{ padding: '6px 14px 10px' }}>
            {byAuthor.length === 0
              ? <div style={{ fontSize: 12, color: 'var(--t3)', padding: '8px 0' }}>다른 글이 없습니다.</div>
              : byAuthor.map(r => (
                <div key={r.id} className="rci" onClick={() => onPostClick(r)}>
                  <div className="rct">{r.title}</div>
                  <div className="rcm"><span>{r.time}</span><span>♥ {r.likes}</span></div>
                </div>
              ))
            }
          </div>
        </div>

        <div className="card">
          <div className="chd"><span className="ct">지금 인기글</span><span className="bhot">HOT</span></div>
          <div className="sbb" style={{ padding: '6px 14px 10px' }}>
            {ALL_POSTS.slice(0, 5).map((r, i) => (
              <div key={r.id} className="rci" onClick={() => onPostClick(r)}>
                <div style={{ display: 'flex', gap: 6, alignItems: 'flex-start' }}>
                  <span style={{ fontSize: 12, fontWeight: 700, color: rankColor(i + 1), flexShrink: 0, width: 16 }}>{i + 1}</span>
                  <div className="rct" style={{ flex: 1 }}>{r.title}</div>
                </div>
                <div className="rcm" style={{ paddingLeft: 22 }}>
                  <span>♥ {r.likes}</span><span>댓글 {r.comments}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <button className="mbtn" style={{ background: 'var(--bg)', border: '.5px solid var(--b)', borderRadius: 12 }} onClick={onBack}>
          ← 목록으로
        </button>
      </div>
    </div>
  );
}
