import { useState } from "react";
import { editPictureName } from "../api/PictureAPI";
import ButtonWrapper from "./ButtonWrapper";
import { motion, AnimatePresence } from "framer-motion";

const EditPicNameModal = ({ isEditModalOpen, closeEditModal, image }) => {
  const [newName, setNewName] = useState(null);

  const edit = async (picId, newName) => {
    try {
      const response = editPictureName(picId, newName);
      closeEditModal(newName);
      return response;
    } catch (err) {
      return new Error(err);
    }
  };

  return (
    <AnimatePresence>
      {isEditModalOpen && (
        <motion.div
          key="edit-backdrop"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.1, ease: "easeInOut" }}
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm"
          onClick={() => closeEditModal(image.fileName)} // backdrop closes modal
        >
          <motion.div
            key="edit-modal"
            initial={{ scale: 0.85, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.85, opacity: 0 }}
            transition={{ duration: 0.1, ease: "easeInOut" }}
            className="bg-gray-900 text-white rounded-lg shadow-lg p-6 w-full max-w-sm flex flex-col items-center gap-4"
            onClick={(e) => e.stopPropagation()} // prevent close when clicking inside
          >

            <input
              id="newName"
              type="text"
              onChange={(e) => setNewName(e.target.value)}
              className="w-full h-10 px-3 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
              placeholder="Enter new name..."
            />

            <div className="flex gap-4 mt-4">
              <ButtonWrapper clickable={newName != null && newName.length > 0}>
                <button
                  onClick={() => edit(image.picId, newName)}
                  className="px-4 py-2 bg-indigo-600 rounded-lg hover:bg-indigo-500 transition"
                >
                  Confirm
                </button>
              </ButtonWrapper>

              <button
                onClick={() => closeEditModal(image.fileName)}
                className="px-4 py-2 bg-gray-700 rounded-lg hover:bg-gray-600 transition"
              >
                Cancel
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default EditPicNameModal;
