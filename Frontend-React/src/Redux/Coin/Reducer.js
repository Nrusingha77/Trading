import * as types from './ActionTypes';

const initialState = {
  coinList: [],
  top50: [],
  coinDetails: null,
  searchCoinList: [],
  loading: false,
  error: null,
  totalPages: 0,
  currentPage: 0,
};

export const coinReducer = (state = initialState, action) => {
  switch (action.type) {
  
    case types.FETCH_COIN_LIST_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case types.FETCH_COIN_LIST_SUCCESS:
      return {
        ...state,
        coinList: Array.isArray(action.payload.coins) ? action.payload.coins : [],
        totalPages: action.payload.totalPages || 0,
        currentPage: action.payload.currentPage || 0,
        loading: false,
        error: null,
      };

    case types.FETCH_COIN_LIST_ERROR:
      return {
        ...state,
        coinList: [],
        loading: false,
        error: action.payload,
      };

    case types.GET_TOP_50_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case types.GET_TOP_50_SUCCESS:
      return {
        ...state,
        top50: Array.isArray(action.payload) ? action.payload : [],
        loading: false,
        error: null,
      };

    case types.GET_TOP_50_ERROR:
      return {
        ...state,
        top50: [],
        loading: false,
        error: action.payload,
      };

   
    case types.FETCH_COIN_DETAILS_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case types.FETCH_COIN_DETAILS_SUCCESS:
      return {
        ...state,
        coinDetails: action.payload,
        loading: false,
        error: null,
      };

    case types.FETCH_COIN_DETAILS_ERROR:
      return {
        ...state,
        coinDetails: null,
        loading: false,
        error: action.payload,
      };


    case types.SEARCH_COIN_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case types.SEARCH_COIN_SUCCESS:
      return {
        ...state,
        searchCoinList: Array.isArray(action.payload) ? action.payload : [],
        loading: false,
        error: null,
      };

    case types.SEARCH_COIN_FAILURE:
      return {
        ...state,
        searchCoinList: [],
        loading: false,
        error: action.payload,
      };


    case types.FETCH_TRADING_COIN_REQUEST:
      return {
        ...state,
        loading: true,
      };

    case types.FETCH_TRADING_COIN_SUCCESS:
      return {
        ...state,
        top50: Array.isArray(action.payload) ? action.payload : [],
        loading: false,
      };

    case types.FETCH_TRADING_COIN_ERROR:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    default:
      return state;
  }
};

export default coinReducer;
