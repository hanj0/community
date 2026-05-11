import type { Post } from '../../types';
import { rankColor } from '../../utils/rank';
import ChTag from '../common/ChTag';

interface HotPostCardProps {
  post: Post;
  rank: number;
  onClick: () => void;
}

export default function HotPostCard({ post, rank, onClick }: HotPostCardProps) {
  const color = rankColor(rank);
  return (
    <div className="hp" onClick={onClick}>
      <div className="hrk" style={{ color }}>{rank}</div>
      <div className="hb">
        <div className="pbadges" style={{ marginBottom: 4 }}>
          <ChTag channel={post.channel} channelColor={post.channelColor} />
          {post.isPin && <span className="bpin">고정</span>}
        </div>
        <div className="hpt">{post.title}</div>
        <div className="hpm">
          <span className="mi">{post.author}</span>
          <span className="mi">{post.time}</span>
          <span className="mi">♥ {post.likes.toLocaleString()}</span>
          <span className="mi">댓글 {post.comments.toLocaleString()}</span>
          <span className="mi">조회 {post.views.toLocaleString()}</span>
        </div>
      </div>
      {post.hasImg && <div className="ith" style={{ width: 50, height: 40 }}>IMG</div>}
    </div>
  );
}
