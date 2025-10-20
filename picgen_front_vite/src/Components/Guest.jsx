import { useEffect, useState } from "react";
import ImageModal from "./ImageModal";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import ButtonWrapper from "./ButtonWrapper";
import Header from "./Header";
import { savePicture } from "../api/PictureAPI";
import { useToken } from "./TokenContext";
import { generateImage, pollHF } from "../api/PictureAPI";

const Guest = ({ isGuest = true }) => {
  const [prompt, setPrompt] = useState("");
  const [imageModalOpen, setImageModalOpen] = useState(false);
  const [image, setImage] = useState(null);
  const [generating, setGenerating] = useState(false);

  const { loading, setLoading } = useToken();

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

    try {
      const response = await generateImage(prompt, 512, 20, 10);
      const eventId = response.data?.event_id;
      const queueTime = response.data.number_of_processes;
      if (!eventId) throw new Error("No event_id returned from server");
      console.log("queueTime:", queueTime, "delayMs:", delayMs);

      const delayMs = queueTime * 60 * 1000;

      setTimeout(async () => {
        try {
          console.log("Delayed poll after 6 minutes, event:", eventId);
          const pollResp = await pollHF(eventId);

          if (pollResp.data?.success && pollResp.data?.image) {
            setImage(`data:image/png;base64,${pollResp.data.image}`);
          } else {
            console.warn("Image not ready yet or error:", pollResp.data);
            alert("Please retry prompt");
          }
        } catch (error) {
          console.error("Delayed polling error:", error);
          alert("Error during generation. Please try again.");
        } finally {
          setGenerating(false);
          openModal();
        }
      }, delayMs);

    } catch (error) {
      console.error("Generate error:", error);
      alert("Failed to start generation. Please try again.");
      setGenerating(false);
    }
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

  const handlePromptChange = (e) => {
    const input = e.target.value;
    const words = input.trim().split(/\s+/);
    if (words.length <= 25) {
      updatePrompt(e);
    }
  };

  return (
    <div className="min-h-screen bg-gray-950 text-white px-4 py-8">
      <Header isGuest={isGuest} />

      <ImageModal
        isOpen={imageModalOpen}
        onClose={closeModal}
        image={image}
        isGuest={isGuest}
        generating={generating}
        savePic={savePic}
        disableClose={generating || loading}
      />


      <div className="max-w-xl mx-auto mt-10 space-y-8">
        <div className="w-full max-w-lg bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8 space-y-6">
          <header className="text-xl font-semibold text-center">
            What image would you like to create?
          </header>



        <textarea
          id="prompt"
          value={prompt}
          onChange={handlePromptChange}
          onInput={(e) => {
            const words = e.target.value.trim().split(/\s+/);
            if (words.length > 25) {
              // Prevents typing extra characters visually
              e.target.value = words.slice(0, 25).join(" ");
            }
          }}
          className="w-full h-32 px-4 py-3 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none resize-none"
          placeholder="Enter a prompt (max 25 words)..."
        />


          {/* <select>
            Inference Steps
            <option value={10}/>
            <option value={15}/>
            <option value={25}/>
            <option value={50}/>
          </select>
                    <select>
            Guidance Scale
            <option value={5}/>
            <option value={7}/>
            <option value={9}/>
            <option value={10}/>
          </select> */}

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
