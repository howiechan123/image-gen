
import { useState } from "react";
import Header from "./Header";
import { useUser } from "./UserContext";
import { FaEdit } from "react-icons/fa";

const Account = () => {
  const { user } = useUser();
  const [editingField, setEditingField] = useState(null);
  const [formValues, setFormValues] = useState({
    name: user.name,
    email: user.email,
    password: "",
  });

  const handleChange = (e) => {
    setFormValues({ ...formValues, [e.target.name]: e.target.value });
  };

  const handleConfirm = (field) => {
    console.log("Confirm new value:", field, formValues[field]);
    setEditingField(null);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
      <Header />

      <div className="max-w-xl mx-auto mt-10 space-y-8">
        {/* Username */}
        <div>
          <label className="block text-sm text-gray-400 mb-1">Username</label>
          <div className="flex items-center justify-between border-b border-gray-700 pb-3">
            <h1 className="text-3xl font-bold">{user.name}</h1>
            <button
              className="text-gray-400 hover:text-white"
              onClick={() =>
                setEditingField(editingField === "name" ? null : "name")
              }
            >
              <FaEdit size={20} />
            </button>
          </div>

          <div
            className={`transition-all duration-300 ease-in-out overflow-hidden ${
              editingField === "name" ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="text"
                name="name"
                value={formValues.name}
                onChange={handleChange}
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <button
                onClick={() => handleConfirm("name")}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>

        {/* Email */}
        <div>
          <label className="block text-sm text-gray-400 mb-1">Email</label>
          <div className="flex items-center justify-between border-b border-gray-700 pb-3">
            <p className="text-lg">{user.email}</p>
            <button
              className="text-gray-400 hover:text-white"
              onClick={() =>
                setEditingField(editingField === "email" ? null : "email")
              }
            >
              <FaEdit size={18} />
            </button>
          </div>

          <div
            className={`transition-all duration-300 ease-in-out overflow-hidden ${
              editingField === "email" ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="email"
                name="email"
                value={formValues.email}
                onChange={handleChange}
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <button
                onClick={() => handleConfirm("email")}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>

        {/* Password */}
        <div>
          <label className="block text-sm text-gray-400 mb-1">Password</label>
          <div className="flex items-center justify-between border-b border-gray-700 pb-3">
            <p className="text-lg text-gray-500">••••••••</p>
            <button
              className="text-gray-400 hover:text-white"
              onClick={() =>
                setEditingField(editingField === "password" ? null : "password")
              }
            >
              <FaEdit size={18} />
            </button>
          </div>

          <div
            className={`transition-all duration-300 ease-in-out overflow-hidden ${
              editingField === "password" ? "max-h-40 opacity-100 mt-3" : "max-h-0 opacity-0"
            }`}
          >
            <div className="flex gap-3 items-center">
              <input
                type="password"
                name="password"
                value={formValues.password}
                onChange={handleChange}
                className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <button
                onClick={() => handleConfirm("password")}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Account;

