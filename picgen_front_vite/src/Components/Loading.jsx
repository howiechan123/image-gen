const Loading = () => {
  return (
    <div className="fixed inset-0 flex flex-col items-center justify-center bg-black/80 backdrop-blur-sm z-50">
      
      <div className="relative w-16 h-16 mb-4">
        <div className="absolute inset-0 rounded-full border-4 border-indigo-500 border-t-transparent animate-spin"></div>
      </div>

      
      <p className="text-indigo-400 font-semibold animate-pulse tracking-wide text-lg">
        Loading...
      </p>
    </div>
  );
};

export default Loading;
