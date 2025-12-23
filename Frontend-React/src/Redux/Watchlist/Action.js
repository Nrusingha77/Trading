import * as types from './ActionTypes';
import api from '@/Api/api';


export const getUserWatchlist = () => async (dispatch) => {
  dispatch({ type: types.GET_USER_WATCHLIST_REQUEST });

  try {
    const jwt = localStorage.getItem("jwt");
    
    if (!jwt) {
      console.warn(" No JWT token found");
      throw new Error("JWT token not found");
    }
    
    console.log("Fetching user watchlist");
    
    const response = await api.get('/api/watchlist/user', {
      headers: {
        Authorization: `Bearer ${jwt}`
      }
    });

    console.log("Watchlist fetched:", response.data);
    
    dispatch({
      type: types.GET_USER_WATCHLIST_SUCCESS,
      payload: response.data,
    });
  } catch (error) {
    console.error(" Error fetching watchlist:", {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message,
    });
    
    dispatch({
      type: types.GET_USER_WATCHLIST_FAILURE,
      payload: error.message,
    });
  }
};


export const addItemToWatchlist = (coinId) => async (dispatch) => {
  dispatch({ type: types.ADD_COIN_TO_WATCHLIST_REQUEST });

  try {
    const jwt = localStorage.getItem("jwt");
    
    if (!jwt) {
      console.warn("No JWT token found");
      throw new Error("JWT token not found");
    }
    
    console.log("Adding/removing coin:", coinId);
    
    const response = await api.patch(`/api/watchlist/add/coin/${coinId}`, {}, {
      headers: {
        Authorization: `Bearer ${jwt}`
      }
    });

    console.log("Coin processed:", response.data);
    
    dispatch({
      type: types.ADD_COIN_TO_WATCHLIST_SUCCESS,
      payload: response.data,
    });
    
 
    setTimeout(() => {
      dispatch(getUserWatchlist());
    }, 500);
    
  } catch (error) {
    console.error("Error in watchlist operation:", {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message,
    });
    
    dispatch({
      type: types.ADD_COIN_TO_WATCHLIST_FAILURE,
      payload: error.message,
    });
  }
};
