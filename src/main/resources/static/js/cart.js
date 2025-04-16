document.addEventListener("DOMContentLoaded", function() {
  // 페이지 로드 시 금액 포매팅 함수
  function formatPrice() {
    // 상품 금액 포매팅
    const priceElements = document.querySelectorAll('.product-total');
    priceElements.forEach(priceElement => {
      let price = parseInt(priceElement.textContent.replace(/[^0-9]/g, '')); // '원' 제거하고 숫자만 추출
      if (!isNaN(price)) {
        priceElement.textContent = price.toLocaleString() + '원'; // 천 단위 구분 기호 추가하고 원 붙이기
      }
    });

    // 총 결제 금액 포매팅
    const totalPriceElement = document.getElementById("cart-total-price");
    if (totalPriceElement) {
      let totalPrice = parseInt(totalPriceElement.textContent.replace(/[^0-9]/g, '')); // '원' 제거하고 숫자만 추출
      if (!isNaN(totalPrice)) {
        totalPriceElement.textContent = totalPrice.toLocaleString() + '원'; // 천 단위 구분 기호 추가하고 원 붙이기
      }
    }
  }

  // 페이지 로드시 금액 포매팅 실행
  formatPrice();

  // 수량 변경 버튼을 찾고 클릭 시 동작을 설정
  document.querySelectorAll('.increase-btn, .decrease-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      const productId = this.getAttribute('data-product-id');

      if (!productId) {
        console.error("Product ID가 존재하지 않습니다.");
        return;
      }

      // 해당 상품의 수량 input 찾기
      const input = document.querySelector(`.quantity-input[data-product-id="${productId}"]`);
      if (!input) {
        console.error("수량 입력 요소를 찾을 수 없습니다.");
        return;
      }

      let quantity = parseInt(input.value);

      // 수량 변경
      if (this.classList.contains('increase-btn')) {
        quantity++;
      } else if (this.classList.contains('decrease-btn') && quantity > 1) {
        quantity--;
      }

      // 수량 반영
      input.value = quantity;

      // 서버에 업데이트 요청
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
          // 서버에서 응답이 왔을 때
          if (data.success) {
              // 개별 상품 금액 갱신
              const priceElement = document.getElementById(`price-${productId}`);
              if (priceElement) {
                  priceElement.textContent = data.itemTotalPrice.toLocaleString() + '원';
              }

              // 총 결제 금액 갱신
              const totalPriceElement = document.getElementById("cart-total-price");
              if (totalPriceElement) {
                  totalPriceElement.textContent = data.cartTotalPrice.toLocaleString() + '원';
              }

              // 금액 포매팅
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
});
