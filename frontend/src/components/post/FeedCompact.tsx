import type { PostSummary } from '../../types';
import { formatRelativeTime } from '../../utils/time';
import ChTag from '../common/ChTag';

interface FeedCompactProps {
  post: PostSummary;
  channelColor: string;
  onClick: () => void;
}

export default function FeedCompact({ post, channelColor, onClick }: FeedCompactProps) {
  return (
    <div className="fcc" onClick={onClick}>
      {post.isNotice
        ? <span className="nlbl">공지</span>
        : <ChTag channel={post.channelName} channelColor={channelColor} />
      }
      <span className="fct">{post.title}</span>
      {post.hasImage && <span style={{ fontSize: 10, color: 'var(--t3)' }}>IMG</span>}
      <div className="fcm">
        <span style={{ fontSize: 11, color: 'var(--t3)' }}>{post.author}</span>
        <span style={{ fontSize: 11, color: 'var(--red, #E24B4A)' }}>♥ {post.likes}</span>
        <span style={{ fontSize: 11, color: 'var(--t3)' }}>댓글 {post.commentCount}</span>
        <span style={{ fontSize: 11, color: 'var(--t3)' }}>{formatRelativeTime(post.createdAt)}</span>
      </div>
    </div>
  );
}
