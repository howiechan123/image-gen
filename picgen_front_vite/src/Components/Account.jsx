import Header from "./Header";
import { useUser } from "./UserContext";

const Account = () => {
    const {user} = useUser();
    return(
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
            <Header />
            {user.id}{user.name}{user.email}
        </div>
    );
}

export default Account;