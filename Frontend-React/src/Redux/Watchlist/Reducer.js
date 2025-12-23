import * as types from "./ActionTypes";

const initialState = {
  watchlist: null,
  coins: [], // ✅ Initialize as empty array, NOT items
  loading: false,
  error: null,
};

export const watchlistReducer = (state = initialState, action) => {
  switch (action.type) {
    case types.GET_USER_WATCHLIST_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case types.GET_USER_WATCHLIST_SUCCESS:
      return {
        ...state,
        watchlist: action.payload,
        coins: Array.isArray(action.payload?.coins) ? action.payload.coins : [], // ✅ Ensure array
        loading: false,
        error: null,
      };

    case types.GET_USER_WATCHLIST_FAILURE:
      return {
        ...state,
        watchlist: null,
        coins: [], // ✅ Reset to empty array
        loading: false,
        error: action.payload,
      };

    case types.ADD_COIN_TO_WATCHLIST_REQUEST:
      return {
        ...state,
        loading: true,
      };

    case types.ADD_COIN_TO_WATCHLIST_SUCCESS:
      return {
        ...state,
        loading: false,
      };

    case types.ADD_COIN_TO_WATCHLIST_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case types.REMOVE_COIN_FROM_WATCHLIST_REQUEST:
      return {
        ...state,
        loading: true,
      };

    case types.REMOVE_COIN_FROM_WATCHLIST_SUCCESS:
      return {
        ...state,
        loading: false,
      };

    case types.REMOVE_COIN_FROM_WATCHLIST_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    default:
      return state;
  }
};

export default watchlistReducer;
