import * as types from "./ActionTypes";

const initialState = {
  jwt: localStorage.getItem("jwt") || null, // ✅ Load from localStorage on init
  user: null,
  status: "idle",
  error: null,
  isAuthenticated: !!localStorage.getItem("jwt"),
};

export default function authReducer(state = initialState, action) {
  switch (action.type) {
    case types.REGISTER_REQUEST:
    case types.LOGIN_REQUEST:
    case types.GET_USER_REQUEST:
    case types.UPDATE_USER_PROFILE_REQUEST:
    case types.UPLOAD_PROFILE_IMAGE_REQUEST:
    case types.SEND_RESET_PASSWORD_OTP_REQUEST:
    case types.SEND_VERIFICATION_OTP_REQUEST:
    case types.ENABLE_TWO_STEP_VERIFICATION_REQUEST:
    case types.LOGIN_WITH_OTP_REQUEST:
    case types.VERIFY_RESET_PASSWORD_OTP_REQUEST:
      return { ...state, loading: true, error: null };

    case types.LOGIN_SUCCESS:
    case types.REGISTER_SUCCESS:
    case types.LOGIN_WITH_OTP_SUCCESS:
      // ✅ Save JWT to both Redux and localStorage
      localStorage.setItem("jwt", action.payload.jwt);
      return {
        ...state,
        loading: false,
        error: null,
        jwt: action.payload.jwt,
        user: action.payload.user,
        isAuthenticated: true,
        status: "success",
      };

    case types.GET_USER_SUCCESS:
    case types.VERIFY_OTP_SUCCESS:
    case types.UPDATE_USER_PROFILE_SUCCESS:
    case types.UPLOAD_PROFILE_IMAGE_SUCCESS:
    case types.SEND_RESET_PASSWORD_OTP_SUCCESS:
    case types.SEND_VERIFICATION_OTP_SUCCESS:
    case types.ENABLE_TWO_STEP_VERIFICATION_SUCCESS:
    case types.VERIFY_RESET_PASSWORD_OTP_SUCCESS:
      return { ...state, loading: false, error: null, user: action.payload };

    case types.REGISTER_FAILURE:
    case types.LOGIN_FAILURE:
    case types.GET_USER_FAILURE:
    case types.UPDATE_USER_PROFILE_FAILURE:
    case types.UPLOAD_PROFILE_IMAGE_FAILURE:
    case types.SEND_RESET_PASSWORD_OTP_FAILURE:
    case types.SEND_VERIFICATION_OTP_FAILURE:
    case types.ENABLE_TWO_STEP_VERIFICATION_FAILURE:
    case types.LOGIN_WITH_OTP_FAILURE:
    case types.VERIFY_RESET_PASSWORD_OTP_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case types.LOGOUT:
     
      localStorage.removeItem("jwt");
      return {
        ...state,
        jwt: null,
        user: null,
        isAuthenticated: false,
      };

    default:
      return state;
  }
}