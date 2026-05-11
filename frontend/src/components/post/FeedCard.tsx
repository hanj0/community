import type { FeedPost } from '../../types';
import ChTag from '../common/ChTag';

interface FeedCardProps {
  post: FeedPost;
  onClick: () => void;
}

export default function FeedCard({ post, onClick }: FeedCardProps) {
  return (
    <div className={'fpc' + (post.isNotice ? ' ntc' : '')} onClick={onClick}>
      <div className="fpb">
        <div className="pbadges" style={{ marginBottom: 4 }}>
          {post.isNotice
            ? <span className="nlbl">공지</span>
            : <ChTag channel={post.channel} channelColor={post.channelColor} />
          }
        </div>
        <div className="fpt">{post.title}</div>
        <div className="fpm">
          <span className="fs">{post.author}</span>
          <span className="fs">·</span>
          <span className="fs">{post.time}</span>
          <span className="fs lk">♥ {post.likes}</span>
          <span className="fs">댓글 {post.comments}</span>
          <span className="fs">조회 {post.views.toLocaleString()}</span>
        </div>
      </div>
      {post.hasImg && <div className="ith" style={{ width: 52, height: 42, fontSize: 9 }}>IMG</div>}
    </div>
  );
}
