/**
 * marketData.js — Direct Yahoo Finance HTTP API for real-time Indian market data.
 * Uses the v8/finance/chart endpoint for quotes (v7/quote is now auth-gated).
 * Search uses v1/finance/search which remains open.
 */
const axios = require('axios');

const YAHOO_CHART_URL = 'https://query1.finance.yahoo.com/v8/finance/chart';
const YAHOO_SEARCH_URL = 'https://query2.finance.yahoo.com/v1/finance/search';

const HEADERS = {
  'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
};

/**
 * Fetch a real-time quote for a single symbol using the chart endpoint.
 */
async function getSingleQuote(symbol) {
  const url = `${YAHOO_CHART_URL}/${symbol}?range=1d&interval=1m&includePrePost=false`;
  const resp = await axios.get(url, { headers: HEADERS });
  const result = resp.data?.chart?.result?.[0];
  if (!result) return null;

  const meta = result.meta;
  const price = meta.regularMarketPrice;
  const previousClose = meta.chartPreviousClose || meta.previousClose;
  const change = price - previousClose;
  const changePercent = previousClose ? (change / previousClose) * 100 : 0;

  return {
    symbol: meta.symbol,
    shortName: meta.shortName || meta.symbol,
    price,
    change: parseFloat(change.toFixed(2)),
    changePercent: parseFloat(changePercent.toFixed(2)),
    previousClose,
    currency: meta.currency || 'INR',
    exchange: meta.exchangeName,
    marketState: meta.marketState || 'UNKNOWN'
  };
}

/**
 * Fetch real-time quotes for multiple symbols.
 * @param {string[]} symbols - e.g. ['^NSEI', 'TATAMOTORS.NS', 'RELIANCE.NS']
 * @returns {Object} { "RELIANCE.NS": { price, change, changePercent, ... }, ... }
 */
async function getQuotes(symbols) {
  const results = {};
  const promises = symbols.map(async (sym) => {
    try {
      const quote = await getSingleQuote(sym);
      if (quote) results[sym] = quote;
    } catch (err) {
      console.warn(`[marketData] Could not fetch ${sym}: ${err.message}`);
    }
  });
  await Promise.all(promises);
  return results;
}

/**
 * Search for stocks, indices, ETFs — covers all NSE/BSE companies.
 * @param {string} query - e.g. "Tata Motors", "NIFTY", "Reliance"
 * @returns {Array} matching securities
 */
async function searchSymbols(query) {
  const url = `${YAHOO_SEARCH_URL}?q=${encodeURIComponent(query)}&quotesCount=25&newsCount=0&enableFuzzyQuery=true&region=IN&lang=en-IN`;
  const resp = await axios.get(url, { headers: HEADERS });
  const quotes = resp.data?.quotes || [];
  return quotes
    .filter(q => q.isYahooFinance)
    .map(q => ({
      symbol: q.symbol,
      shortName: q.shortname || q.longname || q.symbol,
      exchange: q.exchDisp || q.exchange,
      type: q.typeDisp || q.quoteType,
      sector: q.sectorDisp || q.sector || '',
      industry: q.industryDisp || q.industry || ''
    }));
}

/**
 * Fetch chart data for a symbol.
 * @param {string} symbol
 * @param {string} range - '1d', '5d', '1mo', '3mo', '6mo', '1y', '5y'
 * @param {string} interval - '1m', '5m', '15m', '1h', '1d'
 */
async function getChart(symbol, range = '1d', interval = '5m') {
  const url = `${YAHOO_CHART_URL}/${symbol}?range=${range}&interval=${interval}&includePrePost=false`;
  const resp = await axios.get(url, { headers: HEADERS });
  const result = resp.data?.chart?.result?.[0];
  if (!result) return null;
  const timestamps = result.timestamp || [];
  const ohlcv = result.indicators?.quote?.[0] || {};
  return timestamps.map((ts, i) => ({
    time: ts,
    open: ohlcv.open?.[i],
    high: ohlcv.high?.[i],
    low: ohlcv.low?.[i],
    close: ohlcv.close?.[i],
    volume: ohlcv.volume?.[i]
  }));
}

module.exports = { getQuotes, searchSymbols, getChart };
