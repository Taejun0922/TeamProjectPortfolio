document.addEventListener("DOMContentLoaded", function () {
  // 금액 포매팅 함수
  function formatPrice() {
    const priceElements = document.querySelectorAll('.product-total');
    priceElements.forEach(priceElement => {
      let price = parseInt(priceElement.textContent.replace(/[^0-9]/g, ''));
      if (!isNaN(price)) {
        priceElement.textContent = price.toLocaleString() + '원';
      }
    });

    const totalPriceElement = document.getElementById("cart-total-price");
    if (totalPriceElement) {
      let totalPrice = parseInt(totalPriceElement.textContent.replace(/[^0-9]/g, ''));
      if (!isNaN(totalPrice)) {
        totalPriceElement.textContent = totalPrice.toLocaleString() + '원';
      }
    }
  }

  // 금액 포매팅 실행
  formatPrice();

  // 수량 버튼 처리
  document.querySelectorAll('.increase-btn, .decrease-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      const productId = this.getAttribute('data-product-id');
      if (!productId) return console.error("Product ID가 존재하지 않습니다.");

      const input = document.querySelector(`.quantity-input[data-product-id="${productId}"]`);
      if (!input) return console.error("수량 입력 요소를 찾을 수 없습니다.");

      let quantity = parseInt(input.value);

      if (this.classList.contains('increase-btn')) {
        quantity++;
      } else if (this.classList.contains('decrease-btn') && quantity > 1) {
        quantity--;
      }

      input.value = quantity;

      fetch('/CampingMarket/cart/update', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          productId: productId,
          quantity: quantity
        })
      })
        .then(res => res.json())
        .then(data => {
          if (data.success) {
            const priceElement = document.getElementById(`price-${productId}`);
            if (priceElement) {
              priceElement.textContent = data.itemTotalPrice.toLocaleString() + '원';
            }

            const totalPriceElement = document.getElementById("cart-total-price");
            if (totalPriceElement) {
              totalPriceElement.textContent = data.cartTotalPrice.toLocaleString() + '원';
            }

            formatPrice();
          } else {
            console.error('상품 수량 업데이트에 실패했습니다.');
          }
        })
        .catch(err => {
          console.error("AJAX 오류:", err);
          alert("서버와의 통신에 문제가 발생했습니다. 다시 시도해주세요.");
        });
    });
  });

  // 전체 선택 체크박스
  const checkAllBox = document.querySelector('.cartChkTot');
  const checkboxes = document.querySelectorAll('.cartChkOne');

  if (checkAllBox) {
    checkAllBox.addEventListener('change', function () {
      checkboxes.forEach(chk => chk.checked = this.checked);
    });
  }

  checkboxes.forEach(chk => {
    chk.addEventListener('change', function () {
      const allChecked = [...checkboxes].every(chk => chk.checked);
      checkAllBox.checked = allChecked;
    });
  });

  // ✅ 선택 주문하기 버튼 처리
    const selectedOrderBtn = document.getElementById('btn-selected-order');
    if (selectedOrderBtn) {
      selectedOrderBtn.addEventListener('click', function () {
        const selectedForm = document.getElementById('selected-order-form');
        selectedForm.innerHTML = ''; // 기존 데이터 제거

        const selectedItems = document.querySelectorAll('.cartChkOne:checked');

        if (selectedItems.length === 0) {
          alert('선택된 상품이 없습니다.');
          return;
        }

        selectedItems.forEach(chk => {
          const row = chk.closest('tr');
          const productId = row.getAttribute('data-product-id');
          const quantity = row.querySelector('.quantity-input').value;

          // hidden input 생성
          const idInput = document.createElement('input');
          idInput.type = 'hidden';
          idInput.name = 'productIds';
          idInput.value = productId;
          selectedForm.appendChild(idInput);

          const qtyInput = document.createElement('input');
          qtyInput.type = 'hidden';
          qtyInput.name = 'quantities';
          qtyInput.value = quantity;
          selectedForm.appendChild(qtyInput);
        });

        selectedForm.action = '/CampingMarket/order/selected';
        selectedForm.method = 'POST';
        selectedForm.submit();
      });
    }
});