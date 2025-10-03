import { deletePicture } from "../api/PictureAPI";

const ConfirmDeleteModal = ({ isDeleteModalOpen, image, onDelete, close }) => {
    if (!isDeleteModalOpen) return null;

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
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <div className="bg-gray-900 text-white rounded-lg shadow-lg p-6 w-full max-w-sm flex flex-col items-center gap-4">
                <p className="text-center">
                    Are you sure you want to delete this picture? <br /> This action cannot be undone.
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
            </div>
        </div>
    );
};

export default ConfirmDeleteModal;
