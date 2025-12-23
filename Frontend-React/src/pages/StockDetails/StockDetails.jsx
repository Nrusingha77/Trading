/* eslint-disable no-unused-vars */
/* eslint-disable no-constant-condition */
import { Button } from "@/components/ui/button";
import {
  BookmarkFilledIcon,
  BookmarkIcon,
  DotIcon,
  HeartIcon,
} from "@radix-ui/react-icons";
import StockChart from "./StockChart";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import TradingForm from "./TreadingForm";
import { useParams } from "react-router-dom";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchCoinById } from "@/Redux/Coin/Action";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { existInWatchlist } from "@/Util/existInWatchlist";
import { addItemToWatchlist, getUserWatchlist } from "@/Redux/Watchlist/Action";
import { getAssetDetails } from "@/Redux/Assets/Action";
import { getUserWallet } from "@/Redux/Wallet/Action";
import SpinnerBackdrop from "@/components/custome/SpinnerBackdrop";

const StockDetails = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const { coin, watchlist, auth } = useSelector((store) => store);

  useEffect(() => {
    if (id && auth?.jwt) {
      console.log("📥 Fetching coin details for:", id);
      dispatch(
        fetchCoinById({
          coinId: id,
          jwt: auth.jwt || localStorage.getItem("jwt"),
        })
      );
    }
  }, [id, auth?.jwt, dispatch]);

  useEffect(() => {
    const jwt = localStorage.getItem("jwt");
    if (jwt) {
      console.log("📋 Fetching user watchlist");
      dispatch(getUserWatchlist());
      dispatch(getUserWallet(jwt));
    }
  }, [dispatch]);

  const handleAddToWatchlist = () => {
    if (coin.coinDetails?.id) {
      console.log("➕ Adding coin to watchlist:", coin.coinDetails.id);
      dispatch(addItemToWatchlist(coin.coinDetails.id));
    }
  };

  if (coin.loading) {
    return <SpinnerBackdrop />;
  }

  return (
    <>
      {coin.loading ? (
        "loading..."
      ) : (
        <div className="p-5 mt-5">
          <div className="flex justify-between">
            <div className="flex gap-5 items-center">
              <div>
                {coin.coinDetails && (
                  <Avatar>
                    <AvatarImage
                      src={`https://assets.coincap.io/assets/icons/${coin.coinDetails.symbol.toLowerCase()}@2x.png`}
                    />
                  </Avatar>
                )}
              </div>
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
                        {parseFloat(coin.coinDetails.changePercent24Hr).toFixed(
                          2
                        )}
                        %)
                      </span>
                    </p>
                  </div>
                )}
              </div>
            </div>
            <div className="flex items-center gap-5">
              <Button
                onClick={handleAddToWatchlist}
                className="h-10 w-10"
                variant="outline"
                size="icon"
              >
                {/* ✅ FIXED: Use watchlist.coins instead of watchlist.items */}
                {existInWatchlist(watchlist?.coins || [], coin.coinDetails) ? (
                  <BookmarkFilledIcon className="h-6 w-6" />
                ) : (
                  <BookmarkIcon className="h-6 w-6" />
                )}
              </Button>

              <Dialog>
                <DialogTrigger>
                  <Button size="lg">TREAD</Button>
                </DialogTrigger>
                <DialogContent className="">
                  <DialogHeader className="">
                    <DialogTitle className="px-10 pt-5 text-center">
                      how much do you want to spend?
                    </DialogTitle>
                  </DialogHeader>
                  <TradingForm />
                </DialogContent>
              </Dialog>
            </div>
          </div>
          <div className="mt-10">
            <StockChart coinId={coin.coinDetails?.id} />
          </div>
        </div>
      )}
    </>
  );
};

export default StockDetails;
