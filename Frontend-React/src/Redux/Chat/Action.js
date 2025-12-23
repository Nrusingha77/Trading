/* eslint-disable no-unused-vars */
import api from "@/Api/api";
import {
  CHAT_BOT_FAILURE,
  CHAT_BOT_REQUEST,
  CHAT_BOT_SUCCESS,
  APPEND_CHAT_MESSAGE,
  CHAT_ADD_MESSAGE,
} from "./ActionTypes";

export const sendMessage = ({ prompt, jwt }) => async (dispatch) => {
  dispatch({
    type: CHAT_BOT_REQUEST,
  });

  try {
    const { data } = await api.post(
      "/api/chatbot/ask",
      { prompt },
      {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      }
    );

    // data is now a plain STRING from backend (assistantText)
    // NOT an object with .message property
    const assistantContent = typeof data === "string" ? data : (data?.content || JSON.stringify(data));

    // dispatch user message + assistant message together
    dispatch({
      type: APPEND_CHAT_MESSAGE,
      payload: [
        { content: prompt, role: "user" },
        { content: assistantContent, role: "assistant" }, // <- use role "assistant" not "model"
      ],
    });
    
    console.log("get success ans", assistantContent);
  } catch (error) {
    dispatch({ type: CHAT_BOT_FAILURE, payload: error });
    console.log(error);
  }
};
