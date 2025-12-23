/* eslint-disable no-unused-vars */
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getUserWatchlist, addItemToWatchlist } from "@/Redux/Watchlist/Action";
import { useNavigate } from "react-router-dom";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { TrashIcon } from "@radix-ui/react-icons";
import SpinnerBackdrop from "@/components/custome/SpinnerBackdrop";

const Watchlist = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { watchlist } = useSelector((store) => store);

  useEffect(() => {
    console.log("📋 Loading watchlist");
    dispatch(getUserWatchlist());
  }, [dispatch]);

  const handleRemoveFromWatchlist = (coinId) => {
    console.log("➖ Removing coin from watchlist:", coinId);
    dispatch(addItemToWatchlist(coinId)); // Toggle removes if already exists
  };

  const handleCoinClick = (coinId) => {
    navigate(`/market/${coinId}`);
  };

  if (watchlist.loading) {
    return <SpinnerBackdrop />;
  }

  // ✅ Use watchlist.coins instead of watchlist.items
  const coins = watchlist?.coins || [];

  return (
    <div className="p-5">
      <h1 className="text-2xl font-bold mb-5">My Watchlist</h1>

      <div className="w-full overflow-x-auto">
        <Table>
          <TableHeader>
            <TableRow className="sticky top-0 bg-background border-b border-slate-700">
              <TableHead className="text-slate-300">Coin</TableHead>
              <TableHead className="text-slate-300">Symbol</TableHead>
              <TableHead className="text-right text-slate-300">Price</TableHead>
              <TableHead className="text-right text-slate-300">24h Change</TableHead>
              <TableHead className="text-center text-slate-300">Action</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {coins.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} className="text-center text-slate-400 py-8">
                  No coins in your watchlist
                </TableCell>
              </TableRow>
            ) : (
              coins.map((coin) => (
                <TableRow
                  key={coin?.id}
                  className="hover:bg-slate-800/50 cursor-pointer border-b border-slate-700 transition-colors"
                >
                  <TableCell
                    onClick={() => handleCoinClick(coin?.id)}
                    className="py-4 flex items-center gap-2"
                  >
                    <Avatar className="h-8 w-8">
                      <AvatarImage
                        src={`https://assets.coincap.io/assets/icons/${coin?.symbol?.toLowerCase()}@2x.png`}
                        alt={coin?.name}
                      />
                      <AvatarFallback>{coin?.symbol?.[0]}</AvatarFallback>
                    </Avatar>
                    <span className="text-slate-100">{coin?.name}</span>
                  </TableCell>

                  <TableCell
                    onClick={() => handleCoinClick(coin?.id)}
                    className="text-slate-200 py-4"
                  >
                    {coin?.symbol?.toUpperCase()}
                  </TableCell>

                  <TableCell
                    onClick={() => handleCoinClick(coin?.id)}
                    className="text-right text-slate-200 py-4"
                  >
                    ${parseFloat(coin?.priceUsd).toFixed(2)}
                  </TableCell>

                  <TableCell
                    onClick={() => handleCoinClick(coin?.id)}
                    className={`text-right py-4 ${
                      parseFloat(coin?.changePercent24Hr) < 0
                        ? "text-red-500"
                        : "text-green-500"
                    }`}
                  >
                    {parseFloat(coin?.changePercent24Hr).toFixed(2)}%
                  </TableCell>

                  <TableCell className="text-center py-4">
                    <Button
                      onClick={() => handleRemoveFromWatchlist(coin?.id)}
                      className="h-8 w-8"
                      variant="outline"
                      size="icon"
                    >
                      <TrashIcon className="h-4 w-4 text-red-500" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};

export default Watchlist;
