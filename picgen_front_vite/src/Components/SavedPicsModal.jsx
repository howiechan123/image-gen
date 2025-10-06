import EditPicNameModal from "./EditPicNameModal";
import { useState, useRef } from "react";
import { FaTrash, FaDownload, FaEdit } from "react-icons/fa";
import ConfirmDeleteModal from "./ConfirmDeleteModal";
import { motion, AnimatePresence } from "framer-motion";

function SavedPicsModal({ isOpen, image, onClose, onDelete }) {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const touchStartY = useRef(null);

  const openDeleteModal = () => setIsDeleteModalOpen(true);
  const closeDeleteModal = () => setIsDeleteModalOpen(false);

  const openEditModal = () => setIsEditModalOpen(true);
  const closeEditModal = (newName) => {
    image.fileName = newName;
    setIsEditModalOpen(false);
  };

  const handleTouchStart = (e) => {
    touchStartY.current = e.touches[0].clientY;
  };

  const handleTouchEnd = (e) => {
    if (touchStartY.current === null) return;
    const touchEndY = e.changedTouches[0].clientY;
    const deltaY = touchEndY - touchStartY.current;
    if (deltaY > 100) {
      onClose();
    }
    touchStartY.current = null;
  };

  function downloadFile(url, filename) {
    const a = document.createElement('a');
    a.href = url;
    a.download = filename; // optional hint to browser
    a.target = '_blank';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.1, ease: "easeInOut" }}
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 backdrop-blur-sm"
          onClick={onClose}
          onTouchStart={handleTouchStart}
          onTouchEnd={handleTouchEnd}
        >
          <motion.div
            initial={{ scale: 0.85, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.85, opacity: 0 }}
            transition={{ duration: 0.1, ease: "easeInOut" }}
            className="relative w-full h-full flex flex-col items-center justify-center"
            onClick={(e) => e.stopPropagation()} // Prevent closing on inner click
          >
            <img
              src={image.filePath}
              alt={image.fileName}
              className="w-full h-full object-contain"
            />

            <div className="absolute right-6 bottom-1/2 transform translate-y-1/2 flex flex-col gap-4">
              <button
                className="w-12 h-12 bg-white/30 hover:bg-white/50 text-white rounded-full flex items-center justify-center shadow-lg transition"
                onClick={() => downloadFile(image.filePath,"e")}
              >
                <FaDownload size={20} />
              </button>

              <button
                onClick={openDeleteModal}
                className="w-12 h-12 bg-red-500/60 hover:bg-red-500 text-white rounded-full flex items-center justify-center shadow-lg transition"
              >
                <FaTrash size={20} />
              </button>
            </div>

            <button
              onClick={onClose}
              className="absolute top-4 right-4 bg-black/60 text-white rounded-full p-3 hover:bg-black/80 transition z-50"
            >
              ✕
            </button>

            <div className="absolute bottom-6 flex items-center gap-2 bg-black/50 px-4 py-2 rounded-lg">
              <span className="text-white font-medium">{image.fileName}</span>
              <button
                className="text-white hover:text-gray-300 transition"
                onClick={openEditModal}
              >
                <FaEdit />
              </button>
            </div>

            <EditPicNameModal
              isEditModalOpen={isEditModalOpen}
              image={image}
              closeEditModal={closeEditModal}
            />

            <ConfirmDeleteModal
              isDeleteModalOpen={isDeleteModalOpen}
              image={image}
              onDelete={onDelete}
              close={closeDeleteModal}
            />
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

export default SavedPicsModal;
