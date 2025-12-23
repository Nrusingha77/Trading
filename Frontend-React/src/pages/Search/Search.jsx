/* eslint-disable no-unused-vars */
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Input } from "@/components/ui/input";
import { SearchIcon } from "lucide-react";
import { searchCoin } from "@/Redux/Coin/Action";
import { useNavigate, useLocation } from "react-router-dom";
import SpinnerBackdrop from "@/components/custome/SpinnerBackdrop";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

const SearchCoin = () => {
  const dispatch = useDispatch();
  const { coin, auth } = useSelector((store) => store);
  const [keyword, setKeyword] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  // ✅ Get JWT
  const jwt = auth?.jwt || localStorage.getItem("jwt");

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const q = params.get("q") || "";
    if (q) {
      setKeyword(q);
      dispatch(searchCoin(q));
    }
  }, [location.search, dispatch]);

  const handleSearchCoin = () => {
    if (!keyword || !keyword.trim()) return;
    console.log("🔍 Searching for:", keyword);
    dispatch(searchCoin(keyword));
    navigate(`/search?q=${encodeURIComponent(keyword)}`);
  };

  // ✅ Read from searchCoinList (guaranteed to be array by reducer)
  const results = coin?.searchCoinList || [];

  if (coin?.loading) {
    return <SpinnerBackdrop />;
  }

  return (
    <div className="w-full min-h-screen px-4 py-10 lg:px-0 lg:w-[60%] lg:mx-auto">
      <div className="flex items-center justify-center pb-16 gap-2">
        <Input
          className="p-5 w-full lg:w-[70%] rounded-r-none"
          placeholder="explore market..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSearchCoin();
          }}
        />
        <Button onClick={handleSearchCoin} className="p-5 rounded-l-none px-6">
          <SearchIcon className="h-5 w-5" />
        </Button>
      </div>

      <div className="w-full overflow-x-auto">
        <Table>
          <TableHeader className="py-9">
            <TableRow className="sticky top-0 left-0 right-0 bg-background border-b border-slate-700">
              <TableHead className="py-3 text-slate-300">Rank</TableHead>
              <TableHead className="text-slate-300">Coin</TableHead>
              <TableHead className="text-right text-slate-300">SYMBOL</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {results.length === 0 ? (
              <TableRow className="border-b border-slate-700">
                <TableCell colSpan={3} className="text-center text-slate-400 py-8">
                  {keyword ? "No results found" : "Start searching for coins"}
                </TableCell>
              </TableRow>
            ) : (
              results.map((item, idx) => {
                const id = item?.id || item?.symbol;
                const rank = item?.rank || "-";
                const name = item?.name || item?.id || "Unknown";
                const symbol = item?.symbol || "-";
                const img = item?.image || "";

                return (
                  <TableRow
                    onClick={() => id && navigate(`/market/${id}`)}
                    key={`${id}-${idx}`}
                    className="hover:bg-slate-800/50 cursor-pointer border-b border-slate-700 transition-colors"
                  >
                    <TableCell className="py-4">
                      <p className="text-sm text-slate-200">{rank}</p>
                    </TableCell>

                    <TableCell className="font-medium flex items-center gap-2 py-4">
                      <Avatar className="-z-50 h-8 w-8 flex-shrink-0">
                        {img ? (
                          <AvatarImage src={img} alt={name} className="object-cover" />
                        ) : (
                          <AvatarFallback className="bg-amber-400 text-slate-900 font-bold">
                            {name?.[0]?.toUpperCase() || "?"}
                          </AvatarFallback>
                        )}
                      </Avatar>
                      <span className="text-slate-100 truncate">{name}</span>
                    </TableCell>

                    <TableCell className="text-right text-slate-200 py-4">
                      <span className="inline-block px-2 py-1 bg-slate-800/60 rounded text-sm font-medium">
                        {symbol.toUpperCase()}
                      </span>
                    </TableCell>
                  </TableRow>
                );
              })
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};

export default SearchCoin;
