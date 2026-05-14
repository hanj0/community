import type { PostSummary } from '../../types';
import { formatRelativeTime } from '../../utils/time';
import { rankColor } from '../../utils/rank';
import ChTag from '../common/ChTag';

interface HotPostCardProps {
  post: PostSummary;
  channelColor: string;
  rank: number;
  onClick: () => void;
}

export default function HotPostCard({ post, channelColor, rank, onClick }: HotPostCardProps) {
  const color = rankColor(rank);
  return (
    <div className="hp" onClick={onClick}>
      <div className="hrk" style={{ color }}>{rank}</div>
      <div className="hb">
        <div className="pbadges" style={{ marginBottom: 4 }}>
          <ChTag channel={post.channelName} channelColor={channelColor} />
          {post.isPinned && <span className="bpin">고정</span>}
        </div>
        <div className="hpt">{post.title}</div>
        <div className="hpm">
          <span className="mi">{post.author}</span>
          <span className="mi">{formatRelativeTime(post.createdAt)}</span>
          <span className="mi">♥ {post.likes.toLocaleString()}</span>
          <span className="mi">댓글 {post.commentCount.toLocaleString()}</span>
          <span className="mi">조회 {post.views.toLocaleString()}</span>
        </div>
      </div>
      {post.hasImage && <div className="ith" style={{ width: 50, height: 40 }}>IMG</div>}
    </div>
  );
}
