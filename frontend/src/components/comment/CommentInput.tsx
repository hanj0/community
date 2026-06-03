import { useState } from 'react';

interface CommentInputProps {
  placeholder?: string;
  onSubmit: (text: string) => void;
  onFocus?: () => void;
}

const MAX_LENGTH = 500;

export default function CommentInput({ placeholder = '댓글을 입력하세요', onSubmit, onFocus }: CommentInputProps) {
  const [text, setText] = useState('');

  const handleSubmit = () => {
    if (text.trim()) {
      onSubmit(text);
      setText('');
    }
  };

  return (
    <div className="cia">
      <div className="cir">
        <div className="cmav" style={{ background: '#B5D4F4', color: '#0C447C' }}>김</div>
        <div className="cibx">
          <textarea
            className="cita"
            placeholder={placeholder}
            value={text}
            onChange={e => setText(e.target.value.slice(0, MAX_LENGTH))}
            onFocus={onFocus}
            rows={3}
          />
          <div className="cibt">
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <span className="cich">{text.length}/{MAX_LENGTH}</span>
              <button className="cisub" onClick={handleSubmit}>등록</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
