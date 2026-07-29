const http = require('http');

// Test 1: Check the HTML being served
http.get('http://localhost:3000/', res => {
  let d = '';
  res.on('data', c => d += c);
  res.on('end', () => {
    console.log('=== HTML VERIFICATION ===');
    console.log('Script tag:', d.includes('app.js') ? 'FOUND' : 'MISSING');
    console.log('Has type=module:', d.includes('type="module"') ? 'YES (BAD)' : 'NO (GOOD)');
    console.log('Search bar:', d.includes('stock-search-input') ? 'FOUND' : 'MISSING');
    console.log('Ticker NSEI ID:', d.includes('tkr-^NSEI-val') ? 'FOUND' : 'MISSING');
    console.log('Ticker RELIANCE ID:', d.includes('tkr-RELIANCE.NS-val') ? 'FOUND' : 'MISSING');
    console.log('Ticker TMCV ID:', d.includes('tkr-TMCV.NS-val') ? 'FOUND' : 'MISSING');
    console.log('Cache-Control header:', res.headers['cache-control']);
    
    // Test 2: Hit the market API
    http.get('http://localhost:3000/api/market?symbols=^NSEI,RELIANCE.NS,TMCV.NS,BHARTIARTL.NS', res2 => {
      let d2 = '';
      res2.on('data', c => d2 += c);
      res2.on('end', () => {
        console.log('\n=== LIVE MARKET API RESPONSE ===');
        const data = JSON.parse(d2);
        for (const [sym, q] of Object.entries(data)) {
          console.log(`${sym}: Price=₹${q.price}, Change=${q.changePercent}`);
        }
      });
    });

    // Test 3: Hit the search API
    http.get('http://localhost:3000/api/search?q=Infosys', res3 => {
      let d3 = '';
      res3.on('data', c => d3 += c);
      res3.on('end', () => {
        console.log('\n=== SEARCH API: "Infosys" ===');
        const results = JSON.parse(d3);
        results.forEach(r => console.log(`  ${r.symbol} — ${r.shortName} (${r.exchange})`));
      });
    });
  });
});
