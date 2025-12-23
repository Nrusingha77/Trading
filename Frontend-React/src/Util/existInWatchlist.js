/**
 * Check if a coin exists in watchlist
 * @param {Array} watchlistCoins - Array of coins in watchlist
 * @param {Object} coin - Coin object to check
 * @returns {boolean} - True if coin exists in watchlist
 */
export const existInWatchlist = (watchlistCoins, coin) => {
  // ✅ Handle undefined, null, or non-array inputs
  if (!watchlistCoins || !Array.isArray(watchlistCoins)) {
    console.warn("⚠️ watchlistCoins is not iterable:", watchlistCoins);
    return false;
  }

  if (!coin || !coin.id) {
    console.warn("⚠️ Invalid coin object:", coin);
    return false;
  }

  // ✅ Check if coin exists in watchlist array
  return watchlistCoins.some((item) => item?.id === coin.id);
};