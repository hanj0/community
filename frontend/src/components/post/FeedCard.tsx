import type { PostSummary } from '../../types';
import { formatRelativeTime } from '../../utils/time';
import ChTag from '../common/ChTag';

interface FeedCardProps {
  post: PostSummary;
  channelColor: string;
  onClick: () => void;
}

export default function FeedCard({ post, channelColor, onClick }: FeedCardProps) {
  return (
    <div className={'fpc' + (post.isNotice ? ' ntc' : '')} onClick={onClick}>
      <div className="fpb">
        <div className="pbadges" style={{ marginBottom: 4 }}>
          {post.isNotice
            ? <span className="nlbl">공지</span>
            : <ChTag channel={post.channelName} channelColor={channelColor} />
          }
        </div>
        <div className="fpt">{post.title}</div>
        <div className="fpm">
          <span className="fs">{post.authorInfo?.username}</span>
          <span className="fs">·</span>
          <span className="fs">{formatRelativeTime(post.createdAt)}</span>
          <span className="fs lk">♥ {post.likeCount}</span>
          <span className="fs">댓글 {post.commentCount}</span>
          <span className="fs">조회 {post.viewCount.toLocaleString()}</span>
        </div>
      </div>
      {post.hasImage && <div className="ith" style={{ width: 52, height: 42, fontSize: 9 }}>IMG</div>}
    </div>
  );
}
