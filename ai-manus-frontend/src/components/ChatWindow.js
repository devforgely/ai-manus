import { useState, useEffect, useRef } from 'react';
import { ArrowLeft, Send, Sparkles, BrainCircuit } from 'lucide-react';
import ChatMessageBubble from './ChatMessageBubble';
import { generateChatId } from '../utils/chatIdGenerator';
import API_BASE_URL from '../config/config';


const ChatWindow = ({ title, type, onBack }) => {
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const [isStreaming, setIsStreaming] = useState(false);
  const [chatId, setChatId] = useState('');
  const messagesEndRef = useRef(null);
  const eventSourceRef = useRef(null);
  const lastChunkRef = useRef(null);
  const DONE_TOKENS = ["[Done]", "[Terminate]"];

  // Initialize Chat ID on mount if it's the Focus App
  useEffect(() => {
    if (type === 'focus') {
      const newChatId = generateChatId();
      console.log(`Initialized Focus App with Chat ID: ${newChatId}`);
      setChatId(newChatId);
    }
  }, [type]);

  // Auto-scroll to bottom when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Cleanup EventSource on unmount
  useEffect(() => {
    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  }, []);

  const handleSend = async () => {
    if (!inputValue.trim() || isStreaming) return;

    const userMessage = inputValue.trim();
    setInputValue('');

    // 1. Add User Message
    const newMessages = [...messages, { role: 'user', content: userMessage }];
    setMessages(newMessages);
    setIsStreaming(true);

    // 2. Prepare for AI Response (Placeholder)
    setMessages(prev => [...prev, { role: 'ai', content: '' }]);

    // 3. Construct URL based on App Type
    let url = '';
    const encodedMsg = encodeURIComponent(userMessage);

    if (type === 'focus') {
      // Maps to: @GetMapping(value = "/focus_app/chat/sse")
      url = `${API_BASE_URL}/ai/focus_app/chat/sse?message=${encodedMsg}&chatId=${chatId}`;
    } else if (type === 'manus') {
      // Maps to: @GetMapping("/manus/chat")
      url = `${API_BASE_URL}/ai/manus/chat?message=${encodedMsg}`;
    }

    try {
      // 4. Initialize SSE
      console.log(`Connecting to SSE: ${url}`);
      
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }

      const es = new EventSource(url);
      eventSourceRef.current = es;

      es.onmessage = (event) => {
        let data = event.data;
        // Check for specific tokens or just append data
        // Assuming the backend sends raw text chunks in the 'data' field

        let isDone = false;
        for (const token of DONE_TOKENS) {
          if (data.startsWith(token)) {
            isDone = true;
            data = data.slice(token.length); // remove the token from the front
            break;
          }
        }

        // If there is still content after the done token, append it
        if (data) {
          setMessages(prev => {
            if (lastChunkRef.current === data) {
              return prev; // Skip duplicate chunk
            }
            lastChunkRef.current = data;

            const updated = [...prev];
            const lastMsg = updated[updated.length - 1];

            if (lastMsg && lastMsg.role === "ai") {
              const prevContent = lastMsg.content || "";
              let nextChunk = data;

              // If prev ends with a letter/number and next starts with a letter/number,
              // but there is no space, insert one.
              if (
                prevContent &&
                /[A-Za-z0-9]$/.test(prevContent) &&
                /^[A-Za-z0-9]/.test(nextChunk)
              ) {
                nextChunk = " " + nextChunk;
              }

              lastMsg.content = prevContent + nextChunk;
            } else {
              updated.push({ role: "ai", content: data });
            }

            return updated;
          });
        }

        // If it was a done/terminate chunk, now close the stream
        if (isDone) {
          es.close();
          setIsStreaming(false);
          eventSourceRef.current = null;
        }
      };

      es.onerror = (err) => {
        console.error("EventSource failed:", err);
        // Usually, closing on error is safe for a simple request-response chat stream 
        // unless the backend keeps the connection open indefinitely.
        es.close();
        setIsStreaming(false);
        eventSourceRef.current = null;
      };

      // Optional: If you have a specific 'done' event from backend, listen for it
      // es.addEventListener('complete', () => { ... })

    } catch (error) {
      console.error("Error initiating chat:", error);
      setIsStreaming(false);
      setMessages(prev => [...prev, { role: 'ai', content: "Error: Could not connect to the AI server." }]);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="flex flex-col h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b px-4 py-3 flex items-center shadow-sm z-10">
        <button 
          onClick={onBack} 
          className="p-2 mr-2 hover:bg-gray-100 rounded-full transition-colors text-gray-600"
        >
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="font-bold text-lg text-gray-800 flex items-center gap-2">
            {type === 'focus' ? <BrainCircuit className="text-blue-600" /> : <Sparkles className="text-purple-600" />}
            {title}
          </h1>
          {type === 'focus' && (
            <p className="text-xs text-gray-400 font-mono">ID: {chatId}</p>
          )}
        </div>
      </header>

      {/* Chat Area */}
      <div className="flex-1 overflow-y-auto p-4 md:p-6 space-y-4">
        {messages.length === 0 ? (
          <div className="h-full flex flex-col items-center justify-center text-gray-400 opacity-60">
            {type === 'focus' ? <BrainCircuit size={64} /> : <Sparkles size={64} />}
            <p className="mt-4 text-sm font-medium">Start a conversation with {title}</p>
          </div>
        ) : (
          messages.map((msg, index) => (
            <ChatMessageBubble key={index} role={msg.role} content={msg.content} />
          ))
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="bg-white border-t p-4">
        <div className="max-w-4xl mx-auto relative flex items-end gap-2 bg-gray-100 rounded-2xl p-2 border border-transparent focus-within:bg-white focus-within:border-blue-300 focus-within:ring-2 focus-within:ring-blue-100 transition-all">
          <textarea
            className="flex-1 bg-transparent border-none focus:outline-none focus:ring-0 resize-none max-h-32 min-h-[44px] py-3 px-2 text-gray-700 placeholder-gray-400"
            placeholder="Type your message here..."
            rows={1}
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
            disabled={isStreaming}
          />
          <button 
            onClick={handleSend}
            disabled={!inputValue.trim() || isStreaming}
            className={`p-3 rounded-xl flex-shrink-0 transition-all ${
              !inputValue.trim() || isStreaming
                ? 'bg-gray-200 text-gray-400 cursor-not-allowed' 
                : 'bg-blue-600 text-white hover:bg-blue-700 shadow-md'
            }`}
          >
            {isStreaming ? (
              <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <Send size={20} />
            )}
          </button>
        </div>
        <p className="text-center text-xs text-gray-400 mt-2">
          AI generated content may be inaccurate.
        </p>
      </div>
    </div>
  );
};

export default ChatWindow;