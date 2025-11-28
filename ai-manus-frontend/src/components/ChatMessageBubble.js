import { Bot } from 'lucide-react';


const ChatMessageBubble = ({ role, content }) => {
  const isUser = role === 'user';
  return (
    <div className={`flex w-full ${isUser ? 'justify-end' : 'justify-start'} mb-4`}>
      <div className={`flex max-w-[80%] md:max-w-[70%] ${isUser ? 'flex-row-reverse' : 'flex-row'} items-start gap-3`}>
        {/* Avatar */}
        <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${isUser ? 'bg-blue-600' : 'bg-emerald-600'}`}>
          {isUser ? <div className="text-white text-xs font-bold">ME</div> : <Bot className="text-white w-5 h-5" />}
        </div>

        {/* Message Content */}
        <div 
          className={`p-3 rounded-2xl shadow-sm whitespace-pre-wrap leading-relaxed ${
            isUser 
              ? 'bg-blue-600 text-white rounded-tr-none' 
              : 'bg-white text-gray-800 border border-gray-100 rounded-tl-none'
          }`}
        >
          {content}
        </div>
      </div>
    </div>
  );
};

export default ChatMessageBubble;