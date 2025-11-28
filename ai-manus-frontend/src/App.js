import React, { useState } from 'react';
import ChatSelector from './components/ChatSelector';
import ChatWindow from './components/ChatWindow';

export default function App() {
  const [currentView, setCurrentView] = useState('chat-selector');

  const renderContent = () => {
    switch (currentView) {
      case 'focus':
        return (
          <ChatWindow 
            title="Focus Master" 
            type="focus" 
            onBack={() => setCurrentView('chat-selector')} 
          />
        );
      case 'manus':
        return (
          <ChatWindow 
            title="Super Agent Manus" 
            type="manus" 
            onBack={() => setCurrentView('chat-selector')} 
          />
        );
      case 'chat-selector':
      default:
        return <ChatSelector onSelectApp={setCurrentView} />;
    }
  };

  return (
    <div className="font-sans antialiased text-gray-900">
      {renderContent()}
    </div>
  );
}
