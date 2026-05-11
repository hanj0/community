import { useState } from 'react';
import type { AnyPost, PageType, User } from '../types';
import { ALL_POSTS, RECENT_POSTS, NOTICES_TEXT } from '../constants/data';
import PostRankItem from '../components/post/PostRankItem';
import ChTag from '../components/common/ChTag';
import Sidebar from '../components/layout/Sidebar';

interface HomePageProps {
  onGoHot: () => void;
  onPostClick: (post: AnyPost) => void;
  user: User | null;
  onNavigate: (page: PageType) => void;
}

export default function HomePage({ onGoHot, onPostClick, user, onNavigate }: HomePageProps) {
  const [period, setPeriod] = useState('24h');
  const top5 = ALL_POSTS.slice(0, 5);

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
                <button key={v} className={'ptab' + (period === v ? ' active' : '')} onClick={() => setPeriod(v)}>
                  {l}
                </button>
              ))}
            </div>
          </div>
          {top5.map((p) => (
            <PostRankItem key={p.id} post={p} onClick={() => onPostClick(p)} />
          ))}
          <button className="mbtn" onClick={onGoHot}>인기글 더보기 →</button>
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
          {RECENT_POSTS.map(p => (
            <div className="reitem" key={p.id} onClick={() => onPostClick(p)}>
              <div className="retop">
                <ChTag channel={p.channel} channelColor={p.channelColor} />
                <span className="retit">{p.title}</span>
                {p.hasImg && <div className="ith" style={{ width: 34, height: 26, fontSize: 8 }}>IMG</div>}
              </div>
              <div className="pmeta">
                <span className="mi">{p.author}</span>
                <span className="mi">·</span>
                <span className="mi">{p.time}</span>
                <span className="mi">♥ {p.likes}</span>
                <span className="mi">댓글 {p.comments}</span>
              </div>
            </div>
          ))}
          <button className="mbtn">전체 최신글 보기 →</button>
        </div>
      </div>
      <Sidebar user={user} onNavigate={onNavigate} />
    </div>
  );
}
