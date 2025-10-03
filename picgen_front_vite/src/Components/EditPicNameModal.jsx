import { useState } from "react";
import { editPictureName } from "../api/PictureAPI";
import ButtonWrapper from "./ButtonWrapper";

const EditPicNameModal = ({ isEditModalOpen, closeEditModal, image }) => {
    if (!isEditModalOpen) return null;

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
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <div className="bg-gray-900 text-white rounded-lg shadow-lg p-6 w-full max-w-sm flex flex-col items-center gap-4">
                
                <p className="text-center">
                    Enter a new name for this picture:
                </p>

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

            </div>
        </div>
    );
};

export default EditPicNameModal;
