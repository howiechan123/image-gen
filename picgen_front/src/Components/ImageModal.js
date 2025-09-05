
function ImageModal({isOpen, onClose, image}){
    if(!isOpen){
        return null;
    }

    return(
        <div>
            <img src={image} alt="loading..."></img>
            <div></div>
            <button onClick={onClose}>Close</button>
        </div>
    );
}

export default ImageModal;