(() => {
  const base = document.body.dataset.base || '';
  const endpoint = base + '/api/v1/chat';
  const FALLBACK = 'Sorry, I could not reach the helper just now. Please try again in a moment.';
  const GREETING = "Hi! I'm the KaviMart helper. Ask me about orders, your bag, reviews or selling.";
  const SUGGESTIONS = [
    'How do I cancel an order?',
    'How do I search for products?',
    'How do I sell on KaviMart?',
    'How do reviews work?'
  ];

  const style = document.createElement('link');
  style.rel = 'stylesheet';
  style.href = base + '/assets/chat.css';
  document.head.appendChild(style);

  const root = document.createElement('div');
  root.className = 'kchat';
  root.innerHTML =
    '<button class="kchat-toggle" type="button" aria-expanded="false" aria-controls="kchat-panel">' +
    '<span class="kchat-toggle-icon" aria-hidden="true">&#10022;</span><span>Ask KaviMart</span></button>' +
    '<section class="kchat-panel" id="kchat-panel" role="dialog" aria-label="KaviMart helper">' +
    '<header class="kchat-head"><div><strong>KaviMart helper</strong>' +
    '<small>Ask about orders, your bag, reviews or selling</small></div>' +
    '<button class="kchat-close" type="button" aria-label="Close chat">&times;</button></header>' +
    '<div class="kchat-log" role="log" aria-live="polite"></div>' +
    '<div class="kchat-chips"></div>' +
    '<form class="kchat-form">' +
    '<input class="kchat-input" type="text" maxlength="300" placeholder="Type your question" ' +
    'aria-label="Your question" autocomplete="off">' +
    '<button class="kchat-send" type="submit">Send</button></form></section>';
  document.body.appendChild(root);

  const toggle = root.querySelector('.kchat-toggle');
  const closeButton = root.querySelector('.kchat-close');
  const log = root.querySelector('.kchat-log');
  const chips = root.querySelector('.kchat-chips');
  const form = root.querySelector('.kchat-form');
  const input = root.querySelector('.kchat-input');
  const sendButton = root.querySelector('.kchat-send');
  let busy = false;

  function setOpen(open) {
    root.classList.toggle('open', open);
    toggle.setAttribute('aria-expanded', String(open));
    if (open) { input.focus(); } else { toggle.focus(); }
  }

  function addMessage(text, kind) {
    const node = document.createElement('div');
    node.className = 'kchat-msg kchat-' + kind;
    node.textContent = text;
    log.appendChild(node);
    log.scrollTop = log.scrollHeight;
    return node;
  }

  async function ask(text) {
    const message = text.trim();
    if (!message || busy) { return; }
    busy = true;
    sendButton.disabled = true;
    chips.hidden = true;
    addMessage(message, 'user');
    const typing = addMessage('Typing...', 'bot kchat-typing');
    let reply = FALLBACK;
    let kind = 'error';
    try {
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message }),
        credentials: 'same-origin'
      });
      let payload = null;
      try { payload = await response.json(); } catch (ignore) { payload = null; }
      if (response.ok && payload && payload.success && payload.data) {
        reply = payload.data.reply;
        kind = 'bot';
      } else if (payload && payload.error && payload.error.message) {
        reply = payload.error.message;
      }
    } catch (ignore) {
      reply = FALLBACK;
    }
    typing.remove();
    addMessage(reply, kind);
    busy = false;
    sendButton.disabled = false;
    input.focus();
  }

  SUGGESTIONS.forEach(text => {
    const chip = document.createElement('button');
    chip.type = 'button';
    chip.className = 'kchat-chip';
    chip.textContent = text;
    chip.addEventListener('click', () => ask(text));
    chips.appendChild(chip);
  });

  addMessage(GREETING, 'bot');
  toggle.addEventListener('click', () => setOpen(!root.classList.contains('open')));
  closeButton.addEventListener('click', () => setOpen(false));
  root.addEventListener('keydown', event => {
    if (event.key === 'Escape' && root.classList.contains('open')) { setOpen(false); }
  });
  form.addEventListener('submit', event => {
    event.preventDefault();
    const value = input.value;
    input.value = '';
    ask(value);
  });
})();