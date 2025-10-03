import { useState } from "react";
import Header from "./Header";
import { useUser } from "./UserContext";
import { FaEdit } from "react-icons/fa";
import ButtonWrapper from "./ButtonWrapper";
import { updateUser } from "../api/UserAPI";

const Account = () => {
  const { user, setUser } = useUser();
  
  const [isEditingName, setIsEditingName] = useState(false);
  const [isEditingEmail, setIsEditingEmail] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);
  const [isDeletingAccount, setIsDeletingAccount] = useState(false);

  
  const [tempName, setTempName] = useState("");
  const [tempEmail, setTempEmail] = useState("");
  const [tempPassword, setTempPassword] = useState("");
  const [deleteInput, setDeleteInput] = useState("");

  const toggleEditing = (field) => {
    
    setIsEditingName(field === "name" ? !isEditingName : false);
    setIsEditingEmail(field === "email" ? !isEditingEmail : false);
    setIsEditingPassword(field === "password" ? !isEditingPassword : false);
    setIsDeletingAccount(field === "delete" ? !isDeletingAccount : false);

    
    if (field !== "name") setTempName("");
    if (field !== "email") setTempEmail("");
    if (field !== "password") setTempPassword("");
    if (field !== "delete") setDeleteInput("");
  };

  const confirmName = async() => {
    const response = await updateUser(user.id, tempName, null, null);
    setIsEditingName(false);
    setTempName("");
    setUser(response.data.dto);
    return response;
  };

  const confirmEmail = async() => {
    const response = await updateUser(user.id, null, tempEmail, null);
    setIsEditingEmail(false);
    setTempEmail("");
    setUser(response.data.dto);
    return response;
  };

  const confirmPassword = async() => {
    const response = await updateUser(user.id, null, null, tempPassword);
    setIsEditingPassword(false);
    setTempPassword("");
    return response;
  };

  const handleDeleteAccount = () => {
    console.log("Account deleted!");
    
    setIsDeletingAccount(false);
    setDeleteInput("");
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
      <Header />

      <div className="max-w-xl mx-auto mt-10 space-y-8">
        
        <div>
          <label className="block text-sm text-gray-400 mb-1">Username</label>
          <div className="flex items-center justify-between border-b border-gray-700 pb-3">
            <h1 className="text-3xl font-bold">{user.name}</h1>
            <button
              className="text-gray-400 hover:text-white"
              onClick={() => toggleEditing("name")}
            >
              <FaEdit size={20} />
            </button>
          </div>

          <div
            className={`transition-all duration-100 ease-in-out overflow-hidden ${
              isEditingName ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="text"
                value={tempName}
                onChange={(e) => setTempName(e.target.value)}
                placeholder="Enter new name..."
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <ButtonWrapper clickable={tempName != null && tempName.length > 0}>
              <button
                onClick={() => confirmName()}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg"
              >
                Confirm
              </button>
              </ButtonWrapper>
            </div>
          </div>
        </div>

        
        <div>
          <label className="block text-sm text-gray-400 mb-1">Email</label>
          <div className="flex items-center justify-between border-b border-gray-700 pb-3">
            <p className="text-lg">{user.email}</p>
            <button
              className="text-gray-400 hover:text-white"
              onClick={() => toggleEditing("email")}
            >
              <FaEdit size={18} />
            </button>
          </div>

          <div
            className={`transition-all duration-100 ease-in-out overflow-hidden ${
              isEditingEmail ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="email"
                value={tempEmail}
                onChange={(e) => setTempEmail(e.target.value)}
                placeholder="Enter new email..."
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <ButtonWrapper clickable={tempEmail != null && tempEmail.length > 0}>
              <button
                onClick={() => confirmEmail()}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg"
              >
                Confirm
              </button>
              </ButtonWrapper>
            </div>
          </div>
        </div>

        <div>
          <label className="block text-sm text-gray-400 mb-1">Password</label>
          <div className="flex items-center justify-between border-b border-gray-700 pb-3">
            <p className="text-lg text-gray-500">••••••••</p>
            <button
              className="text-gray-400 hover:text-white"
              onClick={() => toggleEditing("password")}
            >
              <FaEdit size={18} />
            </button>
          </div>

          <div
            className={`transition-all duration-100 ease-in-out overflow-hidden ${
              isEditingPassword ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="password"
                value={tempPassword}
                onChange={(e) => setTempPassword(e.target.value)}
                placeholder="Enter new password..."
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <ButtonWrapper clickable={tempPassword != null && tempPassword.length > 0}>
              <button
                onClick={confirmPassword}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg"
              >
                Confirm
              </button>
              </ButtonWrapper>
            </div>
          </div>
        </div>

        <div>
          <label className="block text-sm text-gray-400 mb-1">Delete Account</label>
          <div className="flex items-center justify-between border-b border-red-600 pb-3">
            <p className="text-lg text-red-500">Permanently delete your account</p>
            <button
              className="text-red-400 hover:text-red-600"
              onClick={() => toggleEditing("delete")}
            >
              Delete
            </button>
          </div>

          <div
            className={`transition-all duration-100 ease-in-out overflow-hidden ${
              isDeletingAccount ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="text"
                value={deleteInput}
                onChange={(e) => setDeleteInput(e.target.value)}
                placeholder='Type "DELETE" to confirm'
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-red-600 focus:ring-2 focus:ring-red-500 outline-none"
              />
              <button
                onClick={handleDeleteAccount}
                disabled={deleteInput !== "DELETE"}
                className={`px-4 py-2 rounded-lg ${
                  deleteInput === "DELETE"
                    ? "bg--600 hover:bg-red-500"
                    : "bg-red-800 cursor-not-allowed"
                }`}
              >
                Delete
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Account;
