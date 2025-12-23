import React from 'react';
import ReactMarkdown from 'react-markdown';
import rehypeRaw from 'rehype-raw';

const MarkdownResponse = ({ content }) => {
  const safeContent =
    typeof content === 'string'
      ? content
      : content?.text || content?.message || JSON.stringify(content);

  return (
    <div className="markdown-response">
      <ReactMarkdown rehypePlugins={[rehypeRaw]}>{safeContent}</ReactMarkdown>
    </div>
  );
};

export default React.memo(MarkdownResponse);