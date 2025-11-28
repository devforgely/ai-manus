/**
 * Utility: Generate a simple UUID for Chat ID
 */
const generateChatId = () => {
  return 'chat-' + Math.random().toString(36).substr(2, 9) + '-' + Date.now();
};

export { generateChatId };