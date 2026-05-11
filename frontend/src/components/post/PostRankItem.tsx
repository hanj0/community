import type { Post } from '../../types';
import ChTag from '../common/ChTag';

interface PostRankItemProps {
  post: Post;
  onClick: () => void;
}

export default function PostRankItem({ post, onClick }: PostRankItemProps) {
  return (
    <div className="pi" onClick={onClick}>
      <div className="pr">
        <div className="pb">
          <div className="pbadges">
            <ChTag channel={post.channel} channelColor={post.channelColor} />
            {post.isPin && <span className="bpin">고정</span>}
          </div>
          <div className="ptit">{post.title}</div>
          <div className="pmeta">
            <span className="mi">♥ {post.likes.toLocaleString()}</span>
            <span className="mi">댓글 {post.comments.toLocaleString()}</span>
            <span className="mi">조회 {post.views.toLocaleString()}</span>
          </div>
        </div>
        {post.hasImg && <div className="ith">IMG</div>}
      </div>
    </div>
  );
}
