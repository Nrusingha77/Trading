import { CHAT_BOT_FAILURE, CHAT_BOT_REQUEST, APPEND_CHAT_MESSAGE, CHAT_ADD_MESSAGE } from "./ActionTypes";


const initialState = {
  message: null,
  messages: [],
  loading: false,
  error: null,
};

const chatBotReducer = (state = initialState, action) => {
  switch (action.type) {
    case CHAT_BOT_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case APPEND_CHAT_MESSAGE:
      return {
        ...state,
        messages: [...state.messages, ...action.payload],
        loading: false,
        error: null,
      };
    case CHAT_ADD_MESSAGE:
      const msg = action.payload;
      // ensure content is string
      const safeContent = typeof msg.content === "string" ? msg.content : (msg.content?.text || JSON.stringify(msg.content));
      return { ...state, messages: [...state.messages, { ...msg, content: safeContent }] };
    case CHAT_BOT_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    default:
      return state;
  }
};

export default chatBotReducer;
