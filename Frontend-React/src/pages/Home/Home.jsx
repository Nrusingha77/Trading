/* eslint-disable no-unused-vars */
import { useEffect, useRef, useState } from "react";
import { AssetTable } from "./AssetTable";
import { Button } from "@/components/ui/button";
import StockChart from "../StockDetails/StockChart";
import {
  ChatBubbleIcon,
  ChevronLeftIcon,
  Cross1Icon,
  DotIcon,
} from "@radix-ui/react-icons";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchCoinDetails,
  fetchCoinList,
  fetchTreadingCoinList,
  getTop50CoinList,
} from "@/Redux/Coin/Action";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
} from "@/components/ui/pagination";
import { MessageCircle } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { sendMessage } from "@/Redux/Chat/Action";
import { ScrollArea } from "@/components/ui/scroll-area";
import SpinnerBackdrop from "@/components/custome/SpinnerBackdrop";
import MarkdownResponse from "@/components/custome/MarkdownResponse";

const Home = () => {
  const dispatch = useDispatch();
  const [page, setPage] = useState(1);
  const [category, setCategory] = useState("all");
  const { coin, chatBot, auth } = useSelector((store) => store);
  const [isBotRelease, setIsBotRelease] = useState(false);

  // ✅ Get JWT from Redux or localStorage
  const getJwt = () => {
    return auth?.jwt || localStorage.getItem("jwt");
  };

  // ✅ Fetch coin list with JWT
  useEffect(() => {
    const jwt = getJwt();
    if (jwt) {
      console.log("Fetching coin list with JWT...");
      dispatch(fetchCoinList(page, jwt));
    } else {
      console.warn("No JWT token found");
    }
  }, [page, dispatch]);

  // ✅ Fetch Bitcoin details with JWT
  useEffect(() => {
    const jwt = getJwt();
    if (jwt) {
      console.log("Fetching Bitcoin details...");
      dispatch(
        fetchCoinDetails({
          coinId: "bitcoin",
          jwt: jwt,
        })
      );
    } else {
      console.warn("No JWT token for coin details");
    }
  }, [dispatch]);

  // ✅ Fetch category-specific data with JWT
  useEffect(() => {
    const jwt = getJwt();
    if (!jwt) {
      console.warn("No JWT token for category fetch");
      return;
    }

    if (category === "top50") {
      console.log("Fetching top 50 coins...");
      dispatch(getTop50CoinList(jwt));
    } else if (category === "trading") {
      console.log("Fetching trading coins...");
      dispatch(fetchTreadingCoinList(jwt));
    }
  }, [category, dispatch]);

  const handlePageChange = (page) => {
    setPage(page);
  };

  const handleBotRelease = () => setIsBotRelease(!isBotRelease);

  const [inputValue, setInputValue] = useState("");

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      console.log("Enter key pressed:", inputValue);
      const jwt = getJwt();
      if (jwt) {
        dispatch(
          sendMessage({
            prompt: inputValue,
            jwt: jwt,
          })
        );
        setInputValue("");
      } else {
        console.error("No JWT token for chat message");
      }
    }
  };

  const handleChange = (event) => {
    setInputValue(event.target.value);
  };

  const chatContainerRef = useRef(null);

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [chatBot.messages]);

  if (coin.loading) {
    return <SpinnerBackdrop />;
  }

  return (
    <div className="relative">
      {/* Mobile View: Chart on top */}
      <div className="lg:hidden p-3">
        <StockChart coinId={coin.coinDetails?.id} />
        <div className="flex gap-5 items-center mt-4">
          <div>
            <div className="flex items-center gap-2">
              <p>{coin.coinDetails?.symbol?.toUpperCase()}</p>
              <DotIcon className="text-gray-400" />
              <p className="text-gray-400">{coin.coinDetails?.name}</p>
            </div>
            {coin.coinDetails && (
              <div className="flex items-end gap-2">
                <p className="text-xl font-bold">
                  ${parseFloat(coin.coinDetails.priceUsd).toFixed(2)}
                </p>
                <p className={`${coin.coinDetails.changePercent24Hr < 0 ? "text-red-600" : "text-green-600"}`}>
                  <span>({parseFloat(coin.coinDetails.changePercent24Hr).toFixed(2)}%)</span>
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
      <div className="lg:flex ">
        <div className="lg:w-[50%] border-r">
          <div className="p-3 flex items-center gap-4 ">
            <Button
              variant={category == "all" ? "default" : "outline"}
              onClick={() => setCategory("all")}
              className="rounded-full"
            >
              All
            </Button>
            <Button
              variant={category == "top50" ? "default" : "outline"}
              onClick={() => setCategory("top50")}
              className="rounded-full"
            >
              Top 50
            </Button>
          </div>
          <AssetTable
            category={category}
            coins={category == "all" ? coin.coinList : coin.top50}
          />
          {category == "all" && (
            <Pagination className="border-t py-3">
              <PaginationContent>
                <PaginationItem>
                  <Button
                    variant="ghost"
                    disabled={page == 1}
                    onClick={() => handlePageChange(page - 1)}
                  >
                    <ChevronLeftIcon className="h-4 w-4 mr-1" />
                    Previous
                  </Button>
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink
                    onClick={() => handlePageChange(1)}
                    isActive={page == 1}
                  >
                    1
                  </PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink
                    onClick={() => handlePageChange(2)}
                    isActive={page == 2}
                  >
                    2
                  </PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink
                    onClick={() => handlePageChange(3)}
                    isActive={page == 3}
                  >
                    3
                  </PaginationLink>
                </PaginationItem>
                {page > 3 && (
                  <PaginationItem>
                    <PaginationLink
                      onClick={() => handlePageChange(3)}
                      isActive
                    >
                      {page}
                    </PaginationLink>
                  </PaginationItem>
                )}
                <PaginationItem>
                  <PaginationEllipsis />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext
                    className="cursor-pointer"
                    onClick={() => handlePageChange(page + 1)}
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          )}
        </div>

        <div className="hidden lg:block lg:w-[50%] p-5">
          <StockChart coinId={coin.coinDetails?.id} />
          <div className="flex gap-5 items-center">
            <div>
              <div className="flex items-center gap-2">
                <p>{coin.coinDetails?.symbol?.toUpperCase()}</p>
                <DotIcon className="text-gray-400" />
                <p className="text-gray-400">{coin.coinDetails?.name}</p>
              </div>
              {coin.coinDetails && (
                <div className="flex items-end gap-2">
                  <p className="text-xl font-bold">
                    ${parseFloat(coin.coinDetails.priceUsd).toFixed(2)}
                  </p>
                  <p
                    className={`${
                      coin.coinDetails.changePercent24Hr < 0
                        ? "text-red-600"
                        : "text-green-600"
                    }`}
                  >
                    <span>
                      (
                      {parseFloat(coin.coinDetails.changePercent24Hr).toFixed(2)}
                      %)
                    </span>
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
      {/* Chat Bot Section - Fixed Position */}
      <section className="fixed bottom-5 right-5 z-40 flex flex-col justify-end items-end gap-2">
        {isBotRelease && (
          <div className="rounded-lg w-[20rem] md:w-[25rem] h-[70vh] bg-slate-900 shadow-2xl">
            <div className="flex justify-between items-center border-b px-6 h-[12%]">
              <p>Chat Bot</p>
              <Button onClick={handleBotRelease} size="icon" variant="ghost">
                <Cross1Icon />
              </Button>
            </div>

            <ScrollArea className="h-[76%]">
              <div className="flex flex-col gap-5 px-5 py-2">
              <div className="self-start pb-5 w-auto">
                <div className="justify-end self-end px-5 py-2 rounded-md bg-slate-800 w-auto">
                  {`hi, ${auth.user?.fullName}`}
                  <p>You can ask any crypto-related question.</p>
                  <p>like, price, market cap extra...</p>
                </div>
              </div>
              {chatBot.messages.map((item, index) => (
                <div
                  ref={chatContainerRef}
                  key={index}
                  className={`${
                    item.role == "user" ? "self-end" : "self-start"
                  } pb-5 w-auto`}
                >
                  {item.role === "user" ? (
                    <div className="justify-end self-end px-5 py-2 rounded-full bg-slate-800 w-auto">
                      {item.content}
                    </div>
                  ) : (
                    <div className="w-full">
                      <div className="bg-slate-700 flex gap-2 py-4 px-4 rounded-md min-w-[15rem] w-full">
                        <MarkdownResponse content={item.content} />
                      </div>
                    </div>
                  )}
                </div>
              ))}
              {chatBot.loading && <p>fetchin data...</p>}
              </div>
            </ScrollArea>

            <div className="h-[12%] border-t">
              <Input
                className="w-full h-full border-none outline-none"
                placeholder="write prompt"
                onChange={handleChange}
                value={inputValue}
                onKeyPress={handleKeyPress}
              />
            </div>
          </div>
        )}
        <div
          onClick={handleBotRelease}
          className="relative cursor-pointer group"
        >
          {/* Round icon button for mobile, larger with text for desktop */}
          <Button className="rounded-full h-14 w-14 md:h-auto md:w-auto md:px-4 md:py-2 md:gap-2 items-center justify-center">
            <MessageCircle
              className="fill-black -rotate-[90deg] stroke-none"
              size={30}
            />
            <span className="hidden md:block text-lg">Chat Bot</span>
          </Button> 
        </div>
      </section>
    </div>
  );
};

export default Home;
