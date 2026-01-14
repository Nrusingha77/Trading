import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  AvatarIcon,
  DragHandleHorizontalIcon,
  MagnifyingGlassIcon,
} from "@radix-ui/react-icons";
import SideBar from "../SideBar/SideBar";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { useNavigate } from "react-router-dom";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useState, useEffect, useRef } from "react";
import { useSelector, useDispatch } from "react-redux"; // <-- added useDispatch
import api from "@/Api/api";
import { searchCoin } from "@/Redux/Coin/Action"; // <-- import search action

const MarketTicker = () => {
  const [coins, setCoins] = useState([]);
  const [idx, setIdx] = useState(0);
  const [displayPrice, setDisplayPrice] = useState("");
  const rafRef = useRef(null);
  const prevPriceRef = useRef(0);

  const fmt = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2,
  });

  useEffect(() => {
    let mounted = true;
    const fetchCoins = async () => {
      try {
        const res = await api.get("/api/coins/top50");
        if (!mounted) return;
        setCoins(res.data || []);
        if ((res.data || []).length > 0) {
          const p = parseFloat(res.data[0].priceUsd || res.data[0].price || 0);
          prevPriceRef.current = p;
          setDisplayPrice(fmt.format(p));
        }
      } catch (e) {
        console.error("❌ Error fetching top 50:", e.message);
      }
    };
    fetchCoins();
    const interval = setInterval(fetchCoins, 15000);
    return () => {
      mounted = false;
      clearInterval(interval);
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
    };
  }, []);

  useEffect(() => {
    if (!coins || coins.length === 0) return;
    const id = setInterval(() => {
      setIdx((s) => (coins.length ? (s + 1) % coins.length : 0));
    }, 3000);
    return () => clearInterval(id);
  }, [coins]);

  useEffect(() => {
    if (!coins || coins.length === 0) return;
    const coin = coins[idx];
    const target = parseFloat(coin?.priceUsd || coin?.price || 0);
    const start = prevPriceRef.current || target;
    const duration = 600;
    const startTime = performance.now();

    const step = (now) => {
      const t = Math.min(1, (now - startTime) / duration);
      const ease = 1 - (1 - t) * (1 - t);
      const cur = start + (target - start) * ease;
      setDisplayPrice(fmt.format(isFinite(cur) ? cur : 0));
      if (t < 1) {
        rafRef.current = requestAnimationFrame(step);
      } else {
        prevPriceRef.current = target;
      }
    };

    if (rafRef.current) cancelAnimationFrame(rafRef.current);
    rafRef.current = requestAnimationFrame(step);

    return () => {
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
    };
  }, [idx, coins]);

  if (!coins || coins.length === 0) {
    return (
      <div className="flex items-center gap-3">
        <div className="text-xs text-slate-400 hidden sm:block">Market</div>
        <div className="px-2 py-1 rounded-md bg-slate-700/40 border border-slate-700 text-sm text-slate-300">
          Loading...
        </div>
      </div>
    );
  }

  const coin = coins[idx];
  const change = parseFloat(coin?.changePercent24Hr || coin?.changePercent || 0);
  const changeClass = isNaN(change)
    ? "text-slate-300"
    : change >= 0
      ? "text-emerald-400"
      : "text-rose-400";

  return (
    <div className="flex items-center gap-2 lg:gap-4 select-none">
      <div className="text-xs text-slate-400 hidden sm:block">Market</div>
      <div
        className="flex items-center gap-2 sm:gap-3 px-2 sm:px-3 py-1 rounded-lg border border-slate-700 bg-gradient-to-b from-slate-800/60 to-slate-800/40"
        title={`${coin.name} (${coin.symbol})`}
      >
        <div className="flex flex-col leading-tight">
          <div className="text-sm font-medium text-slate-100">{coin.symbol?.toUpperCase()}</div>
          <div className="text-xs text-slate-400 truncate max-w-[4rem] sm:max-w-[10rem]">{coin.name}</div>
        </div>

        <div className="border-l border-slate-700 h-8" />

        <div className="flex flex-col items-end min-w-[3.5rem] sm:min-w-[4rem]">
          <div className="text-sm font-semibold text-slate-100">{displayPrice}</div>
          <div className={`text-xs ${changeClass}`}>{isNaN(change) ? "-" : `${change.toFixed(2)}%`}</div>
        </div>
      </div>
    </div>
  );
};

const Navbar = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch(); // <-- new
  const { auth } = useSelector((store) => store);
  const [searchTerm, setSearchTerm] = useState(""); // <-- new
  const [searchKeyword, setSearchKeyword] = useState(""); // <-- new

  const handleNavigate = () => {
    if (auth.user) {
      auth.user.role === "ROLE_ADMIN" ? navigate("/admin/withdrawal") : navigate("/profile");
    }
  };

  const doSearch = (term) => {
    const q = String(term || "").trim();
    if (!q) return;
    // dispatch search so store is populated, then navigate to Search page with query
    dispatch(searchCoin(q));
    navigate(`/search?q=${encodeURIComponent(q)}`);
  };

  const handleSearch = () => {
    if (!searchKeyword.trim()) return;
    console.log("🔍 Searching for:", searchKeyword);
    navigate(`/search?q=${encodeURIComponent(searchKeyword)}`);
    setSearchKeyword("");  // ✅ Clear search box
};

  return (
    <>
      <div className="sticky top-0 z-50">
        <div
          className="flex items-center justify-between gap-4 px-4 py-3
                        bg-gradient-to-r from-slate-900 via-zinc-900 to-slate-800
                        border-b border-slate-800 shadow-lg backdrop-blur-sm"
        >
          {/* left - menu + brand */}
          <div className="flex items-center gap-4">
            <Sheet>
              <SheetTrigger>
                <Button
                  className="rounded-full h-11 w-11 flex items-center justify-center
                             bg-transparent hover:bg-slate-800 border border-slate-700"
                  variant="ghost"
                  size="icon"
                >
                  <DragHandleHorizontalIcon className="h-6 w-6 text-slate-200" />
                </Button>
              </SheetTrigger>
              <SheetContent className="w-72 border-r-0 flex flex-col justify-start p-0" side="left">
                <SheetHeader>
                  <SheetTitle>
                    <div className="flex items-center gap-3 px-4 py-4">
                      <div className="h-8 w-8 rounded-md flex items-center justify-center bg-gradient-to-br from-amber-500 to-orange-500 text-slate-900 font-bold">
                        BC
                      </div>
                      <div className="leading-tight">
                        <div className="text-lg font-extrabold text-white tracking-tight">
                          <span className="text-amber-400">Bharat</span> <span className="text-slate-100">Crypto</span>
                        </div>
                        <div className="text-xs text-slate-400">Trust · Trade · Track · Invest</div>
                      </div>
                    </div>
                  </SheetTitle>
                </SheetHeader>
                <SideBar />
              </SheetContent>
            </Sheet>

            <button onClick={() => navigate("/")} className="flex items-center gap-2">
              <div className="h-8 w-8 rounded-md flex items-center justify-center bg-gradient-to-br from-amber-500 to-orange-500 text-slate-900 font-bold">
                BC
              </div>
              <div className="text-lg lg:text-base text-slate-100 font-semibold hover:opacity-90 hidden md:block">
                Bharat <span className="text-amber-400">Crypto</span>
              </div>
            </button>
          </div>

          {/* Mobile: Ticker + Search Icon */}
          <div className="flex md:hidden items-center gap-1 sm:gap-2">
            <MarketTicker />
            <Button variant="ghost" size="icon" onClick={() => navigate("/search")} className="text-slate-200">
              <MagnifyingGlassIcon className="h-5 w-5" />
            </Button>
          </div>

          {/* center - search (visible on md+) */}
          <div className="flex-1 max-w-2xl mx-4 hidden md:flex items-center gap-3">
            <div className="flex items-center flex-1 bg-slate-800/40 rounded-lg px-3 py-1 border border-slate-700">
              <MagnifyingGlassIcon className="text-slate-400 h-5 w-5 mr-2" />
              <Input
                placeholder="Search coins..."
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                onKeyDown={(e) => {
                    if (e.key === "Enter") handleSearch();
                }}
                className="bg-transparent border-0 ring-0 text-slate-100 placeholder:text-slate-400"
              />
              <Button className="ml-3" variant="ghost" onClick={() => doSearch(searchTerm)}>Search</Button>
            </div>

            <MarketTicker />
          </div>

          {/* right - avatar */}
          <div className="flex items-center gap-3">
            <div className="hidden sm:flex flex-col items-end mr-2">
              <div className="text-xs text-slate-400">Welcome</div>
              <div className="text-sm text-slate-100 font-medium truncate max-w-[12rem]">
                {auth.user ? auth.user.fullName || auth.user.email : "Guest"}
              </div>
            </div>

            {/* ✅ Avatar with picture from backend or fallback to initials */}
            <Avatar 
              className="cursor-pointer h-8 w-8 sm:h-10 sm:w-10 ring-2 ring-amber-500/50 bg-slate-800" 
              onClick={handleNavigate}
            >
              {!auth.user ? (
                <AvatarIcon className="h-6 w-6 text-slate-200" />
              ) : auth.user?.picture ? (
                <AvatarImage src={auth.user.picture} alt={auth.user.fullName || "User"} />
              ) : (
                <AvatarFallback className="bg-gradient-to-br from-amber-500 to-orange-500 text-slate-900 font-bold">
                  {(auth.user?.fullName && auth.user.fullName[0])
                    ? auth.user.fullName[0].toUpperCase()
                    : auth.user?.email?.[0]?.toUpperCase() || "U"}
                </AvatarFallback>
              )}
            </Avatar>
          </div>
        </div>
      </div>
    </>
  );
};

export default Navbar;
