import type { FeedPost } from '../../types';
import { COLORS } from '../../constants/data';
import ChTag from '../common/ChTag';

interface FeedCompactProps {
  post: FeedPost;
  onClick: () => void;
}

export default function FeedCompact({ post, onClick }: FeedCompactProps) {
  return (
    <div className="fcc" onClick={onClick}>
      {post.isNotice
        ? <span className="nlbl">공지</span>
        : <ChTag channel={post.channel} channelColor={post.channelColor} />
      }
      <span className="fct">{post.title}</span>
      {post.hasImg && <span style={{ fontSize: 10, color: 'var(--t3)' }}>IMG</span>}
      <div className="fcm">
        <span style={{ fontSize: 11, color: 'var(--t3)' }}>{post.author}</span>
        <span style={{ fontSize: 11, color: COLORS.red }}>♥ {post.likes}</span>
        <span style={{ fontSize: 11, color: 'var(--t3)' }}>댓글 {post.comments}</span>
        <span style={{ fontSize: 11, color: 'var(--t3)' }}>{post.time}</span>
      </div>
    </div>
  );
}
