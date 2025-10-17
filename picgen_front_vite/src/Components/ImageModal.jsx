import { motion, AnimatePresence } from "framer-motion";
import Loading from "./Loading.jsx";
import ButtonWrapper from "./ButtonWrapper";

function ImageModal({ isOpen, onClose, image, isGuest, generating, savePic, disableClose }) {
    return (
        <AnimatePresence>
            {isOpen && (
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.1, ease: "easeInOut" }}
                    className="fixed inset-0 flex items-center justify-center bg-black/70 backdrop-blur-sm z-50 p-4"
                    onClick={() => {
                        if (!disableClose) onClose();
                    }}
                >
                    <motion.div
                        initial={{ scale: 0.85, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        exit={{ scale: 0.85, opacity: 0 }}
                        transition={{ duration: 0.1, ease: "easeInOut" }}
                        className="bg-gray-900/90 text-white backdrop-blur-md rounded-2xl shadow-2xl w-full max-w-5xl h-[80vh] flex flex-col p-6"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <div className="flex-1 flex items-center justify-center overflow-hidden">
                            {(generating || disableClose) ? (
                                <Loading />
                            ) : (
                                <img
                                    src={image}
                                    alt="Generated"
                                    className="w-full h-full object-contain"
                                />
                            )}
                        </div>

                        {!disableClose && (
                            <div className="mt-4 flex justify-center gap-4">
                                <ButtonWrapper clickable>
                                    <button
                                        onClick={onClose}
                                        className="px-6 py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
                                    >
                                        Close
                                    </button>
                                </ButtonWrapper>

                                {!isGuest && (
                                    <ButtonWrapper clickable>
                                        <button
                                            onClick={savePic}
                                            className="px-6 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md"
                                        >
                                            Save
                                        </button>
                                    </ButtonWrapper>
                                )}
                            </div>
                        )}
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
}

export default ImageModal;

