// import ButtonWrapper from "./ButtonWrapper";
// import Loading from "./Loading.jsx"
// function ImageModal({ isOpen, onClose, image, isGuest, generating, savePic }) {
//     if (!isOpen) return null;

//     return (
//         <div className="fixed inset-0 flex items-center justify-center bg-black/70 z-50 p-4">
//             <div className="bg-gray-900/90 backdrop-blur-md rounded-2xl shadow-2xl w-full max-w-5xl h-[80vh] flex flex-col items-center p-6">
//                 <div className="flex-1 w-full flex items-center justify-center">
//                     {!generating ? <img
//                         src={image}
//                         alt="Generated"
//                         className="w-full h-full object-contain rounded-lg border border-gray-700"
//                     /> : <Loading/>}
//                 </div>
//                 <div className="flex-1 w-full flex items-center justify-center space-x-4">
                    
//                     {!generating && 
//                     <button
//                         onClick={() => onClose()}
//                         className="mt-4 px-6 py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
//                     >
//                         Close
//                     </button>
//                     }
                    
//                     {!isGuest && !generating && (
//                         <button
//                             onClick={() => savePic()}
//                             className="mt-4 px-6 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md"
//                         >
//                             Save
//                         </button>
//                     )}
//                 </div>

//             </div>
//         </div>
//     );
// }

// export default ImageModal;

import Loading from "./Loading.jsx"

function ImageModal({ isOpen, onClose, image, isGuest, generating, savePic }) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black/70 z-50 p-4">
            <div className="bg-gray-900/90 backdrop-blur-md rounded-2xl shadow-2xl w-full max-w-5xl h-[80vh] flex flex-col p-6">
                
                <div className="flex-1 flex items-center justify-center overflow-hidden">
                {!generating ? (
                    <img
                    src={image}
                    alt="Generated"
                    className="w-full h-full object-contain" 
                    />
                ) : (
                    <Loading />
                )}
                </div>

                {!generating && (
                    <div className="mt-4 flex justify-center space-x-4">
                        <button
                            onClick={onClose}
                            className="px-6 py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
                        >
                            Close
                        </button>

                        {!isGuest && (
                            <button
                                onClick={savePic}
                                className="px-6 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md"
                            >
                                Save
                            </button>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default ImageModal;

