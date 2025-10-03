import EditPicNameModal from "./EditPicNameModal";
import { useState } from "react";
import { FaTrash, FaDownload, FaEdit } from "react-icons/fa";
import ConfirmDeleteModal from "./ConfirmDeleteModal";

function SavedPicsModal({ isOpen, image, onClose, onDelete }) {
    if (!isOpen) return null;
    
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);

    const openDeleteModal = () => setIsDeleteModalOpen(true);
    const closeDeleteModal = () => setIsDeleteModalOpen(false);

    const openEditModal = () => setIsEditModalOpen(true);
    const closeEditModal = () => setIsEditModalOpen(false);

    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 backdrop-blur-sm"
            onClick={onClose}
        >
            <div
                className="relative w-full h-full flex flex-col items-center justify-center"
                onClick={(e) => e.stopPropagation()} // Prevent clicks inside from closing
            >

                <img
                    src={image.filePath}
                    alt={image.fileName}
                    className="w-full h-full object-contain"
                />

 
                <div className="absolute right-6 bottom-1/2 transform translate-y-1/2 flex flex-col gap-4">
                    <button className="w-12 h-12 bg-white/30 hover:bg-white/50 text-white rounded-full flex items-center justify-center shadow-lg transition"
                    onClick={() => {console.log("dload")}}
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
                    onClick={() => openEditModal()}
                    >
                        <FaEdit />
                    </button>
                </div>

                <EditPicNameModal
                isEditModalOpen={isEditModalOpen}
                image={image}
                closeEditModal={() => closeEditModal()}
                />

                
                <ConfirmDeleteModal
                    isDeleteModalOpen={isDeleteModalOpen}
                    image={image}
                    onDelete={onDelete}
                    close={closeDeleteModal}
                />
            </div>
        </div>
    );
}

export default SavedPicsModal;
