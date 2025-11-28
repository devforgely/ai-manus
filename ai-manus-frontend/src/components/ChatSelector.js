import { ArrowLeft, Sparkles, BrainCircuit } from 'lucide-react';


const ChatSelector = ({ onSelectApp }) => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 flex items-center justify-center p-6">
      <div className="max-w-4xl w-full">
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-4 tracking-tight">
            AI Innovation Hub
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Select an application to start your intelligent conversation journey.
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-8">
          {/* Card 1: Focus App */}
          <div 
            onClick={() => onSelectApp('focus')}
            className="group bg-white rounded-3xl p-8 shadow-xl hover:shadow-2xl hover:-translate-y-1 transition-all cursor-pointer border border-gray-100"
          >
            <div className="w-16 h-16 bg-blue-100 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-blue-600 transition-colors">
              <BrainCircuit className="w-8 h-8 text-blue-600 group-hover:text-white transition-colors" />
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-3 group-hover:text-blue-600 transition-colors">
              Focus App
            </h2>
            <p className="text-gray-500 leading-relaxed mb-6">
              AI Focus Master. Designed for deep work and concentration. Automatically generates session IDs for context management.
            </p>
            <div className="flex items-center text-blue-600 font-semibold group-hover:translate-x-2 transition-transform">
              Launch App <ArrowLeft className="rotate-180 ml-2 w-4 h-4" />
            </div>
          </div>

          {/* Card 2: Manus App */}
          <div 
            onClick={() => onSelectApp('manus')}
            className="group bg-white rounded-3xl p-8 shadow-xl hover:shadow-2xl hover:-translate-y-1 transition-all cursor-pointer border border-gray-100"
          >
            <div className="w-16 h-16 bg-purple-100 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-purple-600 transition-colors">
              <Sparkles className="w-8 h-8 text-purple-600 group-hover:text-white transition-colors" />
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-3 group-hover:text-purple-600 transition-colors">
              Super Agent
            </h2>
            <p className="text-gray-500 leading-relaxed mb-6">
              Manus AI Super Agent. Capable of handling complex queries with advanced tool integration.
            </p>
            <div className="flex items-center text-purple-600 font-semibold group-hover:translate-x-2 transition-transform">
              Launch App <ArrowLeft className="rotate-180 ml-2 w-4 h-4" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatSelector;