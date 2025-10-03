import { useEffect, useState } from "react";
import ImageModal from "./ImageModal";
import loadgif from "./loading.gif";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import ButtonWrapper from "./ButtonWrapper";
import Header from "./Header";
import { savePicture } from "../api/PictureAPI";
import { useToken } from "./TokenContext";

const Guest = ({ isGuest = true }) => {
  const [prompt, setPrompt] = useState("");
  const [imageModalOpen, setImageModalOpen] = useState(false);
  const [image, setImage] = useState(null);
  const [generating, setGenerating] = useState(false);

  const { setLoading } = useToken();

  const updatePrompt = (e) => setPrompt(e.target.value);

  const openModal = () => setImageModalOpen(true);
  const closeModal = () => {
    setImageModalOpen(false);
    setGenerating(false);
  };

  const handleGenerate = async () => {
    if (!prompt) return window.alert("Please enter a prompt");
    setGenerating(true);
    openModal();

    const data = { prompt };
    try {
      const response = await axios.post(
        "http://127.0.0.1:8000/generate-image",
        data
      );
      if (response.data.success) {
        setImage(`data:image/png;base64,${response.data.image}`);
        openModal();
      }
    } catch (error) {
      window.alert(error);
    }

    setGenerating(false);
  };

  const stripBase64Prefix = (base64) => {
    if (base64.startsWith("data:image")) {
      return base64.substring(base64.indexOf(",") + 1);
    }
    return base64;
  };

  const savePic = async () => {
    let base64 = stripBase64Prefix(image);
    setLoading(true);
    try {
      await savePicture(prompt, base64);
    } catch (err) {
      throw new Error(err);
    } finally {
      closeModal();
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
      <Header isGuest={isGuest} />

      <ImageModal
        isOpen={imageModalOpen}
        onClose={closeModal}
        image={image}
        isGuest={isGuest}
        generating={generating}
        savePic={savePic}
      />

      <div className="max-w-xl mx-auto mt-10 space-y-8">
        <div className="w-full max-w-lg bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8 space-y-6">
          <header className="text-xl font-semibold text-center">
            What image would you like to create?
          </header>

          <textarea
            id="prompt"
            onChange={updatePrompt}
            value={prompt}
            className="w-full h-32 px-4 py-3 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none resize-none"
            placeholder="Describe your pixel art idea..."
          ></textarea>

          <div className="flex justify-center">
            <ButtonWrapper
              clickable={!generating && prompt != null && prompt.length > 0}
            >
              <button
                id="generate"
                onClick={handleGenerate}
                className="px-6 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md disabled:opacity-50"
              >
                {generating ? "Generating..." : "Generate!"}
              </button>
            </ButtonWrapper>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Guest;
