import api from "@/Api/api";
import * as types from './ActionTypes';

export const register = (userData) => async (dispatch) => {
  dispatch({ type: types.REGISTER_REQUEST });
  try{
    const { data } = await api.post(`/auth/signup`, userData);
    if (data.jwt) {
      localStorage.setItem("jwt", data.jwt);
      dispatch({ type: types.REGISTER_SUCCESS, payload: data });
    }
    console.log("register success", data);
  } catch (error) {
    dispatch({ type: types.REGISTER_FAILURE, payload: error.message });
  }
};

export const login = (userData) => async (dispatch) => {
  dispatch({ type: types.LOGIN_REQUEST });
  try {
    const { data } = await api.post(`/auth/signin`, userData);
    if (data.jwt) {
      localStorage.setItem("jwt", data.jwt);
      dispatch({ type: types.LOGIN_SUCCESS, payload: data });
    }
    console.log("login success", data);
  } catch (error) {
    console.log(error);
    dispatch({ type: types.LOGIN_FAILURE, payload: error.message });
  }
};

export const getUser = (jwt) => async (dispatch) => {
  dispatch({ type: types.GET_USER_REQUEST });
  try {
    const { data } = await api.get(`/api/users/profile`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
    dispatch({ type: types.GET_USER_SUCCESS, payload: data });
    console.log("get user success", data);
  } catch (error) {
    console.log(error);
    dispatch({ type: types.GET_USER_FAILURE, payload: error.message });
  }
};

export const updateUserProfile = (formData) => async (dispatch) => {
  dispatch({ type: types.UPDATE_USER_PROFILE_REQUEST });

  try {
    const jwt = localStorage.getItem("jwt");
    
    const response = await api.put('/api/users/profile/update', formData, {
      headers: {
        Authorization: `Bearer ${jwt}`
      }
    });

    dispatch({
      type: types.UPDATE_USER_PROFILE_SUCCESS,
      payload: response.data,
    });

    return response.data;
  } catch (error) {
    console.error("Update profile error", error);
    dispatch({
      type: types.UPDATE_USER_PROFILE_FAILURE,
      payload: error.message,
    });
    throw error;
  }
};
export const uploadProfileImage = (base64Image) => async (dispatch) => {
  dispatch({ type: types.UPLOAD_PROFILE_IMAGE_REQUEST });

  try {
    const jwt = localStorage.getItem("jwt");
    
    const response = await api.post('/api/users/profile/upload-image', 
      { image: base64Image },
      {
        headers: {
          Authorization: `Bearer ${jwt}`
        }
      }
    );

    dispatch({
      type: types.UPLOAD_PROFILE_IMAGE_SUCCESS,
      payload: response.data,
    });

    return response.data;
  } catch (error) {
    console.error("Upload image error", error);
    dispatch({
      type: types.UPLOAD_PROFILE_IMAGE_FAILURE,
      payload: error.message,
    });
    throw error;
  }
};

export const fetchUserProfile = () => async (dispatch) => {
  dispatch({ type: types.GET_USER_PROFILE_REQUEST });

  try {
    const jwt = localStorage.getItem("jwt");
    const response = await api.get('/api/users/profile', {
      headers: {
        Authorization: `Bearer ${jwt}`
      }
    });

    dispatch({
      type: types.GET_USER_PROFILE_SUCCESS,
      payload: response.data,
    });
  } catch (error) {
    console.error("Fetch profile error", error);
    dispatch({
      type: types.GET_USER_PROFILE_FAILURE,
      payload: error.message,
    });
  }
};

export const verifyOtp = ({ jwt, otp }) => async (dispatch) => {
  dispatch({ type: types.VERIFY_OTP_REQUEST });
  try {
    const { data } = await api.patch(`/api/users/verification/verify-otp/${otp}`, {}, {
      headers: { Authorization: `Bearer ${jwt}` },
    });
    dispatch({ type: types.VERIFY_OTP_SUCCESS, payload: data });
  } catch (error) {
    dispatch({ type: types.VERIFY_OTP_FAILURE, payload: error });
  }
};

export const enableTwoStepAuthentication = ({ jwt, otp }) => async (dispatch) => {
  dispatch({ type: types.ENABLE_TWO_STEP_VERIFICATION_REQUEST });
  try {
    const { data } = await api.patch(`/api/users/enable-two-factor/verify-otp/${otp}`, {}, {
      headers: { Authorization: `Bearer ${jwt}` },
    });
    dispatch({ type: types.ENABLE_TWO_STEP_VERIFICATION_SUCCESS, payload: data });
    console.log("enable two step auth success", data);
  } catch (error) {
    console.log(error);
    dispatch({
      type: types.ENABLE_TWO_STEP_VERIFICATION_FAILURE,
      payload: error.message,
    });
  }
};

export const logout = () => (dispatch) => {
  localStorage.removeItem("jwt");
  dispatch({ type: types.LOGOUT });
};

export const sendResetPasswordOTP = (reqData) => async (dispatch) => {
  dispatch({ type: types.SEND_RESET_PASSWORD_OTP_REQUEST });
  try {
    const { data } = await api.post(
      `/auth/users/reset-password/send-otp`,
      reqData
    );

    console.log("send reset password otp success", data);
    dispatch({ type: types.SEND_RESET_PASSWORD_OTP_SUCCESS, payload: data });

  } catch (error) {
    console.log(error);
    dispatch({ type: types.SEND_RESET_PASSWORD_OTP_FAILURE, payload: error.message });
  }
};

export const sendVerificationOtp = ({jwt, verificationType}) => async (dispatch) => {
  dispatch({ type: types.SEND_VERIFICATION_OTP_REQUEST });
  try {
    const { data } = await api.post(
      `/api/users/verification/${verificationType}/send-otp`,
      {},
      {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      }
    );

    console.log("send verification otp success", data);
    dispatch({ type: types.SEND_VERIFICATION_OTP_SUCCESS, payload: data });

  } catch (error) {
    dispatch({ type: types.SEND_VERIFICATION_OTP_FAILURE, payload: error.message });
  }
};

export const twoStepVerification = (reqData) => async (dispatch) => {
  dispatch({ type: types.LOGIN_WITH_OTP_REQUEST });
  try {
    const { data } = await api.post(`/auth/two-factor/otp`, reqData);

    if (data.jwt) {
      localStorage.setItem("jwt", data.jwt);
      dispatch({ type: types.LOGIN_WITH_OTP_SUCCESS, payload: data });
    }
    console.log("login with otp success", data);

  } catch (error) {
    console.log(error);
    dispatch({ type: types.LOGIN_WITH_OTP_FAILURE, payload: error.message });
  }
};

export const verifyResetPasswordOTP = ({ otp, password, session, navigate }) => async (dispatch) => {
  dispatch({ type: types.VERIFY_RESET_PASSWORD_OTP_REQUEST });
  try {
    const { data } = await api.patch(
      `/auth/users/reset-password/verify-otp?id=${session}`,
      { otp, password }
    );

    console.log("verify reset password otp success", data);
    dispatch({ type: types.VERIFY_RESET_PASSWORD_OTP_SUCCESS, payload: data });
    navigate("/login");

  } catch (error) {
    console.log(error);
    dispatch({
      type: types.VERIFY_RESET_PASSWORD_OTP_FAILURE, payload: error.message
    });
  }
};
