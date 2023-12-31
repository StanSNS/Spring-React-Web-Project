import axios from "axios";
import {getToken} from "./AuthService";

// Axios interceptor to add the Authorization token to all outgoing requests.
axios.interceptors.request.use(
    function (config) {
        // Set the 'Authorization' header in the request with the token obtained from AuthService.
        config.headers['Authorization'] = getToken();
        return config;
    },
    function (error) {
        return Promise.reject(error);
    }
);

// Function to get all users based on the provided username and JWT token.
export const getAllUsers = (username, jwtToken) => {
    return axios.get(`http://localhost:8000/admin?action=getAllUsersAsAdmin&username=${username}&jwtToken=${jwtToken}`)
        .then((response) => {
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error('Failed to get all users');
            }
        })
        .catch((error) => {
            throw error;
        });
};

// Function to get all user inquiries based on the provided currentUsername and username and JWT token.
export const getAllInquiriesForUser = (username, jwtToken, currentUsername) => {
    return axios.get(`http://localhost:8000/admin?action=getAllInquiriesForUser&username=${username}&jwtToken=${jwtToken}&currentUsername=${currentUsername}`)
        .then((response) => {
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error('Failed to get all inquiries');
            }
        })
        .catch((error) => {
            throw error;
        });
};

// Function to get all user reports based on the provided currentUsername and username and JWT token.
export const getAllReportsForUser = (username, jwtToken, currentUsername) => {
    return axios.get(`http://localhost:8000/admin?action=getAllReportsForUser&username=${username}&jwtToken=${jwtToken}&currentUsername=${currentUsername}`)
        .then((response) => {
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error('Failed to get all inquiries');
            }
        })
        .catch((error) => {
            throw error;
        });
};

// Function to update the roles of a user.
export const updateBlockedUserRoles = (loggedUsername, jwtToken, banUsername, roles) => {
    return axios.put(`http://localhost:8000/admin?banUsername=${banUsername}&loggedUsername=${loggedUsername}&jwtToken=${jwtToken}`, roles)
        .then((response) => {
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error('Failed to block the user');
            }
        }).catch((error) => {
            throw error;
        });
};
