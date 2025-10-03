import { useEffect, useState } from "react";
import { getPictures } from "../api/PictureAPI";
import Header from "./Header";
import SavedPicsModal from "./SavedPicsModal";

function SavedPics() {
  const [pics, setPics] = useState([]);
  const [selectedPic, setSelectedPic] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const getPics = async () => {
    try {
      const response = await getPictures();
      if (response.data.success) {
        const mapped = response.data.dto.pics.map((pic) => ({
          filePath: pic.filePath,
          fileName: pic.fileName,
          picId: pic.pictureId,
          deleteUrl: pic.deleteUrl
        }));
        console.log(response.data);
        setPics(mapped);
      }
    } catch (error) {
      console.log(error);
    }
  };
    const openModal = (pic) => {
        setSelectedPic(pic);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        // wait until animation ends before removing pic
        setTimeout(() => setSelectedPic(null), 300); // 300ms = same as exit duration
    };


  useEffect(() => {
    getPics();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
      <div className="w-full max-w-3xl mx-auto">
        <Header isGuest={false} />

        
        <div className="grid grid-cols-3 gap-2 mt-6">
          {pics.map((p, idx) => (
            <div
              key={idx}
              onClick={() => openModal(p)}
              className="cursor-pointer overflow-hidden rounded-md"
            >
              <img
                src={p.filePath}
                alt={p.fileName}
                loading="lazy"
                className="w-full aspect-square object-cover hover:opacity-80 transition"
              />
            </div>
          ))}
        </div>
      </div>

      
        {selectedPic && (
        <SavedPicsModal
            isOpen={isModalOpen}
            image={selectedPic}
            onClose={closeModal}
            onDelete={(picId) => {
            setPics((prev) => prev.filter((pic) => pic.picId !== picId));
            closeModal();
            }}
        />
        )}
    </div>
  );
}

export default SavedPics;
