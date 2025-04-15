document.addEventListener("DOMContentLoaded", function() {
  // 수량 변경 버튼을 찾고 클릭 시 동작을 설정
  document.querySelectorAll('.increase-btn, .decrease-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      // 버튼 클릭 시 alert창 띄우기
      alert('수량 변경 버튼이 클릭되었습니다!');

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
      fetch('/cart/update', {
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
