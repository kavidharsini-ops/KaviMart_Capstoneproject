(() => {
  const base = document.body.dataset.base || '';
  const api = base + '/api/v1';
  let toastTimer;
  function toast(message) {
    let node = document.querySelector('.toast');
    if (!node) { node = document.createElement('div'); node.className = 'toast'; node.setAttribute('role', 'status'); document.body.appendChild(node); }
    node.textContent = message; node.classList.add('visible'); clearTimeout(toastTimer);
    toastTimer = setTimeout(() => node.classList.remove('visible'), 2600);
  }
  async function request(path, method, body) {
    const response = await fetch(api + path, { method, headers: body ? { 'Content-Type': 'application/json' } : {}, body: body ? JSON.stringify(body) : undefined, credentials: 'same-origin' });
    const payload = await response.json();
    if (!response.ok || !payload.success) throw new Error(payload.error?.message || 'The request could not be completed.');
    return payload.data;
  }
  document.querySelectorAll('[data-add-product]').forEach(button => button.addEventListener('click', async () => {
    const productId = Number(button.dataset.addProduct);
    const quantity = button.dataset.useQuantity ? Number(document.querySelector('[data-quantity]')?.value || 1) : 1;
    try { await request('/cart', 'POST', { productId, quantity }); toast('Added to your bag.'); button.classList.add('added'); setTimeout(() => button.classList.remove('added'), 700); }
    catch (error) { if (error.message.toLowerCase().includes('sign in')) window.location.href = base + '/auth/login'; else toast(error.message); }
  }));
  async function refreshCart(data) {
    const total = document.querySelector('[data-cart-total]');
    if (total) total.textContent = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(data.total);
    document.querySelectorAll('[data-cart-item]').forEach(row => {
      const id = row.dataset.cartItem;
      const entry = data.items.find(item => String(item.product.id) === id);
      if (!entry) { row.remove(); return; }
      const lineTotal = row.querySelector('.cart-line-price');
      if (lineTotal) lineTotal.textContent = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(entry.lineTotal);
    });
  }
  document.querySelectorAll('[data-update-quantity]').forEach(input => input.addEventListener('change', async () => {
    try { const data = await request('/cart/' + input.dataset.updateQuantity, 'PUT', { quantity: Number(input.value) }); await refreshCart(data); toast('Bag updated.'); }
    catch (error) { toast(error.message); window.location.reload(); }
  }));
  document.querySelectorAll('[data-remove-item]').forEach(button => button.addEventListener('click', async () => {
    try { const data = await request('/cart/' + button.dataset.removeItem, 'DELETE'); await refreshCart(data); if (!data.items.length) window.location.reload(); else toast('Removed from your bag.'); }
    catch (error) { toast(error.message); }
  }));
  document.querySelector('[data-checkout]')?.addEventListener('click', async event => {
    const button = event.currentTarget; button.disabled = true; button.textContent = 'Placing your order…';
    try { await request('/orders', 'POST', {}); window.location.href = base + '/buyer/orders?placed=1'; }
    catch (error) { button.disabled = false; button.textContent = 'Continue to checkout →'; const status = document.querySelector('[data-cart-message]'); if (status) status.textContent = error.message; }
  });
})();
