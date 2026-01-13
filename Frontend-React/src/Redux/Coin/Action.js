import api from "@/Api/api";
import * as types from './ActionTypes';

export const fetchCoinList = (page, jwt) => async (dispatch) => {
  try {
    dispatch({ type: types.FETCH_COIN_LIST_REQUEST });
    
    console.log(`Fetching coins for page: ${page}`);
    
    const response = await api.get(`/api/coins?page=${page - 1}`, {
      headers: { Authorization: `Bearer ${jwt}` }
    });
    
    const coinData = response.data.content || [];
    
    console.log("Coin list fetched:", coinData.length, "coins");
    
    dispatch({
      type: types.FETCH_COIN_LIST_SUCCESS,
      payload: {
        coins: coinData,
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        currentPage: response.data.number,
      },
    });
  } catch (error) {
    console.error("Error fetching coin list:", error.message);
    dispatch({
      type: types.FETCH_COIN_LIST_ERROR,
      payload: error.message,
    });
  }
};


export const fetchCoinById = ({ coinId, jwt }) => async (dispatch, getState) => {
  return fetchCoinDetails({ coinId, jwt })(dispatch, getState);
};


export const fetchCoinDetails = ({ coinId, jwt }) => async (dispatch, getState) => {
  const { coin } = getState();
  if (coin.coinDetails && coin.coinDetails.id === coinId) {
    console.log(`Using cached details for coin: ${coinId}`);
    return;
  }

  try {
    dispatch({ type: types.FETCH_COIN_DETAILS_REQUEST });
    
    console.log(`Fetching details for coin: ${coinId}`);
    
    const response = await api.get(`/api/coins/details/${coinId}`, {
      headers: { Authorization: `Bearer ${jwt}` }
    });
    
    console.log("Coin details fetched:", response.data.name);
    
    dispatch({
      type: types.FETCH_COIN_DETAILS_SUCCESS,
      payload: response.data,
    });
  } catch (error) {
    console.error("Error fetching coin details:", error.message);
    dispatch({
      type: types.FETCH_COIN_DETAILS_ERROR,
      payload: error.message,
    });
  }
};

export const getTop50CoinList = (jwt) => async (dispatch, getState) => {
  const { coin } = getState();
  if (coin.top50 && Array.isArray(coin.top50) && coin.top50.length > 0) {
    console.log("Using cached top 50 coins, skipping fetch.");
    return;
  }
  try {
    dispatch({ type: types.GET_TOP_50_REQUEST });
    
    console.log(" Fetching top 50 coins");
    
    const response = await api.get("/api/coins/top50", {
      headers: { Authorization: `Bearer ${jwt}` }
    });
    
    const coinData = Array.isArray(response.data) ? response.data : [];
    
    console.log("Top 50 coins fetched:", coinData.length, "coins");
    
    dispatch({
      type: types.GET_TOP_50_SUCCESS,
      payload: coinData,
    });
  } catch (error) {
    console.error("Error fetching top 50:", error.message);
    dispatch({
      type: types.GET_TOP_50_ERROR,
      payload: error.message,
    });
  }
};

export const searchCoin = (keyword) => async (dispatch) => {
  dispatch({ type: types.SEARCH_COIN_REQUEST });
  try {
    console.log("Searching for coin:", keyword);
    
    const response = await api.get(`/api/coins/search?q=${encodeURIComponent(keyword)}`);
    
    console.log("Search results:", response.data);
    
    dispatch({ 
      type: types.SEARCH_COIN_SUCCESS, 
      payload: Array.isArray(response.data) ? response.data : [] 
    });
  } catch (error) {
    console.error("Error searching coin:", error.message);
    dispatch({
      type: types.SEARCH_COIN_FAILURE,
      payload: error.message,
    });
  }
};

export const fetchTreadingCoinList = (jwt) => async (dispatch, getState) => {
  console.log("fetchTreadingCoinList delegating to getTop50CoinList");
  return getTop50CoinList(jwt)(dispatch, getState);
};