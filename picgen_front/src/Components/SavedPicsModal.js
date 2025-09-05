

function SavedPicsModal({isOpen, onClose}) {
    if(!isOpen){
        return null;
    }

    return (
        <div>
            hello
            <div>
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    );
}

export default SavedPicsModal;