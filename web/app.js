/* ==========================================================================
   BHARAT INVEST OS - APPLICATION STATE & UI CONTROLLER
   ========================================================================== */

import { AgentRoles, runSimulatedAgentScan } from './agents.js';
import { queryCioAssistantEngine } from './cio_assistant.js';

// Application State
const state = {
  activeTab: 'dashboard',
  isScanning: false,
  holdings: [
    { symbol: 'TATAMOTORS', name: 'Tata Motors Ltd', sector: 'Automobile', qty: 450, avgPrice: 982.50, currentPrice: 988.00, strategy: 'SWING_BREAKOUT', target: 1120.00, stopLoss: 935.00, thesis: 'JLR 148k backlog + 50-EMA Golden Cross' },
    { symbol: 'BHARTIARTL', name: 'Bharti Airtel Ltd', sector: 'Telecom', qty: 250, avgPrice: 1445.00, currentPrice: 1455.00, strategy: 'BLUECHIP_COMPOUNDER', target: 1680.00, stopLoss: 1380.00, thesis: 'ARPU ₹211+ free cash flow conversion' },
    { symbol: 'PERSISTENT', name: 'Persistent Systems', sector: 'IT Services', qty: 60, avgPrice: 5410.00, currentPrice: 5430.00, strategy: 'SWING_BREAKOUT', target: 6250.00, stopLoss: 5120.00, thesis: 'GenAI 15%+ TCV booking growth' }
  ],
  tradeEntries: [
    { symbol: 'TATAMOTORS', buyPrice: 982.50, qty: 450, reason: 'Golden Cross 50-EMA breakout', target: 1120.00, stopLoss: 935.00, status: 'OPEN' },
    { symbol: 'BHARTIARTL', buyPrice: 1445.00, qty: 250, reason: '20-EMA rebound + ARPU growth', target: 1680.00, stopLoss: 1380.00, status: 'OPEN' },
    { symbol: 'PERSISTENT', buyPrice: 5410.00, qty: 60, reason: 'Flag pattern breakout on 2.8x volume', target: 6250.00, stopLoss: 5120.00, status: 'OPEN' },
    { symbol: 'BHEL', buyPrice: 285.00, qty: 800, reason: 'Thermal capex backlog beat', target: 360.00, stopLoss: 260.00, status: 'TARGET_HIT' },
    { symbol: 'SUZLON', buyPrice: 58.00, qty: 3000, reason: '100% Debt Free turnaround', target: 82.00, stopLoss: 52.00, status: 'TARGET_HIT' }
  ],
  chatMessages: [
    { sender: 'CIO', text: 'Greetings. I am your Chief Investment Officer Agent. I am monitoring the Indian stock market (NSE/BSE) across 27 specialized sub-agents with 30-minute autonomous background scanning. Ask me for immediate swing trades, targeted single-agent scans, or catalysts!', time: '16:54' }
  ],
  scanResults: []
};

// Initialize Application
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  setupEventListeners();
  updateLiveClockAndMarketStatus();
  setInterval(updateLiveClockAndMarketStatus, 1000);
  renderDashboard();
  renderAgentsGrid();
  renderPortfolio();
  renderJournal();
  renderChatHistory();

  // Start Live Market Data Polling
  fetchLiveMarketData();
  setInterval(fetchLiveMarketData, 15000); // Update every 15s
});

// --- LIVE MARKET DATA MODULE ---
const LIVE_SYMBOLS = ['^NSEI', '^NSEBANK', '^BSESN', '^INDIAVIX', 'TMCV.NS', 'BHARTIARTL.NS', 'PERSISTENT.NS', 'RELIANCE.NS'];

async function fetchLiveMarketData() {
  try {
    const res = await fetch(`/api/market?symbols=${LIVE_SYMBOLS.join(',')}`);
    const data = await res.json();
    
    Object.keys(data).forEach(sym => {
      const quote = data[sym];
      const valEl = document.getElementById(`tkr-${sym}-val`);
      const changeEl = document.getElementById(`tkr-${sym}-change`);
      
      if (valEl && quote.price) {
        const formatted = sym.startsWith('^') 
          ? quote.price.toLocaleString('en-IN', { maximumFractionDigits: 2 })
          : `₹${quote.price.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
        valEl.textContent = formatted;
      }
      
      if (changeEl && quote.changePercent) {
        changeEl.textContent = quote.changePercent;
        const isUp = quote.changePercent.startsWith('+');
        changeEl.className = `ticker-change ${isUp ? 'up' : 'down'}`;
      }
      
      // Update Portfolio Holdings
      const baseSym = sym.replace('.NS', '').replace('.BO', '');
      const holding = state.holdings.find(h => h.symbol === baseSym);
      if (holding && quote.price) {
        holding.currentPrice = quote.price;
      }
    });

    // Re-render UI with new prices
    renderDashboard();
    renderPortfolio();
  } catch (err) {
    console.error('Failed to fetch live market data', err);
  }
}

// --- STOCK SEARCH MODULE ---
let searchTimeout = null;
const searchInput = document.getElementById('stock-search-input');
const searchResults = document.getElementById('search-results');

if (searchInput) {
  searchInput.addEventListener('input', () => {
    clearTimeout(searchTimeout);
    const query = searchInput.value.trim();
    if (query.length < 2) {
      searchResults.classList.remove('active');
      return;
    }
    searchTimeout = setTimeout(() => performSearch(query), 300);
  });

  searchInput.addEventListener('focus', () => {
    if (searchResults.innerHTML) searchResults.classList.add('active');
  });

  // Close on click outside
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.search-bar-wrapper')) {
      searchResults.classList.remove('active');
    }
  });

  // Ctrl+K shortcut
  document.addEventListener('keydown', (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
      e.preventDefault();
      searchInput.focus();
    }
    if (e.key === 'Escape') {
      searchResults.classList.remove('active');
      searchInput.blur();
    }
  });
}

async function performSearch(query) {
  try {
    const res = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
    const results = await res.json();
    
    if (results.length === 0) {
      searchResults.innerHTML = '<div class="search-no-results">No results found</div>';
    } else {
      searchResults.innerHTML = results.map(r => `
        <div class="search-result-item" data-symbol="${r.symbol}" onclick="onSearchResultClick('${r.symbol}', '${r.shortName.replace(/'/g, "\\'")}')">
          <div class="search-result-left">
            <span class="search-result-symbol">${r.symbol}</span>
            <span class="search-result-name">${r.shortName}</span>
          </div>
          <div class="search-result-right">
            <span class="search-result-exchange">${r.exchange}</span>
            <span class="search-result-type">${r.type}</span>
          </div>
        </div>
      `).join('');
    }
    searchResults.classList.add('active');
  } catch (err) {
    console.error('Search failed', err);
  }
}

async function onSearchResultClick(symbol, name) {
  searchResults.classList.remove('active');
  searchInput.value = `${symbol} — ${name}`;
  
  // Fetch real-time quote for the selected stock
  try {
    const res = await fetch(`/api/market?symbols=${symbol}`);
    const data = await res.json();
    const quote = data[symbol];
    if (quote) {
      const msg = `📊 ${name} (${symbol})\nPrice: ₹${quote.price?.toLocaleString('en-IN')}\nChange: ${quote.changePercent}\nPrev Close: ₹${quote.previousClose?.toLocaleString('en-IN')}`;
      alert(msg);
    }
  } catch (err) {
    console.error('Quote fetch failed', err);
  }
}

// Live Clock & Market Timing Engine
function updateLiveClockAndMarketStatus() {
  const now = new Date();
  const timeStr = now.toLocaleTimeString('en-US', { hour12: false });
  document.getElementById('clock-time').textContent = `${timeStr} IST`;

  const hours = now.getHours();
  const mins = now.getMinutes();
  const timeInMins = hours * 60 + mins;

  let statusText = 'AFTER HOURS';
  let dotClass = 'dot-amber';
  let countdownText = 'Opens 09:15 AM IST';

  if (timeInMins >= 540 && timeInMins < 555) {
    statusText = 'PRE-OPEN (MATCHING)';
    dotClass = 'dot-amber';
    countdownText = 'Continuous Trading at 09:15 AM';
  } else if (timeInMins >= 555 && timeInMins < 930) {
    statusText = 'LIVE CONTINUOUS TRADING';
    dotClass = 'dot-green';
    const minsLeft = 930 - timeInMins;
    const h = Math.floor(minsLeft / 60);
    const m = minsLeft % 60;
    countdownText = `Closes in ${h}h ${m}m`;
  } else if (timeInMins >= 930 && timeInMins < 960) {
    statusText = 'POST-CLOSING RECONCILIATION';
    dotClass = 'dot-amber';
    countdownText = 'Market Closed';
  }

  const elStatus = document.getElementById('market-status');
  if (elStatus) {
    elStatus.textContent = `${statusText} (${countdownText})`;
    const dot = document.getElementById('status-dot');
    if (dot) dot.className = `dot ${dotClass}`;
  }
}

// Sidebar Navigation
function setupNavigation() {
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(item => {
    item.addEventListener('click', () => {
      navItems.forEach(i => i.classList.remove('active'));
      item.classList.add('active');

      const tab = item.getAttribute('data-tab');
      state.activeTab = tab;

      document.querySelectorAll('.view-panel').forEach(panel => panel.classList.remove('active'));
      const activePanel = document.getElementById(`view-${tab}`);
      if (activePanel) activePanel.classList.add('active');
    });
  });
}

// Event Listeners Setup
function setupEventListeners() {
  // Run Scan Button
  const btnScan = document.getElementById('btn-run-scan');
  if (btnScan) btnScan.addEventListener('click', triggerMultiAgentScan);

  // Chat Send Button
  const btnSend = document.getElementById('btn-send-chat');
  const chatInput = document.getElementById('chat-input');
  if (btnSend && chatInput) {
    const handleSend = () => {
      const txt = chatInput.value.trim();
      if (!txt) return;

      const userTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      state.chatMessages.push({ sender: 'USER', text: txt, time: userTime });
      chatInput.value = '';
      renderChatHistory();

      setTimeout(() => {
        const cioResult = queryCioAssistantEngine(txt, state.chatMessages);
        const cioTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        // Trigger live scan for the selected sub-agents
        if (cioResult.triggeredAgentKeys && cioResult.triggeredAgentKeys.length > 0) {
          cioResult.triggeredAgentKeys.forEach(key => {
            const scan = runSimulatedAgentScan(key);
            // Replace or append scan result
            const idx = state.scanResults.findIndex(r => r.roleKey === key);
            if (idx >= 0) {
              state.scanResults[idx] = scan;
            } else {
              state.scanResults.push(scan);
            }
          });
          renderAgentsGrid();
        }

        state.chatMessages.push({ sender: 'CIO', text: cioResult.response, time: cioTime });
        renderChatHistory();
        
        // Speak response
        if ('speechSynthesis' in window) {
          const utterance = new SpeechSynthesisUtterance(cioResult.response);
          utterance.rate = 1.05;
          utterance.pitch = 1.0;
          window.speechSynthesis.speak(utterance);
        }
      }, 400);
    };

    btnSend.addEventListener('click', handleSend);
    chatInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleSend(); });

    // Voice Input Setup
    const btnVoice = document.getElementById('btn-voice-chat');
    if (btnVoice) {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      if (SpeechRecognition) {
        const recognition = new SpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-IN'; // Indian English
        
        let isRecording = false;

        recognition.onstart = () => {
          isRecording = true;
          btnVoice.style.backgroundColor = 'var(--accent-red)';
          btnVoice.style.color = 'white';
          chatInput.placeholder = 'Listening... Speak now.';
        };

        recognition.onresult = (event) => {
          const transcript = event.results[0][0].transcript;
          chatInput.value = transcript;
          setTimeout(handleSend, 500); // Auto-send after reading
        };

        recognition.onerror = (event) => {
          console.error('Speech recognition error', event.error);
          chatInput.placeholder = 'Error listening. Try again.';
          resetVoiceBtn();
        };

        recognition.onend = () => {
          resetVoiceBtn();
        };

        function resetVoiceBtn() {
          isRecording = false;
          btnVoice.style.backgroundColor = 'var(--bg-darker)';
          btnVoice.style.color = 'var(--accent-purple)';
          chatInput.placeholder = 'Ask CIO Assistant for swing trades, position sizing, 27-agent support, or risk limits...';
        }

        btnVoice.addEventListener('click', () => {
          if (isRecording) {
            recognition.stop();
          } else {
            // Cancel any ongoing speech so they don't overlap
            if ('speechSynthesis' in window) {
               window.speechSynthesis.cancel();
            }
            recognition.start();
          }
        });
      } else {
        btnVoice.style.display = 'none';
        console.warn('Speech Recognition API not supported in this browser.');
      }
    }

    // Quick Chip Buttons
    document.querySelectorAll('.chip-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const promptText = btn.getAttribute('data-prompt');
        if (promptText) {
          chatInput.value = promptText;
          handleSend();
        }
      });
    });
  }

  // Modals
  document.getElementById('btn-add-holding')?.addEventListener('click', () => {
    document.getElementById('modal-holding').classList.add('active');
  });
  document.getElementById('btn-close-holding-modal')?.addEventListener('click', () => {
    document.getElementById('modal-holding').classList.remove('active');
  });
  document.getElementById('form-holding')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const sym = document.getElementById('h-symbol').value.toUpperCase();
    const name = document.getElementById('h-name').value;
    const price = parseFloat(document.getElementById('h-price').value);
    const qty = parseInt(document.getElementById('h-qty').value);
    const tgt = parseFloat(document.getElementById('h-target').value);
    const stop = parseFloat(document.getElementById('h-stop').value);

    state.holdings.push({
      symbol: sym, name: name, sector: 'Equities', qty: qty, avgPrice: price, currentPrice: price,
      strategy: 'SWING_TRADE', target: tgt, stopLoss: stop, thesis: 'Manual user entry'
    });

    renderPortfolio();
    renderDashboard();
    document.getElementById('modal-holding').classList.remove('active');
  });
}

// Trigger 27-Agent Scan
function triggerMultiAgentScan() {
  if (state.isScanning) return;
  state.isScanning = true;

  const btn = document.getElementById('btn-run-scan');
  if (btn) btn.textContent = 'Scanning 27 Agents...';

  state.scanResults = [];
  const roles = Object.keys(AgentRoles);
  
  let i = 0;
  const interval = setInterval(() => {
    if (i < roles.length) {
      const res = runSimulatedAgentScan(roles[i]);
      state.scanResults.push(res);
      renderAgentsGrid();
      i++;
    } else {
      clearInterval(interval);
      state.isScanning = false;
      if (btn) btn.textContent = '⚡ Run 27-Agent Scan';
      renderDashboard();
    }
  }, 100);
}

// Render Dashboard
function renderDashboard() {
  const totalValue = state.holdings.reduce((sum, h) => sum + (h.qty * h.currentPrice), 0);
  const totalCost = state.holdings.reduce((sum, h) => sum + (h.qty * h.avgPrice), 0);
  const pnl = totalValue - totalCost;
  const pnlPct = totalCost > 0 ? (pnl / totalCost) * 100 : 0;

  const elVal = document.getElementById('dash-portfolio-val');
  if (elVal) elVal.textContent = `₹${(totalValue / 100000).toFixed(2)} Lakhs`;

  const elPnl = document.getElementById('dash-pnl');
  if (elPnl) {
    elPnl.textContent = `${pnl >= 0 ? '+' : ''}₹${pnl.toFixed(2)} (${pnlPct.toFixed(2)}%)`;
    elPnl.style.color = pnl >= 0 ? 'var(--accent-green)' : 'var(--accent-red)';
  }
}

// Render 27 Agents Matrix Grid
function renderAgentsGrid() {
  const grid = document.getElementById('agents-grid-container');
  if (!grid) return;

  grid.innerHTML = '';
  Object.keys(AgentRoles).forEach(key => {
    const role = AgentRoles[key];
    const card = document.createElement('div');
    card.className = 'agent-card';

    const lastScan = state.scanResults.find(r => r.roleKey === key);

    card.innerHTML = `
      <div class="agent-header">
        <div class="agent-title">${role.title}</div>
        <div class="agent-cat">${role.category}</div>
      </div>
      <div class="agent-desc">${role.description}</div>
      <div class="agent-tools">Tools: ${role.tools.join(', ')}</div>
      <div style="font-size: 11px; font-family: var(--font-mono); color: var(--accent-green);">
        ${lastScan ? `✓ Scanned [${lastScan.timestamp}] | Confidence: ${lastScan.confidencePct}%` : '🟢 Status: Active (30-Min Cron)'}
      </div>
    `;
    grid.appendChild(card);
  });
}

// Render Portfolio View
function renderPortfolio() {
  const tbody = document.getElementById('portfolio-table-body');
  if (!tbody) return;

  tbody.innerHTML = '';
  state.holdings.forEach(h => {
    const value = h.qty * h.currentPrice;
    const pnl = (h.currentPrice - h.avgPrice) * h.qty;
    const pnlPct = ((h.currentPrice - h.avgPrice) / h.avgPrice) * 100;

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${h.symbol}</strong><br><span style="font-size:11px; color:var(--text-muted);">${h.name}</span></td>
      <td><span class="agent-cat">${h.strategy}</span></td>
      <td>${h.qty}</td>
      <td>₹${h.avgPrice.toFixed(2)}</td>
      <td>₹${h.currentPrice.toFixed(2)}</td>
      <td><strong>₹${value.toFixed(2)}</strong></td>
      <td style="color:${pnl >= 0 ? 'var(--accent-green)' : 'var(--accent-red)'}; font-weight:700;">
        ${pnl >= 0 ? '+' : ''}₹${pnl.toFixed(2)} (${pnlPct.toFixed(1)}%)
      </td>
      <td>Target: ₹${h.target} | SL: ₹${h.stopLoss}</td>
    `;
    tbody.appendChild(tr);
  });
}

// Render Journal View
function renderJournal() {
  const tbody = document.getElementById('journal-table-body');
  if (!tbody) return;

  tbody.innerHTML = '';
  state.tradeEntries.forEach(t => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${t.symbol}</strong></td>
      <td>₹${t.buyPrice.toFixed(2)}</td>
      <td>${t.qty}</td>
      <td>Target: ₹${t.target} | Stop: ₹${t.stopLoss}</td>
      <td>${t.reason}</td>
      <td><span class="agent-cat" style="color: ${t.status.includes('TARGET') ? 'var(--accent-green)' : 'var(--accent-blue)'};">${t.status}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

// Render Chat History
function renderChatHistory() {
  const container = document.getElementById('chat-history-container');
  if (!container) return;

  container.innerHTML = '';
  state.chatMessages.forEach(msg => {
    const div = document.createElement('div');
    div.className = `chat-bubble ${msg.sender.toLowerCase()}`;
    div.innerHTML = `
      <div style="font-weight:700; font-size:11px; margin-bottom:4px; opacity:0.8;">
        ${msg.sender === 'USER' ? 'You' : 'CIO Assistant'} • ${msg.time}
      </div>
      <div>${msg.text.replace(/\n/g, '<br>')}</div>
    `;
    container.appendChild(div);
  });

  container.scrollTop = container.scrollHeight;
}
