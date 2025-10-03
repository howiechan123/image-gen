import { motion, AnimatePresence } from "framer-motion";
import { deletePicture } from "../api/PictureAPI";

const ConfirmDeleteModal = ({ isDeleteModalOpen, image, onDelete, close }) => {
  const deletePic = async () => {
    try {
      const response = await deletePicture(image.picId);
      onDelete(image.picId);
      close();
      return response;
    } catch (err) {
      throw new Error(err);
    }
  };

  return (
    <AnimatePresence>
      {isDeleteModalOpen && (
        <motion.div
          key="backdrop"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.25 }}
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm"
          onClick={close} // clicking backdrop closes modal
        >
          <motion.div
            key="modal"
            initial={{ scale: 0.85, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.85, opacity: 0 }}
            transition={{ duration: 0.3, ease: "easeInOut" }}
            className="bg-gray-900 text-white rounded-lg shadow-lg p-6 w-full max-w-sm flex flex-col items-center gap-4"
            onClick={(e) => e.stopPropagation()} // prevent backdrop close on inner click
          >
            <p className="text-center">
              Are you sure you want to delete this picture? <br /> This action
              cannot be undone.
            </p>

            <div className="flex gap-4 mt-4">
              <button
                onClick={deletePic}
                className="px-4 py-2 bg-red-600 rounded-lg hover:bg-red-500 transition"
              >
                Confirm
              </button>
              <button
                onClick={close}
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

export default ConfirmDeleteModal;
