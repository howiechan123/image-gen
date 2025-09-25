
// function ImageModal({isOpen, onClose, image}){
//     if(!isOpen){
//         return null;
//     }

//     return(
//         <div>
//             <img src={image} alt="loading..."></img>
//             <div></div>
//             <button onClick={onClose}>Close</button>
//         </div>
//     );
// }

// export default ImageModal;

function ImageModal({ isOpen, onClose, image }) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black/70 z-50 p-4">
            <div className="bg-gray-900/90 backdrop-blur-md rounded-2xl shadow-2xl w-full max-w-5xl h-[80vh] flex flex-col items-center p-6">
                <div className="flex-1 w-full flex items-center justify-center">
                    <img
                        src={image}
                        alt="Generated"
                        className="w-full h-full object-contain rounded-lg border border-gray-700"
                    />
                </div>
                <button
                    onClick={onClose}
                    className="mt-4 px-6 py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
                >
                    Close
                </button>
            </div>
        </div>
    );
}

export default ImageModal;

