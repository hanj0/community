import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import type { PostSummary } from '../types';
import { fetchHotPosts, fetchPosts } from '../api/posts';
import { useChannels } from '../hooks/useChannels';
import { NOTICES_TEXT } from '../constants/data';
import { formatRelativeTime } from '../utils/time';
import PostRankItem from '../components/post/PostRankItem';
import ChTag from '../components/common/ChTag';
import Sidebar from '../components/layout/Sidebar';

export default function HomePage() {
  const navigate = useNavigate();
  const channels = useChannels();
  const [period, setPeriod] = useState<'24h' | '7d' | '30d'>('24h');
  const [hotPosts, setHotPosts] = useState<PostSummary[]>([]);
  const [recentPosts, setRecentPosts] = useState<PostSummary[]>([]);

  const colorMap = useMemo(
    () => Object.fromEntries(channels.map(c => [c.id, c.color])),
    [channels],
  );

  useEffect(() => {
    fetchHotPosts({ period, size: 5 })
      .then(res => setHotPosts(res.data))
      .catch(() => {});
  }, [period]);

  useEffect(() => {
    fetchPosts({ sort: 'latest', size: 5 })
      .then(res => setRecentPosts(res.data.filter(p => !p.isNotice)))
      .catch(() => {});
  }, []);

  return (
    <div className="layout">
      <div className="main">
        <div className="card">
          <div className="chd">
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span className="ct">인기글</span>
              <span className="bhot">HOT</span>
            </div>
            <div className="ptabs">
              {([['24h', '24시간'], ['7d', '7일'], ['30d', '30일']] as const).map(([v, l]) => (
                <button
                  key={v}
                  className={'ptab' + (period === v ? ' active' : '')}
                  onClick={() => setPeriod(v)}
                >
                  {l}
                </button>
              ))}
            </div>
          </div>
          {hotPosts.length === 0
            ? <div className="empty" style={{ padding: '20px 0', fontSize: 13 }}>인기글이 없습니다.</div>
            : hotPosts.map(p => (
              <PostRankItem
                key={p.id}
                post={p}
                channelColor={colorMap[p.channelId] ?? '#888'}
                onClick={() => navigate(`/posts/${p.id}`)}
              />
            ))
          }
          <button className="mbtn" onClick={() => navigate('/hot')}>인기글 더보기 →</button>
        </div>

        <div className="card">
          <div className="chd"><span className="ct">공지사항</span><span className="bpin">고정</span></div>
          {NOTICES_TEXT.map((n, i) => (
            <div className="nrow" key={i}>
              <span className="npin">고정</span>
              <span>{n}</span>
            </div>
          ))}
        </div>

        <div className="card">
          <div className="chd"><span className="ct">최신글</span><span className="bnew">NEW</span></div>
          {recentPosts.length === 0
            ? <div className="empty" style={{ padding: '20px 0', fontSize: 13 }}>최신글이 없습니다.</div>
            : recentPosts.map(p => (
              <div className="reitem" key={p.id} onClick={() => navigate(`/posts/${p.id}`)}>
                <div className="retop">
                  <ChTag channel={p.channelName} channelColor={colorMap[p.channelId] ?? '#888'} />
                  <span className="retit">{p.title}</span>
                  {p.hasImage && <div className="ith" style={{ width: 34, height: 26, fontSize: 8 }}>IMG</div>}
                </div>
                <div className="pmeta">
                  <span className="mi">{p.author}</span>
                  <span className="mi">·</span>
                  <span className="mi">{formatRelativeTime(p.createdAt)}</span>
                  <span className="mi">♥ {p.likeCount}</span>
                  <span className="mi">댓글 {p.commentCount}</span>
                </div>
              </div>
            ))
          }
          <button className="mbtn" onClick={() => navigate('/all')}>전체 최신글 보기 →</button>
        </div>
      </div>
      <Sidebar />
    </div>
  );
}
