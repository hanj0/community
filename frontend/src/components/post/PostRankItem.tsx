import type { PostSummary } from '../../types';
import ChTag from '../common/ChTag';

interface PostRankItemProps {
  post: PostSummary;
  channelColor: string;
  onClick: () => void;
}

export default function PostRankItem({ post, channelColor, onClick }: PostRankItemProps) {
  return (
    <div className="pi" onClick={onClick}>
      <div className="pr">
        <div className="pb">
          <div className="pbadges">
            <ChTag channel={post.channelName} channelColor={channelColor} />
            {post.isPinned && <span className="bpin">고정</span>}
          </div>
          <div className="ptit">{post.title}</div>
          <div className="pmeta">
            <span className="mi">♥ {post.likes.toLocaleString()}</span>
            <span className="mi">댓글 {post.commentCount.toLocaleString()}</span>
            <span className="mi">조회 {post.views.toLocaleString()}</span>
          </div>
        </div>
        {post.hasImage && <div className="ith">IMG</div>}
      </div>
    </div>
  );
}
